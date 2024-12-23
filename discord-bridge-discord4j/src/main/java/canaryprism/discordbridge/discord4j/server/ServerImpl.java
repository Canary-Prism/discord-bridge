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

package canaryprism.discordbridge.discord4j.server;

import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.data.interaction.CommandData;
import canaryprism.discordbridge.api.data.interaction.slash.SlashCommandData;
import canaryprism.discordbridge.api.interaction.Command;
import canaryprism.discordbridge.api.interaction.slash.SlashCommand;
import canaryprism.discordbridge.api.server.Server;
import canaryprism.discordbridge.discord4j.DiscordBridgeDiscord4J;
import canaryprism.discordbridge.discord4j.interaction.slash.SlashCommandImpl;
import discord4j.core.DiscordClient;
import discord4j.core.object.entity.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public record ServerImpl(DiscordBridgeDiscord4J bridge, Guild server, DiscordClient client, CompletableFuture<Long> app_id) implements Server {
    
    @Override
    public @NotNull String getName() {
        return server.getName();
    }
    
    @Override
    public @NotNull CompletableFuture<? extends @NotNull @Unmodifiable Set<? extends @NotNull SlashCommand>> getServerSlashCommands() {
        return client.getApplicationService()
                .getGuildApplicationCommands(app_id.join(), server.getId().asLong())
                .filter((e) -> e.type().toOptional().map((type) -> type == 1).orElse(false))
                .map((e) -> new SlashCommandImpl(bridge, e, client))
                .collect(Collectors.toUnmodifiableSet())
                .toFuture();
    }
    
    @Override
    public @NotNull CompletableFuture<? extends @NotNull @Unmodifiable Set<? extends @NotNull Command>> bulkUpdateServerCommands(Set<? extends @NotNull CommandData> commands) {
        return client.getApplicationService()
                .bulkOverwriteGuildApplicationCommand(
                        app_id.join(),
                        server.getId().asLong(),
                        commands.stream()
                                .map(SlashCommandData.class::cast)
                                .map(bridge::convertData)
                                .toList())
                .map((e) -> new SlashCommandImpl(bridge, e, client))
                .collect(Collectors.toUnmodifiableSet())
                .toFuture();
    }
    
    @Override
    public @NotNull String getIdAsString() {
        return server.getId().asString();
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return server;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
}
