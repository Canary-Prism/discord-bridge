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
    UNKNOWN,
    
    /// Private Channels (DMs)
    PRIVATE,
    
    /// Group Channel (Group Chats)
    ///
    /// Unused as bots can't join group chats
    GROUP,
    
    /// Server Text Channel
    SERVER_TEXT,
    
    /// Server Voice Channel
    SERVER_VOICE,
    
    /// Channel Category (Server only)
    SERVER_CATEGORY,
    
    /// Server Announcement Channel
    SERVER_NEWS,
    
    /// Server Stage Channel
    SERVER_STAGE,
    
    /// Server Announcement Channel Thread
    SERVER_THREAD_NEWS,
    
    /// Server Public Channel Thread
    SERVER_THREAD_PUBLIC,
    
    /// Server Private Channel Thread
    SERVER_THREAD_PRIVATE,
    
    /// Server Forum Channel
    SERVER_FORUM,
    
    /// Server Media Channel
    SERVER_MEDIA,
    
    /// Server Shop Channel
    SERVER_SHOP,
    
    /// Server Directory Channel
    SERVER_DIRECTORY,
    
    ;
    
    
    
    /// Subtypes not implemented yet
    ///
    /// @return Channel.class
    @Override
    public @NotNull Class<Channel> getTypeRepresentation() {
        return Channel.class;
    }
}
