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

package com.luzfaltex.sponge.titles.catalogs;

import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class GroupCatalogModule implements AdditionalCatalogRegistryModule<GroupCatalog> {

    private final Map<String, GroupCatalog> groupMap = new HashMap();

    public GroupCatalogModule() {
        // Defaults
    }

    @Override
    public Optional<GroupCatalog> getById(String id) {
        return Optional.ofNullable(groupMap.get(id));
    }

    @Override
    public Collection<GroupCatalog> getAll() {
        return groupMap.values();
    }

    @Override
    public void registerAdditionalCatalog(GroupCatalog extraCatalog) {
        groupMap.put(extraCatalog.getId(), extraCatalog);
    }

    public void removeCatalog(String groupId) {
        groupMap.remove(groupId);
    }

    public void updateCatalog(GroupCatalog group, Function<GroupCatalog, GroupCatalog> updateAction) {
        groupMap.remove(group.getId());
        GroupCatalog updatedCatalog = updateAction.apply(group);
        groupMap.put(updatedCatalog.getId(), updatedCatalog);
    }
}
