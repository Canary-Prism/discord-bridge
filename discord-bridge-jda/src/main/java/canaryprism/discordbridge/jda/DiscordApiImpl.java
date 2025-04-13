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

import canaryprism.commons.event.EventListenerList;
import canaryprism.discordbridge.api.DiscordApi;
import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.data.interaction.CommandData;
import canaryprism.discordbridge.api.data.interaction.slash.SlashCommandData;
import canaryprism.discordbridge.api.interaction.slash.SlashCommand;
import canaryprism.discordbridge.api.listener.ApiAttachableListener;
import canaryprism.discordbridge.api.listener.interaction.SlashCommandAutocompleteListener;
import canaryprism.discordbridge.api.listener.interaction.SlashCommandInvokeListener;
import canaryprism.discordbridge.api.server.Server;
import canaryprism.discordbridge.jda.event.interaction.SlashCommandAutocompleteEventImpl;
import canaryprism.discordbridge.jda.event.interaction.SlashCommandInvokeEventImpl;
import canaryprism.discordbridge.jda.interaction.slash.SlashCommandImpl;
import canaryprism.discordbridge.jda.server.ServerImpl;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public record DiscordApiImpl(DiscordBridgeJDA bridge, JDA jda, EventListenerList<ApiAttachableListener> listener_list) implements DiscordApi {
    
    public DiscordApiImpl(DiscordBridgeJDA bridge, JDA jda) {
        this(bridge, jda, new EventListenerList<>());
    }
    
    public DiscordApiImpl {
        jda.addEventListener(new ListenerAdapter() {
            @Override
            public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent e) {
                var event = new SlashCommandAutocompleteEventImpl(bridge, e);
                for (var listener : listener_list.getListeners(SlashCommandAutocompleteListener.class))
                    listener.onSlashCommandAutocomplete(event);
            }
            
            @Override
            public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent e) {
                var event = new SlashCommandInvokeEventImpl(bridge, e);
                for (var listener : listener_list.getListeners(SlashCommandInvokeListener.class))
                    listener.onSlashCommandInvoke(event);
            }
        });
    }
    
    public static final Map<Long, Command> command_cache = Collections.synchronizedMap(new HashMap<>());
    
    @Override
    public @NotNull CompletableFuture<? extends @NotNull Set<? extends @NotNull SlashCommand>> getGlobalSlashCommands() {
        return jda.retrieveCommands()
                .submit()
                .thenApply((list) ->
                        list.stream()
                                .filter((e) -> e.getType() == Command.Type.SLASH)
                                .peek((e) -> command_cache.put(e.getIdLong(), e))
                                .map((e) -> new SlashCommandImpl(bridge, e))
                                .collect(Collectors.toUnmodifiableSet())
                );
    }
    
    @Override
    public @NotNull CompletableFuture<? extends @NotNull Set<? extends @NotNull SlashCommand>> bulkUpdateGlobalCommands(@NotNull Set<? extends @NotNull CommandData> commands) {
        return jda.updateCommands()
                .addCommands(commands.stream()
                        .map(SlashCommandData.class::cast)
                        .map(bridge::convertData)
                        .collect(Collectors.toSet()))
                .submit()
                .thenApply((list) ->
                        list.stream()
                                .peek((e) -> command_cache.put(e.getIdLong(), e))
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
    
    @Override
    public <T extends ApiAttachableListener> void addListener(@NotNull Class<T> type, @NotNull T listener) {
        listener_list.addListener(type, listener);
    }
    
    @Override
    public <T extends ApiAttachableListener> void removeListener(@NotNull Class<T> type, @NotNull T listener) {
        listener_list.removeListener(type, listener);
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
