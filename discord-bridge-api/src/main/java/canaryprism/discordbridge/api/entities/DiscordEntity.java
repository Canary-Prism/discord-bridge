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

package canaryprism.discordbridge.api.entities;

import canaryprism.discordbridge.api.DiscordBridgeApi;
import org.jetbrains.annotations.NotNull;

/// Represents an entity in Discord. An entity has a Snowflake ID
public interface DiscordEntity extends DiscordBridgeApi {
    
    /// Gets the ID of this entity as a long
    ///
    /// @return the ID of this entity as a long
    /// @implNote The default implementation just parses [#getIdAsString()] to a long
    default long getId() {
        return Long.parseLong(getIdAsString());
    }
    
    /// Gets the ID of this entity as a String
    ///
    /// @return the ID of this entity as a String
    @NotNull String getIdAsString();
}
