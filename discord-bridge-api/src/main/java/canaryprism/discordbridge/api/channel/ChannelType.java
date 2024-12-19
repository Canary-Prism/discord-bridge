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

package canaryprism.discordbridge.api.channel;

import canaryprism.discordbridge.api.enums.DiscordBridgeEnum;
import canaryprism.discordbridge.api.enums.PartialSupport;
import canaryprism.discordbridge.api.enums.TypeValue;
import org.jetbrains.annotations.NotNull;

/// Represents a type of Discord Channel
public enum ChannelType implements PartialSupport, TypeValue<Class<? extends Channel>>, DiscordBridgeEnum {
    
    /// Unknown Channel
    UNKNOWN(Channel.class),
    
    /// Private Channels (DMs)
    PRIVATE(TextChannel.class),
    
    /// Group Channel (Group Chats)
    ///
    /// Unused as bots can't join group chats
    GROUP(TextChannel.class),
    
    /// Server Text Channel
    SERVER_TEXT(ServerTextChannel.class),
    
    /// Server Voice Channel
    SERVER_VOICE(ServerChannel.class),
    
    /// Channel Category (Server only)
    SERVER_CATEGORY(ServerChannel.class),
    
    /// Server Announcement Channel
    SERVER_NEWS(ServerTextChannel.class),
    
    /// Server Stage Channel
    SERVER_STAGE(ServerChannel.class),
    
    /// Server Announcement Channel Thread
    SERVER_THREAD_NEWS(ServerTextChannel.class),
    
    /// Server Public Channel Thread
    SERVER_THREAD_PUBLIC(ServerTextChannel.class),
    
    /// Server Private Channel Thread
    SERVER_THREAD_PRIVATE(ServerTextChannel.class),
    
    /// Server Forum Channel
    SERVER_FORUM(ServerChannel.class),
    
    /// Server Media Channel
    SERVER_MEDIA(ServerChannel.class),
    
    /// Server Shop Channel
    SERVER_SHOP(ServerChannel.class),
    
    /// Server Directory Channel
    SERVER_DIRECTORY(ServerChannel.class),
    
    ;
    
    public final Class<? extends Channel> type_representation;
    
    ChannelType(Class<? extends Channel> type_representation) {
        this.type_representation = type_representation;
    }
    
    /// Subtypes not implemented yet
    ///
    /// @return Channel.class
    @Override
    public @NotNull Class<? extends Channel> getTypeRepresentation() {
        return this.type_representation;
    }
}
