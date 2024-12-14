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

import canaryprism.discordbridge.api.entities.Nameable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;

/// Represents a Slash Command Interaction Option
public interface SlashCommandInteractionOption extends Nameable, SlashCommandInteractionOptionProvider {
    
    /// Spreads out this option's options
    ///
    /// if this option is of type `SUBCOMMAND` or `SUBCOMMAND_GROUP`, it'll get its options while recursively spreading them too
    ///
    /// if not this will return an empty Stream
    ///
    /// This method is mainly for use from [SlashCommandInteractionOptionProvider#getArguments()]
    ///
    /// @return Stream of spread options
    default Stream<SlashCommandInteractionOption> spreadOptions() {
        return Stream.concat(
                Stream.of(this),
                getOptions()
                        .stream()
                        .flatMap(SlashCommandInteractionOption::spreadOptions)
        );
    }
    
    /// Gets whether this option is currently focused for autocompleting
    ///
    /// Returns [Optional#empty()] if this option isn't autocompletable at all
    ///
    /// @return whether this option is the autocomplete target
    @NotNull Optional<Boolean> isAutocompleteTarget();
    
    /// Gets the value of this option
    ///
    /// The value is always present unless this option is of type `SUBCOMMAND` or `SUBCOMMAND_GROUP`
    ///
    /// @return the value
    @NotNull Optional<?> getValue();
    
    /// Gets the value of this option as the provided type
    ///
    /// Empty if [#getValue()] is empty or the type cast cannot be made
    ///
    /// @return the value
    /// @see #getValue()
    default <T> @NotNull Optional<T> getValue(Class<T> type) {
        return getValue()
                .filter(type::isInstance)
                .map(type::cast);
    }
    
    /// Gets the value of this option as the provided option type
    ///
    /// The value will be attempted to be casted to the [SlashCommandOptionType]'s type representation
    /// by calling [SlashCommandOptionType#getTypeRepresentation()]
    ///
    /// Empty if [#getValue()] is empty or the cast cannot be made
    ///
    /// @return the value
    /// @see #getValue(Class)
    /// @see #getValue()
    default @NotNull Optional<?> getValue(SlashCommandOptionType option_type) {
        return getValue(option_type.getTypeRepresentation());
    }
}
