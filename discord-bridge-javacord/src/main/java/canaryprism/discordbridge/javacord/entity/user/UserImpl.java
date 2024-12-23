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

package canaryprism.discordbridge.javacord.entity.user;

import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.entity.user.User;
import org.jetbrains.annotations.NotNull;

public record UserImpl(DiscordBridge bridge, org.javacord.api.entity.user.User user) implements User {
    
    @Override
    public long getId() {
        return user.getId();
    }
    
    @Override
    public @NotNull String getIdAsString() {
        return user.getIdAsString();
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return user;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
    
    @Override
    public @NotNull String getName() {
        return user.getName();
    }
}
