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

import canaryprism.discordbridge.api.PartialSupport;
import canaryprism.discordbridge.api.TypeValue;
import canaryprism.discordbridge.api.channel.Channel;
import canaryprism.discordbridge.api.entities.Mentionable;
import canaryprism.discordbridge.api.entities.user.User;
import canaryprism.discordbridge.api.message.Attachment;
import canaryprism.discordbridge.api.server.permission.Role;
import org.jetbrains.annotations.NotNull;

/// Represents a type for [SlashCommandOption]
public enum SlashCommandOptionType implements PartialSupport, TypeValue<Class<?>> {
    SUBCOMMAND(Void.class),
    SUBCOMMAND_GROUP(Void.class),
    STRING(String.class),
    INTEGER(Long.class),
    NUMBER(Double.class),
    BOOLEAN(Boolean.class),
    USER(User.class),
    CHANNEL(Channel.class),
    ROLE(Role.class),
    MENTIONABLE(Mentionable.class),
    ATTACHMENT(Attachment.class),
    UNKNOWN(Object.class),
    ;
    
    public final Class<?> type;
    
    SlashCommandOptionType(Class<?> type) {
        this.type = type;
    }
    
    @Override
    public @NotNull Class<?> getTypeRepresentation() {
        return type;
    }
}
