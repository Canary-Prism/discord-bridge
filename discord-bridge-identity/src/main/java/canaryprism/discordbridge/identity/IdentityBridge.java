/*
 *    Copyright 2025 Canary Prism <canaryprsn@gmail.com>
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

package canaryprism.discordbridge.identity;

import canaryprism.discordbridge.api.DiscordApi;
import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.DiscordBridgeApi;
import canaryprism.discordbridge.api.enums.DiscordBridgeEnum;
import canaryprism.discordbridge.api.enums.PartialSupport;
import canaryprism.discordbridge.api.enums.TypeValue;
import canaryprism.discordbridge.api.exceptions.UnsupportedImplementationException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.Set;

public final class IdentityBridge implements DiscordBridge {
    
    @Override
    public boolean canLoadApi(@NotNull Object o) {
        return false;
    }
    
    @Override
    public @NotNull DiscordApi loadApi(@NotNull Object api) {
        throw new UnsupportedImplementationException("discord-bridge-identity cannot load any object");
    }
    
    @Override
    public <T extends PartialSupport> @NotNull Set<? extends @NotNull T> getSupportedValues(Class<T> type) {
        return Set.of(type.getEnumConstants());
    }
    
    @Override
    public @NotNull Type getInternalTypeRepresentation(@NotNull TypeValue<?> value) {
        return value.getTypeRepresentation();
    }
    
    @Override
    public @NotNull Object getImplementationValue(@NotNull DiscordBridgeEnum value) {
        return value;
    }
    
    @Override
    public <T extends DiscordBridgeEnum> @NotNull T convertInternalObject(@NotNull Class<T> type, @NotNull Object value) {
        if (value.getClass() != type)
            throw new ClassCastException("can't convert %s to %s".formatted(value, type));
        return type.cast(value);
    }
    
    @Override
    public @NotNull Optional<? extends Class<?>> getImplementationType(@NotNull Class<? extends DiscordBridgeApi> type) {
        return Optional.of(type);
    }
    
    @Override
    public @NotNull String toString() {
        return "DiscordBridge Identity (nop) Implementation";
    }
}
