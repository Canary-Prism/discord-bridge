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

package canaryprism.discordbridge.kord;

import canaryprism.commons.event.EventListenerList;
import canaryprism.discordbridge.api.DiscordApi;
import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.data.interaction.CommandData;
import canaryprism.discordbridge.api.data.interaction.slash.SlashCommandData;
import canaryprism.discordbridge.api.interaction.slash.SlashCommand;
import canaryprism.discordbridge.api.listener.ApiAttachableListener;
import canaryprism.discordbridge.api.listener.interaction.SlashCommandAutocompleteListener;
import canaryprism.discordbridge.api.listener.interaction.SlashCommandInvokeListener;
import canaryprism.discordbridge.api.server.Server;
import canaryprism.discordbridge.kord.event.interaction.SlashCommandAutocompleteEventImpl;
import canaryprism.discordbridge.kord.event.interaction.SlashCommandInvokeEventImpl;
import canaryprism.discordbridge.kord.interaction.slash.SlashCommandImpl;
import canaryprism.discordbridge.kord.server.ServerImpl;
import dev.kord.common.entity.DiscordApplicationCommand;
import dev.kord.core.Kord;
import dev.kord.core.event.interaction.AutoCompleteInteractionCreateEvent;
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public record DiscordApiImpl(DiscordBridgeKord bridge, Kord kord, EventListenerList<ApiAttachableListener> listener_list) implements DiscordApi {
    
    private static final Logger logger = LoggerFactory.getLogger(DiscordApiImpl.class);
    
    public DiscordApiImpl(DiscordBridgeKord bridge, Kord kord) {
        this(bridge, kord, new EventListenerList<>());
        DiscordBridgeKord.on(kord, ChatInputCommandInteractionCreateEvent.class, (e) -> {
            for (var listener : listener_list.getListeners(SlashCommandInvokeListener.class))
                listener.onSlashCommandInvoke(new SlashCommandInvokeEventImpl(bridge, e));
        }, logger);
        DiscordBridgeKord.on(kord, AutoCompleteInteractionCreateEvent.class, (e) -> {
            for (var listener : listener_list.getListeners(SlashCommandAutocompleteListener.class))
                listener.onSlashCommandAutocomplete(new SlashCommandAutocompleteEventImpl(bridge, e));
        }, logger);
    }
    
    @Override
    public @NotNull CompletableFuture<? extends @NotNull @Unmodifiable Set<? extends @NotNull SlashCommand>> getGlobalSlashCommands() {
        
        var future = new CompletableFuture<Set<? extends SlashCommand>>();
        
        kord.getRest()
                .getInteraction()
                .getGlobalApplicationCommands(kord.getSelfId(), true, new Continuation<>() {
                    @Override
                    public @NotNull CoroutineContext getContext() {
                        return EmptyCoroutineContext.INSTANCE;
                    }
                    
                    @Override
                    public void resumeWith(@NotNull Object o) {
                        @SuppressWarnings("unchecked") var list = ((List<DiscordApplicationCommand>) o);
                        
                        future.complete(list.stream()
                                .map((e) -> new SlashCommandImpl(bridge, e, kord))
                                .collect(Collectors.toUnmodifiableSet()));
                    }
                });
            
        return future;
    }
    
    @Override
    public @NotNull CompletableFuture<? extends @NotNull @Unmodifiable Set<? extends @NotNull SlashCommand>> bulkUpdateGlobalCommands(@NotNull Set<? extends @NotNull CommandData> commands) {
        var future = new CompletableFuture<Set<SlashCommandImpl>>();
        kord.getRest()
                .getInteraction()
                .createGlobalApplicationCommands(
                        kord.getSelfId(),
                        commands.stream()
                                .map(SlashCommandData.class::cast)
                                .map(bridge::convertData)
                                .toList(),
                        new Continuation<>() {
                            @Override
                            public @NotNull CoroutineContext getContext() {
                                return EmptyCoroutineContext.INSTANCE;
                            }
                            
                            @SuppressWarnings("unchecked")
                            @Override
                            public void resumeWith(@NotNull Object o) {
                                var list = ((List<DiscordApplicationCommand>) o);
                                
                                future.complete(list.stream()
                                        .map((e) -> new SlashCommandImpl(bridge, e, kord))
                                        .collect(Collectors.toUnmodifiableSet()));
                            }
                        });
        return future;
    }
    
    @Override
    public @NotNull @Unmodifiable Set<? extends Server> getServers() {
        var future = new CompletableFuture<Set<? extends Server>>();
        var set = new HashSet<ServerImpl>();
        kord.getGuilds()
                .collect((e, c) -> {
                    set.add(new ServerImpl(bridge, e, kord));
                    c.resumeWith(Unit.INSTANCE);
                    return Unit.INSTANCE;
                }, new Continuation<>() {
                    @Override
                    public @NotNull CoroutineContext getContext() {
                        return EmptyCoroutineContext.INSTANCE;
                    }
                    
                    @Override
                    public void resumeWith(@NotNull Object o) {
                        future.complete(set);
                    }
                });
        
        return future.join();
    }
    
    @Override
    public <T extends ApiAttachableListener> void addListener(@NotNull Class<T> type, @NotNull T listener) {
        listener_list.addListener(type, listener);
    }
    
    @Override
    public <T extends ApiAttachableListener> void removeListener(@NotNull Class<T> type, @NotNull T listener) {
        listener_list.removeListener(type, listener);
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return kord;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
}
