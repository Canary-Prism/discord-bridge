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

package canaryprism.discordbridge.api.event.interaction;

import canaryprism.discordbridge.api.interaction.slash.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;

/// An event for a user running a Slash Command
public interface SlashCommandInvokeEvent extends CommandEvent {
    
    /// Gets the [SlashCommandInteraction] of this event
    ///
    /// @return the SlashCommandInteraction of this event
    default @NotNull SlashCommandInteraction getSlashCommandInteraction() {
        return ((SlashCommandInteraction) getInteraction());
    }
}
