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

import canaryprism.discordbridge.api.TypeValue;
import canaryprism.discordbridge.api.PartialSupport;

/// Represents a type of Discord Channel
public enum ChannelType implements PartialSupport, TypeValue {
    PRIVATE,
    GROUP, // unused as bots can't join group chats
    SERVER_TEXT,
    SERVER_VOICE,
    SERVER_CATEGORY,
    SERVER_NEWS,
    SERVER_STAGE,
    SERVER_THREAD_NEWS,
    SERVER_THREAD_PUBLIC,
    SERVER_THREAD_PRIVATE,
    SERVER_FORUM,
    SERVER_MEDIA,
    
    UNKNOWN
}
