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

import canaryprism.discordbridge.api.entities.LocalizedNamable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface SlashCommandOptionChoice extends LocalizedNamable {
    
    /// Gets the [SlashCommandOptionType#STRING] value of this option choice
    ///
    /// @return String value of this option choice
    @NotNull Optional<String> getStringValue();
    
    /// Gets the [SlashCommandOptionType#INTEGER] value of this option choice
    ///
    /// @return long value of this option choice
    @NotNull Optional<Long> getIntegerValue();
    
    /// Gets the [SlashCommandOptionType#NUMBER] value of this option choice
    ///
    /// @return double value of this option choice
    @NotNull Optional<Double> getNumberValue();
}
