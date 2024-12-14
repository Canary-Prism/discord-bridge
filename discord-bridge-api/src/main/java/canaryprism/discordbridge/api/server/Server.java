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

package canaryprism.discordbridge.api.server;

import canaryprism.discordbridge.api.entities.DiscordEntity;
import canaryprism.discordbridge.api.interaction.Command;
import canaryprism.discordbridge.api.interaction.slash.SlashCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

/// Represents a Discord Server (formerly Guild, not to be confused with the current Guilds)
public interface Server extends DiscordEntity {
    
    /// Requests an unmodifiable set of registered slash commands for this server
    ///
    /// @return all registered server slash commands
    @NotNull CompletableFuture<? extends Set<? extends SlashCommand>> getServerSlashCommands();
    
    /// Bulk updates the commands for this server
    ///
    /// This overwrites the command list for this bot
    /// and all commands you didn't put in the passed set will be gone
    ///
    /// @param commands the set of commands to register
    /// @return a future for the update request
    @NotNull CompletableFuture<? extends Set<? extends Command>> bulkUpdateServerCommands(Set<? extends Command> commands);
}
