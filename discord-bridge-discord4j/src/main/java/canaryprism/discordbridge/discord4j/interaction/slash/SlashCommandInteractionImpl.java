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
import canaryprism.discordbridge.api.channel.TextChannel;
import canaryprism.discordbridge.api.entities.user.User;
import canaryprism.discordbridge.api.interaction.response.FollowupResponder;
import canaryprism.discordbridge.api.interaction.response.ImmediateResponder;
import canaryprism.discordbridge.api.interaction.response.ResponseUpdater;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandInteraction;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandInteractionOption;
import canaryprism.discordbridge.api.server.Server;
import canaryprism.discordbridge.discord4j.DiscordBridgeDiscord4J;
import canaryprism.discordbridge.discord4j.channel.ChannelDirector;
import canaryprism.discordbridge.discord4j.entities.user.UserImpl;
import canaryprism.discordbridge.discord4j.interaction.response.FollowupResponderImpl;
import canaryprism.discordbridge.discord4j.interaction.response.ImmediateResponderImpl;
import canaryprism.discordbridge.discord4j.interaction.response.ResponseUpdaterImpl;
import canaryprism.discordbridge.discord4j.server.ServerImpl;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteraction;
import discord4j.core.object.command.Interaction;
import discord4j.core.spec.InteractionCallbackSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public record SlashCommandInteractionImpl(DiscordBridgeDiscord4J bridge, ChatInputInteractionEvent event, Interaction interaction, ApplicationCommandInteraction command_interaction, Mono<ServerImpl> server, Mono<? extends TextChannel> channel) implements SlashCommandInteraction {
    
    public SlashCommandInteractionImpl(DiscordBridgeDiscord4J bridge, ChatInputInteractionEvent event) {
        this(
                bridge,
                event,
                event.getInteraction(),
                event.getInteraction()
                        .getCommandInteraction()
                        .orElseThrow(),
                event.getInteraction()
                        .getGuild()
                        .map((e) -> new ServerImpl(
                                bridge,
                                e,
                                event.getClient()
                                        .rest(),
                                event.getClient()
                                        .rest()
                                        .getApplicationId()
                                        .toFuture())),
                event.getInteraction()
                        .getChannel()
                        .map((e) -> ChannelDirector.wrapChannel(bridge, e))
        );
    }
    
    @Override
    public long getApplicationId() {
        return interaction.getApplicationId().asLong();
    }
    
    @Override
    public long getCommandId() {
        return command_interaction.getId().orElseThrow().asLong();
    }
    
    @Override
    public @NotNull String getCommandName() {
        return command_interaction.getName().orElseThrow();
    }
    
    @Override
    public @NotNull Optional<Long> getServerCommandServerId() {
        return interaction.getGuildId().map(Snowflake::asLong);
    }
    
    @Override
    public @NotNull ImmediateResponder createImmediateResponder() {
        return new ImmediateResponderImpl(bridge, event.reply());
    }
    
    @Override
    public @NotNull CompletableFuture<? extends @NotNull ResponseUpdater> respondLater(boolean ephemeral) {
        return event.deferReply(InteractionCallbackSpec.builder().ephemeral(ephemeral).build())
                .toFuture()
                .thenApply((ignored) -> new ResponseUpdaterImpl(bridge, event.editReply()));
    }
    
    @Override
    public @NotNull FollowupResponder createFollowupResponder() {
        return new FollowupResponderImpl(bridge, event);
    }
    
    @Override
    public @NotNull User getUser() {
        return new UserImpl(bridge, interaction.getUser());
    }
    
    @Override
    public @NotNull Optional<? extends Server> getServer() {
        return server.blockOptional();
    }
    
    @Override
    public @NotNull Optional<? extends TextChannel> getChannel() {
        return channel.blockOptional();
    }
    
    @Override
    public @NotNull @Unmodifiable List<? extends @NotNull SlashCommandInteractionOption> getOptions() {
        return command_interaction.getOptions()
                .stream()
                .map((e) -> new SlashCommandInteractionOptionImpl(bridge, e))
                .toList();
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return event;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
}
