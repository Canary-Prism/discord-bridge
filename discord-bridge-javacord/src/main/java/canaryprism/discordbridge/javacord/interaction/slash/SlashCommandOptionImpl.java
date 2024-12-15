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
import canaryprism.discordbridge.api.channel.ChannelType;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOption;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionChoice;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType;
import canaryprism.discordbridge.javacord.DiscordBridgeJavacord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.stream.Collectors;

public record SlashCommandOptionImpl(DiscordBridge bridge, org.javacord.api.interaction.SlashCommandOption option) implements SlashCommandOption {
    
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
        return option.isAutocompletable();
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
        return option.getOptions()
                .stream()
                .map((e) -> new SlashCommandOptionImpl(bridge, e))
                .toList();
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
        return option.getLongMinValue();
    }
    
    @Override
    public @NotNull Optional<@Range(from = MIN_NUMBER, to = MAX_NUMBER) Long> getIntegerBoundsMax() {
        return option.getLongMaxValue();
    }
    
    @Override
    public @NotNull Optional<@Range(from = MIN_NUMBER, to = MAX_NUMBER) Double> getNumberBoundsMin() {
        return option.getDecimalMinValue();
    }
    
    @Override
    public @NotNull Optional<@Range(from = MIN_NUMBER, to = MAX_NUMBER) Double> getNumberBoundsMax() {
        return option.getDecimalMaxValue();
    }
    
    @Override
    public @NotNull Optional<@Range(from = 0, to = Long.MAX_VALUE) Long> getStringLengthBoundsMin() {
        return option.getMinLength();
    }
    
    @Override
    public @NotNull Optional<@Range(from = 0, to = Long.MAX_VALUE) Long> getStringLengthBoundsMax() {
        return option.getMaxLength();
    }
    
    @Override
    public @NotNull String getName() {
        return option.getName();
    }
    
    @Override
    public @NotNull Map<Locale, @NotNull String> getNameLocalizations() {
        return option.getNameLocalizations()
                .entrySet()
                .stream()
                .map((e) -> Map.entry(
                        DiscordBridgeJavacord.convertLocale(e.getKey()),
                        e.getValue()
                ))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    @Override
    public @NotNull String getDescription() {
        return option.getDescription();
    }
    
    @Override
    public @NotNull Map<Locale, @NotNull String> getDescriptionLocalizations() {
        return option.getDescriptionLocalizations()
                .entrySet()
                .stream()
                .map((e) -> Map.entry(
                        DiscordBridgeJavacord.convertLocale(e.getKey()),
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
