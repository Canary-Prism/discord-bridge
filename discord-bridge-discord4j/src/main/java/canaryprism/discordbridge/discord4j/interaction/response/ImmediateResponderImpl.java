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

package canaryprism.discordbridge.discord4j.interaction.response;

import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.interaction.response.ImmediateResponder;
import canaryprism.discordbridge.api.message.MessageFlag;
import canaryprism.discordbridge.discord4j.DiscordBridgeDiscord4J;
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;

public class ImmediateResponderImpl implements ImmediateResponder {
    
    public final DiscordBridgeDiscord4J bridge;
    public InteractionApplicationCommandCallbackReplyMono responder;
    
    public ImmediateResponderImpl(DiscordBridgeDiscord4J bridge, InteractionApplicationCommandCallbackReplyMono responder) {
        this.bridge = bridge;
        this.responder = responder;
    }
    
    @Override
    public @NotNull CompletableFuture<?> respond() {
        return responder.toFuture();
    }
    
    @Override
    public @NotNull ImmediateResponderImpl setContent(@NotNull String text) {
        responder = responder.withContent(text);
        return this;
    }
    
    @Override
    public @NotNull ImmediateResponderImpl setFlags(EnumSet<MessageFlag> flags) {
        responder = responder.withEphemeral(false);
        for (var e : flags) {
            responder = switch (e) {
                case UNKNOWN -> throw new IllegalArgumentException("UNKNOWN flag disallowed here");
                case EPHEMERAL -> responder.withEphemeral(true);
                case SILENT -> throw new IllegalArgumentException("SILENT unsupported");
            };
        }
        return this;
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return responder;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
}
