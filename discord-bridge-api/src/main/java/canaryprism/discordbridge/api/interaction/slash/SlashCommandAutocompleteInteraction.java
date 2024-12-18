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

package canaryprism.discordbridge.api.interaction.slash;

import canaryprism.discordbridge.api.data.interaction.slash.SlashCommandOptionChoiceData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/// Represents a SlashCommand autocomplete interaction
///
/// As this interaction is triggered by the user typing in an autocompletable option,
/// the values in options may be incomplete invalid or missing
public interface SlashCommandAutocompleteInteraction extends SlashCommandInteraction {
    
    /// Finds the targeted (focused) option for autocompletion
    ///
    /// Specifically, finds the argument option of this interaction where
    /// [SlashCommandInteractionOption#isAutocompleteTarget()] is `true`
    ///
    /// @return the targeted option
    default @NotNull SlashCommandInteractionOption getTargetOption() {
        return getArguments()
                .stream()
                .filter((e) -> e.isAutocompleteTarget().orElse(false))
                .findAny()
                .orElseThrow();
    }
    
    /// Suggests option choices for the user to enter in the autocompleting option
    ///
    /// The option choice data types must match with the type of the target option
    ///
    /// @param choices the option choices to suggest
    /// @return a future for the request
    /// @throws IllegalArgumentException if the choices are invalid
    @NotNull CompletableFuture<?> suggest(@NotNull List<? extends @NotNull SlashCommandOptionChoiceData> choices);
}
