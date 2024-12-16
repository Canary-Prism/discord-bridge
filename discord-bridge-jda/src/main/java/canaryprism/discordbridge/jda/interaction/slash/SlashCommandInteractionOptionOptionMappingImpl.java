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
import canaryprism.discordbridge.api.interaction.slash.SlashCommandInteractionOption;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public record SlashCommandInteractionOptionOptionMappingImpl(DiscordBridge bridge, CommandInteractionPayload interaction, OptionMapping mapping, CompletableFuture<SlashCommandOptionOptionImpl> future) implements SlashCommandInteractionOption {
    
    public SlashCommandInteractionOptionOptionMappingImpl(DiscordBridge bridge, CommandInteractionPayload interaction, OptionMapping mapping) {
        this(bridge, interaction, mapping,
                interaction.getJDA()
                        .retrieveCommandById(interaction.getCommandId())
                        .submit()
                        .thenApply((command) -> command.getOptions()
                                .stream()
                                .filter((option) -> option.getName().equals(mapping.getName()))
                                .map((option) -> new SlashCommandOptionOptionImpl(bridge, option))
                                .findAny()
                                .orElseThrow()));
    }
    
    @Override
    public @NotNull Optional<Boolean> isAutocompleteTarget() {
        var is_autocomplete = interaction instanceof CommandAutoCompleteInteraction autocomplete
                && autocomplete.getFocusedOption().getName().equals(mapping.getName());
        if (is_autocomplete)
            return Optional.of(true);
        else if (future.join().isAutocompletable())
            return Optional.of(false);
        else
            return Optional.empty();
    }
    
    @SuppressWarnings("DuplicateBranchesInSwitch")
    @Override
    public @NotNull Optional<?> getValue() {
        return Optional.ofNullable(switch (mapping.getType()) {
            case UNKNOWN -> mapping.getAsString();
            case SUB_COMMAND -> null;
            case SUB_COMMAND_GROUP -> null;
            case STRING -> mapping.getAsString();
            case INTEGER -> mapping.getAsLong();
            case BOOLEAN -> mapping.getAsBoolean();
            case USER -> mapping.getAsUser();
            case CHANNEL -> mapping.getAsChannel();
            case ROLE -> mapping.getAsRole();
            case MENTIONABLE -> mapping.getAsMentionable();
            case NUMBER -> mapping.getAsDouble();
            case ATTACHMENT -> mapping.getAsAttachment();
        });
    }
    
    @Override
    public @NotNull String getName() {
        return mapping.getName();
    }
    
    @Override
    public @NotNull List<? extends @NotNull SlashCommandInteractionOption> getOptions() {
        return List.of();
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return mapping;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
}
