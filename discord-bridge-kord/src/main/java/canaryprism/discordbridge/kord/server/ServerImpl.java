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

package canaryprism.discordbridge.kord.server;

import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.data.interaction.CommandData;
import canaryprism.discordbridge.api.data.interaction.slash.SlashCommandData;
import canaryprism.discordbridge.api.interaction.Command;
import canaryprism.discordbridge.api.interaction.slash.SlashCommand;
import canaryprism.discordbridge.api.server.Server;
import canaryprism.discordbridge.kord.DiscordBridgeKord;
import canaryprism.discordbridge.kord.interaction.slash.SlashCommandImpl;
import dev.kord.common.entity.DiscordApplicationCommand;
import dev.kord.core.Kord;
import dev.kord.core.entity.Guild;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public record ServerImpl(DiscordBridgeKord bridge, Guild server, Kord kord) implements Server {
    
    @Override
    public @NotNull CompletableFuture<? extends @NotNull @Unmodifiable Set<? extends @NotNull SlashCommand>> getServerSlashCommands() {
        var future = new CompletableFuture<Set<SlashCommandImpl>>();
        kord.getRest()
                .getInteraction()
                .getGuildApplicationCommands(kord.getSelfId(), server.getId(), true, new Continuation<>() {
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
    public @NotNull CompletableFuture<? extends @NotNull @Unmodifiable Set<? extends @NotNull Command>> bulkUpdateServerCommands(Set<? extends @NotNull CommandData> commands) {
        var future = new CompletableFuture<Set<SlashCommandImpl>>();
        kord.getRest()
                .getInteraction()
                .createGuildApplicationCommands(
                        kord.getSelfId(),
                        server.getId(),
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
    public @NotNull String getIdAsString() {
        return server.getId().toString();
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return server;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
    
    @Override
    public @NotNull String getName() {
        return server.getName();
    }
}
