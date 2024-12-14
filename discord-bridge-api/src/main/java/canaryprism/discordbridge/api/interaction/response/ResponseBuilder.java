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

import canaryprism.discordbridge.api.DiscordBridgeApi;
import canaryprism.discordbridge.api.message.MessageFlag;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;

/// Builder for a Response for an Interaction
///
/// @param <T> Self type
public interface ResponseBuilder<T extends ResponseBuilder<T>> extends DiscordBridgeApi {
    
    /// Set the String contents of this response builder
    ///
    /// @param text text to set to
    /// @return this
    @NotNull T setContent(@NotNull String text);
    
    /// Set the message flags of this response builder
    ///
    /// @param flags the flags to set
    /// @return this
    @NotNull T setFlags(EnumSet<MessageFlag> flags);
    
    /// Set the message flags of this response builder
    ///
    /// Being varargs this is probably much slower than [#setFlags(java.util.EnumSet)]
    ///
    /// @param flags the flags to set
    /// @return this
    default @NotNull T setFlags(@NotNull MessageFlag @NotNull... flags) {
        if (flags.length == 0)
            return setFlags(EnumSet.noneOf(MessageFlag.class));
        return setFlags(EnumSet.copyOf(Set.of(flags)));
    }
}
