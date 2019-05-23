package com.luzfaltex.sponge.titles.services.luckperms;

import com.luzfaltex.sponge.titles.TitleEntry;
import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.LuckPermsApi;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface ILuckPermsService {

    /**
     * Gets the prefix assigned to the specified user object
     * @param uuid The UUID of the player
     * @return A CompletableFuture&lt;String&gt; containing the user's prefix
     */
    CompletableFuture<String> getUserPrefix(UUID uuid);
    /**
     * Gets a collection of prefixes assigned to the specified user object, checking inheritance with the provided contexts
     * @param uuid The UUID of the player
     * @param contexts Any contexts to filter under
     * @return A CompletableFuture&lt;List&lt;String&gt;&gt; containing the user's prefixes
     */
    CompletableFuture<List<Map.Entry<Integer, String>>> getUserEvaluatedPrefixes(UUID uuid, Contexts contexts);
    /**
     * Gets the suffix assigned to the specified user object
     * @param uuid The UUID of the player
     * @return A CompletableFuture&lt;String&gt; containing the user's suffix
     */
    CompletableFuture<String> getUserSuffix(UUID uuid);
    /**
     * Gets a collection of suffixes assigned to the specified user object, checking inheritance with the provided contexts
     * @param uuid The UUID of the player
     * @param contexts Any contexts to filter under
     * @return A CompletableFuture&lt;List&lt;String&gt;&gt; containing the user's suffixes
     */
    CompletableFuture<List<Map.Entry<Integer, String>>> getUserEvaluatedSuffix(UUID uuid, Contexts contexts);

    /**
     * Gets the value of the specified user meta item assigned to the user.
     * @param uuid The UUID of the user to check
     * @param key The Key of the Entry for the user's meta
     * @return
     */
    CompletableFuture<String> getUserMeta(UUID uuid, String key);

    /**
     * Gets the value of the specified user meta item assigned to the user.
     * @param uuid The UUID of the user to check
     * @param key The Key of the Entry for the user's meta
     * @return
     */
    CompletableFuture<List<String>> getUserEvaluatedMeta(UUID uuid, String key, Contexts contexts);

    CompletableFuture<?> setUserMeta(UUID uuid, String key, String value);

    /**
     * Gets the group's assigned prefix
     * @param groupName The name of the group to look up
     * @return A CompletableFuture&lt;String&gt; containing the group's prefix
     */
    CompletableFuture<String> getGroupPrefix(String groupName);
    /**
     * Gets the group's assigned suffix
     * @param groupName The name of the group to look up
     * @return A CompletableFuture&lt;String&gt; containing the group's suffix
     */
    CompletableFuture<String> getGroupSuffix(String groupName);
    /**
     * Gets the value of the specified group meta item assigned to the group.
     * @param groupName The name of the group to check
     * @param key The Key of the Entry for the group's meta
     * @return
     */
    CompletableFuture<String> getGroupMeta(String groupName, String key);

    Optional<LuckPermsApi> getLuckPermsApi();

    Optional<Group> getGroup(String name);

    CompletableFuture<Integer> getSelectedTitleId(UUID uuid);
    void setSelectedTitle(UUID uuid, TitleEntry title);
}
