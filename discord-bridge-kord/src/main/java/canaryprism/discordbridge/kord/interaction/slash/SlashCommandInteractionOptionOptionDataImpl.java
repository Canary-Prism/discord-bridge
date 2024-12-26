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

package canaryprism.discordbridge.kord.interaction.slash;

import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandInteractionOption;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType;
import canaryprism.discordbridge.kord.DiscordBridgeKord;
import canaryprism.discordbridge.kord.channel.ChannelDirector;
import canaryprism.discordbridge.kord.entity.user.UserImpl;
import canaryprism.discordbridge.kord.message.AttachmentImpl;
import canaryprism.discordbridge.kord.server.permission.RoleImpl;
import dev.kord.core.cache.data.OptionData;
import dev.kord.core.entity.Role;
import dev.kord.core.entity.User;
import dev.kord.core.entity.interaction.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record SlashCommandInteractionOptionOptionDataImpl(DiscordBridgeKord bridge, OptionData option, Map<String, OptionValue<?>> option_map) implements SlashCommandInteractionOption {
    
    @Override
    public @NotNull Optional<Boolean> isAutocompleteTarget() {
        return Optional.ofNullable(option.getFocused().getAsNullable());
    }
    
    @Override
    public @NotNull Optional<?> getValue() {
        return Optional.ofNullable(option.getValue().getValue())
                .map((e) -> switch (bridge.convertInternalObject(SlashCommandOptionType.class, e.getType())) {
                    case UNKNOWN -> e.getValue();
                    case SUBCOMMAND -> null;
                    case SUBCOMMAND_GROUP -> null;
                    case STRING -> ((String) e.getValue());
                    case INTEGER -> ((Long) e.getValue());
                    case NUMBER -> ((Double) e.getValue());
                    case BOOLEAN -> ((Boolean) e.getValue());
                    case USER -> new UserImpl(bridge, ((UserOptionValue) option_map.get(option.getName())).getResolvedObject());
                    case CHANNEL -> ChannelDirector.wrapChannel(bridge, ((ChannelOptionValue) option_map.get(option.getName())).getResolvedObject());
                    case ROLE -> new RoleImpl(bridge, ((RoleOptionValue) option_map.get(option.getName())).getResolvedObject());
                    case MENTIONABLE -> {
                        var value = ((MentionableOptionValue) this.option_map.get(option.getName())).getResolvedObject();
                        if (value instanceof Role role)
                            yield new RoleImpl(bridge, role);
                        else if (value instanceof User user)
                            yield new UserImpl(bridge, user);
                        else
                            throw new UnsupportedOperationException("unrecognised mentionable type: not User or Role");
                    }
                    case ATTACHMENT -> new AttachmentImpl(bridge, ((AttachmentOptionValue) option_map.get(option.getName())).getResolvedObject());
                });
    }

    @Override
    public @NotNull @Unmodifiable List<? extends @NotNull SlashCommandInteractionOption> getOptions() {
        return Optional.ofNullable(option.getValues().getValue())
                .orElse(List.of())
                .stream()
                .map((e) -> new SlashCommandInteractionOptionCommandArgumentImpl(bridge, e, option_map))
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
    
    @Override
    public @NotNull String getName() {
        return option.getName();
    }
}
