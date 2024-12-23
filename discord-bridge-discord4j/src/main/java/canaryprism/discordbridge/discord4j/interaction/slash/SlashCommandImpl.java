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

package canaryprism.discordbridge.discord4j.interaction.slash;

import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.interaction.ContextType;
import canaryprism.discordbridge.api.interaction.slash.SlashCommand;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOption;
import canaryprism.discordbridge.api.misc.DiscordLocale;
import canaryprism.discordbridge.api.server.permission.PermissionType;
import canaryprism.discordbridge.discord4j.DiscordBridgeDiscord4J;
import discord4j.core.DiscordClient;
import discord4j.discordjson.json.ApplicationCommandData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public record SlashCommandImpl(DiscordBridgeDiscord4J bridge, ApplicationCommandData data, DiscordClient client) implements SlashCommand {
    
    @Override
    public @NotNull String getName() {
        return data.name();
    }
    
    @Override
    public @NotNull @Unmodifiable Map<DiscordLocale, @NotNull String> getNameLocalizations() {
        return data.nameLocalizations()
                .toOptional()
                .flatMap(Function.identity())
                .orElse(Map.of())
                .entrySet()
                .stream()
                .map((e) -> Map.entry(
                        DiscordBridgeDiscord4J.convertLocale(e.getKey()),
                        e.getValue()
                ))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    @Override
    public @NotNull String getDescription() {
        return data.description();
    }
    
    @Override
    public @NotNull @Unmodifiable Map<DiscordLocale, @NotNull String> getDescriptionLocalizations() {
        return data.descriptionLocalizations()
                .toOptional()
                .flatMap(Function.identity())
                .orElse(Map.of())
                .entrySet()
                .stream()
                .map((e) -> Map.entry(
                        DiscordBridgeDiscord4J.convertLocale(e.getKey()),
                        e.getValue()
                ))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    @Override
    @NotNull
    @Unmodifiable
    public List<? extends @NotNull SlashCommandOption> getOptions() {
        return data.options()
                .toOptional()
                .orElse(List.of())
                .stream()
                .map((e) -> new SlashCommandOptionImpl(bridge, e))
                .toList();
    }
    
    @Override
    public @NotNull String getIdAsString() {
        return data.id().asString();
    }
    
    @Override
    public long getApplicationId() {
        return data.applicationId()
                .asLong();
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public boolean isDefaultDisabled() {
        return !data.defaultPermission()
                .toOptional()
                .orElse(true);
    }
    
    @Override
    public @NotNull Optional<? extends @Unmodifiable Set<? extends PermissionType>> getDefaultRequiredPermissions() {
        return data.defaultMemberPermissions()
                .map(Long::parseLong)
                .map(bridge::parsePermissionBitFlag);
    }
    
    @Override
    public boolean isEnabledInDMs() {
        return !data.dmPermission()
                .toOptional()
                .orElse(true);
    }
    
    @Override
    public @NotNull Optional<? extends @Unmodifiable Set<? extends ContextType>> getAllowedContexts() {
        throw new UnsupportedOperationException(String.format("%s does not support contexts", bridge));
    }
    
    @Override
    public boolean isGlobalCommand() {
        return data.guildId().isAbsent();
    }
    
    @Override
    public boolean isServerCommand() {
        return !isGlobalCommand();
    }
    
    @Override
    public boolean isNSFW() {
        throw new UnsupportedOperationException(String.format("%s does not support NSFW", bridge));
    }
    
    @Override
    public CompletableFuture<Void> delete() {
        if (isGlobalCommand())
            return client.getApplicationService()
                    .deleteGlobalApplicationCommand(
                            data.applicationId().asLong(),
                            data.id().asLong())
                    .toFuture();
        else
            return client.getApplicationService()
                    .deleteGuildApplicationCommand(
                            data.applicationId().asLong(),
                            data.guildId().get().asLong(),
                            data.id().asLong())
                    .toFuture();
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return data;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
}
