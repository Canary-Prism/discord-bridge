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
import canaryprism.discordbridge.api.channel.ChannelType;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOption;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionChoice;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType;
import canaryprism.discordbridge.api.misc.DiscordLocale;
import canaryprism.discordbridge.jda.DiscordBridgeJDA;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.stream.Collectors;

public record SlashCommandOptionOptionImpl(DiscordBridge bridge, Command.Option option) implements SlashCommandOption {
    
    @Override
    public @NotNull SlashCommandOptionType getType() {
        return bridge.convertInternalObject(SlashCommandOptionType.class, option.getType());
    }
    
    @Override
    public boolean isRequired() {
        return option.isRequired();
    }
    
    @Override
    public boolean isAutocompletable() {
        return option.isAutoComplete();
    }
    
    @Override
    public @NotNull List<? extends @NotNull SlashCommandOptionChoice> getChoices() {
        return option.getChoices()
                .stream()
                .map((e) -> new SlashCommandOptionChoiceImpl(bridge, e))
                .toList();
    }
    
    @Override
    public @NotNull List<? extends @NotNull SlashCommandOption> getOptions() {
        return List.of();
    }
    
    @Override
    public @NotNull Set<? extends @NotNull ChannelType> getChannelTypeBounds() {
        return option.getChannelTypes()
                .stream()
                .map((e) -> bridge.convertInternalObject(ChannelType.class, e))
                .collect(Collectors.toUnmodifiableSet());
    }
    
    @Override
    public @NotNull Optional<@Range(from = MIN_NUMBER, to = MAX_NUMBER) Long> getIntegerBoundsMin() {
        return option.getType() == OptionType.INTEGER ? Optional.ofNullable(option.getMinValue()).map(Number::longValue) : Optional.empty();
    }
    
    @Override
    public @NotNull Optional<@Range(from = MIN_NUMBER, to = MAX_NUMBER) Long> getIntegerBoundsMax() {
        return option.getType() == OptionType.INTEGER ? Optional.ofNullable(option.getMaxValue()).map(Number::longValue) : Optional.empty();
    }
    
    @Override
    public @NotNull Optional<@Range(from = MIN_NUMBER, to = MAX_NUMBER) Double> getNumberBoundsMin() {
        return option.getType() == OptionType.NUMBER ? Optional.ofNullable(option.getMinValue()).map(Number::doubleValue) : Optional.empty();
    }
    
    @Override
    public @NotNull Optional<@Range(from = MIN_NUMBER, to = MAX_NUMBER) Double> getNumberBoundsMax() {
        return option.getType() == OptionType.NUMBER ? Optional.ofNullable(option.getMaxValue()).map(Number::doubleValue) : Optional.empty();
    }
    
    @Override
    public @NotNull Optional<@Range(from = 0, to = Long.MAX_VALUE) Long> getStringLengthBoundsMin() {
        return Optional.ofNullable(option.getMinLength()).map(Integer::longValue);
    }
    
    @Override
    public @NotNull Optional<@Range(from = 0, to = Long.MAX_VALUE) Long> getStringLengthBoundsMax() {
        return Optional.ofNullable(option.getMaxLength()).map(Integer::longValue);
    }
    
    @Override
    public @NotNull String getName() {
        return option.getName();
    }
    
    @Override
    public @NotNull Map<DiscordLocale, @NotNull String> getNameLocalizations() {
        return option.getNameLocalizations()
                .toMap()
                .entrySet()
                .stream()
                .map((e) -> Map.entry(
                        DiscordBridgeJDA.convertLocale(e.getKey()),
                        e.getValue()
                ))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    @Override
    public @NotNull String getDescription() {
        return option.getDescription();
    }
    
    @Override
    public @NotNull Map<DiscordLocale, @NotNull String> getDescriptionLocalizations() {
        return option.getDescriptionLocalizations()
                .toMap()
                .entrySet()
                .stream()
                .map((e) -> Map.entry(
                        DiscordBridgeJDA.convertLocale(e.getKey()),
                        e.getValue()
                ))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
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
