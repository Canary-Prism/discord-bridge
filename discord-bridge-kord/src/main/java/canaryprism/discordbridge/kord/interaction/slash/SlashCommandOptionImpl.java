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
import canaryprism.discordbridge.api.channel.ChannelType;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOption;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionChoice;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType;
import canaryprism.discordbridge.api.misc.DiscordLocale;
import canaryprism.discordbridge.kord.DiscordBridgeKord;
import dev.kord.common.entity.optional.Optional.Value;
import dev.kord.core.cache.data.ApplicationCommandOptionChoiceData;
import dev.kord.core.cache.data.ApplicationCommandOptionData;
import kotlinx.serialization.json.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public record SlashCommandOptionImpl(DiscordBridgeKord bridge, ApplicationCommandOptionData option) implements SlashCommandOption {
    
    @Override
    public @NotNull SlashCommandOptionType getType() {
        return bridge.convertInternalObject(SlashCommandOptionType.class, option.getType());
    }
    
    @Override
    public boolean isRequired() {
        return option.getRequired().orElse(false);
    }
    
    @Override
    public boolean isAutocompletable() {
        throw new UnsupportedOperationException(String.format("autocompletable data inaccessible by %s", bridge));
    }
    
    @Override
    public @NotNull @Unmodifiable List<? extends @NotNull SlashCommandOptionChoice> getChoices() {
        return (option.getChoices() instanceof Value<List<ApplicationCommandOptionChoiceData>> value) ?
                value.getValue()
                        .stream()
                        .map((e) -> new SlashCommandOptionChoiceImpl(bridge, e))
                        .toList()
                : List.of();
    }
    
    @Override
    public @NotNull @Unmodifiable List<? extends @NotNull SlashCommandOption> getOptions() {
        return (option.getOptions() instanceof Value<List<ApplicationCommandOptionData>> value) ?
                value.getValue()
                        .stream()
                        .map((e) -> new SlashCommandOptionImpl(bridge, e))
                        .toList()
                : List.of();
    }
    
    @Override
    public @NotNull @Unmodifiable Set<? extends @NotNull ChannelType> getChannelTypeBounds() {
        return (option.getChannelTypes() instanceof Value<List<dev.kord.common.entity.ChannelType>> value) ?
                value.getValue()
                        .stream()
                        .map((e) -> bridge.convertInternalObject(ChannelType.class, e))
                        .collect(Collectors.toUnmodifiableSet())
                : Set.of();
    }
    
    @Override
    public @NotNull Optional<@Range(from = MIN_NUMBER, to = MAX_NUMBER) Long> getIntegerBoundsMin() {
        return (option.getMinValue() instanceof Value<JsonPrimitive> value) ?
                Optional.of(value.getValue().getContent())
                        .filter((ignored) -> this.getType() == SlashCommandOptionType.INTEGER)
                        .map(Long::parseLong)
                : Optional.empty();
    }
    
    @Override
    public @NotNull Optional<@Range(from = MIN_NUMBER, to = MAX_NUMBER) Long> getIntegerBoundsMax() {
        return (option.getMaxValue() instanceof Value<JsonPrimitive> value) ?
                Optional.of(value.getValue().getContent())
                        .filter((ignored) -> this.getType() == SlashCommandOptionType.INTEGER)
                        .map(Long::parseLong)
                : Optional.empty();
    }
    
    @Override
    public @NotNull Optional<@Range(from = MIN_NUMBER, to = MAX_NUMBER) Double> getNumberBoundsMin() {
        return (option.getMinValue() instanceof Value<JsonPrimitive> value) ?
                Optional.of(value.getValue().getContent())
                        .filter((ignored) -> this.getType() == SlashCommandOptionType.NUMBER)
                        .map(Double::parseDouble)
                : Optional.empty();
    }
    
    @Override
    public @NotNull Optional<@Range(from = MIN_NUMBER, to = MAX_NUMBER) Double> getNumberBoundsMax() {
        return (option.getMaxValue() instanceof Value<JsonPrimitive> value) ?
                Optional.of(value.getValue().getContent())
                        .filter((ignored) -> this.getType() == SlashCommandOptionType.NUMBER)
                        .map(Double::parseDouble)
                : Optional.empty();
    }
    
    @Override
    public @NotNull Optional<@Range(from = 0, to = Long.MAX_VALUE) Long> getStringLengthBoundsMin() {
        return Optional.ofNullable(option.getMinLength().getAsNullable())
                .filter((ignored) -> this.getType() == SlashCommandOptionType.STRING)
                .map(Integer::longValue);
    }
    
    @Override
    public @NotNull Optional<@Range(from = 0, to = Long.MAX_VALUE) Long> getStringLengthBoundsMax() {
        return Optional.ofNullable(option.getMaxLength().getAsNullable())
                .filter((ignored) -> this.getType() == SlashCommandOptionType.STRING)
                .map(Integer::longValue);
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
    public @NotNull @Unmodifiable Map<DiscordLocale, @NotNull String> getDescriptionLocalizations() {
        throw new UnsupportedOperationException(String.format("localization data inaccessible by %s", bridge));
    }
    
    @Override
    public @NotNull String getDescription() {
        return option.getDescription();
    }
    
    @Override
    public @NotNull @Unmodifiable Map<DiscordLocale, @NotNull String> getNameLocalizations() {
        throw new UnsupportedOperationException(String.format("localization data inaccessible by %s", bridge));
    }
    
    @Override
    public @NotNull String getName() {
        return option.getName();
    }
}
