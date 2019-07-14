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

package com.luzfaltex.sponge.titles.services.data

import com.luzfaltex.sponge.titles.catalogs.TitleCatalog
import com.luzfaltex.sponge.titles.Titles
import com.luzfaltex.sponge.titles.catalogs.GroupCatalog
import com.luzfaltex.sponge.titles.results.Result
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.DataContainer
import org.spongepowered.api.data.DataQuery
import org.spongepowered.api.data.persistence.DataFormats
import java.nio.file.Files
import java.util.*
import kotlin.collections.HashSet


class DataService(val plugin: Titles) : IDataService {

    private val _selectedTitles: MutableMap<UUID, TitleCatalog> = mutableMapOf()
    private val _personalTitles: MutableMap<UUID, Set<TitleCatalog>> = mutableMapOf()
    private val _groups: MutableMap<String, GroupCatalog> = mutableMapOf()

    private val selectedTitleLabel = "SelectedTitle"
    private val personalTitlesLabel = "PersonalTitles"
    private val groupsLabel = "Groups"

    // region selectedTitle

    /**
     * Sets the player's current title
     */
    override fun setSelectedTitle(playerId: UUID, title: TitleCatalog): Result {
        _selectedTitles[playerId] = title
        saveToFile()
        return Result.FromSuccess()
    }

    /**
     * Retrieves the currently selected title from the [org.spongepowered.api.entity.living.player.Player] specified by the provided [UUID]
     * @return [String] containing the user's selected title
     */
    override fun getSelectedTitle(uuid: UUID): TitleCatalog = _selectedTitles[uuid] ?: plugin.defaultTitleCatalog

    // endregion

    // region personalTitle

    /**
     * Retrieves the personal titles from the [org.spongepowered.api.entity.living.player.Player] specified by the provided [UUID]
     * @return [List] containing the user's personal titles
     */
    override fun getPersonalTitleIds(uuid: UUID): HashSet<TitleCatalog> = _personalTitles[uuid].orEmpty() as HashSet<TitleCatalog>

    /**
     * Adds a title to the listing of personal titles a player is entitled to
     * @return [SuccessResult] on pass; otherwise, [FailureResult].
     */
    override fun addPersonalTitle(playerId: UUID, title: TitleCatalog) : Result {
        val titles = _personalTitles[playerId].orEmpty() as HashSet<TitleCatalog>

        if (!titles.add(title))
            return Result.FromFailure("Title with id '" + title.id +"' already exists as a personal title.")

        _personalTitles[playerId] = titles

        saveToFile()

        return Result.FromSuccess()
    }

    /**
     * Removes a title from the listing of personal titles a player is entitled to.
     * @return [SuccessResult]
     */
    override fun removePersonalTitle(playerId: UUID, title: TitleCatalog) : Result {
        val titles = _personalTitles[playerId].orEmpty() as HashSet<TitleCatalog>

        val didRemove = titles.remove(title)

        _personalTitles[playerId] = titles

        if (!didRemove)
            return Result.FromSuccess("Nothing to do.")

        return Result.FromSuccess()
    }

    // endregion

    // region title

    /**
     * Registers the specified [TitleCatalog]
     * @return [Result.FromSuccess] on success; otherwise, [Result.FromFailure]
     */
    override fun addTitle(title: TitleCatalog, groupId: String?) : Result {
        registerTitle(title, Optional.ofNullable(groupId))
        return Result.FromSuccess()
    }

    override fun removeTitle(title: TitleCatalog, groupId: String?) : Result {
        unregisterTitle(title, Optional.ofNullable(groupId))
        return Result.FromSuccess()
    }

    override fun updatePersonalTitle(title: TitleCatalog, playerId: UUID): Result {
        if (!_personalTitles[playerId].orEmpty().contains(title))
            return Result.FromFailure("Specified player does not own title with id '${title.id}'.")

        plugin.titleCatalogModule.unregisterCatalog(title.id)
        plugin.titleCatalogModule.registerAdditionalCatalog(title)

        saveToFile()

        return Result.FromSuccess()
    }

    override fun updateTitle(title: TitleCatalog, groupId: String?): Result {
        unregisterTitle(title, Optional.ofNullable(groupId))
        registerTitle(title, Optional.ofNullable(groupId))

        return Result.FromSuccess()
    }

    //endregion

    // region group

    /**
     * Registers the specified [GroupCatalog]
     * @return [Result.FromSuccess] on success; otherwise, [Result.FromFailure].
     */
    override fun addGroup(group: GroupCatalog) : Result {
        if (_groups.containsKey(group.id))
            return Result.FromFailure("A group with id '" + group.id + "' already exists!")

        registerGroup(group)

        return Result.FromSuccess()
    }

    /**
     * Removes the specified [GroupCatalog]
     * @return [Result.FromSuccess]
     */
    override fun removeGroup(groupId: String) : Result {
        if (!_groups.containsKey(groupId))
            return Result.FromSuccess("Nothing to do.")

        unregisterGroup(groupId)

        return Result.FromSuccess()
    }

    override fun updateGroup(group: GroupCatalog): Result {
        unregisterGroup(group.id)
        registerGroup(group)
        return Result.FromSuccess()
    }

    // endregion

    /**
     * Retrieves a collection of titles the specified [org.spongepowered.api.entity.living.player.Player] is entitled to.
     * @return [HashSet] containing the user's available titles
     */
    override fun getAvailableTitles(uuid: UUID): HashMap<String, TitleCatalog> {
        val titles = HashMap<String, TitleCatalog>()

        // First let's add any personal titles
        for (title in _personalTitles[uuid]!!.iterator()) {
            titles.putIfAbsent(title.id, title)
        }

        // Now let's add the default title, since everyone has that one
        titles.putIfAbsent(plugin.defaultTitleCatalog.id, plugin.defaultTitleCatalog)

        // Now let's iterate through groups and add the appropriate titles
        for (group in _groups.values) {
            if (group.hasMember(uuid))
                for (title in group.titles)
                    titles.putIfAbsent(title.id, title)
        }

        return titles
    }

    /**
     * Retrieves a collection of every title regardless of entitlement
     * @return [HashSet] containing every title
     */
    override fun getAllTitles() : HashSet<TitleCatalog> {
        val titles = HashSet<TitleCatalog>()

        // add all personal titles
        for (titleSet in _personalTitles.values)
            titles.addAll(titleSet)

        // Add the default title
        titles.add(plugin.defaultTitleCatalog)

        // Add the group titles
        for (group in _groups.values) {
            titles.addAll(group.titles)
        }

        return titles
    }

    override fun getAllPersonalTitles(playerId: UUID): HashSet<TitleCatalog> = HashSet(_personalTitles[playerId])

    override fun getAllGroups(): HashSet<GroupCatalog> = HashSet(_groups.values)

    override fun findTitle(titleId: String): Optional<TitleCatalog> {
        return Sponge.getRegistry().getType(TitleCatalog::class.java, titleId)
    }

    override fun findGroup(groupId: String): Optional<GroupCatalog> {
        return Sponge.getRegistry().getType(GroupCatalog::class.java, groupId)
    }

    override fun findTitleGroup(title: TitleCatalog): Optional<GroupCatalog> {
        var groupCatalog: GroupCatalog? = null

        for (group in _groups.values) {
            if (group.hasTitle(title)) {
                groupCatalog = group
                break
            }
        }

        return Optional.ofNullable(groupCatalog)
    }

    override fun loadFromFile() {
        val container = Files.newInputStream(plugin.defaultConfig).use { DataFormats.NBT.readFrom(it) }

        // Selected Titles
        val selectedTitlesView = container.getView(DataQuery.of(selectedTitleLabel)).get()
        for (key in selectedTitlesView.getKeys(false)) {
            val id = UUID.fromString(key.toString())
            val title = selectedTitlesView.getSerializable(key, TitleCatalog::class.java).orElse(plugin.defaultTitleCatalog)
            _selectedTitles[id] = title
        }

        // Personal Titles
        val personalTitlesView = container.getView(DataQuery.of(personalTitlesLabel)).get()
        for (key in personalTitlesView.getKeys(false)) {
            val id = UUID.fromString(key.toString())
            val titles = selectedTitlesView.getSerializableList(key, TitleCatalog::class.java).orElse(Collections.emptyList())
            _personalTitles[id] = HashSet<TitleCatalog>(titles)
        }

        // Groups
        val groupsView = container.getView(DataQuery.of(groupsLabel)).get()
        for (key in groupsView.getKeys(false)) {
            val id = key.toString()
            val group = groupsView.getSerializable(key, GroupCatalog::class.java).get()
            _groups[id] = group
        }
    }

    override fun saveToFile() {
        val container = DataContainer.createNew()

        // Selected Titles
        val selectedTitlesView = container.createView(DataQuery.of(selectedTitleLabel))
        for ((id, title) in _selectedTitles) {
            selectedTitlesView[DataQuery.of(id.toString())] = title
        }

        // Personal Titles
        val personalTitlesView = container.createView(DataQuery.of(personalTitlesLabel))
        for ((id, title) in _personalTitles) {
            personalTitlesView[DataQuery.of(id.toString())] = title
        }

        // Groups
        val groupsView = container.createView(DataQuery.of(groupsLabel))
        for ((id, group) in _groups) {
            groupsView[DataQuery.of(id)] = group
        }

        Files.newOutputStream(plugin.defaultConfig).use { DataFormats.NBT.writeTo(it, container) }
    }

    private fun registerTitle(title: TitleCatalog, groupId: Optional<String>) {
        if (groupId.isPresent) {
            val group = _groups[groupId.get()]
            group?.addTitle(title)
            plugin.groupCatalogModule.updateCatalog(group) {
                it.addTitle(title)
                it
            }
        }
        plugin.titleCatalogModule.registerAdditionalCatalog(title)
        saveToFile()
    }

    private fun unregisterTitle(title: TitleCatalog, groupId: Optional<String>) {
        if (groupId.isPresent) {
            val group = _groups[groupId.get()]!!
            group.removeTitle(title)
        }
        plugin.titleCatalogModule.unregisterCatalog(title.id)
        saveToFile()
    }

    private fun registerGroup(group: GroupCatalog) {
        _groups[group.id] = group
        plugin.groupCatalogModule.registerAdditionalCatalog(group)
        saveToFile()
    }

    private fun unregisterGroup(groupId: String) {
        _groups.remove(groupId)
        plugin.groupCatalogModule.removeCatalog(groupId)
        saveToFile()
    }
}