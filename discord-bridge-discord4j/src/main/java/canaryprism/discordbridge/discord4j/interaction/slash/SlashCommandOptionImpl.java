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
import canaryprism.discordbridge.api.channel.ChannelType;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOption;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionChoice;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType;
import canaryprism.discordbridge.api.misc.DiscordLocale;
import canaryprism.discordbridge.discord4j.DiscordBridgeDiscord4J;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.channel.Channel;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public record SlashCommandOptionImpl(DiscordBridgeDiscord4J bridge, ApplicationCommandOptionData data) implements SlashCommandOption {
    
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
    public @NotNull String getDescription() {
        return data.description();
    }
    
    @Override
    public @NotNull @Unmodifiable Map<DiscordLocale, @NotNull String> getDescriptionLocalizations() {
        return data.descriptionLocalizations()
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
    public @NotNull SlashCommandOptionType getType() {
        return bridge.convertInternalObject(SlashCommandOptionType.class, ApplicationCommandOption.Type.of(data.type()));
    }
    
    @Override
    public boolean isRequired() {
        return data.required().toOptional().orElse(false);
    }
    
    @Override
    public boolean isAutocompletable() {
        return data.autocomplete().toOptional().orElse(false);
    }
    
    @Override
    public @NotNull @Unmodifiable List<? extends @NotNull SlashCommandOptionChoice> getChoices() {
        return data.choices()
                .toOptional()
                .orElse(List.of())
                .stream()
                .map((e) -> new SlashCommandOptionChoiceImpl(bridge, e))
                .toList();
    }
    
    @Override
    public @NotNull @Unmodifiable List<? extends @NotNull SlashCommandOption> getOptions() {
        return data.options()
                .toOptional()
                .orElse(List.of())
                .stream()
                .map((e) -> new SlashCommandOptionImpl(bridge, e))
                .toList();
    }
    
    @Override
    public @NotNull @Unmodifiable Set<? extends @NotNull ChannelType> getChannelTypeBounds() {
        return data.channelTypes()
                .toOptional()
                .orElse(List.of())
                .stream()
                .map(Channel.Type::of)
                .map((e) -> bridge.convertInternalObject(ChannelType.class, e))
                .collect(Collectors.toUnmodifiableSet());
    }
    
    @Override
    public @NotNull Optional<@Range(from = MIN_NUMBER, to = MAX_NUMBER) Long> getIntegerBoundsMin() {
        return data.minValue()
                .toOptional()
                .filter((ignored) -> ApplicationCommandOption.Type.of(data.type()) == ApplicationCommandOption.Type.INTEGER)
                .map(Double::longValue);
    }
    
    @Override
    public @NotNull Optional<@Range(from = MIN_NUMBER, to = MAX_NUMBER) Long> getIntegerBoundsMax() {
        return data.maxValue()
                .toOptional()
                .filter((ignored) -> ApplicationCommandOption.Type.of(data.type()) == ApplicationCommandOption.Type.INTEGER)
                .map(Double::longValue);
    }
    
    @Override
    public @NotNull Optional<@Range(from = MIN_NUMBER, to = MAX_NUMBER) Double> getNumberBoundsMin() {
        return data.minValue()
                .toOptional()
                .filter((ignored) -> ApplicationCommandOption.Type.of(data.type()) == ApplicationCommandOption.Type.NUMBER);
    }
    
    @Override
    public @NotNull Optional<@Range(from = MIN_NUMBER, to = MAX_NUMBER) Double> getNumberBoundsMax() {
        return data.maxValue()
                .toOptional()
                .filter((ignored) -> ApplicationCommandOption.Type.of(data.type()) == ApplicationCommandOption.Type.NUMBER);

    }
    
    @Override
    public @NotNull Optional<@Range(from = 0, to = Long.MAX_VALUE) Long> getStringLengthBoundsMin() {
        return data.minLength()
                .toOptional()
                .map(Integer::longValue);

    }
    
    @Override
    public @NotNull Optional<@Range(from = 0, to = Long.MAX_VALUE) Long> getStringLengthBoundsMax() {
        return data.maxLength()
                .toOptional()
                .map(Integer::longValue);
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
