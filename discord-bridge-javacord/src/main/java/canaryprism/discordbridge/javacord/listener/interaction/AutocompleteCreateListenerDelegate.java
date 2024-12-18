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

package canaryprism.discordbridge.javacord.listener.interaction;

import canaryprism.discordbridge.api.listener.interaction.SlashCommandAutocompleteListener;
import canaryprism.discordbridge.javacord.DiscordBridgeJavacord;
import canaryprism.discordbridge.javacord.event.interaction.SlashCommandAutocompleteEventImpl;
import org.javacord.api.event.interaction.AutocompleteCreateEvent;
import org.javacord.api.listener.interaction.AutocompleteCreateListener;

public record AutocompleteCreateListenerDelegate(DiscordBridgeJavacord bridge, SlashCommandAutocompleteListener listener) implements AutocompleteCreateListener {
    
    @Override
    public void onAutocompleteCreate(AutocompleteCreateEvent event) {
        listener.onSlashCommandAutocomplete(new SlashCommandAutocompleteEventImpl(bridge, event));
    }
}
