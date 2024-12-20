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
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionChoice;
import canaryprism.discordbridge.api.misc.DiscordLocale;
import canaryprism.discordbridge.javacord.DiscordBridgeJavacord;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public record SlashCommandOptionChoiceImpl(DiscordBridge bridge, org.javacord.api.interaction.SlashCommandOptionChoice choice) implements SlashCommandOptionChoice {
    
    @Override
    public @NotNull Optional<?> getValue() {
        return Optional.empty()
                .or(choice::getStringValue)
                .or(choice::getLongValue);
    }
    
    @Override
    public @NotNull Map<DiscordLocale, @NotNull String> getNameLocalizations() {
        return choice.getNameLocalizations()
                .entrySet()
                .stream()
                .map((e) -> Map.entry(
                        DiscordBridgeJavacord.convertLocale(e.getKey()),
                        e.getValue()
                ))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    @Override
    public @NotNull String getName() {
        return choice.getName();
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return choice;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
}
