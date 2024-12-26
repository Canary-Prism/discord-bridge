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

package canaryprism.discordbridge.kord.channel;

import canaryprism.discordbridge.api.channel.ServerChannel;
import canaryprism.discordbridge.api.server.Server;
import canaryprism.discordbridge.kord.DiscordBridgeKord;
import canaryprism.discordbridge.kord.server.ServerImpl;
import dev.kord.core.entity.Guild;
import dev.kord.core.entity.channel.GuildChannel;
import kotlin.ResultKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ServerChannelImpl<T extends GuildChannel> extends ChannelImpl<T> implements ServerChannel {
    
    public ServerChannelImpl(DiscordBridgeKord bridge, T channel) {
        super(bridge, channel);
    }
    
    @Override
    public @NotNull Server getServer() {
        var future = new CompletableFuture<ServerImpl>();
        
        channel.getGuild(new Continuation<>() {
            
            @Override
            public @NotNull CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }
            
            @Override
            public void resumeWith(@NotNull Object o) {
                try {
                    ResultKt.throwOnFailure(o);
                    future.complete(new ServerImpl(bridge, ((Guild) o), channel.getKord()));
                } catch (Throwable t) {
                    future.completeExceptionally(t);
                }
            }
        });
        
        return future.join();
    }
    
    @Override
    public @NotNull CompletableFuture<Void> delete() {
        var future = new CompletableFuture<Void>();
        channel.delete(null, new Continuation<>() {
            @Override
            public @NotNull CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }
            
            @Override
            public void resumeWith(@NotNull Object o) {
                try {
                    ResultKt.throwOnFailure(o);
                    future.complete(null);
                } catch (Throwable t) {
                    future.completeExceptionally(t);
                }
            }
        });
        
        return future;
    }
}
