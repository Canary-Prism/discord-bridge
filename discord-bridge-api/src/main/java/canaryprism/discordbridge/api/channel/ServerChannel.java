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

package canaryprism.discordbridge.api.channel;

import canaryprism.discordbridge.api.server.Server;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/// Represents a [Channel] in a [Server]
///
/// Also includes server categories
public interface ServerChannel extends Channel {
    
    /// Gets the [Server] this channel belongs to
    ///
    /// @return the server
    @NotNull Server getServer();
    
    /// Deletes the channel
    ///
    /// The bot must have the necessary permissions to be able to delete the channel
    ///
    /// @return a future for the request
    @NotNull CompletableFuture<Void> delete();
}
