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

package canaryprism.discordbridge.kord;

import canaryprism.discordbridge.api.DiscordApi;
import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.DiscordBridgeApi;
import canaryprism.discordbridge.api.data.interaction.slash.SlashCommandData;
import canaryprism.discordbridge.api.data.interaction.slash.SlashCommandOptionChoiceData;
import canaryprism.discordbridge.api.data.interaction.slash.SlashCommandOptionData;
import canaryprism.discordbridge.api.enums.DiscordBridgeEnum;
import canaryprism.discordbridge.api.enums.PartialSupport;
import canaryprism.discordbridge.api.enums.TypeValue;
import canaryprism.discordbridge.api.exceptions.UnsupportedImplementationException;
import canaryprism.discordbridge.api.exceptions.UnsupportedValueException;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType;
import canaryprism.discordbridge.api.misc.DiscordLocale;
import canaryprism.discordbridge.api.server.permission.PermissionType;
import canaryprism.discordbridge.kord.channel.ChannelImpl;
import canaryprism.discordbridge.kord.channel.MessageChannelImpl;
import canaryprism.discordbridge.kord.channel.ServerChannelImpl;
import canaryprism.discordbridge.kord.channel.ServerMessageChannelImpl;
import canaryprism.discordbridge.kord.entity.user.UserImpl;
import canaryprism.discordbridge.kord.event.interaction.SlashCommandAutocompleteEventImpl;
import canaryprism.discordbridge.kord.event.interaction.SlashCommandInvokeEventImpl;
import canaryprism.discordbridge.kord.interaction.response.FollowupResponderImpl;
import canaryprism.discordbridge.kord.interaction.response.ImmediateResponderImpl;
import canaryprism.discordbridge.kord.interaction.response.ResponseUpdaterImpl;
import canaryprism.discordbridge.kord.interaction.slash.*;
import canaryprism.discordbridge.kord.message.AttachmentImpl;
import canaryprism.discordbridge.kord.server.ServerImpl;
import canaryprism.discordbridge.kord.server.permission.RoleImpl;
import dev.kord.common.entity.*;
import dev.kord.common.entity.optional.OptionalBoolean;
import dev.kord.core.Kord;
import dev.kord.core.entity.Attachment;
import dev.kord.core.entity.Entity;
import dev.kord.core.entity.Role;
import dev.kord.core.entity.User;
import dev.kord.core.entity.channel.*;
import dev.kord.core.entity.channel.thread.NewsChannelThread;
import dev.kord.core.entity.channel.thread.TextChannelThread;
import dev.kord.core.event.Event;
import dev.kord.rest.json.request.ApplicationCommandCreateRequest;
import kotlin.Unit;
import kotlin.collections.MapsKt;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.internal.Reflection;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineStart;
import kotlinx.coroutines.channels.BufferOverflow;
import kotlinx.coroutines.flow.FlowKt;
import kotlinx.serialization.json.JsonElementKt;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Map.entry;

/// The JDA implementation of [DiscordBridge]
public class DiscordBridgeKord implements DiscordBridge {
    
    private static final Logger log = LoggerFactory.getLogger(DiscordBridgeKord.class);
    
    @Override
    public boolean canLoadApi(@NotNull Object o) {
        return o instanceof Kord;
    }
    
    @Override
    public @NotNull DiscordApi loadApi(@NotNull Object api) {
        log.trace("loading '{}'", api);
        try {
            return new DiscordApiImpl(this, ((Kord) api));
        } catch (ClassCastException e) {
            throw new UnsupportedImplementationException(
                    String.format("%s implementation can't load object %s", this, api));
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T extends PartialSupport> @NotNull Set<? extends @NotNull T> getSupportedValues(Class<T> type) {
        class SupportedValues {
            static final Set<?> slash_command_option_type = getSupported(SlashCommandOptionType.class);
            
            static final Set<?> channel_type = getSupported(canaryprism.discordbridge.api.channel.ChannelType.class);
            
            static final Set<?> message_flag_type = getSupported(canaryprism.discordbridge.api.message.MessageFlag.class);
            
            static final Set<?> permission_type = getSupported(PermissionType.class);
        }
        
        if (type == SlashCommandOptionType.class)
            return (Set<? extends T>) SupportedValues.slash_command_option_type;
        
        if (type == canaryprism.discordbridge.api.channel.ChannelType.class)
            return (Set<? extends T>) SupportedValues.channel_type;
        
        if (type == canaryprism.discordbridge.api.message.MessageFlag.class)
            return (Set<? extends T>) SupportedValues.message_flag_type;
        
        if (type == PermissionType.class)
            return (Set<? extends T>) SupportedValues.permission_type;
        
        return Set.of();
    }
    
    private static <T extends PartialSupport> @NotNull Set<T> getSupported(@NotNull Class<T> type) {
        return Arrays.stream(type.getEnumConstants())
                .filter((e) -> {
                    try {
                        staticGetImplementationValue(e);
                        return true;
                    } catch (UnsupportedValueException n) {
                        return false;
                    }
                })
                .collect(Collectors.toUnmodifiableSet());
    }
    
    @Override
    public @NotNull Type getInternalTypeRepresentation(@NotNull TypeValue<?> value) {
        if (value instanceof canaryprism.discordbridge.api.channel.ChannelType type) {
            class ChannelTypeConversion {
                static final Map<ChannelType, Class<? extends Channel>> MAP = Map.ofEntries(
                        entry(ChannelType.DM.INSTANCE, DmChannel.class),
                        entry(ChannelType.GroupDM.INSTANCE, Channel.class),
                        entry(ChannelType.GuildText.INSTANCE, TextChannel.class),
                        entry(ChannelType.GuildVoice.INSTANCE, VoiceChannel.class),
                        entry(ChannelType.GuildCategory.INSTANCE, Category.class),
                        entry(ChannelType.GuildNews.INSTANCE, NewsChannel.class),
                        entry(ChannelType.GuildStageVoice.INSTANCE, StageChannel.class),
                        entry(ChannelType.PublicNewsThread.INSTANCE, NewsChannelThread.class),
                        entry(ChannelType.PublicGuildThread.INSTANCE, TextChannelThread.class),
                        entry(ChannelType.PrivateThread.INSTANCE, TextChannelThread.class),
                        entry(ChannelType.GuildForum.INSTANCE, ForumChannel.class),
                        entry(ChannelType.GuildMedia.INSTANCE, MediaChannel.class),
                        entry(ChannelType.GuildDirectory.INSTANCE, GuildChannel.class)
                );
            }
            return ChannelTypeConversion.MAP.getOrDefault(((ChannelType) getImplementationValue(type)), Channel.class);
        }
        else if (value instanceof SlashCommandOptionType type)
            return switch (type) {
                case SUBCOMMAND, SUBCOMMAND_GROUP -> Void.class;
                case STRING -> String.class;
                case INTEGER -> Long.class;
                case NUMBER -> Double.class;
                case BOOLEAN -> Boolean.class;
                case USER -> User.class;
                case CHANNEL -> GuildChannel.class;
                case ROLE -> Role.class;
                case MENTIONABLE -> Entity.class;
                case ATTACHMENT -> Attachment.class;
                case UNKNOWN -> Object.class;
            };
        
        throw new IllegalArgumentException(String.format("Unreachable; Unknown Value %s", value));
    }
    
    @Override
    public @NotNull Object getImplementationValue(@NotNull DiscordBridgeEnum value) {
        return staticGetImplementationValue(value);
    }
    
    @SuppressWarnings("DuplicateBranchesInSwitch")
    private static @NotNull Object staticGetImplementationValue(@NotNull DiscordBridgeEnum value) {
        if (value instanceof canaryprism.discordbridge.api.channel.ChannelType type)
            
            return switch (type) {
                case UNKNOWN -> throw new UnsupportedValueException(type);
                case PRIVATE -> ChannelType.DM.INSTANCE;
                case GROUP -> ChannelType.GroupDM.INSTANCE;
                case SERVER_TEXT -> ChannelType.GuildText.INSTANCE;
                case SERVER_VOICE -> ChannelType.GuildVoice.INSTANCE;
                case SERVER_CATEGORY -> ChannelType.GuildCategory.INSTANCE;
                case SERVER_NEWS -> ChannelType.GuildNews.INSTANCE;
                case SERVER_STAGE -> ChannelType.GuildStageVoice.INSTANCE;
                case SERVER_THREAD_NEWS -> ChannelType.PublicNewsThread.INSTANCE;
                case SERVER_THREAD_PUBLIC -> ChannelType.PublicGuildThread.INSTANCE;
                case SERVER_THREAD_PRIVATE -> ChannelType.PrivateThread.INSTANCE;
                case SERVER_FORUM -> ChannelType.GuildForum.INSTANCE;
                case SERVER_MEDIA -> ChannelType.GuildMedia.INSTANCE;
                case SERVER_SHOP -> throw new UnsupportedValueException(type);
                case SERVER_DIRECTORY -> ChannelType.GuildDirectory.INSTANCE;
            };
        else if (value instanceof SlashCommandOptionType type)
            return switch (type) {
                case UNKNOWN -> throw new UnsupportedValueException(type);
                case SUBCOMMAND -> ApplicationCommandOptionType.SubCommand.INSTANCE;
                case SUBCOMMAND_GROUP -> ApplicationCommandOptionType.SubCommandGroup.INSTANCE;
                case STRING -> ApplicationCommandOptionType.String.INSTANCE;
                case INTEGER -> ApplicationCommandOptionType.Integer.INSTANCE;
                case NUMBER -> ApplicationCommandOptionType.Number.INSTANCE;
                case BOOLEAN -> ApplicationCommandOptionType.Boolean.INSTANCE;
                case USER -> ApplicationCommandOptionType.User.INSTANCE;
                case CHANNEL -> ApplicationCommandOptionType.Channel.INSTANCE;
                case ROLE -> ApplicationCommandOptionType.Role.INSTANCE;
                case MENTIONABLE -> ApplicationCommandOptionType.Mentionable.INSTANCE;
                case ATTACHMENT -> ApplicationCommandOptionType.Attachment.INSTANCE;
            };
        else if (value instanceof canaryprism.discordbridge.api.message.MessageFlag flag)
            return switch (flag) {
                case UNKNOWN -> throw new UnsupportedValueException(flag);
                case EPHEMERAL -> MessageFlag.Ephemeral.INSTANCE;
                case SILENT -> MessageFlag.SuppressNotifications.INSTANCE;
            };
        else if (value instanceof PermissionType type)
            return switch (type) {
                case UNKNOWN -> throw new UnsupportedValueException(type);
                
                case VIEW_CHANNEL -> Permission.ViewChannel.INSTANCE;
                case MANAGE_CHANNEL -> Permission.ManageChannels.INSTANCE;
                case MANAGE_ROLES -> Permission.ManageRoles.INSTANCE;
                case CREATE_EXPRESSIONS -> Permission.CreateGuildExpressions.INSTANCE;
                case MANAGE_EXPRESSIONS -> Permission.ManageGuildExpressions.INSTANCE;
                case VIEW_AUDIT_LOG -> Permission.ViewAuditLog.INSTANCE;
                case VIEW_SERVER_INSIGHTS -> Permission.ViewGuildInsights.INSTANCE;
                case MANAGE_WEBHOOKS -> Permission.ManageWebhooks.INSTANCE;
                case MANAGE_SERVER -> Permission.ManageGuild.INSTANCE;
                
                case CREATE_INSTANT_INVITE -> Permission.CreateInstantInvite.INSTANCE;
                case CHANGE_NICKNAME -> Permission.ChangeNickname.INSTANCE;
                case MANAGE_NICKNAMES -> Permission.ManageNicknames.INSTANCE;
                case KICK_MEMBERS -> Permission.KickMembers.INSTANCE;
                case BAN_MEMBERS -> Permission.BanMembers.INSTANCE;
                case MODERATE_MEMBERS -> Permission.ModerateMembers.INSTANCE;
                
                case SEND_MESSAGES -> Permission.SendMessages.INSTANCE;
                case SEND_MESSAGES_IN_THREADS -> Permission.SendMessagesInThreads.INSTANCE;
                case CREATE_PUBLIC_THREADS -> Permission.CreatePublicThreads.INSTANCE;
                case CREATE_PRIVATE_THREADS -> Permission.CreatePrivateThreads.INSTANCE;
                case EMBED_LINKS -> Permission.EmbedLinks.INSTANCE;
                case ATTACH_FILE -> Permission.AttachFiles.INSTANCE;
                case ADD_REACTIONS -> Permission.AddReactions.INSTANCE;
                case USE_EXTERNAL_EMOJIS -> Permission.UseExternalEmojis.INSTANCE;
                case USE_EXTERNAL_STICKERS -> Permission.UseExternalStickers.INSTANCE;
                case MENTION_ANYONE -> Permission.MentionEveryone.INSTANCE;
                case MANAGE_MESSAGES -> Permission.ManageMessages.INSTANCE;
                case MANAGE_THREADS -> Permission.ManageThreads.INSTANCE;
                case READ_MESSAGE_HISTORY -> Permission.ReadMessageHistory.INSTANCE;
                case SEND_TTS_MESSAGES -> Permission.SendTTSMessages.INSTANCE;
                case SEND_VOICE_MESSAGES -> Permission.SendVoiceMessages.INSTANCE;
                case CREATE_POLLS -> throw new UnsupportedValueException(type);
                
                case CONNECT_VOICE -> Permission.Connect.INSTANCE;
                case SPEAK -> Permission.Speak.INSTANCE;
                case VIDEO -> Permission.Stream.INSTANCE;
                case USE_SOUNDBOARD -> Permission.UseSoundboard.INSTANCE;
                case USE_EXTERNAL_SOUNDBOARD -> Permission.UseExternalSounds.INSTANCE;
                case USE_VOICE_ACTIVITY -> Permission.UseVAD.INSTANCE;
                case PRIORITY_SPEAKER -> Permission.PrioritySpeaker.INSTANCE;
                case MUTE_MEMBERS -> Permission.MuteMembers.INSTANCE;
                case DEAFEN_MEMBERS -> Permission.DeafenMembers.INSTANCE;
                case MOVE_MEMBERS -> Permission.MoveMembers.INSTANCE;
                case SET_CHANNEL_STATUS -> throw new UnsupportedValueException(type);
                
                case USE_APPLICATION_COMMANDS -> Permission.UseApplicationCommands.INSTANCE;
                case START_EMBEDDED_ACTIVITIES -> Permission.UseEmbeddedActivities.INSTANCE;
                case USE_EXTERNAL_APPS -> throw new UnsupportedValueException(type);
                
                case REQUEST_TO_SPEAK -> Permission.RequestToSpeak.INSTANCE;
                
                case CREATE_EVENTS -> Permission.CreateEvents.INSTANCE;
                case MANAGE_EVENTS -> Permission.ManageEvents.INSTANCE;
                
                case ADMINISTRATOR -> Permission.Administrator.INSTANCE;
                
                case VIEW_MONETIZATION_ANALYTICS -> Permission.ViewCreatorMonetizationAnalytics.INSTANCE;
            };
        
        if (value instanceof PartialSupport partial_support)
            throw new UnsupportedValueException(partial_support);
        
        throw new IllegalArgumentException(String.format("Unreachable; Unknown Value %s", value));
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T extends DiscordBridgeEnum> @NotNull T convertInternalObject(@NotNull Class<T> type, @NotNull Object value) {
        
        conversion_attempt: {
            if (type == canaryprism.discordbridge.api.channel.ChannelType.class) {
                if (!(value instanceof ChannelType e)) break conversion_attempt;
                class ChannelTypeConversion {
                    static final Map<ChannelType, canaryprism.discordbridge.api.channel.ChannelType> MAP = Map.ofEntries(
                            entry(ChannelType.DM.INSTANCE, canaryprism.discordbridge.api.channel.ChannelType.PRIVATE),
                            entry(ChannelType.GroupDM.INSTANCE, canaryprism.discordbridge.api.channel.ChannelType.GROUP),
                            entry(ChannelType.GuildText.INSTANCE, canaryprism.discordbridge.api.channel.ChannelType.SERVER_TEXT),
                            entry(ChannelType.GuildVoice.INSTANCE, canaryprism.discordbridge.api.channel.ChannelType.SERVER_VOICE),
                            entry(ChannelType.GuildCategory.INSTANCE, canaryprism.discordbridge.api.channel.ChannelType.SERVER_CATEGORY),
                            entry(ChannelType.GuildNews.INSTANCE, canaryprism.discordbridge.api.channel.ChannelType.SERVER_NEWS),
                            entry(ChannelType.GuildStageVoice.INSTANCE, canaryprism.discordbridge.api.channel.ChannelType.SERVER_STAGE),
                            entry(ChannelType.PublicNewsThread.INSTANCE, canaryprism.discordbridge.api.channel.ChannelType.SERVER_THREAD_NEWS),
                            entry(ChannelType.PublicGuildThread.INSTANCE, canaryprism.discordbridge.api.channel.ChannelType.SERVER_THREAD_PUBLIC),
                            entry(ChannelType.PrivateThread.INSTANCE, canaryprism.discordbridge.api.channel.ChannelType.SERVER_THREAD_PRIVATE),
                            entry(ChannelType.GuildForum.INSTANCE, canaryprism.discordbridge.api.channel.ChannelType.SERVER_FORUM),
                            entry(ChannelType.GuildMedia.INSTANCE, canaryprism.discordbridge.api.channel.ChannelType.SERVER_MEDIA),
                            entry(ChannelType.GuildDirectory.INSTANCE, canaryprism.discordbridge.api.channel.ChannelType.SERVER_DIRECTORY)
                    );
                }
                return ((T) ChannelTypeConversion.MAP.getOrDefault(e, canaryprism.discordbridge.api.channel.ChannelType.UNKNOWN));
            } else if (type == SlashCommandOptionType.class) {
                if (!(value instanceof ApplicationCommandOptionType e)) break conversion_attempt;
                class SlashCommandOptionTypeConversion {
                    static final Map<ApplicationCommandOptionType, SlashCommandOptionType> MAP = Map.ofEntries(
                            entry(ApplicationCommandOptionType.SubCommand.INSTANCE, SlashCommandOptionType.SUBCOMMAND),
                            entry(ApplicationCommandOptionType.SubCommandGroup.INSTANCE, SlashCommandOptionType.SUBCOMMAND_GROUP),
                            entry(ApplicationCommandOptionType.String.INSTANCE, SlashCommandOptionType.STRING),
                            entry(ApplicationCommandOptionType.Integer.INSTANCE, SlashCommandOptionType.INTEGER),
                            entry(ApplicationCommandOptionType.Number.INSTANCE, SlashCommandOptionType.NUMBER),
                            entry(ApplicationCommandOptionType.Boolean.INSTANCE, SlashCommandOptionType.BOOLEAN),
                            entry(ApplicationCommandOptionType.User.INSTANCE, SlashCommandOptionType.USER),
                            entry(ApplicationCommandOptionType.Channel.INSTANCE, SlashCommandOptionType.CHANNEL),
                            entry(ApplicationCommandOptionType.Role.INSTANCE, SlashCommandOptionType.ROLE),
                            entry(ApplicationCommandOptionType.Mentionable.INSTANCE, SlashCommandOptionType.MENTIONABLE),
                            entry(ApplicationCommandOptionType.Attachment.INSTANCE, SlashCommandOptionType.ATTACHMENT)
                    );
                }
                return (T) SlashCommandOptionTypeConversion.MAP.getOrDefault(e, SlashCommandOptionType.UNKNOWN);
            } else if (type == canaryprism.discordbridge.api.message.MessageFlag.class) {
                if (!(value instanceof MessageFlag e)) break conversion_attempt;
                class MessageFlagConversion {
                    static final Map<MessageFlag, canaryprism.discordbridge.api.message.MessageFlag> MAP = Map.ofEntries(
                            entry(MessageFlag.Ephemeral.INSTANCE, canaryprism.discordbridge.api.message.MessageFlag.EPHEMERAL),
                            entry(MessageFlag.SuppressNotifications.INSTANCE, canaryprism.discordbridge.api.message.MessageFlag.SILENT)
                    );
                }
                return (T) MessageFlagConversion.MAP.getOrDefault(e, canaryprism.discordbridge.api.message.MessageFlag.UNKNOWN);
            } else if (type == PermissionType.class) {
                if (!(value instanceof Permission e)) break conversion_attempt;
                class PermissionTypeConversion {
                    static final Map<Permission, PermissionType> MAP = Map.ofEntries(
                            entry(Permission.ViewChannel.INSTANCE, PermissionType.VIEW_CHANNEL),
                            entry(Permission.ManageChannels.INSTANCE, PermissionType.MANAGE_CHANNEL),
                            entry(Permission.ManageRoles.INSTANCE, PermissionType.MANAGE_ROLES),
                            entry(Permission.CreateGuildExpressions.INSTANCE, PermissionType.CREATE_EXPRESSIONS),
                            entry(Permission.ManageGuildExpressions.INSTANCE, PermissionType.MANAGE_EXPRESSIONS),
                            entry(Permission.ViewAuditLog.INSTANCE, PermissionType.VIEW_AUDIT_LOG),
                            entry(Permission.ViewGuildInsights.INSTANCE, PermissionType.VIEW_SERVER_INSIGHTS),
                            entry(Permission.ManageWebhooks.INSTANCE, PermissionType.MANAGE_WEBHOOKS),
                            entry(Permission.ManageGuild.INSTANCE, PermissionType.MANAGE_SERVER),
                            
                            entry(Permission.CreateInstantInvite.INSTANCE, PermissionType.CREATE_INSTANT_INVITE),
                            entry(Permission.ChangeNickname.INSTANCE, PermissionType.CHANGE_NICKNAME),
                            entry(Permission.ManageNicknames.INSTANCE, PermissionType.MANAGE_NICKNAMES),
                            entry(Permission.KickMembers.INSTANCE, PermissionType.KICK_MEMBERS),
                            entry(Permission.BanMembers.INSTANCE, PermissionType.BAN_MEMBERS),
                            entry(Permission.ModerateMembers.INSTANCE, PermissionType.MODERATE_MEMBERS),
                            
                            entry(Permission.SendMessages.INSTANCE, PermissionType.SEND_MESSAGES),
                            entry(Permission.SendMessagesInThreads.INSTANCE, PermissionType.SEND_MESSAGES_IN_THREADS),
                            entry(Permission.CreatePublicThreads.INSTANCE, PermissionType.CREATE_PUBLIC_THREADS),
                            entry(Permission.CreatePrivateThreads.INSTANCE, PermissionType.CREATE_PRIVATE_THREADS),
                            entry(Permission.EmbedLinks.INSTANCE, PermissionType.EMBED_LINKS),
                            entry(Permission.AttachFiles.INSTANCE, PermissionType.ATTACH_FILE),
                            entry(Permission.AddReactions.INSTANCE, PermissionType.ADD_REACTIONS),
                            entry(Permission.UseExternalEmojis.INSTANCE, PermissionType.USE_EXTERNAL_EMOJIS),
                            entry(Permission.UseExternalStickers.INSTANCE, PermissionType.USE_EXTERNAL_STICKERS),
                            entry(Permission.MentionEveryone.INSTANCE, PermissionType.MENTION_ANYONE),
                            entry(Permission.ManageMessages.INSTANCE, PermissionType.MANAGE_MESSAGES),
                            entry(Permission.ManageThreads.INSTANCE, PermissionType.MANAGE_THREADS),
                            entry(Permission.ReadMessageHistory.INSTANCE, PermissionType.READ_MESSAGE_HISTORY),
                            entry(Permission.SendTTSMessages.INSTANCE, PermissionType.SEND_TTS_MESSAGES),
                            entry(Permission.SendVoiceMessages.INSTANCE, PermissionType.SEND_VOICE_MESSAGES),
                            
                            entry(Permission.Connect.INSTANCE, PermissionType.CONNECT_VOICE),
                            entry(Permission.Speak.INSTANCE, PermissionType.SPEAK),
                            entry(Permission.Stream.INSTANCE, PermissionType.VIDEO),
                            entry(Permission.UseSoundboard.INSTANCE, PermissionType.USE_SOUNDBOARD),
                            entry(Permission.UseExternalSounds.INSTANCE, PermissionType.USE_EXTERNAL_SOUNDBOARD),
                            entry(Permission.UseVAD.INSTANCE, PermissionType.USE_VOICE_ACTIVITY),
                            entry(Permission.PrioritySpeaker.INSTANCE, PermissionType.PRIORITY_SPEAKER),
                            entry(Permission.MuteMembers.INSTANCE, PermissionType.MUTE_MEMBERS),
                            entry(Permission.DeafenMembers.INSTANCE, PermissionType.DEAFEN_MEMBERS),
                            entry(Permission.MoveMembers.INSTANCE, PermissionType.MOVE_MEMBERS),
                            
                            entry(Permission.UseApplicationCommands.INSTANCE, PermissionType.USE_APPLICATION_COMMANDS),
                            entry(Permission.UseEmbeddedActivities.INSTANCE, PermissionType.START_EMBEDDED_ACTIVITIES),
                            
                            entry(Permission.RequestToSpeak.INSTANCE, PermissionType.REQUEST_TO_SPEAK),
                            
                            entry(Permission.CreateEvents.INSTANCE, PermissionType.CREATE_EVENTS),
                            entry(Permission.ManageEvents.INSTANCE, PermissionType.MANAGE_EVENTS),
                            
                            entry(Permission.Administrator.INSTANCE, PermissionType.ADMINISTRATOR),
                            
                            entry(Permission.ViewCreatorMonetizationAnalytics.INSTANCE, PermissionType.VIEW_MONETIZATION_ANALYTICS)
                    );
                }
                return (T) PermissionTypeConversion.MAP.getOrDefault(e, PermissionType.UNKNOWN);
            }
        }
        
        throw new ClassCastException(String.format("Can't convert %s to %s", value, type));
    }
    
    @Override
    public @NotNull Optional<? extends Class<?>> getImplementationType(@NotNull Class<? extends DiscordBridgeApi> type) {
        // the implementation type is stored as the second param in all of the constructors
        // so we can pretty much just fish for those using reflection
        class Holder {
            @SuppressWarnings("unchecked")
            static final Map<Class<? extends DiscordBridgeApi>, Class<?>> map = Stream.of(
                            ChannelImpl.class, MessageChannelImpl.class, ServerChannelImpl.class, ServerMessageChannelImpl.class,
                            UserImpl.class,
                            SlashCommandAutocompleteEventImpl.class, SlashCommandInvokeEventImpl.class,
                            FollowupResponderImpl.class, ImmediateResponderImpl.class, ResponseUpdaterImpl.class,
                            SlashCommandAutocompleteInteractionImpl.class,
                            SlashCommandImpl.class,
                            SlashCommandInteractionImpl.class,
                            // SlashCommandInteractionOptionImpl.class,
                            SlashCommandOptionChoiceImpl.class,
                            SlashCommandOptionImpl.class,
                            AttachmentImpl.class,
                            RoleImpl.class,
                            ServerImpl.class,
                            DiscordApiImpl.class)
                    .map((e) -> entry(
                            ((Class<? extends DiscordBridgeApi>) e.getInterfaces()[0]),
                            e.getConstructors()[0].getParameterTypes()[1]
                    ))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        
        return Optional.ofNullable(Holder.map.get(type));
    }
    
    @Override
    public @NotNull String toString() {
        return "DiscordBridge Kord 0.15.0 Implementation";
    }
    
    public static DiscordLocale convertLocale(@NotNull dev.kord.common.Locale locale) {
        return DiscordLocale.fromLocale(new Locale(locale.getLanguage(), Optional.ofNullable(locale.getCountry()).orElse("")))
                .orElse(DiscordLocale.UNKNOWN);
    }
    
    public static dev.kord.common.Locale convertLocale(@NotNull DiscordLocale locale) {
        return dev.kord.common.Locale.Companion.fromString(locale.locale.toLanguageTag());
    }
    
    public @NotNull ApplicationCommandCreateRequest convertData(@NotNull SlashCommandData data) {
        if (data.getAllowedContexts().isPresent()) {
            throw new UnsupportedOperationException(String.format("%s does not support contexts", this));
        }
        if (data.getAllowedInstallationTypes().isPresent()) {
            throw new UnsupportedOperationException(String.format("%s does not support installation types", this));
        }
        return new ApplicationCommandCreateRequest(
                data.getName(),
                dev.kord.common.entity.optional.Optional.Companion.invoke(data.getNameLocalizations()
                        .entrySet()
                        .stream()
                        .map((e) -> entry(convertLocale(e.getKey()), e.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))),
                ApplicationCommandType.ChatInput.INSTANCE,
                dev.kord.common.entity.optional.Optional.Companion.invoke(data.getDescription()),
                dev.kord.common.entity.optional.Optional.Companion.invoke(data.getDescriptionLocalizations()
                        .entrySet()
                        .stream()
                        .map((e) -> entry(convertLocale(e.getKey()), e.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))),
                dev.kord.common.entity.optional.Optional.Companion.invoke(data.getOptions()
                        .stream()
                        .map(this::convertData)
                        .toList()),
                data.getRequiredPermissions()
                        .<dev.kord.common.entity.optional.Optional<Permissions>>map((set) -> dev.kord.common.entity.optional.Optional.Companion.invoke(
                                PermissionKt.Permissions(set.stream()
                                        .map((e) -> ((Permission) getImplementationValue(e)))
                                        .toArray(Permission[]::new))))
                        .orElse(dev.kord.common.entity.optional.Optional.Companion.invoke()),
                new OptionalBoolean.Value(data.isEnabledInDMs()),
                new OptionalBoolean.Value(!data.isDefaultDisabled()),
                new OptionalBoolean.Value(data.isNSFW())
        );
    }
    
    public ApplicationCommandOption convertData(@NotNull SlashCommandOptionData data) {
        return new ApplicationCommandOption(
                ((ApplicationCommandOptionType) getImplementationValue(data.getType())),
                data.getName(),
                dev.kord.common.entity.optional.Optional.Companion.invoke(data.getNameLocalizations()
                        .entrySet()
                        .stream()
                        .map((e) -> entry(convertLocale(e.getKey()), e.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))),
                data.getDescription(),
                dev.kord.common.entity.optional.Optional.Companion.invoke(data.getDescriptionLocalizations()
                        .entrySet()
                        .stream()
                        .map((e) -> entry(convertLocale(e.getKey()), e.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))),
                OptionalBoolean.Missing.INSTANCE,
                new OptionalBoolean.Value(data.isRequired()),
                dev.kord.common.entity.optional.Optional.Companion.invoke(data.getChoices()
                        .stream()
                        .map(this::convertData)
                        .toList()),
                new OptionalBoolean.Value(data.isAutocompletable()),
                dev.kord.common.entity.optional.Optional.Companion.invoke(data.getOptions()
                        .stream()
                        .map(this::convertData)
                        .toList()),
                dev.kord.common.entity.optional.Optional.Companion.invoke(data.getChannelTypeBounds()
                        .stream()
                        .map((e) -> ((ChannelType) getImplementationValue(e)))
                        .toList()),
                dev.kord.common.entity.optional.Optional.Companion.invoke(JsonElementKt.JsonPrimitive(Optional.<Number>empty()
                        .or(data::getNumberBoundsMin)
                        .or(data::getIntegerBoundsMin)
                        .orElse(null))),
                dev.kord.common.entity.optional.Optional.Companion.invoke(JsonElementKt.JsonPrimitive(Optional.<Number>empty()
                        .or(data::getNumberBoundsMax)
                        .or(data::getIntegerBoundsMax)
                        .orElse(null))),
                data.getStringLengthBoundsMin()
                        .map(Long::intValue)
                        .<dev.kord.common.entity.optional.OptionalInt>map(dev.kord.common.entity.optional.OptionalInt.Value::new)
                        .orElse(dev.kord.common.entity.optional.OptionalInt.Missing.INSTANCE),
                data.getStringLengthBoundsMax()
                        .map(Long::intValue)
                        .<dev.kord.common.entity.optional.OptionalInt>map(dev.kord.common.entity.optional.OptionalInt.Value::new)
                        .orElse(dev.kord.common.entity.optional.OptionalInt.Missing.INSTANCE)
        );
    }
    
    public Choice convertData(@NotNull SlashCommandOptionChoiceData data) {
        dev.kord.common.entity.optional.Optional.Companion.invoke(MapsKt.toMap(data.getNameLocalizations()
                .entrySet()
                .stream()
                .map((e) -> entry(convertLocale(e.getKey()), e.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))));
        return switch (data.getType()) {
            case NUMBER -> new Choice.NumberChoice(
                    data.getName(),
                    dev.kord.common.entity.optional.Optional.Companion.invoke(data.getNameLocalizations()
                            .entrySet()
                            .stream()
                            .map((e) -> entry(convertLocale(e.getKey()), e.getValue()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))),
                    ((Double) data.getValue())
            );
            case INTEGER -> new Choice.IntegerChoice(
                    data.getName(),
                    dev.kord.common.entity.optional.Optional.Companion.invoke(data.getNameLocalizations()
                            .entrySet()
                            .stream()
                            .map((e) -> entry(convertLocale(e.getKey()), e.getValue()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))),
                    ((Integer) data.getValue())
            );
            case STRING -> new Choice.StringChoice(
                    data.getName(),
                    dev.kord.common.entity.optional.Optional.Companion.invoke(data.getNameLocalizations()
                            .entrySet()
                            .stream()
                            .map((e) -> entry(convertLocale(e.getKey()), e.getValue()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))),
                    ((String) data.getValue())
            );
            default -> throw new UnsupportedOperationException(
                    String.format("%s doesn't support option choices for type %s", this, data.getType()));
        };
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends Event> void on(Kord kord, Class<T> type, Consumer<? super T> listener, Logger logger) {
        FlowKt.launchIn(
                FlowKt.onEach(
                        FlowKt.filterIsInstance(
                                FlowKt.buffer(
                                        kord.getEvents(),
                                        kotlinx.coroutines.channels.Channel.BUFFERED,
                                        BufferOverflow.SUSPEND),
                                Reflection.getOrCreateKotlinClass(type)),
                        (e, c) -> {
                            BuildersKt.launch(
                                    kord,
                                    EmptyCoroutineContext.INSTANCE,
                                    CoroutineStart.DEFAULT,
                                    (scope, c2) -> {
                                        try {
                                            listener.accept(((T) e));
                                        } catch (Throwable throwable) {
                                            logger.error("Exception in event listener", throwable);
                                        }
                                        c.resumeWith(Unit.INSTANCE);
                                        return Unit.INSTANCE;
                                    });
                            c.resumeWith(Unit.INSTANCE);
                            return Unit.INSTANCE;
                        }),
                kord);
    }
}
