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

package canaryprism.discordbridge.api.exceptions;

import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.DiscordBridgeApi;

/// Thrown when an implementation receives or somehow ends up with an object from a different implementation
public class ImplementationMismatchException extends RuntimeException {
    
    /// Creates a new ImplementationMismatchException with the given parameters
    ///
    /// @param bridge the current supposed DiscordBridge implementation
    /// @param value the value with the unexpected DiscordBridge implementation
    public ImplementationMismatchException(DiscordBridge bridge, DiscordBridgeApi value) {
        super(String.format("Value %s belongs to DiscordBridge implementation %s but appeared in implementation %s!",
                value, value.getBridge(), bridge));
    }
}
