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

import canaryprism.discordbridge.api.channel.Channel;
import canaryprism.discordbridge.discord4j.DiscordBridgeDiscord4J;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ChannelDirector {
    private ChannelDirector() {}
    
    @SuppressWarnings("unchecked")
    public static <T extends Channel> T wrapChannel(DiscordBridgeDiscord4J bridge, discord4j.core.object.entity.channel.Channel channel) {
        var compatible = Stream.of(ChannelImpl.class, MessageChannelImpl.class, ServerChannelImpl.class, ServerMessageChannelImpl.class)
                .map((e) -> e.getConstructors()[0])
                .filter((e) -> e.getParameters()[1].getType().isInstance(channel))
                .collect(Collectors.toSet());
        
        try {
            return ((T) compatible.stream()
                    .max(Comparator.comparing((constructor) ->
                            compatible.stream()
                                    .filter((e) -> e.getParameters()[1]
                                            .getType()
                                            .isAssignableFrom(constructor.getParameters()[1]
                                                    .getType()))
                                    .count()
                    ))
                    .orElseThrow()
                    .newInstance(bridge, channel));
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
