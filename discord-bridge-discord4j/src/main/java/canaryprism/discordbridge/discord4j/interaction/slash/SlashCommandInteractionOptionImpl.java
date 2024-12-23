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

package canaryprism.discordbridge.discord4j.interaction.slash;

import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandInteractionOption;
import canaryprism.discordbridge.discord4j.DiscordBridgeDiscord4J;
import canaryprism.discordbridge.discord4j.channel.ChannelDirector;
import canaryprism.discordbridge.discord4j.entities.user.UserImpl;
import canaryprism.discordbridge.discord4j.message.AttachmentImpl;
import canaryprism.discordbridge.discord4j.server.permission.RoleImpl;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public record SlashCommandInteractionOptionImpl(DiscordBridgeDiscord4J bridge, ApplicationCommandInteractionOption option) implements SlashCommandInteractionOption {
    
    @Override
    public @NotNull String getName() {
        return option.getName();
    }
    
    @Override
    public @NotNull Optional<Boolean> isAutocompleteTarget() {
        return Optional.of(option.isFocused());
    }
    
    @Override
    public @NotNull Optional<?> getValue() {
        return option.getValue()
                .map(this::getAnyValue);
    }
    
    private @Nullable Object getAnyValue(ApplicationCommandInteractionOptionValue value) {
        class Holder {
            static final Field type_field;
            
            static {
                try {
                    type_field = ApplicationCommandInteractionOptionValue.class.getDeclaredField("type");
                    type_field.trySetAccessible();
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        
        try {
            var type = ApplicationCommandOption.Type.of(((int) Holder.type_field.get(value)));
            
            return switch (type) {
                case UNKNOWN -> value.getRaw();
                case SUB_COMMAND, SUB_COMMAND_GROUP -> null;
                case STRING -> value.asString();
                case INTEGER -> value.asLong();
                case BOOLEAN -> value.asBoolean();
                case USER -> new UserImpl(bridge, value.asUser().block());
                case CHANNEL -> ChannelDirector.wrapChannel(bridge, value.asChannel().block());
                case ROLE -> new RoleImpl(bridge, value.asRole().block());
                case MENTIONABLE -> {
                    try {
                        yield new UserImpl(bridge, value.asUser().block());
                    } catch (RuntimeException e) {
                        yield new RoleImpl(bridge, value.asRole().block());
                    }
                }
                case NUMBER -> value.asDouble();
                case ATTACHMENT -> new AttachmentImpl(bridge, value.asAttachment());
            };
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public @NotNull @Unmodifiable List<? extends @NotNull SlashCommandInteractionOption> getOptions() {
        return option.getOptions()
                .stream()
                .map((e) -> new SlashCommandInteractionOptionImpl(bridge, e))
                .toList();
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return option;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
}
