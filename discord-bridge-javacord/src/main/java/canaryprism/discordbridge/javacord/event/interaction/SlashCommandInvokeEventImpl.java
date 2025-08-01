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

package canaryprism.discordbridge.javacord.event.interaction;

import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.event.interaction.SlashCommandInvokeEvent;
import canaryprism.discordbridge.api.interaction.Interaction;
import canaryprism.discordbridge.javacord.DiscordBridgeJavacord;
import canaryprism.discordbridge.javacord.interaction.slash.SlashCommandInvokeInteractionImpl;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.jetbrains.annotations.NotNull;

public record SlashCommandInvokeEventImpl(DiscordBridge bridge, SlashCommandCreateEvent event) implements SlashCommandInvokeEvent {
    
    @Override
    public @NotNull Interaction getInteraction() {
        return new SlashCommandInvokeInteractionImpl(((DiscordBridgeJavacord) bridge), event.getSlashCommandInteraction());
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
