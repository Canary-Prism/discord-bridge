/*
 *    Copyright 2025 Canary Prism <canaryprsn@gmail.com>
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

import canaryprism.discordbridge.api.DiscordBridgeApi;
import canaryprism.discordbridge.api.channel.MessageChannel;
import canaryprism.discordbridge.api.entity.user.User;
import canaryprism.discordbridge.api.interaction.response.FollowupResponder;
import canaryprism.discordbridge.api.interaction.response.ImmediateResponder;
import canaryprism.discordbridge.api.interaction.response.ResponseUpdater;
import canaryprism.discordbridge.api.server.Server;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/// The base Interaction interface
///
/// Represents an instance of any Interaction
public interface Interaction extends DiscordBridgeApi {
    
    /// Gets the ID of the application (bot) this command belongs to
    ///
    /// @return the ID of the application this command belongs to
    long getApplicationId();
    
    /// Gets the ID of this application command
    ///
    /// @return the ID of the command
    long getCommandId();
    
    /// Gets the name of this application command
    ///
    /// @return the name of the command
    @NotNull String getCommandName();
    
    /// Gets the ID of the server this command was registered on,
    /// if this command is a server command
    ///
    /// @return the ID of the server this command was registered on
    @NotNull Optional<Long> getServerCommandServerId();
    
    
    /// Gets the user that caused this interaction
    ///
    /// @return the user
    @NotNull User getUser();
    
    /// Gets the server this interaction originated from
    ///
    /// @return the server
    @NotNull Optional<? extends Server> getServer();
    
    /// Gets the channel this interaction originated from
    ///
    /// @return the channel
    @NotNull Optional<? extends MessageChannel> getChannel();
}
