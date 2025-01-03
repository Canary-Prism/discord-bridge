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

import canaryprism.discordbridge.api.entity.Mentionable;
import canaryprism.discordbridge.api.interaction.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/// Represents a Discord Slash Command
public interface SlashCommand extends Command, Mentionable {
    
    @Override
    default @NotNull String getMentionTag() {
        return String.format("</%s:%s>", getName(), getIdAsString());
    }
    
    /// Gets the options of this slash command
    ///
    /// @return a list of options for this slash command
    @NotNull @Unmodifiable
    List<? extends @NotNull SlashCommandOption> getOptions();
}
