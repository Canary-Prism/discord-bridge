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

package canaryprism.discordbridge.api;

import org.jetbrains.annotations.NotNull;

/// Base interface for all Discord api interfaces in this library
public interface DiscordBridgeApi {
    /// Gets the internal implementation backing this object, this should never return null
    ///
    /// This library is meant as a common api to interface with other Discord api wrappers, and this method returns the actual object backing the bridge implementation
    ///
    /// For example, calling [DiscordApi]'s `getImplementation()` may return a `org.javacord.api.DiscordApi` or a `net.dev8tion.jda.api.JDA`
    ///
    /// @return the internal implementation backing this object
    @NotNull Object getImplementation();
    
    /// Gets the [DiscordBridge] implementation this object belongs to
    ///
    /// @return the [DiscordBridge] implementation this object belongs to
    @NotNull DiscordBridge getBridge();
}
