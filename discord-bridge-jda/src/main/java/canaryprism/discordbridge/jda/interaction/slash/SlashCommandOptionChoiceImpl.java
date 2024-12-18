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
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionChoice;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public record SlashCommandOptionChoiceImpl(DiscordBridge bridge, Command.Choice choice) implements SlashCommandOptionChoice {
    
    @Override
    public @NotNull Optional<?> getValue() {
        return Optional.ofNullable(switch (choice.getType()) {
            case STRING -> choice.getAsString();
            case INTEGER -> choice.getAsLong();
            case NUMBER -> choice.getAsDouble();
            default -> null;
        });
    }
    
    
    @Override
    public @NotNull Map<Locale, @NotNull String> getNameLocalizations() {
        return choice.getNameLocalizations()
                .toMap()
                .entrySet()
                .stream()
                .map((e) -> Map.entry(
                        Locale.forLanguageTag(e.getKey().getLocale()),
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
