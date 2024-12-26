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
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionChoice;
import canaryprism.discordbridge.api.misc.DiscordLocale;
import canaryprism.discordbridge.kord.DiscordBridgeKord;
import dev.kord.common.Locale;
import dev.kord.common.entity.Choice;
import dev.kord.common.entity.optional.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.stream.Collectors;

public record SlashCommandOptionChoiceImpl(DiscordBridgeKord bridge, Choice choice) implements SlashCommandOptionChoice {
    
    @Override
    public @NotNull Object getValue() {
        return choice.getValue();
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return choice;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
    
    @Override
    public @NotNull @Unmodifiable Map<DiscordLocale, @NotNull String> getNameLocalizations() {
        return (choice.getNameLocalizations() instanceof Optional.Value<Map<Locale, String>> localizations) ?
                localizations.getValue()
                        .entrySet()
                        .stream()
                        .map((e) -> Map.entry(
                                DiscordBridgeKord.convertLocale(e.getKey()),
                                e.getValue()
                        ))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                : Map.of();    }
    
    @Override
    public @NotNull String getName() {
        return choice.getName();
    }
}
