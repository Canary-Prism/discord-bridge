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

import canaryprism.discordbridge.api.DiscordApi;
import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.data.interaction.CommandData;
import canaryprism.discordbridge.api.interaction.slash.SlashCommand;
import canaryprism.discordbridge.api.listener.ApiAttachableListener;
import canaryprism.discordbridge.api.server.Server;
import canaryprism.discordbridge.kord.interaction.slash.SlashCommandImpl;
import dev.kord.common.entity.DiscordApplication;
import dev.kord.core.Kord;
import dev.kord.core.entity.application.ChatInputCommandCommand;
import io.ktor.client.utils.CoroutineDispatcherUtilsKt;
import kotlin.Result;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineContextKt;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.flow.FlowKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public record DiscordApiImpl(DiscordBridgeKord bridge, Kord kord) implements DiscordApi {
    
    @Override
    public @NotNull CompletableFuture<? extends @NotNull @Unmodifiable Set<? extends @NotNull SlashCommand>> getGlobalSlashCommands() {

        var set = new HashSet<SlashCommand>();
        
        var future = new CompletableFuture<Set<? extends SlashCommand>>();
        
        kord.getGlobalApplicationCommands(true)
                .collect((e, c) -> {
                    if (e instanceof ChatInputCommandCommand command)
                        set.add(new SlashCommandImpl(bridge, command));
                    
                    c.resumeWith(Unit.INSTANCE);
                    return null;
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
            
        return future;
    }
    
    @Override
    @NotNull CompletableFuture<? extends @NotNull @Unmodifiable Set<? extends @NotNull SlashCommand>> bulkUpdateGlobalCommands(@NotNull Set<? extends @NotNull CommandData> commands) {
        var future = new CompletableFuture<>();
        kord.getRest()
                .getApplication()
                .getCurrentApplicationInfo(new Continuation<DiscordApplication>() {
                    @Override
                    public @NotNull CoroutineContext getContext() {
                        return EmptyCoroutineContext.INSTANCE;
                    }
                    
                    @Override
                    public void resumeWith(@NotNull Object o) {
                        var result = (DiscordApplication) o;
                        Result
                        result
                    }
                });
    }
    
    @Override
    @NotNull
    @Unmodifiable
    Set<? extends Server> getServers() {
        return Set.of();
    }
    
    @Override
    <T extends ApiAttachableListener> void addListener(@NotNull Class<T> type, @NotNull T listener) {
    
    }
    
    @Override
    <T extends ApiAttachableListener> void removeListener(@NotNull Class<T> type, @NotNull T listener) {
    
    }
    
    @Override
    @NotNull Object getImplementation() {
        return null;
    }
    
    @Override
    @NotNull DiscordBridge getBridge() {
        return null;
    }
}
