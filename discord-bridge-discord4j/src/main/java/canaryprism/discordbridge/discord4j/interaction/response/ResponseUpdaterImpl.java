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
import canaryprism.discordbridge.api.interaction.response.ResponseUpdater;
import canaryprism.discordbridge.api.message.MessageFlag;
import canaryprism.discordbridge.discord4j.DiscordBridgeDiscord4J;
import discord4j.core.spec.InteractionReplyEditMono;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;

public class ResponseUpdaterImpl implements ResponseUpdater {
    
    public final DiscordBridgeDiscord4J bridge;
    public InteractionReplyEditMono updater;
    
    public ResponseUpdaterImpl(DiscordBridgeDiscord4J bridge, InteractionReplyEditMono updater) {
        this.bridge = bridge;
        this.updater = updater;
    }
    
    @Override
    public @NotNull CompletableFuture<?> update() {
        return updater.toFuture();
    }
    
    @Override
    public @NotNull ResponseUpdater setContent(@NotNull String text) {
        updater = updater.withContentOrNull(text);
        return this;
    }
    
    @Override
    public @NotNull ResponseUpdater setFlags(EnumSet<MessageFlag> flags) {
        throw new IllegalArgumentException("Message flags cannot be set here");
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return updater;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
}
