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

import canaryprism.discordbridge.api.channel.ChannelType;
import canaryprism.discordbridge.api.entities.LocalizedDescribable;
import canaryprism.discordbridge.api.entities.LocalizedNamable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/// Represents a Slash Command Option
public interface SlashCommandOption extends LocalizedNamable, LocalizedDescribable {
    
    /// Gets the type of this option
    ///
    /// @return the type of this option
    @NotNull SlashCommandOptionType getType();

    /// Gets whether this slash command option is required or not
    ///
    /// @return whether this slash command option is required
    boolean isRequired();
    
    /// Gets whether this slash command option is autocompletable or not
    ///
    /// Autocompletable options will send AutocompleteEvents FIXME: add a real link
    /// as a user types in the option
    ///
    /// @return whether this slash command option is autocompletable
    boolean isAutocompletable();
    
    /// Gets the choices of this option
    ///
    /// if this is not empty the user can only input values from these choices
    ///
    /// @return the choices of this option
    @NotNull List<@NotNull SlashCommandOptionChoice> getChoices();
    
    /// Gets the options of this option
    ///
    /// This will only not be empty if this option is of type [SlashCommandOptionType#SUBCOMMAND] or [SlashCommandOptionType#SUBCOMMAND_GROUP]
    ///
    /// @return a list of options for this option
    @NotNull List<@NotNull SlashCommandOption> getOptions();
    
    /// Gets the channel type bounds of this option
    ///
    /// Only applicable if this option is of type [SlashCommandOptionType#CHANNEL], and means that the user
    /// can only input channels of these types
    ///
    /// @return set of channel types for the channel type bounds
    @NotNull Set<@NotNull ChannelType> getChannelTypeBounds();
    
    /// Gets the minimum of the integer bounds of this option
    /// (Integer in the sense of [SlashCommandOptionType#INTEGER], not [Integer])
    ///
    /// Only applicable if this option is of type [SlashCommandOptionType#INTEGER]
    /// and requires the user to input a value no less than this amount
    ///
    /// @return minimum of integer bounds
    @NotNull Optional<Long> getIntegerBoundsMin();
    
    /// Gets the maximum of the integer bounds of this option
    /// (Integer in the sense of [SlashCommandOptionType#INTEGER], not [Integer])
    ///
    /// Only applicable if this option is of type [SlashCommandOptionType#INTEGER]
    /// and requires the user to input a value no more than this amount
    ///
    /// @return maximum of integer bounds
    @NotNull Optional<Long> getIntegerBoundsMax();
    
    /// Gets the minimum of the decimal number bounds of this option
    ///
    /// Only applicable if this option is of type [SlashCommandOptionType#NUMBER]
    /// and requires the user to input a value no less than this amount
    ///
    /// @return minimum of number bounds
    @NotNull Optional<Double> getNumberBoundsMin();
    
    /// Gets the maximum of the decimal number bounds of this option
    ///
    /// Only applicable if this option is of type [SlashCommandOptionType#NUMBER]
    /// and requires the user to input a value no more than this amount
    ///
    /// @return maximum of number bounds
    @NotNull Optional<Double> getNumberBoundsMax();
    
    /// Gets the minimum of the string length bounds of this option
    ///
    /// Only applicable if this option is of type [SlashCommandOptionType#STRING]
    /// and requires the user to input a String with at least this many characters
    ///
    /// @return minimum of string length bounds
    @NotNull Optional<String> getStringLengthBoundsMin();
    
    /// Gets the maximum of the string length bounds of this option
    ///
    /// Only applicable if this option is of type [SlashCommandOptionType#STRING]
    /// and requires the user to input a String with at most this many characters
    ///
    /// @return maximum of string length bounds
    @NotNull Optional<String> getStringLengthBoundsMax();
}
