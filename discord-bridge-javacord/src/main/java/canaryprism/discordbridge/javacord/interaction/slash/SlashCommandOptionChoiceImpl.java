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
import canaryprism.discordbridge.javacord.DiscordBridgeJavacord;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public record SlashCommandOptionChoiceImpl(DiscordBridge bridge, org.javacord.api.interaction.SlashCommandOptionChoice choice) implements SlashCommandOptionChoice {
    
    @Override
    public @NotNull Optional<String> getStringValue() {
        return choice.getStringValue();
    }
    
    @Override
    public @NotNull Optional<Long> getIntegerValue() {
        return choice.getLongValue();
    }
    
    /// Number choices are not implemented in javacord
    @Override
    public @NotNull Optional<Double> getNumberValue() {
        return Optional.empty();
    }
    
    @Override
    public @NotNull Map<Locale, @NotNull String> getNameLocalizations() {
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
}
