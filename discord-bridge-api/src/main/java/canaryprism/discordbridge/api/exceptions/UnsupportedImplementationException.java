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

/// Exception to indicate that a suitable implementation of [canaryprism.discordbridge.api.DiscordBridge] couldn't be found
public class UnsupportedImplementationException extends IllegalArgumentException {
    
    /// Creates a new UnsupportedImplementationException with the specified message
    ///
    /// @param message the message
    public UnsupportedImplementationException(String message) {
        super(message);
    }
}
