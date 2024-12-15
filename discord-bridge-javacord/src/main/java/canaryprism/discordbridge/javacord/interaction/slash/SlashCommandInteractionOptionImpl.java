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

package canaryprism.discordbridge.javacord.interaction.slash;

import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandInteractionOption;
import canaryprism.discordbridge.javacord.channel.ChannelImpl;
import canaryprism.discordbridge.javacord.entities.user.UserImpl;
import canaryprism.discordbridge.javacord.message.AttachmentImpl;
import canaryprism.discordbridge.javacord.server.permission.RoleImpl;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record SlashCommandInteractionOptionImpl(DiscordBridge bridge, org.javacord.api.interaction.SlashCommandInteractionOption option) implements SlashCommandInteractionOption {
    
    @Override
    public @NotNull Optional<Boolean> isAutocompleteTarget() {
        return option.isFocused();
    }
    
    @SuppressWarnings("unchecked")
    public static Optional<?> getAnyValue(DiscordBridge bridge, org.javacord.api.interaction.SlashCommandInteractionOption option) {
        return ((Optional<Object>) (Optional<?>) option.getStringValue())
                .or(() -> option.getAttachmentValue().map((e) -> new AttachmentImpl(bridge, e)))
                .or(option::getBooleanValue)
                .or(() -> option.getChannelValue().map((e) -> new ChannelImpl(bridge, e)))
                .or(option::getDecimalValue)
                .or(option::getLongValue)
                .or(() -> option.getMentionableValue().map((e) ->
                        (e instanceof User user) ?
                                new UserImpl(bridge, user)
                                :
                                (e instanceof Role role) ?
                                        new RoleImpl(bridge, role)
                                        :
                                        null))
                .or(() -> option.getRoleValue().map((e) -> new RoleImpl(bridge, e)))
                .or(() -> option.getUserValue().map((e) -> new UserImpl(bridge, e)));
    }
    
    @Override
    public @NotNull Optional<?> getValue() {
        return getAnyValue(bridge, option);
    }
    
    @Override
    public @NotNull String getName() {
        return option.getName();
    }
    
    @Override
    public @NotNull List<? extends @NotNull SlashCommandInteractionOption> getOptions() {
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
