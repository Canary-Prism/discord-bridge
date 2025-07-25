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

package canaryprism.discordbridge.kord.interaction.slash;

import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.channel.MessageChannel;
import canaryprism.discordbridge.api.entity.user.User;
import canaryprism.discordbridge.api.interaction.response.FollowupResponder;
import canaryprism.discordbridge.api.interaction.response.ImmediateResponder;
import canaryprism.discordbridge.api.interaction.response.ResponseUpdater;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandInvokeInteraction;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandInteractionOption;
import canaryprism.discordbridge.api.server.Server;
import canaryprism.discordbridge.kord.DiscordBridgeKord;
import canaryprism.discordbridge.kord.channel.ChannelDirector;
import canaryprism.discordbridge.kord.entity.user.UserImpl;
import canaryprism.discordbridge.kord.interaction.response.FollowupResponderImpl;
import canaryprism.discordbridge.kord.interaction.response.ImmediateResponderImpl;
import canaryprism.discordbridge.kord.interaction.response.ResponseUpdaterImpl;
import canaryprism.discordbridge.kord.server.ServerImpl;
import dev.kord.common.entity.Snowflake;
import dev.kord.core.behavior.interaction.response.DeferredMessageInteractionResponseBehavior;
import dev.kord.core.entity.Guild;
import dev.kord.core.entity.interaction.ChatInputCommandInteraction;
import kotlin.ResultKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public record SlashCommandInvokeInteractionImpl(DiscordBridgeKord bridge, ChatInputCommandInteraction interaction) implements SlashCommandInvokeInteraction {
    
    @Override
    public long getApplicationId() {
        return Long.parseLong(interaction.getApplicationId().toString());
    }
    
    @Override
    public long getCommandId() {
        return Long.parseLong(interaction.getInvokedCommandId().toString());
    }
    
    @Override
    public @NotNull String getCommandName() {
        return interaction.getInvokedCommandName();
    }
    
    @Override
    public @NotNull Optional<Long> getServerCommandServerId() {
        return Optional.ofNullable(interaction.getInvokedCommandGuildId())
                .map(Snowflake::toString)
                .map(Long::parseLong);
    }
    
    @Override
    public @NotNull ImmediateResponder createImmediateResponder() {
        return new ImmediateResponderImpl(bridge, interaction);
    }
    
    @Override
    public @NotNull CompletableFuture<? extends @NotNull ResponseUpdater> respondLater(boolean ephemeral) {
        var future = new CompletableFuture<DeferredMessageInteractionResponseBehavior>();
        var continuation = new Continuation<DeferredMessageInteractionResponseBehavior>() {
            
            @Override
            public @NotNull CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }
            
            @Override
            public void resumeWith(@NotNull Object o) {
                future.complete(((DeferredMessageInteractionResponseBehavior) o));
            }
        };
        
        if (ephemeral)
            interaction.deferEphemeralResponse(continuation);
        else
            interaction.deferPublicResponse(continuation);
        
        return future.thenApply((e) -> new ResponseUpdaterImpl(bridge, e));
    }
    
    @Override
    public @NotNull FollowupResponder createFollowupResponder() {
        return new FollowupResponderImpl(bridge, interaction);
    }
    
    @Override
    public @NotNull User getUser() {
        return new UserImpl(bridge, interaction.getUser());
    }
    
    @Override
    public @NotNull Optional<? extends Server> getServer() {
        return Optional.ofNullable(interaction.getData().getGuildId().getValue())
                .map((e) -> {
                    var future = new CompletableFuture<ServerImpl>();
                    interaction.getKord()
                            .getGuild(e, interaction.getKord().getResources().getDefaultStrategy(), new Continuation<>() {
                                @Override
                                public @NotNull CoroutineContext getContext() {
                                    return EmptyCoroutineContext.INSTANCE;
                                }
                                
                                @Override
                                public void resumeWith(@NotNull Object o) {
                                    try {
                                        ResultKt.throwOnFailure(o);
                                        future.complete(new ServerImpl(bridge, ((Guild) o), interaction.getKord()));
                                    } catch (Throwable t) {
                                        future.completeExceptionally(t);
                                    }
                                    
                                }
                            });
                    return future.join();
                });
    }
    
    @Override
    public @NotNull Optional<? extends MessageChannel> getChannel() {
        var future = new CompletableFuture<MessageChannel>();
        
        interaction.getChannel(new Continuation<>() {
            @Override
            public @NotNull CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }
            
            @Override
            public void resumeWith(@NotNull Object o) {
                try {
                    ResultKt.throwOnFailure(o);
                    future.complete(ChannelDirector.wrapChannel(bridge, ((dev.kord.core.entity.channel.MessageChannel) o)));
                } catch (Throwable t) {
                    future.completeExceptionally(t);
                }
            }
        });
        
        return Optional.of(future.join());
    }
    
    @Override
    public @NotNull @Unmodifiable List<? extends @NotNull SlashCommandInteractionOption> getOptions() {
        return Optional.ofNullable(interaction.getCommand()
                .getData()
                .getOptions()
                .getValue())
                .orElse(List.of())
                .stream()
                .map((e) -> new SlashCommandInteractionOptionOptionDataImpl(bridge, e, interaction.getCommand().getOptions()))
                .toList();
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
