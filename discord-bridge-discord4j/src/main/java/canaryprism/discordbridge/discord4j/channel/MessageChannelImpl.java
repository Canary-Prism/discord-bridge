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

import canaryprism.discordbridge.api.channel.MessageChannel;
import canaryprism.discordbridge.discord4j.DiscordBridgeDiscord4J;

public class MessageChannelImpl<T extends discord4j.core.object.entity.channel.MessageChannel> extends ChannelImpl<T> implements MessageChannel {
    
    public MessageChannelImpl(DiscordBridgeDiscord4J bridge, T channel) {
        super(bridge, channel);
    }
}