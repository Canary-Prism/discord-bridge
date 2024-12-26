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

package canaryprism.discordbridge.kord.interaction.response;

import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.interaction.response.ResponseUpdater;
import canaryprism.discordbridge.api.message.MessageFlag;
import canaryprism.discordbridge.kord.DiscordBridgeKord;
import dev.kord.common.entity.MessageFlagKt;
import dev.kord.core.behavior.interaction.response.DeferredMessageInteractionResponseBehavior;
import dev.kord.core.behavior.interaction.response.DeferredMessageInteractionResponseBehaviorKt;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ResponseUpdaterImpl implements ResponseUpdater {
    
    public final DiscordBridgeKord bridge;
    public final DeferredMessageInteractionResponseBehavior response_behaviour;
    
    public ResponseUpdaterImpl(DiscordBridgeKord bridge, DeferredMessageInteractionResponseBehavior response_behaviour) {
        this.bridge = bridge;
        this.response_behaviour = response_behaviour;
    }
    
    private String content;
    private Set<MessageFlag> flags;
    
    @Override
    public @NotNull CompletableFuture<?> update() {
        var future = new CompletableFuture<>();
        
        DeferredMessageInteractionResponseBehaviorKt.respond(response_behaviour, (e) -> {
            e.setContent(content);
            e.setFlags(MessageFlagKt.MessageFlags(flags.stream()
                    .map((flag) -> ((dev.kord.common.entity.MessageFlag) bridge.getImplementationValue(flag)))
                    .toList())
            );
            return Unit.INSTANCE;
        }, new Continuation<>() {
            @Override
            public @NotNull CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }
            
            @Override
            public void resumeWith(@NotNull Object o) {
                try {
                    ResultKt.throwOnFailure(o);
                    future.complete(o);
                } catch (Throwable t) {
                    future.completeExceptionally(t);
                }
            }
        });
        
        return future;
    }
    
    @Override
    public @NotNull ResponseUpdater setContent(@NotNull String text) {
        this.content = text;
        return this;
    }
    
    @Override
    public @NotNull ResponseUpdater setFlags(EnumSet<MessageFlag> flags) {
        this.flags = flags.clone();
        return this;
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return response_behaviour;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
}
