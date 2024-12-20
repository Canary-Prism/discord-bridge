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

import canaryprism.discordbridge.api.DiscordBridgeApi;
import canaryprism.discordbridge.api.misc.LocalizedNameable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/// Represents an Option Choice for [SlashCommandOption]s
///
/// Options with Option Choices lock the user into choosing one of the provided Choices
public interface SlashCommandOptionChoice extends DiscordBridgeApi, LocalizedNameable {
    
    /// Gets the value of this option choice
    ///
    /// The value is always present unless this option is of type `SUBCOMMAND` or `SUBCOMMAND_GROUP`
    ///
    /// @return the value
    @NotNull Optional<?> getValue();
    
    /// Gets the value of this option choice as the provided type
    ///
    /// Empty if [#getValue()] is empty or the type cast cannot be made
    ///
    /// @param <T> the type to get as
    /// @param type the runtime class
    /// @return the value
    /// @see #getValue()
    default <T> @NotNull Optional<T> getValue(Class<T> type) {
        return getValue()
                .filter(type::isInstance)
                .map(type::cast);
    }
    
    /// Gets the value of this option choice as the provided option type
    ///
    /// The value will be attempted to be casted to the [SlashCommandOptionType]'s type representation
    /// by calling [SlashCommandOptionType#getTypeRepresentation()]
    ///
    /// Empty if [#getValue()] is empty or the cast cannot be made
    ///
    /// @param option_type the `SlashCommandOptionType` to get the value as
    /// @return the value
    /// @see #getValue(Class)
    /// @see #getValue()
    /// @implNote only SlashCommandOptionTypes with [SlashCommandOptionType#can_be_choices] set to `true`
    /// should be used here, but it will technically accept any
    default @NotNull Optional<?> getValue(SlashCommandOptionType option_type) {
        return getValue(option_type.getTypeRepresentation());
    }
}
