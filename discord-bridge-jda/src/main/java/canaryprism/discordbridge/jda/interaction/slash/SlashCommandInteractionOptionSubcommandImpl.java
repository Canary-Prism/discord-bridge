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

package canaryprism.discordbridge.jda.interaction.slash;

import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandInteractionOption;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record SlashCommandInteractionOptionSubcommandImpl(DiscordBridge bridge, String name, List<SlashCommandInteractionOptionOptionMappingImpl> options) implements SlashCommandInteractionOption {
    
    @Override
    public @NotNull Optional<Boolean> isAutocompleteTarget() {
        return Optional.empty();
    }
    
    @Override
    public @NotNull Optional<?> getValue() {
        return Optional.empty();
    }
    
    @Override
    public @NotNull String getName() {
        return name;
    }
    
    @Override
    public @NotNull List<? extends @NotNull SlashCommandInteractionOption> getOptions() {
        return options;
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return this;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
}
