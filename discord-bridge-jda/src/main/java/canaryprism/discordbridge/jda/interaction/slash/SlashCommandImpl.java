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

package canaryprism.discordbridge.jda.interaction.slash;

import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.interaction.ContextType;
import canaryprism.discordbridge.api.interaction.slash.SlashCommand;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOption;
import canaryprism.discordbridge.api.misc.DiscordLocale;
import canaryprism.discordbridge.api.server.permission.PermissionType;
import canaryprism.discordbridge.jda.DiscordBridgeJDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.internal.interactions.command.CommandImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record SlashCommandImpl(DiscordBridge bridge, Command command) implements SlashCommand {
    
    @Override
    public @NotNull List<? extends @NotNull SlashCommandOption> getOptions() {
        return Stream.concat(
                Stream.concat(
                        command.getSubcommandGroups()
                                .stream()
                                .map((e) -> new SlashCommandOptionSubcommandGroupImpl(bridge, e)),
                        command.getSubcommands()
                                .stream()
                                .map((e) -> new SlashCommandOptionSubcommandImpl(bridge, e))
                ),
                command.getOptions()
                        .stream()
                        .map((e) -> new SlashCommandOptionOptionImpl(bridge, e))
        ).toList();
    }
    
    @Override
    public long getApplicationId() {
        return command.getApplicationIdLong();
    }
    
    @Override
    public boolean isDefaultDisabled() {
        return command.getDefaultPermissions() == DefaultMemberPermissions.DISABLED;
    }
    
    @Override
    public @NotNull Optional<? extends Set<? extends PermissionType>> getDefaultRequiredPermissions() {
        return Optional.ofNullable(command.getDefaultPermissions().getPermissionsRaw())
                .map((value) -> Permission.getPermissions(value)
                        .stream()
                        .map((e) -> bridge.convertInternalObject(PermissionType.class, e))
                        .collect(Collectors.toUnmodifiableSet())
                );
    }
    
    @Override
    public boolean isEnabledInDMs() {
        return !command.isGuildOnly();
    }
    
    @Override
    public @NotNull Optional<? extends @Unmodifiable Set<? extends ContextType>> getAllowedContexts() {
        throw new UnsupportedOperationException(String.format("%s does not support contexts", bridge));
    }
    
    @Override
    public boolean isGlobalCommand() {
        class FieldHolder {
            static final Field guild_field;
            static {
                try {
                     guild_field = CommandImpl.class.getDeclaredField("guild");
                     guild_field.trySetAccessible();
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        try {
            return FieldHolder.guild_field.get(command) == null;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public boolean isServerCommand() {
        return !isGlobalCommand();
    }
    
    @Override
    public boolean isNSFW() {
        return command.isNSFW();
    }
    
    @Override
    public CompletableFuture<Void> delete() {
        return command.delete().submit();
    }
    
    @Override
    public @NotNull String getName() {
        return command.getName();
    }
    
    @Override
    public @NotNull Map<DiscordLocale, @NotNull String> getNameLocalizations() {
        return command.getNameLocalizations()
                .toMap()
                .entrySet()
                .stream()
                .map((e) -> Map.entry(
                        DiscordBridgeJDA.convertLocale(e.getKey()),
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
                .toMap()
                .entrySet()
                .stream()
                .map((e) -> Map.entry(
                        DiscordBridgeJDA.convertLocale(e.getKey()),
                        e.getValue()
                ))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    @Override
    public @NotNull String getIdAsString() {
        return command.getId();
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
