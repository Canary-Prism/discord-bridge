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

package canaryprism.discordbridge.javacord.server;

import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.data.interaction.CommandData;
import canaryprism.discordbridge.api.data.interaction.slash.SlashCommandData;
import canaryprism.discordbridge.api.interaction.Command;
import canaryprism.discordbridge.api.interaction.slash.SlashCommand;
import canaryprism.discordbridge.api.server.Server;
import canaryprism.discordbridge.javacord.DiscordBridgeJavacord;
import canaryprism.discordbridge.javacord.interaction.slash.SlashCommandImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public record ServerImpl(DiscordBridgeJavacord bridge, org.javacord.api.entity.server.Server server) implements Server {
    
    @Override
    public @NotNull CompletableFuture<? extends @NotNull Set<? extends @NotNull SlashCommand>> getServerSlashCommands() {
        return server.getSlashCommands()
                .thenApply((set) -> set.stream()
                        .map((e) -> new SlashCommandImpl(bridge, e))
                        .collect(Collectors.toUnmodifiableSet())
                );
    }
    
    @Override
    public @NotNull CompletableFuture<? extends @NotNull Set<? extends @NotNull Command>> bulkUpdateServerCommands(@NotNull Set<? extends @NotNull CommandData> commands) {
        return server.getApi().bulkOverwriteServerApplicationCommands(server,
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
    public long getId() {
        return server.getId();
    }
    
    @Override
    public @NotNull String getIdAsString() {
        return server.getIdAsString();
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return server;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
    
    @Override
    public @NotNull String getName() {
        return server.getName();
    }
}
