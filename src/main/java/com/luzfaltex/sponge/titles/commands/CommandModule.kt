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

package com.luzfaltex.sponge.titles.commands

import co.aikar.commands.*
import com.luzfaltex.sponge.titles.Message
import com.luzfaltex.sponge.titles.Titles
import org.spongepowered.api.entity.living.player.Player
import co.aikar.commands.ConditionFailedException
import com.google.common.collect.ImmutableList
import com.luzfaltex.sponge.titles.catalogs.GroupCatalog
import com.luzfaltex.sponge.titles.catalogs.TitleCatalog
import org.spongepowered.api.Sponge
import java.util.*
import javax.security.auth.Subject


class CommandModule(private val commandManager: SpongeCommandManager, private val plugin: Titles) {

    private var setupComplete: Boolean = false

    fun registerCommands() {
        if (setupComplete)
            throw UnsupportedOperationException("Commands have already been registered. Something is seriously wrong.")

        fun validatePlayerTitle(uuid: UUID, titleId: String) {
            if (!plugin.dataService.getAvailableTitles(uuid).containsKey(titleId))
                throw ConditionFailedException(Message.SetTitleFailure.getFormattedMessage("You are not authorized to use this title, or it does not exist"))
        }

        // Enable automatic help library.
        @Suppress("DEPRECATION")
        commandManager.enableUnstableAPI("help")

        // region Replacements
        // endregion

        // region Contexts
        // endregion

        // region Completions
        commandManager.commandCompletions.registerAsyncCompletion("ownTitles") { context ->
            plugin.dataService.getAvailableTitles(context.player.uniqueId).values.map { title -> title.name }
        }
        commandManager.commandCompletions.registerAsyncCompletion("theirTitles") { context ->
            plugin.dataService.getAvailableTitles(context.getContextValue(Player::class.java).uniqueId).values.map { title -> title.name }
        }
        commandManager.commandCompletions.registerAsyncCompletion("titles") {
            plugin.dataService.getAllGroups().map { title -> title.name }
        }
        commandManager.commandCompletions.registerAsyncCompletion("groups") {
            plugin.dataService.getAllGroups().map { group -> group.name }
        }
        commandManager.commandCompletions.registerAsyncCompletion("action") { ImmutableList.of("add", "remove") }
        commandManager.commandCompletions.registerAsyncCompletion("type") { ImmutableList.of("user", "title") }
        commandManager.commandCompletions.registerAsyncCompletion("subject") { context ->
            val type = context.getContextValue(SubjectType::class.java)
            if (type == SubjectType.TITLE) {
                plugin.dataService.getAllTitles().map { title -> title.name }
            } else {
                Sponge.getGame().server.onlinePlayers.map { player -> player.name }
            }
        }
        // endregion

        // region Conditions
        commandManager.commandConditions.addCondition(String::class.java, "entitled") { _, context, titleId ->
            val player = context.issuer.player
            validatePlayerTitle(player.uniqueId, titleId)
        }
        commandManager.commandConditions.addCondition(String::class.java, "uniqueTitle") { _, _, titleId ->
            // a title with the specified id is *not* present
            if (Sponge.getRegistry().getType(TitleCatalog::class.java, titleId).isPresent)
                throw ConditionFailedException(Message.CreateTitleFailure.getFormattedMessage("The specified title '$titleId' already exists"))
        }
        commandManager.commandConditions.addCondition(String::class.java, "uniqueGroup") { _, _, groupId ->
            // A group with the specified id is *not* present
            if (Sponge.getRegistry().getType(GroupCatalog::class.java, groupId.toLowerCase()).isPresent)
                throw ConditionFailedException(Message.CreateGroupFailure.getFormattedMessage("The specified group '$groupId' already exists"))
        }
        commandManager.commandConditions.addCondition(String::class.java, "titleExists") { _, _, titleId ->
            if (!Sponge.getRegistry().getType(TitleCatalog::class.java, titleId).isPresent)
                throw ConditionFailedException(Message.CreateTitleFailure.getFormattedMessage("The specified title '$titleId' does not exist"))
        }
        commandManager.commandConditions.addCondition(String::class.java, "groupExists") { _, _, groupId ->
            if (!Sponge.getRegistry().getType(GroupCatalog::class.java, groupId).isPresent)
                throw ConditionFailedException(Message.CreateGroupFailure.getFormattedMessage("The specified group '$groupId' does not exist"))
        }
        commandManager.commandConditions.addCondition(String::class.java, "entitledOther") { _, context, titleId ->
            val player = context.getResolvedArg(Player::class.java) as Player
            validatePlayerTitle(player.uniqueId, titleId)
        }
        commandManager.commandConditions.addCondition(String::class.java, "subject") { _, executionContext, subjectName ->
            if (executionContext.getResolvedArg<SubjectType>("type", SubjectType::class.java) == SubjectType.TITLE) {
                if (!Sponge.getRegistry().getType(TitleCatalog::class.java, subjectName).isPresent) {
                    val groupId = executionContext.getResolvedArg<String>("groupId", String::class.java)
                    throw ConditionFailedException(Message.AddTitleFailure.getFormattedMessage(groupId, "The specified title '$subjectName' does not exist."))
                }
            }
        }
        // endregion

        // region Dependencies
        commandManager.registerDependency(Titles::class.java, plugin)
        // endregion

        // region Commands
        commandManager.registerCommand(Commands())
        // endregion

        commandManager.setDefaultExceptionHandler { command, _, _, _, t ->
            plugin.logger.warn("Error while executing ${command.name}:\n${t.message}\n${t.stackTrace}")
            false
        }

        setupComplete = true
    }
}
