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

package canaryprism.discordbridge.kord.interaction.slash;

import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.interaction.ContextType;
import canaryprism.discordbridge.api.interaction.slash.SlashCommand;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOption;
import canaryprism.discordbridge.api.misc.DiscordLocale;
import canaryprism.discordbridge.api.server.permission.PermissionType;
import canaryprism.discordbridge.kord.DiscordBridgeKord;
import dev.kord.common.Locale;
import dev.kord.common.entity.ApplicationCommandOption;
import dev.kord.common.entity.DiscordApplicationCommand;
import dev.kord.common.entity.Permissions;
import dev.kord.common.entity.Snowflake;
import dev.kord.common.entity.optional.Optional.Value;
import dev.kord.core.Kord;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public record SlashCommandImpl(DiscordBridgeKord bridge, DiscordApplicationCommand command, Kord kord) implements SlashCommand {
    
    @Override
    public @NotNull @Unmodifiable List<? extends @NotNull SlashCommandOption> getOptions() {
        return (command.getOptions() instanceof Value<List<ApplicationCommandOption>> value) ?
                value.getValue()
                        .stream()
                        .map((e) -> new SlashCommandOptionImpl(bridge, e))
                        .toList()
                : List.of();
    }
    
    @Override
    public long getApplicationId() {
        return Long.parseLong(command.getApplicationId().toString());
    }
    
    @Override
    public @NotNull String getName() {
        return command.getName();
    }
    
    @Override
    public @NotNull @Unmodifiable Map<DiscordLocale, @NotNull String> getNameLocalizations() {
        return (command.getNameLocalizations() instanceof Value<Map<Locale, String>> localizations) ?
                localizations.getValue()
                .entrySet()
                .stream()
                .map((e) -> Map.entry(
                        DiscordBridgeKord.convertLocale(e.getKey()),
                        e.getValue()
                ))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                : Map.of();
    }
    
    @Override
    public @NotNull String getDescription() {
        return Optional.ofNullable(command.getDescription())
                .orElse("");
    }
    
    @Override
    public @NotNull @Unmodifiable Map<DiscordLocale, @NotNull String> getDescriptionLocalizations() {
        return (command.getDescriptionLocalizations() instanceof Value<Map<Locale, String>> localizations) ?
                localizations.getValue()
                .entrySet()
                .stream()
                .map((e) -> Map.entry(
                        DiscordBridgeKord.convertLocale(e.getKey()),
                        e.getValue()
                ))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                : Map.of();
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public boolean isDefaultDisabled() {
        return !Optional.ofNullable(command.getDefaultPermission())
                .map((e) -> e.orElse(true))
                .orElse(true);
    }
    
    @Override
    public @NotNull Optional<? extends @Unmodifiable Set<? extends PermissionType>> getDefaultRequiredPermissions() {
        return Optional.ofNullable(command.getDefaultMemberPermissions())
                .map(Permissions::getValues)
                .map((set) -> set.stream()
                        .map((e) -> bridge.convertInternalObject(PermissionType.class, e))
                        .collect(Collectors.toUnmodifiableSet())
                );

    }
    
    @Deprecated(since = "3.0.0")
    @Override
    public boolean isEnabledInDMs() {
        return command.getDmPermission().orElse(true);
    }
    
    @Override
    public @NotNull Optional<? extends @Unmodifiable Set<? extends ContextType>> getAllowedContexts() {
        throw new UnsupportedOperationException(String.format("%s does not support contexts", bridge));
    }
    
    @Override
    public boolean isGlobalCommand() {
        return command.getGuildId().getAsOptional() instanceof dev.kord.common.entity.optional.Optional.Missing<Snowflake>;
    }
    
    @Override
    public boolean isServerCommand() {
        return !isGlobalCommand();
    }
    
    @Override
    public boolean isNSFW() {
        return command.getNsfw().orElse(false);
    }
    
    @Override
    public CompletableFuture<Void> delete() {
        var future = new CompletableFuture<Void>();
        var continuation = new Continuation<Unit>() {
            
            @Override
            public @NotNull CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }
            
            @Override
            public void resumeWith(@NotNull Object o) {
                future.complete(null);
            }
        };
        if (this.isGlobalCommand()) {
            kord.getRest()
                    .getInteraction()
                    .deleteGlobalApplicationCommand(kord.getSelfId(), command.getId(), continuation);
        } else {
            kord.getRest()
                    .getInteraction()
                    .deleteGuildApplicationCommand(kord.getSelfId(), Objects.requireNonNull(command.getGuildId().getValue()), command.getId(), continuation);
        }
        return future;
    }
    
    @Override
    public @NotNull String getIdAsString() {
        return command.getId().toString();
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return command;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
}
