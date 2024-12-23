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
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionChoice;
import canaryprism.discordbridge.api.misc.DiscordLocale;
import canaryprism.discordbridge.discord4j.DiscordBridgeDiscord4J;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record SlashCommandOptionChoiceImpl(DiscordBridgeDiscord4J bridge, ApplicationCommandOptionChoiceData data) implements SlashCommandOptionChoice {
    
    @Override
    public @NotNull Object getValue() {
        return data.value();
    }
    
    @Override
    public @NotNull String getName() {
        return data.name();
    }
    
    @Override
    public @NotNull @Unmodifiable Map<DiscordLocale, @NotNull String> getNameLocalizations() {
        return data.nameLocalizations()
                .toOptional()
                .flatMap(Function.identity())
                .orElse(Map.of())
                .entrySet()
                .stream()
                .map((e) -> Map.entry(
                        DiscordBridgeDiscord4J.convertLocale(e.getKey()),
                        e.getValue()
                ))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return data;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
}
