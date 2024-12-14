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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/// An object that holds [SlashCommandInteractionOption]s
public interface SlashCommandInteractionOptionProvider {
    
    /// Gets the list of [SlashCommandInteractionOption]s
    ///
    /// @return list of options
    @NotNull List<@NotNull SlashCommandInteractionOption> getOptions();
    
    /// Gets a [SlashCommandInteractionOption] by name
    ///
    /// @param name the option name
    /// @return the option
    default @NotNull Optional<SlashCommandInteractionOption> getOptionByName(@Nullable String name) {
        return getOptions()
                .stream()
                .filter((e) -> e.getName().equals(name))
                .findAny();
    }
    
    /// Gets an argument list of [SlashCommandInteractionOption]s
    ///
    /// This differs from [#getOptions()] in that if an option is a `SUBCOMMAND` or `SUBCOMMAND_GROUP`,
    /// **its** options are also retrieved and appended to the list
    ///
    /// @return list of options
    default @NotNull List<@NotNull SlashCommandInteractionOption> getArguments() {
        return getOptions()
                .stream()
                .flatMap(SlashCommandInteractionOption::spreadOptions)
                .toList();
    }
    
    /// Gets an argument [SlashCommandInteractionOption] by name
    ///
    /// This differs from [#getOptionByName(java.lang.String)] in that if an option is a `SUBCOMMAND` or `SUBCOMMAND_GROUP`,
    /// it's also searched recursively for an option of the provided name
    ///
    /// @param name the option name
    /// @return the option
    default @NotNull Optional<@NotNull SlashCommandInteractionOption> getArgumentByName(@Nullable String name) {
        return getArguments()
                .stream()
                .filter((e) -> e.getName().equals(name))
                .findAny();
    }

}
