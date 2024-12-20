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

package canaryprism.discordbridge.javacord.interaction.slash;

import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.interaction.slash.SlashCommand;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOption;
import canaryprism.discordbridge.api.misc.DiscordLocale;
import canaryprism.discordbridge.api.server.permission.PermissionType;
import canaryprism.discordbridge.javacord.DiscordBridgeJavacord;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public record SlashCommandImpl(DiscordBridge bridge, org.javacord.api.interaction.SlashCommand command) implements SlashCommand {
    
    @Override
    public @NotNull List<? extends @NotNull SlashCommandOption> getOptions() {
        return command.getOptions()
                .stream()
                .map((e) -> new SlashCommandOptionImpl(bridge, e))
                .toList();
    }
    
    @Override
    public long getApplicationId() {
        return command.getApplicationId();
    }
    
    @Override
    public boolean isDefaultDisabled() {
        return command.isDisabledByDefault();
    }
    
    @Override
    public Optional<Set<PermissionType>> getDefaultRequiredPermissions() {
        return command.getDefaultRequiredPermissions()
                .map((set) -> set.stream()
                        .map((e) -> bridge.convertInternalObject(PermissionType.class, e))
                        .collect(Collectors.toUnmodifiableSet())
                );
    }
    
    @Override
    public boolean isEnabledInDMs() {
        return command.isEnabledInDms();
    }
    
    @Override
    public boolean isGlobalCommand() {
        return command.isGlobalApplicationCommand();
    }
    
    @Override
    public boolean isServerCommand() {
        return command.isServerApplicationCommand();
    }
    
    @Override
    public boolean isNSFW() {
        return command.isNsfw();
    }
    
    @Override
    public CompletableFuture<Void> delete() {
        return command.delete();
    }
    
    @Override
    public long getId() {
        return command.getId();
    }
    
    @Override
    public @NotNull String getIdAsString() {
        return command.getIdAsString();
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return command;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
    
    @Override
    public @NotNull String getName() {
        return command.getName();
    }
    
    @Override
    public @NotNull Map<DiscordLocale, @NotNull String> getNameLocalizations() {
        return command.getNameLocalizations()
                .entrySet()
                .stream()
                .map((e) -> Map.entry(
                        DiscordBridgeJavacord.convertLocale(e.getKey()),
                        e.getValue()
                ))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    @Override
    public @NotNull String getDescription() {
        return command.getDescription();
    }
    
    @Override
    public @NotNull Map<DiscordLocale, @NotNull String> getDescriptionLocalizations() {
        return command.getDescriptionLocalizations()
                .entrySet()
                .stream()
                .map((e) -> Map.entry(
                        DiscordBridgeJavacord.convertLocale(e.getKey()),
                        e.getValue()
                ))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
