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

package canaryprism.discordbridge.api.interaction;

import canaryprism.discordbridge.api.entities.DiscordEntity;
import canaryprism.discordbridge.api.misc.LocalizedDescribable;
import canaryprism.discordbridge.api.misc.LocalizedNameable;
import canaryprism.discordbridge.api.server.permission.PermissionType;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/// Represents a Discord Application Command
///
/// Application Commands can be Slash Commands or Context Menu Commands
public interface Command extends DiscordEntity, LocalizedNameable, LocalizedDescribable {
    
    /// Gets the ID of the application (bot) this command belongs to
    ///
    /// @return the ID of the application this command belongs to
    long getApplicationId();
    
    /// Whether the command is by default disabled to everyone but administrators
    ///
    /// this is a special flag unrelated to [#getDefaultRequiredPermissions()]
    ///
    /// @return whether the command is default disabled
    boolean isDefaultDisabled();
    
    /// Gets the set of [PermissionType]s required to be **allowed** for any user for them to be able to use this command
    ///
    /// this is only a default and moderators may change this in their server
    ///
    /// @return a set of required [PermissionType]s
    Optional<? extends Set<? extends PermissionType>> getDefaultRequiredPermissions();
    
    /// Gets whether this command can be used in DMs or not
    ///
    /// Server commands (commands registered to an individual server) will always return `false`
    ///
    /// @return whether this command can be used in DMs
    boolean isEnabledInDMs();
    
    /// Gets whether this command is a global command or not
    ///
    /// @return whether this command is a global command
    boolean isGlobalCommand();
    
    /// Gets whether this command is a server command or not
    ///
    /// A server command is registered to a specific server
    ///
    /// if `true` this means [#isEnabledInDMs()] must return `false`
    ///
    /// @return whether this command is a server command
    boolean isServerCommand();
    
    /// Gets whether this command is NSFW or not
    ///
    /// @return whether this command is NSFW
    boolean isNSFW();
    
    /// Deletes this application command
    ///
    /// @return a future for the delete request
    CompletableFuture<Void> delete();
}
