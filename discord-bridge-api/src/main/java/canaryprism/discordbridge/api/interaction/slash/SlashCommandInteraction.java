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

import canaryprism.discordbridge.api.interaction.Interaction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

/// Represents a SlashCommand interaction
public interface SlashCommandInteraction extends Interaction, SlashCommandInteractionOptionProvider {
    
    /// Gets the full command name of this interaction
    ///
    /// Starts with this command's name, then if an option is `SUBCOMMAND` or `SUBCOMMAND_GROUP` its name is also added to the list
    ///
    /// @return the full command name of this interaction
    default @NotNull List<@NotNull String> getFullCommandName() {
        return Stream.concat(
                Stream.of(this.getCommandName()),
                this.getArguments()
                        .stream()
                        .filter((e) -> e.getValue().isEmpty())
                        .map(SlashCommandInteractionOption::getName)
        ).toList();
    }
}
