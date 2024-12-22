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

package canaryprism.discordbridge.api.interaction;

import canaryprism.discordbridge.api.enums.DiscordBridgeEnum;
import canaryprism.discordbridge.api.enums.PartialSupport;

/// Context of the use of an Application Command
public enum ContextType implements DiscordBridgeEnum, PartialSupport {
    /// Unknown Context
    UNKNOWN,
    
    /// Use of a command in a server
    SERVER,
    
    /// Use of a command in a DM with the command's bot
    BOT_DM,
    
    /// Use of a command in a DM unrelated with the command's bot
    OTHER_DM
}
