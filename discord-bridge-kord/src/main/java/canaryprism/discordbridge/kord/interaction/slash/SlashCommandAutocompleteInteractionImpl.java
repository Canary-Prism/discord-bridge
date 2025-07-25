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
import canaryprism.discordbridge.api.data.interaction.slash.SlashCommandOptionChoiceData;
import canaryprism.discordbridge.api.entity.user.User;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandAutocompleteInteraction;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandInteractionOption;
import canaryprism.discordbridge.api.server.Server;
import canaryprism.discordbridge.kord.DiscordBridgeKord;
import canaryprism.discordbridge.kord.channel.ChannelDirector;
import canaryprism.discordbridge.kord.entity.user.UserImpl;
import canaryprism.discordbridge.kord.server.ServerImpl;
import dev.kord.core.behavior.interaction.AutoCompleteInteractionBehaviorKt;
import dev.kord.core.entity.Guild;
import dev.kord.core.entity.interaction.AutoCompleteInteraction;
import kotlin.ResultKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public record SlashCommandAutocompleteInteractionImpl(DiscordBridgeKord bridge, AutoCompleteInteraction interaction) implements SlashCommandAutocompleteInteraction {
    
    @Override
    public @NotNull CompletableFuture<?> suggest(@NotNull List<? extends @NotNull SlashCommandOptionChoiceData> choices) {
        var future = new CompletableFuture<>();
        AutoCompleteInteractionBehaviorKt.suggest(
                interaction,
                choices.stream()
                        .map(bridge::convertData)
                        .toList(),
                new Continuation<>() {
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
    public long getApplicationId() {
        return Long.parseLong(interaction.getApplicationId().toString());
    }
    
    @Override
    public long getCommandId() {
        return Long.parseLong(interaction.getId().toString());
    }
    
    @Override
    public @NotNull String getCommandName() {
        return interaction.getCommand().getRootName();
    }
    
    @Override
    public @NotNull Optional<Long> getServerCommandServerId() {
        throw new UnsupportedOperationException("server command server id inaccessible from autocomplete interaction");
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
