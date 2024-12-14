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

package canaryprism.discordbridge.api.message;

import canaryprism.discordbridge.api.enums.DiscordBridgeEnum;

/// Flags for messages (incomplete)
public enum MessageFlag implements DiscordBridgeEnum {
    
    /// Unknown Flag
    UKNOWN,
    
    /// Ephemeral Messages are only allowed on interaction responses
    /// and make it so that the message is only sent to the user who caused the interaction
    ///
    /// The message will also not be kept in Discord's servers and the message will be lost forever if
    /// the user's client forgets the message, for example if the user dismisses the message or
    /// refreshes the client
    EPHEMERAL,
    
    /// Silent Messages will not send any notifications to any users under any circumstances
    /// regardless of the amount of pings on the message or any user's notification settings
    SILENT
}
