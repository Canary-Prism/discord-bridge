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

package canaryprism.discordbridge.api.interaction.response;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/// A responder to send **Followup Messages** after the original response
///
/// Note that followup messages can only be sent up to 15 minutes after the original response
public interface FollowupResponder extends ResponseBuilder<FollowupResponder> {
    
    /// Sends the response with the current state of the FollowupResponder
    ///
    /// This doesn't reset the state of this FollowupResponder
    ///
    /// @return a future for the send request
    @NotNull CompletableFuture<Void> send();
    
    /// Updates the given message with the current state of the FollowupResponder
    ///
    /// This doesn't reset the state of this FollowupResponder
    ///
    /// @param message_id the message ID of the followup message to update. the message MUST have originated from this respoonder
    /// @return a future for the update request
    @NotNull CompletableFuture<Void> update(long message_id);
}
