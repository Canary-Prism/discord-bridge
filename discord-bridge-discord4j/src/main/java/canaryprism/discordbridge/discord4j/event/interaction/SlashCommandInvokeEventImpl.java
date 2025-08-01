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

package canaryprism.discordbridge.discord4j.event.interaction;

import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.event.interaction.SlashCommandInvokeEvent;
import canaryprism.discordbridge.api.interaction.Interaction;
import canaryprism.discordbridge.discord4j.DiscordBridgeDiscord4J;
import canaryprism.discordbridge.discord4j.interaction.slash.SlashCommandInvokeInteractionImpl;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.jetbrains.annotations.NotNull;

public record SlashCommandInvokeEventImpl(DiscordBridgeDiscord4J bridge, ChatInputInteractionEvent event) implements SlashCommandInvokeEvent {
    
    @Override
    public @NotNull Interaction getInteraction() {
        return new SlashCommandInvokeInteractionImpl(bridge, event);
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
