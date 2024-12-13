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

import canaryprism.discordbridge.api.exceptions.UnsupportedImplementationException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/// # The main interface for this library
///
/// this interface is a service that all implementations of discord-bridge must provide
///
/// all implementations for all interfaces are accessible from this point
public interface DiscordBridge {
    
    /// Tests if an implementation of [DiscordBridge] can load this object as its implementation's api object
    /// and present it as a [DiscordApi] object
    ///
    /// if this returns `true` then [#load(java.lang.Object)] **MUST NOT FAIL**
    ///
    /// @param o the object to test, not null
    /// @return whether this object can be loaded
    boolean canLoadApi(@NotNull Object o);
    
    /// Loads the provided object to a [DiscordApi] instance
    ///
    /// if the object was tested with [#canLoadApi(java.lang.Object)] and returned `true` this **MUST NOT FAIL**
    ///
    /// otherwise a failure should result in a [UnsupportedImplementationException] being thrown
    ///
    /// @param api the object to load, not null
    /// @return the [DiscordApi] to wrap around the provided `api`
    /// @throws UnsupportedImplementationException if the load fails
    @NotNull DiscordApi loadApi(@NotNull Object api);
    
    /// Gets the supported values of the provided enum for this implementation of discord-bridge
    ///
    /// @param type the runtime class of the enum to get
    /// @param <T> the type of the enum to get
    /// @return an unmodifiable set of the supported values
    <T extends PartialSupport> @NotNull Set<? extends @NotNull T> getSupportedValues(Class<T> type);
    
    /// Gets the internal type that can be used to best represent the given enum value
    ///
    /// @param value the value to get the type representation of
    /// @return the type that best represents the given value
    @NotNull Type getInternalTypeRepresentation(TypeValue value);
    
    
    
    private static @NotNull ServiceLoader<DiscordBridge> getServiceLoader() {
        class Holder {
            static final ServiceLoader<DiscordBridge> LOADER = ServiceLoader.load(DiscordBridge.class);
        }
        return Holder.LOADER;
    }
    
    private static Stream<ServiceLoader.Provider<DiscordBridge>> getCanLoad(@NotNull Object o) {
        return getServiceLoader()
                .stream()
                .filter((e) -> e.get().canLoadApi(o));
    }
    
    /// Attempts to load the provided object and wrap it to a [DiscordApi] object,
    /// using any [DiscordBridge] implementation that can load the object
    ///
    /// while it shouldn't happen, if two implementations happen to be able to load any given object, no guarantees are given for the specific implementation which will be used
    ///
    /// @param api the api to load, not null
    /// @return the [DiscordApi] to wrap around the provided `api`
    /// @throws UnsupportedImplementationException if no implementations of [DiscordBridge] could load the object
    /// @throws NullPointerException if `api` is null
    static @NotNull DiscordApi load(@NotNull Object api) {
        Objects.requireNonNull(api, "api can't be null");
        return getCanLoad(api)
                .findAny()
                .orElseThrow(() -> new UnsupportedImplementationException(String.format(
                        "Object %s is not a supported Discord API object", api)))
                .get()
                .loadApi(api);

    }
    
    /// Attempts to load the provided object and wrap it to a [DiscordApi] object,
    /// using any [DiscordBridge] implementation that can load the object
    ///
    /// this differs from [#load(java.lang.Object)] in that
    /// in the unlikely situation that multiple [DiscordBridge] implementations can load the provided object
    /// a [IllegalStateException] is thrown instead
    ///
    /// unlike [#load(java.lang.Object)] this also must load every implementation available to be exact,
    /// instead of short-circuiting when it finds any suitable implementation
    ///
    /// @param api the api to load, not null
    /// @return the [DiscordApi] to wrap around the provided `api`
    /// @throws UnsupportedImplementationException if no implementations of [DiscordBridge] could load the object
    /// @throws NullPointerException if `api` is null
    /// @throws IllegalStateException if multiple [DiscordBridge] implementations can load the provided object
    static @NotNull DiscordApi loadExact(@NotNull Object api) {
        Objects.requireNonNull(api, "api can't be null");
        var providers = getCanLoad(api).collect(Collectors.toSet());
        if (providers.size() > 1)
            throw new IllegalStateException(String.format("multiple eligible implementations found for loading object %s", api));
        
        return providers.stream()
                .findAny()
                .orElseThrow(() -> new UnsupportedImplementationException(String.format(
                        "Object %s is not a supported Discord API object", api)))
                .get()
                .loadApi(api);
    }
}
