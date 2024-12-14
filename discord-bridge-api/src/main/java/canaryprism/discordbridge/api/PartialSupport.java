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
import canaryprism.discordbridge.api.server.permission.PermissionType;

/// Marks an enum as allowing *Partial Support*
///
/// implementations of discord-bridge may not support all the values of this enum
///
/// as such you shouldn't assume all values of the given enum are allowed and instead should call
/// [DiscordBridge#getSupportedValues(java.lang.Class)] to obtain a set of supported values for the implementation
public sealed interface PartialSupport permits ChannelType, SlashCommandOptionType, PermissionType {
    /// Gets whether a specific value is supported by the provided [DiscordBridge] or not
    ///
    /// @param bridge the bridge to query from
    /// @return whether this value is supported
    @SuppressWarnings("SuspiciousMethodCalls")
    default boolean isSupported(DiscordBridge bridge) {
        return bridge.getSupportedValues(this.getClass())
                .contains(this);
    }
}
