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

package canaryprism.discordbridge.api.listener.interaction;

import canaryprism.discordbridge.api.event.interaction.SlashCommandAutocompleteEvent;
import canaryprism.discordbridge.api.listener.ApiAttachableListener;

/// A listener for [SlashCommandAutocompleteEvent]s
@FunctionalInterface
public non-sealed interface SlashCommandAutocompleteListener extends ApiAttachableListener {
    
    /// Called on Slash Command autocomplete
    ///
    /// @param event the event, never null
    void onSlashCommandAutocomplete(SlashCommandAutocompleteEvent event);
}
