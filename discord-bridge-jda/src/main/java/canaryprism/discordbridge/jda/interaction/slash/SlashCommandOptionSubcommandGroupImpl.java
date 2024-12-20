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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.stream.Collectors;

public record SlashCommandOptionSubcommandGroupImpl(DiscordBridge bridge, Command.SubcommandGroup subcommand_group) implements SlashCommandOption {
    
    @Override
    public @NotNull SlashCommandOptionType getType() {
        return SlashCommandOptionType.SUBCOMMAND_GROUP;
    }
    
    @Override
    public boolean isRequired() {
        return true;
    }
    
    @Override
    public boolean isAutocompletable() {
        return false;
    }
    
    @Override
    public @NotNull List<? extends @NotNull SlashCommandOptionChoice> getChoices() {
        return List.of();
    }
    
    @Override
    public @NotNull List<? extends @NotNull SlashCommandOption> getOptions() {
        return subcommand_group.getSubcommands()
                .stream()
                .map((e) -> new SlashCommandOptionSubcommandImpl(bridge, e))
                .toList();
    }
    
    @Override
    public @NotNull Set<? extends @NotNull ChannelType> getChannelTypeBounds() {
        return Set.of();
    }
    
    @Override
    public @NotNull Optional<@Range(from = MIN_NUMBER, to = MAX_NUMBER) Long> getIntegerBoundsMin() {
        return Optional.empty();
    }
    
    @Override
    public @NotNull Optional<@Range(from = MIN_NUMBER, to = MAX_NUMBER) Long> getIntegerBoundsMax() {
        return Optional.empty();
    }
    
    @Override
    public @NotNull Optional<@Range(from = MIN_NUMBER, to = MAX_NUMBER) Double> getNumberBoundsMin() {
        return Optional.empty();
    }
    
    @Override
    public @NotNull Optional<@Range(from = MIN_NUMBER, to = MAX_NUMBER) Double> getNumberBoundsMax() {
        return Optional.empty();
    }
    
    @Override
    public @NotNull Optional<@Range(from = 0, to = Long.MAX_VALUE) Long> getStringLengthBoundsMin() {
        return Optional.empty();
    }
    
    @Override
    public @NotNull Optional<@Range(from = 0, to = Long.MAX_VALUE) Long> getStringLengthBoundsMax() {
        return Optional.empty();
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return subcommand_group;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
    
    @Override
    public @NotNull String getName() {
        return subcommand_group.getName();
    }
    
    @Override
    public @NotNull Map<DiscordLocale, @NotNull String> getNameLocalizations() {
        return subcommand_group.getNameLocalizations()
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
        return subcommand_group.getDescription();
    }
    
    @Override
    public @NotNull Map<DiscordLocale, @NotNull String> getDescriptionLocalizations() {
        return subcommand_group.getDescriptionLocalizations()
                .toMap()
                .entrySet()
                .stream()
                .map((e) -> Map.entry(
                        DiscordBridgeJDA.convertLocale(e.getKey()),
                        e.getValue()
                ))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
