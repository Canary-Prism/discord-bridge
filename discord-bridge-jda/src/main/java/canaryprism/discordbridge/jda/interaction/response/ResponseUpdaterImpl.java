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

package canaryprism.discordbridge.jda.interaction.response;

import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.interaction.response.ResponseUpdater;
import canaryprism.discordbridge.api.message.MessageFlag;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.utils.messages.MessageRequest;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;

public record ResponseUpdaterImpl(DiscordBridge bridge, WebhookMessageEditAction<Message> action) implements ResponseUpdater, MessageRequestDelegate<ResponseUpdater> {
    
    @Override
    public @NotNull CompletableFuture<?> update() {
        return action.submit();
    }
    
    @Override
    public @NotNull MessageRequest<?> getMessageCreateRequest() {
        return action;
    }
    
    @Override
    public @NotNull ResponseUpdater setFlags(EnumSet<MessageFlag> flags) {
        throw new IllegalArgumentException("Message flags cannot be set here");
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return action;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
}
