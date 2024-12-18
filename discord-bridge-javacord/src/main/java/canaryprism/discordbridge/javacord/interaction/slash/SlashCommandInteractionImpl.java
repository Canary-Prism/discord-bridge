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
import canaryprism.discordbridge.api.channel.TextChannel;
import canaryprism.discordbridge.api.entities.user.User;
import canaryprism.discordbridge.api.interaction.response.FollowupResponder;
import canaryprism.discordbridge.api.interaction.response.ImmediateResponder;
import canaryprism.discordbridge.api.interaction.response.ResponseUpdater;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandInteraction;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandInteractionOption;
import canaryprism.discordbridge.api.server.Server;
import canaryprism.discordbridge.javacord.DiscordBridgeJavacord;
import canaryprism.discordbridge.javacord.channel.TextChannelImpl;
import canaryprism.discordbridge.javacord.entities.user.UserImpl;
import canaryprism.discordbridge.javacord.interaction.response.FollowupResponderImpl;
import canaryprism.discordbridge.javacord.interaction.response.ImmediateResponderImpl;
import canaryprism.discordbridge.javacord.interaction.response.ResponseUpdaterImpl;
import canaryprism.discordbridge.javacord.server.ServerImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SlashCommandInteractionImpl implements SlashCommandInteraction {
    
    public final DiscordBridgeJavacord bridge;
    public final org.javacord.api.interaction.SlashCommandInteraction interaction;
    
    public SlashCommandInteractionImpl(DiscordBridgeJavacord bridge, org.javacord.api.interaction.SlashCommandInteraction interaction) {
        this.bridge = bridge;
        this.interaction = interaction;
    }
    
    @Override
    public long getApplicationId() {
        return interaction.getApplicationId();
    }
    
    @Override
    public long getCommandId() {
        return interaction.getApplicationId();
    }
    
    @Override
    public @NotNull String getCommandName() {
        return interaction.getCommandName();
    }
    
    @Override
    public @NotNull Optional<Long> getServerCommandServerId() {
        return interaction.getRegisteredCommandServerId();
    }
    
    @Override
    public @NotNull ImmediateResponder createImmediateResponder() {
        return new ImmediateResponderImpl(bridge, interaction.createImmediateResponder());
    }
    
    @Override
    public @NotNull CompletableFuture<? extends @NotNull ResponseUpdater> respondLater(boolean ephemeral) {
        return interaction.respondLater(ephemeral)
                .thenApply((e) -> new ResponseUpdaterImpl(bridge, e));
    }
    
    @Override
    public @NotNull FollowupResponder createFollowupResponder() {
        return new FollowupResponderImpl(bridge, interaction.createFollowupMessageBuilder());
    }
    
    @Override
    public @NotNull User getUser() {
        return new UserImpl(bridge, interaction.getUser());
    }
    
    @Override
    public @NotNull Optional<Server> getServer() {
        return interaction.getServer().map((e) -> new ServerImpl(((DiscordBridgeJavacord) bridge), e));
    }
    
    @Override
    public @NotNull Optional<? extends TextChannel> getChannel() {
        return interaction.getChannel().map((e) -> new TextChannelImpl(bridge, e));
    }
    
    @Override
    public @NotNull List<? extends @NotNull SlashCommandInteractionOption> getOptions() {
        return interaction.getOptions()
                .stream()
                .map((e) -> new SlashCommandInteractionOptionImpl(bridge, e))
                .toList();
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
                && Objects.equals(bridge, that.bridge) && Objects.equals(interaction, that.interaction);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(bridge, interaction);
    }
}
