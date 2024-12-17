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

package canaryprism.discordbridge.jda;

import canaryprism.discordbridge.api.DiscordApi;
import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.data.interaction.slash.SlashCommandData;
import canaryprism.discordbridge.api.interaction.Command;
import canaryprism.discordbridge.api.interaction.slash.SlashCommand;
import canaryprism.discordbridge.api.listener.ApiAttachableListener;
import canaryprism.discordbridge.api.listener.interaction.SlashCommandInvokeListener;
import canaryprism.discordbridge.api.server.Server;
import canaryprism.discordbridge.jda.interaction.slash.SlashCommandImpl;
import canaryprism.discordbridge.jda.listener.interaction.SlashCommandInteractionEventListenerDelegate;
import canaryprism.discordbridge.jda.server.ServerImpl;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public record DiscordApiImpl(DiscordBridgeJDA bridge, JDA jda) implements DiscordApi {
    
    @Override
    public @NotNull CompletableFuture<? extends @NotNull Set<? extends @NotNull SlashCommand>> getGlobalSlashCommands() {
        return jda.retrieveCommands()
                .submit()
                .thenApply((list) ->
                        list.stream()
                                .map((e) -> new SlashCommandImpl(bridge, e))
                                .collect(Collectors.toUnmodifiableSet())
                );
    }
    
    @Override
    public @NotNull CompletableFuture<? extends @NotNull Set<? extends @NotNull SlashCommand>> bulkUpdateGlobalCommands(@NotNull Set<? extends @NotNull Command> commands) {
        return jda.updateCommands()
                .addCommands(commands.stream()
                        .map(SlashCommandData.class::cast)
                        .map(bridge::convertData)
                        .collect(Collectors.toSet()))
                .submit()
                .thenApply((list) ->
                        list.stream()
                                .map((e) -> new SlashCommandImpl(bridge, e))
                                .collect(Collectors.toUnmodifiableSet()));
    }
    
    @Override
    public @NotNull Set<? extends Server> getServers() {
        return jda.getGuilds()
                .stream()
                .map((e) -> new ServerImpl(bridge, e))
                .collect(Collectors.toUnmodifiableSet());
    }
    
    private static final Map<ApiAttachableListener, ListenerAdapter> listener_delegate_map = new HashMap<>();
    
    @SuppressWarnings("unchecked")
    private static <T extends ApiAttachableListener, R extends ListenerAdapter> R delegate(T listener, Function<? super T, ? extends R> delegate_function) {
        if (listener_delegate_map.containsKey(listener))
            return ((R) listener_delegate_map.get(listener));
        var delegate = delegate_function.apply(listener);
        listener_delegate_map.put(listener, delegate);
        return delegate;
    }
    
    @Override
    public <T extends ApiAttachableListener> void addListener(@NotNull Class<T> type, @NotNull T listener) {
        if (type == SlashCommandInvokeListener.class)
            jda.addEventListener(delegate(((SlashCommandInvokeListener) listener),
                    (e) -> new SlashCommandInteractionEventListenerDelegate(bridge, e)));
    }
    
    @Override
    public void removeListener(@NotNull ApiAttachableListener listener) {
        if (listener_delegate_map.containsKey(listener))
            jda.removeEventListener(listener_delegate_map.get(listener));
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return jda;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
}
