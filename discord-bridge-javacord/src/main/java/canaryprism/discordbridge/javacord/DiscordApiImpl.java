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

package canaryprism.discordbridge.javacord;

import canaryprism.discordbridge.api.DiscordApi;
import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.data.interaction.CommandData;
import canaryprism.discordbridge.api.data.interaction.slash.SlashCommandData;
import canaryprism.discordbridge.api.interaction.slash.SlashCommand;
import canaryprism.discordbridge.api.listener.ApiAttachableListener;
import canaryprism.discordbridge.api.listener.interaction.SlashCommandAutocompleteListener;
import canaryprism.discordbridge.api.listener.interaction.SlashCommandInvokeListener;
import canaryprism.discordbridge.api.server.Server;
import canaryprism.discordbridge.javacord.interaction.slash.SlashCommandImpl;
import canaryprism.discordbridge.javacord.listener.interaction.AutocompleteCreateListenerDelegate;
import canaryprism.discordbridge.javacord.listener.interaction.SlashCommandCreateListenerDelegate;
import canaryprism.discordbridge.javacord.server.ServerImpl;
import org.javacord.api.listener.GloballyAttachableListener;
import org.javacord.api.listener.interaction.AutocompleteCreateListener;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public record DiscordApiImpl(DiscordBridgeJavacord bridge, org.javacord.api.DiscordApi api) implements DiscordApi {
    
    @Override
    public @NotNull CompletableFuture<? extends @NotNull Set<? extends @NotNull SlashCommand>> getGlobalSlashCommands() {
        return api.getGlobalSlashCommands()
                .thenApply((set) -> set.stream()
                        .map((e) -> new SlashCommandImpl(bridge, e))
                        .collect(Collectors.toUnmodifiableSet()));
    }
    
    @Override
    public @NotNull CompletableFuture<? extends @NotNull Set<? extends @NotNull SlashCommand>> bulkUpdateGlobalCommands(@NotNull Set<? extends @NotNull CommandData> commands) {
        return api.bulkOverwriteGlobalApplicationCommands(
                        commands.stream()
                                .map(SlashCommandData.class::cast)
                                .map(bridge::convertData)
                                .collect(Collectors.toUnmodifiableSet()))
                .thenApply((set) ->
                        set.stream()
                                .map(org.javacord.api.interaction.SlashCommand.class::cast)
                                .map((e) -> new SlashCommandImpl(bridge, e))
                                .collect(Collectors.toUnmodifiableSet()));
    }
    
    @Override
    public @NotNull Set<? extends Server> getServers() {
        return api.getServers()
                .stream()
                .map((e) -> new ServerImpl(bridge, e))
                .collect(Collectors.toUnmodifiableSet());
    }
    
    private static final Map<ApiAttachableListener, GloballyAttachableListener> listener_delegate_map = new WeakHashMap<>();
    
    @SuppressWarnings("unchecked")
    private static <T extends ApiAttachableListener, R extends GloballyAttachableListener> R delegate(T listener, Function<? super T, ? extends R> delegate_function) {
        if (listener_delegate_map.containsKey(listener))
            return ((R) listener_delegate_map.get(listener));
        var delegate = delegate_function.apply(listener);
        listener_delegate_map.put(listener, delegate);
        return delegate;
    }
    
    @Override
    public <T extends ApiAttachableListener> void addListener(@NotNull Class<T> type, @NotNull T listener) {
        if (type == SlashCommandInvokeListener.class)
            api.addSlashCommandCreateListener(delegate(((SlashCommandInvokeListener) listener),
                    (e) -> new SlashCommandCreateListenerDelegate(bridge, e)));
        else if (type == SlashCommandAutocompleteListener.class)
            api.addAutocompleteCreateListener(delegate(((SlashCommandAutocompleteListener) listener),
                    (e) -> new AutocompleteCreateListenerDelegate(bridge, e)));
        else
            throw new UnsupportedOperationException(String.format("unsupported listener type %s", type));
    }
    
    @Override
    public <T extends ApiAttachableListener> void removeListener(@NotNull Class<T> type, @NotNull T listener) {
        if (listener_delegate_map.containsKey(listener)) {
            if (type == SlashCommandInvokeListener.class)
                api.removeListener(SlashCommandCreateListener.class, ((SlashCommandCreateListener) listener_delegate_map.get(listener)));
            else if (type == SlashCommandAutocompleteListener.class)
                api.removeListener(AutocompleteCreateListener.class, ((AutocompleteCreateListener) listener_delegate_map.get(listener)));
            else
                throw new UnsupportedOperationException(String.format("unsupported listener type %s", type));
        }
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return api;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
}
