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

package canaryprism.discordbridge.api.interaction.slash;

import canaryprism.discordbridge.api.enums.DiscordBridgeEnum;
import canaryprism.discordbridge.api.enums.PartialSupport;
import canaryprism.discordbridge.api.enums.TypeValue;
import canaryprism.discordbridge.api.channel.Channel;
import canaryprism.discordbridge.api.entities.Mentionable;
import canaryprism.discordbridge.api.entities.user.User;
import canaryprism.discordbridge.api.message.Attachment;
import canaryprism.discordbridge.api.server.permission.Role;
import org.jetbrains.annotations.NotNull;

/// Represents a type for [SlashCommandOption]
public enum SlashCommandOptionType implements PartialSupport, TypeValue<Class<?>>, DiscordBridgeEnum {
    
    /// Unknown type which can be caused by various reasons including a new unsupported option type
    ///
    /// [Object] is used to hold this and implementations can either return an arbitrary [canaryprism.discordbridge.api.DiscordBridgeApi] holding the internal value or the internal implementation itself
    UNKNOWN(Object.class),
    
    /// Subcommands don't have a value but instead have their own [SlashCommandInteractionOption]s
    SUBCOMMAND(Void.class),
    
    /// Subcommand Groups don't have a value but instead have exactly one [SlashCommandInteractionOption]
    /// of the [#SUBCOMMAND] type
    SUBCOMMAND_GROUP(Void.class),
    
    /// String values may contain mention tags
    STRING(String.class),
    
    /// Discord's `INTEGER` is a signed integer between `-2^53` and `2^53`
    /// (aka as much as a float 64 is able to achieve with integer precision)
    ///
    /// so the value is represented as a [Long]
    INTEGER(Long.class),
    
    /// Discord's `NUMBER` means a double between `-2^53` and `2^53`
    /// (aka a double within integer precision)
    NUMBER(Double.class),
    
    /// Just a [Boolean] value
    BOOLEAN(Boolean.class),
    
    /// [User] type
    USER(User.class),
    
    /// [Channel] type
    CHANNEL(Channel.class),
    
    /// [Role] type
    ROLE(Role.class),
    
    /// Even though it's called `MENTIONABLE` it's actually just a union of [User] and [Role],
    /// so values will be of either of those types
    ///
    /// However, [Mentionable] is still the best type to hold both, so it is used here
    MENTIONABLE(Mentionable.class),
    
    /// [Attachment] type
    ATTACHMENT(Attachment.class),
    ;
    
    /// Type representation of this value
    public final Class<?> type;
    
    SlashCommandOptionType(Class<?> type) {
        this.type = type;
    }
    
    @Override
    public @NotNull Class<?> getTypeRepresentation() {
        return type;
    }
}
