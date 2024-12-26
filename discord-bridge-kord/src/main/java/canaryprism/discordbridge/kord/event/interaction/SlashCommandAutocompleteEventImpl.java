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

package canaryprism.discordbridge.kord.event.interaction;

import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.event.interaction.SlashCommandAutocompleteEvent;
import canaryprism.discordbridge.api.interaction.Interaction;
import canaryprism.discordbridge.kord.DiscordBridgeKord;
import canaryprism.discordbridge.kord.interaction.slash.SlashCommandAutocompleteInteractionImpl;
import dev.kord.core.event.interaction.AutoCompleteInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;

public record SlashCommandAutocompleteEventImpl(DiscordBridgeKord bridge, AutoCompleteInteractionCreateEvent event) implements SlashCommandAutocompleteEvent {
    
    @Override
    public @NotNull Interaction getInteraction() {
        return new SlashCommandAutocompleteInteractionImpl(bridge, event.getInteraction());
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return event;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
}
