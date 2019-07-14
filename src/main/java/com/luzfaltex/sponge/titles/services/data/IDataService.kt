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

import com.luzfaltex.sponge.titles.catalogs.GroupCatalog
import com.luzfaltex.sponge.titles.catalogs.TitleCatalog
import com.luzfaltex.sponge.titles.results.Result
import java.util.*

interface IDataService {
    fun getSelectedTitle(uuid:UUID) : TitleCatalog
    fun setSelectedTitle(playerId: UUID, title: TitleCatalog) : Result

    fun getPersonalTitleIds(uuid: UUID) : HashSet<TitleCatalog>
    fun addPersonalTitle(playerId: UUID, title: TitleCatalog) : Result
    fun removePersonalTitle(playerId: UUID, title: TitleCatalog) : Result

    fun addTitle(title: TitleCatalog, groupId: String?) : Result
    fun removeTitle(title: TitleCatalog, groupId: String?) : Result
    fun updatePersonalTitle(title: TitleCatalog, playerId: UUID) : Result
    fun updateTitle(title: TitleCatalog, groupId: String?) : Result

    fun addGroup(group: GroupCatalog) : Result
    fun removeGroup(groupId: String) : Result
    fun updateGroup(group: GroupCatalog) : Result

    fun getAvailableTitles(uuid: UUID) : HashMap<String, TitleCatalog>

    fun getAllTitles() : HashSet<TitleCatalog>
    fun getAllGroups() : HashSet<GroupCatalog>
    fun getAllPersonalTitles(playerId: UUID) : HashSet<TitleCatalog>

    fun findTitle(titleId: String) : Optional<TitleCatalog>
    fun findGroup(groupId: String): Optional<GroupCatalog>
    fun findTitleGroup(title: TitleCatalog) : Optional<GroupCatalog>

    fun loadFromFile()
    fun saveToFile()
}
