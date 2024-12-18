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

import canaryprism.discordbridge.api.data.interaction.slash.SlashCommandOptionChoiceData;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandAutocompleteInteraction;
import canaryprism.discordbridge.jda.DiscordBridgeJDA;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SlashCommandAutocompleteInteractionImpl extends SlashCommandInteractionImpl implements SlashCommandAutocompleteInteraction {
    
    @SuppressWarnings("TypeMayBeWeakened")
    public SlashCommandAutocompleteInteractionImpl(DiscordBridgeJDA bridge, CommandAutoCompleteInteraction interaction) {
        super(bridge, interaction);
    }
    
    @SuppressWarnings("OverlyStrongTypeCast")
    @Override
    public @NotNull CompletableFuture<?> suggest(@NotNull List<? extends @NotNull SlashCommandOptionChoiceData> choices) {
        return ((CommandAutoCompleteInteraction) interaction)
                .replyChoices(choices.stream()
                        .map(bridge::convertData)
                        .toList())
                .submit();
    }
}

