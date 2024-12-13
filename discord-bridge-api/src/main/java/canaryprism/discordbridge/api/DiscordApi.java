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

import canaryprism.discordbridge.api.interaction.Command;
import canaryprism.discordbridge.api.interaction.slash.SlashCommand;
import canaryprism.discordbridge.api.server.Server;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/// Represents a Discord API entity for performing global actions
public interface DiscordApi extends DiscordBridgeApi {
    
    /// Requests an unmodifiable set of registered global slash commands for this bot
    ///
    /// @return all registered global slash commands
    @NotNull CompletableFuture<? extends Set<? extends SlashCommand>> getGlobalSlashCommands();
    
    /// Bulk updates the global commands for this bot
    ///
    /// This overwrites the command list for this bot
    /// and all commands you didn't put in the passed set will be gone
    ///
    /// @param commands the set of global commands to register
    /// @return a future for the update request
    @NotNull CompletableFuture<? extends Set<? extends SlashCommand>> bulkUpdateGlobalCommands(Set<? extends Command> commands);
    
    /// Gets all the servers the bot is in and has cached
    ///
    /// @return unmodifiable set of servers the bot is in
    @NotNull Set<? extends Server> getServers();
    
    /// Gets a server by its id
    ///
    /// @param id the ID of the server
    /// @return the server with the ID, or [#empty()] if there isn't a match
    default @NotNull Optional<? extends Server> getServerById(long id) {
        return this.getServers()
                .stream()
                .filter((e) -> e.getId() == id)
                .findAny();
    }
}
