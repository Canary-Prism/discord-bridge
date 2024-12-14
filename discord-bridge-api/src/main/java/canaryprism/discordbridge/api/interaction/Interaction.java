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

import canaryprism.discordbridge.api.channel.TextChannel;
import canaryprism.discordbridge.api.entities.user.User;
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
public interface Interaction {
    
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
    
    /// Creates an [ImmediateResponder] to respond immediately to this interaction
    ///
    /// Note that Discord requires bots send any form of response to an interaction within 3 seconds,
    /// if longer processing time is needed [#respondLater()] should be used
    ///
    /// @return a new ImmediateResponder
    @NotNull ImmediateResponder createImmediateResponder();
    
    /// Defers a response and obtains a [ResponseUpdater] to let you actually send a response
    ///
    /// This makes the bot respond with the "`<BotName>` is thinking..." text
    /// until you update your response with the returned Response Updater
    ///
    /// You can only update your response up to 15 minutes after this call
    ///
    /// @param ephemeral whether the thinking response should be sent with the `MessageFlag.EPHEMERAL` flag
    /// @return a future for the request and ResponseUpdater
    @NotNull CompletableFuture<@NotNull ResponseUpdater> respondLater(boolean ephemeral);
    
    /// Defers a response and obtains a [ResponseUpdater] to let you actually send a response
    ///
    /// This makes the bot respond with the "`<BotName>` is thinking..." text
    /// until you update your response with the returned Response Updater
    ///
    /// You can only update your response up to 15 minutes after this call
    ///
    /// @return a future for the request and ResponseUpdater
    /// @implNote by default this method just delegates to [#respondLater(boolean)] with `false` passed
    default @NotNull CompletableFuture<@NotNull ResponseUpdater> respondLater() {
        return respondLater(false);
    }
    
    /// Creates a [FollowupResponder] to send followup messages with
    ///
    /// Another, original response must have been sent already for this to work
    ///
    /// You may continue to send followup responses up to 15 minutes after the original response
    ///
    /// @return a new FollowupResponder
    @NotNull FollowupResponder createFollowupResponder();
    
    /// Gets the user that caused this interaction
    ///
    /// @return the user
    @NotNull User getUser();
    
    /// Gets the server this interaction originated from
    ///
    /// @return the server
    @NotNull Optional<Server> getServer();
    
    /// Gets the channel this interaction originated from
    ///
    /// @return the channel
    @NotNull Optional<TextChannel> getChannel();
}
