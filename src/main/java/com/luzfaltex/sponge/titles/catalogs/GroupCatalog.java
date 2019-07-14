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

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.Queries;

import java.util.*;

public class GroupCatalog implements CatalogType, DataSerializable {

    private final String id;
    private final String name;
    private HashSet<TitleCatalog> titles;
    private HashSet<UUID> users;

    public GroupCatalog(String id, String name) {
        this(id, name, null, null);
    }

    public GroupCatalog(String id, String name, TitleCatalog... titles) {
        this(id, name, getHash(titles), null);
    }

    public GroupCatalog(String id, String name, UUID... users) {
        this(id, name, null, getHash(users));
    }

    public GroupCatalog(String id, String name, HashSet<TitleCatalog> titles, HashSet<UUID> users) {
        this.id = id;
        this.name = name;
        this.titles = titles;
        this.users = users;
    }

    private static <T> HashSet<T> getHash(T... objects) {
        return new HashSet<>(Arrays.asList(objects));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public Boolean hasTitle(TitleCatalog title) {
        return titles.contains(title);
    }

    public Boolean hasMember(UUID uuid) {
        return users.contains(uuid);
    }

    public HashSet<TitleCatalog> getTitles() {
        return titles;
    }

    public HashSet<UUID> getUsers() {
        return users;
    }

    public Boolean addTitle(TitleCatalog title) {
        return titles.add(title);
    }

    public Boolean removeTitle(TitleCatalog title) {
        return titles.remove(title);
    }

    public Boolean addUser(UUID uuid) {
        return users.add(uuid);
    }

    public Boolean removeUser(UUID uuid) {
        return users.remove(uuid);
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return DataContainer.createNew()
                .set(DataQuery.of("Id"), id)
                .set(DataQuery.of("Name"), name)
                .set(DataQuery.of("Titles"), titles)
                .set(DataQuery.of("Users"), titles)
                .set(Queries.CONTENT_VERSION, getContentVersion());
    }
}
