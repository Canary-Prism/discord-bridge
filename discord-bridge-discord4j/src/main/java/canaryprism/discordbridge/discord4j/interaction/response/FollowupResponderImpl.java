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
import canaryprism.discordbridge.api.interaction.response.FollowupResponder;
import canaryprism.discordbridge.api.message.MessageFlag;
import canaryprism.discordbridge.discord4j.DiscordBridgeDiscord4J;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;

public class FollowupResponderImpl implements FollowupResponder {
    
    public final DiscordBridgeDiscord4J bridge;
    public final ChatInputInteractionEvent event;
    public String content;
    public boolean ephemeral;
    
    public FollowupResponderImpl(DiscordBridgeDiscord4J bridge, ChatInputInteractionEvent event) {
        this.bridge = bridge;
        this.event = event;
    }
    
    
    @Override
    public @NotNull CompletableFuture<?> send() {
        return event.createFollowup(content)
                .withEphemeral(ephemeral)
                .toFuture();
    }
    
    @Override
    public @NotNull CompletableFuture<?> update(long message_id) {
        if (ephemeral)
            throw new IllegalArgumentException("can't edit message to be ephemeral");
        
        return event.editFollowup(Snowflake.of(message_id))
                .toFuture();
    }
    
    @Override
    public @NotNull FollowupResponderImpl setContent(@NotNull String text) {
        this.content = text;
        return this;
    }
    
    @Override
    public @NotNull FollowupResponderImpl setFlags(EnumSet<MessageFlag> flags) {
        this.ephemeral = false;
        for (var e : flags) {
            switch (e) {
                case UNKNOWN -> throw new IllegalArgumentException("UNKNOWN flag disallowed here");
                case EPHEMERAL -> this.ephemeral = true;
                case SILENT -> throw new IllegalArgumentException("SILENT unsupported");
            };
        }
        return this;
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
