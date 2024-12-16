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
import canaryprism.discordbridge.api.interaction.response.ImmediateResponder;
import canaryprism.discordbridge.api.message.MessageFlag;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateRequest;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;

public record ImmediateResponderImpl(DiscordBridge bridge, ReplyCallbackAction callback) implements ImmediateResponder, MessageRequestDelegate<ImmediateResponder> {
    
    @Override
    public @NotNull CompletableFuture<?> respond() {
        return callback.submit();
    }
    
    @Override
    public @NotNull MessageCreateRequest<?> getMessageCreateRequest() {
        return callback;
    }
    
    @Override
    public @NotNull ImmediateResponder setFlags(EnumSet<MessageFlag> flags) {
        callback.setEphemeral(false).setSuppressedNotifications(false);
        for (var e : flags) {
            switch (e) {
                case UNKNOWN -> throw new IllegalArgumentException("UNKNOWN flag disallowed here");
                case EPHEMERAL -> //noinspection ResultOfMethodCallIgnored
                        callback.setEphemeral(true);
                case SILENT -> callback.setSuppressedNotifications(true);
            }
        }
        return this;
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return callback;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
}
