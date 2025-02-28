/*
 *    Copyright 2024 Canary Prism <canaryprsn@gmail.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package canaryprism.discordbridge.api;

import canaryprism.discordbridge.api.data.interaction.CommandData;
import canaryprism.discordbridge.api.interaction.slash.SlashCommand;
import canaryprism.discordbridge.api.listener.ApiAttachableListener;
import canaryprism.discordbridge.api.listener.interaction.SlashCommandAutocompleteListener;
import canaryprism.discordbridge.api.listener.interaction.SlashCommandInvokeListener;
import canaryprism.discordbridge.api.server.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/// Represents a Discord API entity for performing global actions
public interface DiscordApi extends DiscordBridgeApi {
    
    /// Requests an unmodifiable set of registered global slash commands for this bot
    ///
    /// @return all registered global slash commands
    @NotNull CompletableFuture<? extends @NotNull @Unmodifiable Set<? extends @NotNull SlashCommand>> getGlobalSlashCommands();
    
    /// Bulk updates the global commands for this bot
    ///
    /// This overwrites the command list for this bot
    /// and all commands you didn't put in the passed set will be gone
    ///
    /// @param commands the set of global commands to register
    /// @return a future for the update request
    @NotNull CompletableFuture<? extends @NotNull @Unmodifiable Set<? extends @NotNull SlashCommand>> bulkUpdateGlobalCommands(@NotNull Set<? extends @NotNull CommandData> commands);
    
    /// Gets all the servers the bot is in and has cached
    ///
    /// @return unmodifiable set of servers the bot is in
    @NotNull @Unmodifiable Set<? extends Server> getServers();
    
    /// Gets a server by its ID
    ///
    /// @param id the ID of the server
    /// @return the server with the ID, or [Optional#empty()] if there isn't a match
    default @NotNull Optional<? extends Server> getServerById(long id) {
        return this.getServers()
                .stream()
                .filter((e) -> e.getId() == id)
                .findAny();
    }
    
    /// Adds a listener to this DiscordApi
    ///
    /// @param type the runtime class of the listener
    /// @param listener the listener to add
    /// @param <T> the type of the listener
    <T extends ApiAttachableListener> void addListener(@NotNull Class<T> type, @NotNull T listener);
    
    /// Removes a listener from this DiscordApi
    ///
    /// @param type the runtime class of the listener
    /// @param listener the listener to remove
    /// @param <T> the type of the listener
    <T extends ApiAttachableListener> void removeListener(@NotNull Class<T> type, @NotNull T listener);
    
    /// Adds a [SlashCommandInvokeListener] to this DiscordApi
    ///
    /// @param listener the listener to add
    default void addSlashCommandInvokeListener(@NotNull SlashCommandInvokeListener listener) {
        addListener(SlashCommandInvokeListener.class, listener);
    }
    
    /// Removes a [SlashCommandInvokeListener] from this DiscordApi
    ///
    /// @param listener the listener to remove
    default void removeSlashCommandInvokeListener(@NotNull SlashCommandInvokeListener listener) {
        removeListener(SlashCommandInvokeListener.class, listener);
    }
    
    /// Adds a [SlashCommandAutocompleteListener] to this DiscordApi
    ///
    /// @param listener the listener to add
    default void addSlashCommandAutocompleteListener(@NotNull SlashCommandAutocompleteListener listener) {
        addListener(SlashCommandAutocompleteListener.class, listener);
    }
    
    /// Removes a [SlashCommandAutocompleteListener] from this DiscordApi
    ///
    /// @param listener the listener to remove
    default void removeSlashCommandAutocompleteListener(@NotNull SlashCommandAutocompleteListener listener) {
        removeListener(SlashCommandAutocompleteListener.class, listener);
    }
}
