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

package canaryprism.discordbridge.jda.listener.interaction;

import canaryprism.discordbridge.api.listener.interaction.SlashCommandAutocompleteListener;
import canaryprism.discordbridge.jda.DiscordBridgeJDA;
import canaryprism.discordbridge.jda.event.interaction.SlashCommandAutocompleteEventImpl;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public final class CommandAutocompleteInteractionEventListenerDelegate extends ListenerAdapter {
    
    public final DiscordBridgeJDA bridge;
    public final SlashCommandAutocompleteListener listener;
    
    public CommandAutocompleteInteractionEventListenerDelegate(DiscordBridgeJDA bridge, SlashCommandAutocompleteListener listener) {
        this.bridge = bridge;
        this.listener = listener;
    }
    
    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        listener.onSlashCommandAutocomplete(new SlashCommandAutocompleteEventImpl(bridge, event));
    }
}
