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
import canaryprism.discordbridge.api.channel.MessageChannel;
import canaryprism.discordbridge.api.data.interaction.slash.SlashCommandOptionChoiceData;
import canaryprism.discordbridge.api.entity.user.User;
import canaryprism.discordbridge.api.interaction.response.FollowupResponder;
import canaryprism.discordbridge.api.interaction.response.ImmediateResponder;
import canaryprism.discordbridge.api.interaction.response.ResponseUpdater;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandAutocompleteInteraction;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandInteractionOption;
import canaryprism.discordbridge.api.server.Server;
import canaryprism.discordbridge.discord4j.DiscordBridgeDiscord4J;
import canaryprism.discordbridge.discord4j.channel.ChannelDirector;
import canaryprism.discordbridge.discord4j.entity.user.UserImpl;
import canaryprism.discordbridge.discord4j.server.ServerImpl;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public record SlashCommandAutocompleteInteractionImpl(DiscordBridgeDiscord4J bridge, ChatInputAutoCompleteEvent event) implements SlashCommandAutocompleteInteraction {
    
    @Override
    public @NotNull CompletableFuture<?> suggest(@NotNull List<? extends @NotNull SlashCommandOptionChoiceData> choices) {
        return event.respondWithSuggestions(choices.stream()
                        .map(bridge::convertData)
                        .toList())
                .toFuture();
    }
    
    @Override
    public long getApplicationId() {
        return event.getInteraction().getApplicationId().asLong();
    }
    
    @Override
    public long getCommandId() {
        return event.getCommandId().asLong();
    }
    
    @Override
    public @NotNull String getCommandName() {
        return event.getCommandName();
    }
    
    @Override
    public @NotNull Optional<Long> getServerCommandServerId() {
        return event.getInteraction().getGuildId().map(Snowflake::asLong);
    }
    
    @Override
    public @NotNull ImmediateResponder createImmediateResponder() {
        throw new UnsupportedOperationException("can't respond to Autocompletes");
    }
    
    @Override
    public @NotNull CompletableFuture<? extends @NotNull ResponseUpdater> respondLater(boolean ephemeral) {
        throw new UnsupportedOperationException("can't respond to Autocompletes");
    }
    
    @Override
    public @NotNull FollowupResponder createFollowupResponder() {
        throw new UnsupportedOperationException("can't respond to Autocompletes");
    }
    
    @Override
    public@NotNull User getUser() {
        return new UserImpl(bridge, event.getInteraction().getUser());
    }
    
    @Override
    public @NotNull Optional<? extends Server> getServer() {
        return event.getInteraction()
                .getGuild()
                .blockOptional()
                .map((e) -> new ServerImpl(bridge, e, e.getClient(), e.getClient().rest().getApplicationId().toFuture()));
    }
    
    @Override
    public @NotNull Optional<? extends MessageChannel> getChannel() {
        return event.getInteraction()
                .getChannel()
                .blockOptional()
                .map((e) -> ChannelDirector.wrapChannel(bridge, e));
    }
    
    @Override
    public @NotNull @Unmodifiable List<? extends @NotNull SlashCommandInteractionOption> getOptions() {
        return event.getOptions()
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
