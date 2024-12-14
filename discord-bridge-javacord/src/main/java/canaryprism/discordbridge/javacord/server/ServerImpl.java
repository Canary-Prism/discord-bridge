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
import canaryprism.discordbridge.api.interaction.Command;
import canaryprism.discordbridge.api.interaction.slash.SlashCommand;
import canaryprism.discordbridge.api.server.Server;
import canaryprism.discordbridge.javacord.interaction.slash.SlashCommandImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public record ServerImpl(DiscordBridge bridge, org.javacord.api.entity.server.Server server) implements Server {
    
    @Override
    public @NotNull CompletableFuture<? extends Set<? extends SlashCommand>> getServerSlashCommands() {
        return server.getSlashCommands()
                .thenApply((set) -> set.stream()
                        .map((e) -> new SlashCommandImpl(bridge, e))
                        .collect(Collectors.toUnmodifiableSet())
                );
    }
    
    @Override
    public @NotNull CompletableFuture<? extends Set<? extends Command>> bulkUpdateServerCommands(Set<? extends Command> commands) {
        throw new UnsupportedOperationException(); //FIXME: must change this to be builders
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
}
