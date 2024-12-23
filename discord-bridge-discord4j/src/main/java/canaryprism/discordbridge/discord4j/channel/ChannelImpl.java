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

import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.channel.Channel;
import canaryprism.discordbridge.api.channel.ChannelType;
import canaryprism.discordbridge.discord4j.DiscordBridgeDiscord4J;
import org.jetbrains.annotations.NotNull;

public class ChannelImpl<T extends discord4j.core.object.entity.channel.Channel> implements Channel {
    
    public final DiscordBridgeDiscord4J bridge;
    
    public final T channel;
    
    public ChannelImpl(DiscordBridgeDiscord4J bridge, T channel) {
        this.bridge = bridge;
        this.channel = channel;
    }
    
    @Override
    public @NotNull ChannelType getType() {
        return bridge.convertInternalObject(ChannelType.class, channel.getType());
    }
    
    @Override
    public @NotNull String getIdAsString() {
        return channel.getId().asString();
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return channel;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
}
