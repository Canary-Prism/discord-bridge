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
import canaryprism.discordbridge.api.channel.TextChannel;
import canaryprism.discordbridge.api.entity.user.User;
import canaryprism.discordbridge.api.interaction.response.FollowupResponder;
import canaryprism.discordbridge.api.interaction.response.ImmediateResponder;
import canaryprism.discordbridge.api.interaction.response.ResponseUpdater;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandInteraction;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandInteractionOption;
import canaryprism.discordbridge.api.server.Server;
import canaryprism.discordbridge.jda.DiscordBridgeJDA;
import canaryprism.discordbridge.jda.channel.ChannelDirector;
import canaryprism.discordbridge.jda.entity.user.UserImpl;
import canaryprism.discordbridge.jda.interaction.response.FollowupResponderImpl;
import canaryprism.discordbridge.jda.interaction.response.ImmediateResponderImpl;
import canaryprism.discordbridge.jda.interaction.response.ResponseUpdaterImpl;
import canaryprism.discordbridge.jda.server.ServerImpl;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.callbacks.IDeferrableCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload;
import net.dv8tion.jda.internal.interactions.command.CommandImpl;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SlashCommandInteractionImpl implements SlashCommandInteraction {
    public final DiscordBridgeJDA bridge;
    public final CommandInteractionPayload interaction;
    public final CompletableFuture<Command> future_command;
    
    public SlashCommandInteractionImpl(DiscordBridgeJDA bridge, CommandInteractionPayload interaction) {
        this.bridge = bridge;
        this.interaction = interaction;
        this.future_command = interaction.getJDA()
                        .retrieveCommandById(interaction.getCommandId())
                        .submit();
    }
    
    @Override
    public long getApplicationId() {
        return future_command.join().getApplicationIdLong();
    }
    
    @Override
    public long getCommandId() {
        return interaction.getCommandIdLong();
    }
    
    @Override
    public @NotNull String getCommandName() {
        return interaction.getName();
    }
    
    @Override
    public @NotNull Optional<Long> getServerCommandServerId() {
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
            var server = ((Guild) FieldHolder.guild_field.get(future_command.join()));
            
            return Optional.ofNullable(server)
                    .map(Guild::getIdLong);
            
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public @NotNull ImmediateResponder createImmediateResponder() {
        try {
            return new ImmediateResponderImpl(bridge, ((IReplyCallback) interaction).reply(""));
        } catch (ClassCastException e) {
            throw new UnsupportedOperationException("can't respond to autocomplete interactions", e);
        }
    }
    
    @Override
    public @NotNull CompletableFuture<? extends @NotNull ResponseUpdater> respondLater(boolean ephemeral) {
        try {
            return ((IReplyCallback) interaction).deferReply(ephemeral)
                    .submit()
                    .thenApply((e) -> new ResponseUpdaterImpl(bridge, e.editOriginal("")));
        } catch (ClassCastException e) {
            throw new UnsupportedOperationException("can't respond to autocomplete interactions", e);
        }
    }
    
    @Override
    public @NotNull FollowupResponder createFollowupResponder() {
        try {
            return new FollowupResponderImpl(bridge, ((IDeferrableCallback) interaction).getHook());
        } catch (ClassCastException e) {
            throw new UnsupportedOperationException("can't respond to autocomplete interactions", e);
        }
    }
    
    @Override
    public @NotNull User getUser() {
        return new UserImpl(bridge, interaction.getUser());
    }
    
    @Override
    public @NotNull Optional<? extends Server> getServer() {
        return Optional.ofNullable(interaction.getGuild())
                .map((e) -> new ServerImpl(bridge, e));
    }
    
    @Override
    public @NotNull Optional<? extends TextChannel> getChannel() {
        return (interaction instanceof net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction e) ?
                Optional.of(ChannelDirector.wrapChannel(bridge, e.getChannel()))
                : Optional.empty();
    }
    
    @Override
    public @NotNull List<? extends @NotNull SlashCommandInteractionOption> getOptions() {
        return Optional.<List<? extends @NotNull SlashCommandInteractionOption>>empty()
                .or(this::getSubcommandGroup)
                .or(this::getSubcommand)
                .orElse(interaction.getOptions()
                        .stream()
                        .map((e) -> new SlashCommandInteractionOptionOptionMappingImpl(bridge, interaction, e))
                        .toList()
                );
    }
    
    private @NotNull Optional<List<SlashCommandInteractionOptionSubcommandGroupImpl>> getSubcommandGroup() {
        return Optional.ofNullable(interaction.getSubcommandGroup())
                .map((e) -> List.of(new SlashCommandInteractionOptionSubcommandGroupImpl(bridge, e,
                        getSubcommand().orElse(List.of())))
                );
    }
    
    private @NotNull Optional<List<SlashCommandInteractionOptionSubcommandImpl>> getSubcommand() {
        return Optional.ofNullable(interaction.getSubcommandName())
                .map((name) -> List.of(new SlashCommandInteractionOptionSubcommandImpl(bridge, name,
                        interaction.getOptions()
                                .stream()
                                .map((e) -> new SlashCommandInteractionOptionOptionMappingImpl(bridge, interaction, e))
                                .toList()))
                );
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return interaction;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
    
    @Override
    public boolean equals(Object o) {
        return (o instanceof SlashCommandInteractionImpl that)
                && Objects.equals(bridge, that.bridge) && Objects.equals(interaction, that.interaction) && Objects.equals(future_command, that.future_command);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(bridge, interaction, future_command);
    }
}
