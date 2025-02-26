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

package canaryprism.discordbridge.discord4j;

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
import canaryprism.discordbridge.discord4j.event.interaction.SlashCommandAutocompleteEventImpl;
import canaryprism.discordbridge.discord4j.event.interaction.SlashCommandInvokeEventImpl;
import canaryprism.discordbridge.discord4j.interaction.slash.SlashCommandImpl;
import canaryprism.discordbridge.discord4j.server.ServerImpl;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.ReactiveEventAdapter;
import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class DiscordApiImpl implements DiscordApi {
    
    public final DiscordBridgeDiscord4J bridge;
    public final GatewayDiscordClient api;
    public final CompletableFuture<Long> app_id;
    public final CompletableFuture<Set<ServerImpl>> guilds;
    
    public final canaryprism.commons.event.EventListenerList<ApiAttachableListener> listener_list = new EventListenerList<>();
    
    public DiscordApiImpl(DiscordBridgeDiscord4J bridge, GatewayDiscordClient api) {
        this.bridge = bridge;
        this.api = api;
        this.app_id = api.rest()
                .getApplicationId()
                .toFuture();
        this.guilds = api.getGuilds()
                .map((e) -> new ServerImpl(bridge, e, api, app_id))
                .collect(Collectors.toUnmodifiableSet())
                .toFuture();
        
        api.on(new ReactiveEventAdapter() {
            @Override
            public @NotNull Publisher<?> onChatInputAutoCompleteInteraction(@NotNull ChatInputAutoCompleteEvent e) {
                var event = new SlashCommandAutocompleteEventImpl(bridge, e);
                for (var listener : listener_list.getListeners(SlashCommandAutocompleteListener.class))
                    listener.onSlashCommandAutocomplete(event);
                return Mono.empty();
            }
            
            @Override
            public @NotNull Publisher<?> onChatInputInteraction(@NotNull ChatInputInteractionEvent e) {
                var event = new SlashCommandInvokeEventImpl(bridge, e);
                for (var listener : listener_list.getListeners(SlashCommandInvokeListener.class))
                    listener.onSlashCommandInvoke(event);
                return Mono.empty();
            }
        });
    }
    
    @Override
    public @NotNull CompletableFuture<? extends @NotNull @Unmodifiable Set<? extends @NotNull SlashCommand>> getGlobalSlashCommands() {
        return api.rest()
                .getApplicationService()
                .getGlobalApplicationCommands(app_id.join())
                .filter((e) -> e.type().toOptional().map((type) -> type == 1).orElse(false))
                .map((e) -> new SlashCommandImpl(bridge, e, api))
                .collect(Collectors.toUnmodifiableSet())
                .toFuture();
    }
    
    @Override
    public @NotNull CompletableFuture<? extends @NotNull @Unmodifiable Set<? extends @NotNull SlashCommand>> bulkUpdateGlobalCommands(@NotNull Set<? extends @NotNull CommandData> commands) {
        return api.rest()
                .getApplicationService()
                .bulkOverwriteGlobalApplicationCommand(
                        app_id.join(),
                        commands.stream()
                                .map(SlashCommandData.class::cast)
                                .map(bridge::convertData)
                                .toList())
                .map((e) -> new SlashCommandImpl(bridge, e, api))
                .collect(Collectors.toUnmodifiableSet())
                .toFuture();
    }
    
    @Override
    public @NotNull @Unmodifiable Set<? extends Server> getServers() {
        return guilds.join();
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
        return api;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
}
