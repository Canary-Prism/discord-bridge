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
import canaryprism.discordbridge.api.interaction.response.FollowupResponder;
import canaryprism.discordbridge.api.message.MessageFlag;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;

public class FollowupResponderImpl implements FollowupResponder {
    
    public final DiscordBridge bridge;
    public final InteractionHook hook;
    
    public FollowupResponderImpl(DiscordBridge bridge, InteractionHook hook) {
        this.bridge = bridge;
        this.hook = hook;
    }
    
    private String content;
    private EnumSet<MessageFlag> flags;
    
    private <T extends WebhookMessageCreateAction<Message>> T applyCreate(T action) {
        action.setContent(content);
        
        action.setEphemeral(false).setSuppressedNotifications(false);
        for (var e : flags) {
            switch (e) {
                case UNKNOWN -> throw new IllegalArgumentException("UNKNOWN flag disallowed here");
                case EPHEMERAL -> //noinspection ResultOfMethodCallIgnored
                        action.setEphemeral(true);
                case SILENT -> action.setSuppressedNotifications(true);
            }
        }
        
        return action;
    }
    
    private <T extends WebhookMessageEditAction<Message>> T applyEdit(T action) {
        action.setContent(content);
        
        if (!flags.isEmpty())
            throw new IllegalArgumentException("MessageFlags disallowed here");
        
        return action;
    }
    
    @Override
    public @NotNull CompletableFuture<?> send() {
        return applyCreate(hook.sendMessage("")).submit();
    }
    
    @Override
    public @NotNull CompletableFuture<?> update(long message_id) {
        return applyEdit(hook.editMessageById(message_id, "")).submit();
    }
    
    @Override
    public @NotNull FollowupResponder setContent(@NotNull String text) {
        this.content = text;
        return this;
    }
    
    @Override
    public @NotNull FollowupResponder setFlags(EnumSet<MessageFlag> flags) {
        this.flags = flags;
        return this;
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return hook;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
}
