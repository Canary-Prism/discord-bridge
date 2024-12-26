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
import canaryprism.discordbridge.api.interaction.response.ImmediateResponder;
import canaryprism.discordbridge.api.message.MessageFlag;
import canaryprism.discordbridge.kord.DiscordBridgeKord;
import dev.kord.common.entity.InteractionResponseType;
import dev.kord.common.entity.MessageFlagKt;
import dev.kord.common.entity.optional.Optional;
import dev.kord.common.entity.optional.OptionalBoolean;
import dev.kord.core.entity.interaction.ChatInputCommandInteraction;
import dev.kord.rest.json.request.InteractionApplicationCommandCallbackData;
import dev.kord.rest.json.request.InteractionResponseCreateRequest;
import kotlin.ResultKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ImmediateResponderImpl implements ImmediateResponder {
    
    public final DiscordBridgeKord bridge;
    public final ChatInputCommandInteraction interaction;
    
    public ImmediateResponderImpl(DiscordBridgeKord bridge, ChatInputCommandInteraction interaction) {
        this.bridge = bridge;
        this.interaction = interaction;
    }
    
    public String content;
    public Set<MessageFlag> flags;
    
    @Override
    public @NotNull CompletableFuture<?> respond() {
        var future = new CompletableFuture<>();
        interaction.getKord()
                .getRest()
                .getInteraction()
                .createInteractionResponse(interaction.getId(), interaction.getToken(), new InteractionResponseCreateRequest(
                        InteractionResponseType.ChannelMessageWithSource.INSTANCE,
                        Optional.Companion.invoke(new InteractionApplicationCommandCallbackData(
                                OptionalBoolean.Missing.INSTANCE,
                                Optional.Companion.invoke(content),
                                Optional.Companion.invoke(),
                                Optional.Companion.invoke(),
                                Optional.Companion.invoke(MessageFlagKt.MessageFlags(
                                        flags.stream()
                                                .map((e) -> ((dev.kord.common.entity.MessageFlag) bridge.getImplementationValue(e)))
                                                .toList())
                                ),
                                Optional.Companion.invoke(),
                                Optional.Companion.invoke()
                        ))
                ), new Continuation<>() {
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
    public @NotNull ImmediateResponder setContent(@NotNull String text) {
        this.content = text;
        return this;
    }
    
    @Override
    public @NotNull ImmediateResponder setFlags(EnumSet<MessageFlag> flags) {
        this.flags = flags.clone();
        return this;
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return interaction;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
}
