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

package canaryprism.discordbridge.api.enums;

import canaryprism.discordbridge.api.channel.ChannelType;
import canaryprism.discordbridge.api.interaction.ContextType;
import canaryprism.discordbridge.api.interaction.InstallationType;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType;
import canaryprism.discordbridge.api.message.MessageFlag;
import canaryprism.discordbridge.api.misc.DiscordLocale;
import canaryprism.discordbridge.api.server.permission.PermissionType;

/// Marker interface for enums belonging to the discord-bridge api
public sealed interface DiscordBridgeEnum permits ChannelType, PartialSupport, TypeValue, ContextType, InstallationType, SlashCommandOptionType, MessageFlag, DiscordLocale, PermissionType {
}
