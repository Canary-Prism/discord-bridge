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

package canaryprism.discordbridge.discord4j.channel;

import canaryprism.discordbridge.api.channel.ServerChannel;
import canaryprism.discordbridge.api.server.Server;
import canaryprism.discordbridge.discord4j.DiscordBridgeDiscord4J;
import canaryprism.discordbridge.discord4j.server.ServerImpl;

import discord4j.core.object.entity.channel.GuildChannel;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ServerChannelImpl<T extends GuildChannel> extends ChannelImpl<T> implements ServerChannel {
    
    public final CompletableFuture<ServerImpl> server;
    
    public ServerChannelImpl(DiscordBridgeDiscord4J bridge, T channel) {
        super(bridge, channel);
        server = channel.getGuild()
                .map((e) -> new ServerImpl(
                        bridge,
                        e,
                        channel.getClient()
                                .rest(),
                        channel.getClient()
                                .rest()
                                .getApplicationId()
                                .toFuture()))
                .toFuture();
    }
    
    @Override
    public @NotNull Server getServer() {
        return server.join();
    }
    
    @Override
    public @NotNull CompletableFuture<Void> delete() {
        return channel.delete().toFuture();
    }
}
