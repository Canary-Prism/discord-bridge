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

import canaryprism.discordbridge.api.channel.ChannelType;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

/// A TypeValue is an enum whose values correspond to types, like each value of [ChannelType] might correspond to
/// a specific type that is used to hold an instance of such a channel type
///
/// [#getInternalTypeRepresentation(canaryprism.discordbridge.api.DiscordBridge)]
///
/// [DiscordBridge#getInternalTypeRepresentation(canaryprism.discordbridge.api.TypeValue)] is used
/// to obtain the internal type used to represent a given instance of `TypeValue`
///
/// @param <T> the Type this TypeValue gives (not applicable for the internal type)
public sealed interface TypeValue<T extends Type> permits ChannelType, SlashCommandOptionType {
    
    /// Gets the type representation of this value
    ///
    /// @return the type that best represents this value
    @NotNull T getTypeRepresentation();
    
    /// Gets the internal type that can be used to best represent this enum value
    ///
    /// @param bridge the bridge to query from
    /// @return the internal type that best represents the given value
    /// @see DiscordBridge#getInternalTypeRepresentation(TypeValue)
    default @NotNull Type getInternalTypeRepresentation(DiscordBridge bridge) {
        return bridge.getInternalTypeRepresentation(this);
    }
}
