/*
 * MIT License
 *
 * Copyright (c) 2019 LuzFaltex
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.luzfaltex.sponge.titles

import co.aikar.commands.SpongeCommandManager
import com.google.inject.Inject
import com.luzfaltex.sponge.titles.catalogs.*
import com.luzfaltex.sponge.titles.commands.CommandModule
import com.luzfaltex.sponge.titles.services.confirmation.ConfirmationService
import com.luzfaltex.sponge.titles.services.confirmation.IConfirmationService
import com.luzfaltex.sponge.titles.services.data.DataService
import com.luzfaltex.sponge.titles.services.data.IDataService
import me.rojo8399.placeholderapi.Placeholder
import me.rojo8399.placeholderapi.PlaceholderService
import me.rojo8399.placeholderapi.Source
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.config.DefaultConfig
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.event.game.state.GameStartedServerEvent
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameRegistryEvent
import org.spongepowered.api.event.game.state.GameConstructionEvent
import org.spongepowered.api.plugin.Dependency
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.plugin.PluginContainer

import java.nio.file.Path

@Plugin(id = "titles", name = "Titles", description = "A simple plugin which allows for the display of a title or occupation.", url = "https://github.com/ravenrockrp/Titles", version = "0.1-API7", authors = ["Foxtrek_64"], dependencies = [Dependency(id = "placeholderapi")])
class Titles @Inject constructor(@DefaultConfig(sharedRoot = false) private val _defaultConfig: Path, private val _pluginContainer: PluginContainer) {

    @Inject
    lateinit var logger: Logger

    val defaultConfig: Path
        get() = _defaultConfig

    val pluginContainer: PluginContainer
        get() = _pluginContainer

    val defaultTitleCatalog: TitleCatalog
        get() = Sponge.getRegistry().getType(TitleCatalog::class.java, "empty").get()

    val dataService : IDataService
        get() = _dataService

    val confirmationService: IConfirmationService
        get() = _confirmationService

    val titleCatalogModule : TitleCatalogModule
        get() = _titleCatalogModule

    val groupCatalogModule : GroupCatalogModule
        get() = _groupCatalogModule

    val actionCatalogModule : ActionCatalogModule
        get() = _actionCatalogModule


    private lateinit var _placeholderService: PlaceholderService
    private lateinit var _commandManager: SpongeCommandManager
    private val _dataService: IDataService = DataService(this)
    private lateinit var _confirmationService: IConfirmationService
    private lateinit var _titleCatalogModule: TitleCatalogModule
    private lateinit var _groupCatalogModule: GroupCatalogModule
    private lateinit var _actionCatalogModule: ActionCatalogModule
    private lateinit var _commandModule: CommandModule


    // init services here
    @Listener
    fun onServerInitialization(event: GameInitializationEvent) {
        _commandManager = SpongeCommandManager(pluginContainer)
        _confirmationService = ConfirmationService(this)

        Sponge.getServiceManager().setProvider(this, IDataService::class.java, _dataService)
        Sponge.getServiceManager().setProvider(this, IConfirmationService::class.java, _confirmationService)
    }

    @Listener
    fun onConstruct(event: GameConstructionEvent) {
        // We need to register the catalog module so that Sponge knows how to retrieve our titles.
        // CatalogRegistryModels are the backing storage for every CatalogType registered to the GameRegistry.

        _titleCatalogModule = TitleCatalogModule()
        _groupCatalogModule = GroupCatalogModule()
        _actionCatalogModule = ActionCatalogModule()

        Sponge.getRegistry().registerModule(TitleCatalog::class.java, _titleCatalogModule)
        Sponge.getRegistry().registerModule(GroupCatalog::class.java, _groupCatalogModule)

        // We don't need an onRegisterActions event for this one since these are not written to disk.
        Sponge.getRegistry().registerModule(ActionCatalog::class.java, _actionCatalogModule)
    }

    @Listener
    fun onRegisterTitles(event: GameRegistryEvent.Register<TitleCatalog>) {
        // Register the rest of the titles
        val titles = _dataService.getAllTitles()

        for (title in titles) {
            event.register(title)
        }
    }

    @Listener
    fun onRegisterGroups(event: GameRegistryEvent.Register<GroupCatalog>) {
        // Register the rest of the groups
        val groups = _dataService.getAllGroups()

        for (group in groups) {
            event.register(group)
        }
    }

    @Listener
    fun onServerStart(event: GameStartedServerEvent) {
        // Register commands
        _commandModule = CommandModule(_commandManager, this)
        _commandModule.registerCommands()


        _placeholderService = Sponge.getServiceManager().provide(PlaceholderService::class.java).get()

        _placeholderService.loadAll(this, this).stream()
                .map { builder -> builder.description("Returns the player's selected TitleCatalog") }
                .map { builder -> builder.author("Foxtrek_64").version("1.0") }
                .forEach { builder ->
                    try {
                        builder.buildAndRegister()
                    } catch (ex: Exception) {
                        logger.error(ex.message, ex)
                        ex.printStackTrace()
                    }
                }
    }

    @Placeholder(id = "title")
    fun getTitle(@Source player: Player): String = formatTitle(_dataService.getSelectedTitle(player.uniqueId).name)

    // Provide optional formatting here. Likely just going to be handled in the chat plugin.
    private fun formatTitle(titleText: String) : String {
        return titleText
    }
}
