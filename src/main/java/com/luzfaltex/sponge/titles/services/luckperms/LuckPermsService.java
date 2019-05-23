package com.luzfaltex.sponge.titles.services.luckperms;

import com.luzfaltex.sponge.titles.TitleEntry;
import com.luzfaltex.sponge.titles.Titles;
import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.Node;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.ProviderRegistration;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LuckPermsService implements ILuckPermsService {

    private final LuckPermsApi api;
    private final Titles _plugin;
    private static final String SelectedTitle = "selectedtitle";

    public LuckPermsService(Titles plugin) {
        Optional<ProviderRegistration<LuckPermsApi>> provider = Sponge.getServiceManager().getRegistration(LuckPermsApi.class);
        if (provider.isPresent()) {
            api = provider.get().getProvider();
        } else api = null;

        _plugin = plugin;
    }


    @Override
    public CompletableFuture<String> getUserPrefix(UUID uuid) {
        if (getLuckPermsApi().isPresent()) {
            return api.getUserManager().loadUser(uuid)
                    .thenApplyAsync(user -> user.getOwnNodes().stream()
                        .filter(Node::isPrefix)
                        .collect(Collectors.toList())
                        .get(0))
                    .thenApplyAsync(Node::getPrefix)
                    .thenApplyAsync(Map.Entry::getValue);
        } else return CompletableFuture.completedFuture("");

    }

    @Override
    public CompletableFuture<List<Map.Entry<Integer, String>>> getUserEvaluatedPrefixes(UUID uuid, Contexts contexts) {
        if (getLuckPermsApi().isPresent()) {
            return api.getUserManager().loadUser(uuid)
                    .thenApplyAsync(user -> user.getAllNodes(contexts).stream()
                        .filter(Node::isPrefix)
                        .map(Node::getPrefix)
                        .collect(Collectors.toList()));
        } else return CompletableFuture.completedFuture(Collections.EMPTY_LIST);
    }

    @Override
    public CompletableFuture<String> getUserSuffix(UUID uuid) {
        if (getLuckPermsApi().isPresent()) {
            return api.getUserManager().loadUser(uuid)
                    .thenApplyAsync(user -> user.getOwnNodes().stream()
                            .filter(Node::isSuffix)
                            .collect(Collectors.toList())
                            .get(0))
                    .thenApplyAsync(Node::getPrefix)
                    .thenApplyAsync(Map.Entry::getValue);
        } else return CompletableFuture.completedFuture("");
    }

    @Override
    public CompletableFuture<List<Map.Entry<Integer, String>>> getUserEvaluatedSuffix(UUID uuid, Contexts contexts) {
        if (getLuckPermsApi().isPresent()) {
            return api.getUserManager().loadUser(uuid)
                    .thenApplyAsync(user -> user.getAllNodes(contexts).stream()
                            .filter(Node::isSuffix)
                            .map(Node::getSuffix)
                            .collect(Collectors.toList()));
        } else return CompletableFuture.completedFuture(Collections.EMPTY_LIST);
    }

    @Override
    public CompletableFuture<String> getUserMeta(UUID uuid, String key) {
        if (getLuckPermsApi().isPresent()) {
            return api.getUserManager().loadUser(uuid)
                    .thenApplyAsync(user -> user.getOwnNodes().stream()
                            .filter(Node::isMeta)
                            .filter(n -> n.getMeta().getKey().equals(key))
                            .collect(Collectors.toList())
                            .get(0))
                    .thenApplyAsync(Node::getMeta)
                    .thenApplyAsync(Map.Entry::getValue);
        } else return CompletableFuture.completedFuture("");
    }

    @Override
    public CompletableFuture<List<String>> getUserEvaluatedMeta(UUID uuid, String key, Contexts contexts) {
        if (getLuckPermsApi().isPresent()) {
            return api.getUserManager().loadUser(uuid)
                    .thenApplyAsync(user -> user.getAllNodes(contexts).stream()
                        .filter(Node::isMeta)
                        .filter(n -> n.getMeta().getKey().equals(key))
                        .map(node -> node.getMeta().getValue())
                        .collect(Collectors.toList()));
        } else return CompletableFuture.completedFuture(Collections.EMPTY_LIST);
    }

    @Override
    public CompletableFuture<?> setUserMeta(UUID uuid, String key, String value) {
        if (getLuckPermsApi().isPresent()) {
            Node metaNode = api.getNodeFactory().makeMetaNode(key, value).build();
            return api.getUserManager().loadUser(uuid)
                    .thenApplyAsync(user -> user.setPermission(metaNode));
        } else return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<String> getGroupPrefix(String groupName) {
        if (getLuckPermsApi().isPresent()) {
            return api.getGroupManager().loadGroup(groupName)
                    .thenApplyAsync(group -> {
                        if (group.isPresent()) {
                            Group g = group.get();
                            return g.getOwnNodes().stream()
                                    .filter(Node::isPrefix)
                                    .collect(Collectors.toList())
                                    .get(0);
                        } else {
                            return api.getNodeFactory().makePrefixNode(0, "").build();
                        }
                    })
                    .thenApplyAsync(Node::getPrefix)
                    .thenApplyAsync(Map.Entry::getValue);
        } else return CompletableFuture.completedFuture("");
    }

    @Override
    public CompletableFuture<String> getGroupSuffix(String groupName) {
        if (getLuckPermsApi().isPresent()) {
            return api.getGroupManager().loadGroup(groupName)
                    .thenApplyAsync(group -> {
                        if (group.isPresent()) {
                            Group g = group.get();
                            return g.getOwnNodes().stream()
                                    .filter(Node::isSuffix)
                                    .collect(Collectors.toList())
                                    .get(0);
                        } else {
                            return api.getNodeFactory().makeSuffixNode(0, "").build();
                        }
                    })
                    .thenApplyAsync(Node::getPrefix)
                    .thenApplyAsync(Map.Entry::getValue);
        } else return CompletableFuture.completedFuture("");
    }

    @Override
    public CompletableFuture<String> getGroupMeta(String groupName, String key) {
        if (getLuckPermsApi().isPresent()) {
            return api.getGroupManager().loadGroup(groupName)
                    .thenApplyAsync(group -> {
                        if (group.isPresent()) {
                            Group g = group.get();
                            return g.getOwnNodes().stream()
                                    .filter(Node::isMeta)
                                    .filter(node -> node.getMeta().getKey().equals(key))
                                    .collect(Collectors.toList())
                                    .get(0);
                        } else {
                            return api.getNodeFactory().makeMetaNode(key, "").build();
                        }
                    })
                    .thenApplyAsync(Node::getMeta)
                    .thenApplyAsync(Map.Entry::getValue);
        } else return CompletableFuture.completedFuture("");
    }

    @Override
    public Optional<LuckPermsApi> getLuckPermsApi() {
        return Optional.of(api);
    }

    @Override
    public Optional<Group> getGroup(String name) {
        if(getLuckPermsApi().isPresent()) {
            return api.getGroupManager().getGroupOpt(name);
        }
        else return Optional.empty();
    }

    /**
     * Gets the Id of the currently selected title
     * @param uuid The id of the user
     * @return The Id of the currently selected title; otherwise, 0 if none selected or -1 on error
     */
    @Override
    public CompletableFuture<Integer> getSelectedTitleId(UUID uuid) {
        return getUserMeta(uuid, SelectedTitle)
                .thenApplyAsync(title -> {
                    if (StringUtils.isEmpty(title))
                        return 0;
                    try {
                        return Integer.parseInt(title);
                    } catch (NumberFormatException e) {
                        return -1;
                    }
                });
    }

    @Override
    public void setSelectedTitle(UUID uuid, TitleEntry title) {
        setUserMeta(uuid, SelectedTitle, String.valueOf(title.Id));
    }
}
