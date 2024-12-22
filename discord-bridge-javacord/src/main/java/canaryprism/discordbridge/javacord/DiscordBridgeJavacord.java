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

package canaryprism.discordbridge.javacord;

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
import canaryprism.discordbridge.javacord.channel.ChannelImpl;
import canaryprism.discordbridge.javacord.channel.ServerChannelImpl;
import canaryprism.discordbridge.javacord.channel.ServerTextChannelImpl;
import canaryprism.discordbridge.javacord.channel.TextChannelImpl;
import canaryprism.discordbridge.javacord.entities.user.UserImpl;
import canaryprism.discordbridge.javacord.event.interaction.SlashCommandAutocompleteEventImpl;
import canaryprism.discordbridge.javacord.event.interaction.SlashCommandInvokeEventImpl;
import canaryprism.discordbridge.javacord.interaction.response.FollowupResponderImpl;
import canaryprism.discordbridge.javacord.interaction.response.ImmediateResponderImpl;
import canaryprism.discordbridge.javacord.interaction.response.ResponseUpdaterImpl;
import canaryprism.discordbridge.javacord.interaction.slash.*;
import canaryprism.discordbridge.javacord.message.AttachmentImpl;
import canaryprism.discordbridge.javacord.server.ServerImpl;
import canaryprism.discordbridge.javacord.server.permission.RoleImpl;
import org.javacord.api.entity.Attachment;
import org.javacord.api.entity.Mentionable;
import org.javacord.api.entity.channel.*;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/// The Javacord implementation of [DiscordBridge]
public final class DiscordBridgeJavacord implements DiscordBridge {
    
    @Override
    public boolean canLoadApi(@NotNull Object o) {
        return o instanceof org.javacord.api.DiscordApi;
    }
    
    @Override
    public @NotNull DiscordApi loadApi(@NotNull Object api) {
        try {
            return new DiscordApiImpl(this, ((org.javacord.api.DiscordApi) api));
        } catch (ClassCastException e) {
            throw new UnsupportedImplementationException(
                    String.format("discord-bridge-javacord implementation can't load object %s", api));
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T extends PartialSupport> @NotNull Set<? extends @NotNull T> getSupportedValues(Class<T> type) {
        class SupportedValues {
            static final Set<?> slash_command_option_type = getSupported(canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType.class);
            
            static final Set<?> channel_type = getSupported(canaryprism.discordbridge.api.channel.ChannelType.class);
            
            static final Set<?> message_flag_type = getSupported(canaryprism.discordbridge.api.message.MessageFlag.class);
            
            static final Set<?> permission_type = getSupported(canaryprism.discordbridge.api.server.permission.PermissionType.class);
        }
        
        if (type == canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType.class)
            return (Set<? extends T>) SupportedValues.slash_command_option_type;
        
        if (type == canaryprism.discordbridge.api.channel.ChannelType.class)
            return (Set<? extends T>) SupportedValues.channel_type;
        
        if (type == canaryprism.discordbridge.api.message.MessageFlag.class)
            return (Set<? extends T>) SupportedValues.message_flag_type;
        
        if (type == canaryprism.discordbridge.api.server.permission.PermissionType.class)
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
    
    @SuppressWarnings("DuplicateBranchesInSwitch")
    @Override
    public @NotNull Type getInternalTypeRepresentation(@NotNull TypeValue<?> value) {
        if (value instanceof canaryprism.discordbridge.api.channel.ChannelType type)
            return switch (((ChannelType) getImplementationValue(type))) {
                case UNKNOWN -> Channel.class;
                case PRIVATE_CHANNEL -> PrivateChannel.class;
                case GROUP_CHANNEL -> Channel.class;
                case SERVER_TEXT_CHANNEL -> ServerTextChannel.class;
                case SERVER_VOICE_CHANNEL -> ServerVoiceChannel.class;
                case CHANNEL_CATEGORY -> ChannelCategory.class;
                case SERVER_NEWS_CHANNEL -> ServerTextChannel.class;
                case SERVER_STAGE_VOICE_CHANNEL -> ServerStageVoiceChannel.class;
                case SERVER_NEWS_THREAD -> ServerThreadChannel.class;
                case SERVER_PUBLIC_THREAD, SERVER_PRIVATE_THREAD -> ServerThreadChannel.class;
                case SERVER_FORUM_CHANNEL -> ServerForumChannel.class;
                case SERVER_STORE_CHANNEL -> Channel.class;
                case SERVER_DIRECTORY_CHANNEL -> Channel.class;
            };
        else if (value instanceof canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType type)
            return switch (type) {
                case SUBCOMMAND, SUBCOMMAND_GROUP -> Void.class;
                case STRING -> String.class;
                case INTEGER -> Long.class;
                case NUMBER -> Double.class;
                case BOOLEAN -> Boolean.class;
                case USER -> User.class;
                case CHANNEL -> ServerChannel.class;
                case ROLE -> Role.class;
                case MENTIONABLE -> Mentionable.class;
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
                case UNKNOWN -> ChannelType.UNKNOWN;
                case PRIVATE -> ChannelType.PRIVATE_CHANNEL;
                case GROUP -> ChannelType.GROUP_CHANNEL;
                case SERVER_TEXT -> ChannelType.SERVER_TEXT_CHANNEL;
                case SERVER_VOICE -> ChannelType.SERVER_VOICE_CHANNEL;
                case SERVER_CATEGORY -> ChannelType.CHANNEL_CATEGORY;
                case SERVER_NEWS -> ChannelType.SERVER_NEWS_CHANNEL;
                case SERVER_STAGE -> ChannelType.SERVER_STAGE_VOICE_CHANNEL;
                case SERVER_THREAD_NEWS -> ChannelType.SERVER_NEWS_THREAD;
                case SERVER_THREAD_PUBLIC -> ChannelType.SERVER_PUBLIC_THREAD;
                case SERVER_THREAD_PRIVATE -> ChannelType.SERVER_PRIVATE_THREAD;
                case SERVER_FORUM -> ChannelType.SERVER_FORUM_CHANNEL;
                case SERVER_MEDIA -> throw new UnsupportedValueException(type);
                case SERVER_SHOP -> ChannelType.SERVER_STORE_CHANNEL;
                case SERVER_DIRECTORY -> ChannelType.SERVER_DIRECTORY_CHANNEL;
            };
        else if (value instanceof canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType type)
            return switch (type) {
                case UNKNOWN -> SlashCommandOptionType.UNKNOWN;
                case SUBCOMMAND -> SlashCommandOptionType.SUB_COMMAND;
                case SUBCOMMAND_GROUP -> SlashCommandOptionType.SUB_COMMAND_GROUP;
                case STRING -> SlashCommandOptionType.STRING;
                case INTEGER -> SlashCommandOptionType.LONG;
                case NUMBER -> SlashCommandOptionType.DECIMAL;
                case BOOLEAN -> SlashCommandOptionType.BOOLEAN;
                case USER -> SlashCommandOptionType.USER;
                case CHANNEL -> SlashCommandOptionType.CHANNEL;
                case ROLE -> SlashCommandOptionType.ROLE;
                case MENTIONABLE -> SlashCommandOptionType.MENTIONABLE;
                case ATTACHMENT -> SlashCommandOptionType.ATTACHMENT;
            };
        else if (value instanceof canaryprism.discordbridge.api.message.MessageFlag flag)
            return switch (flag) {
                case UNKNOWN -> MessageFlag.UNKNOWN;
                case EPHEMERAL -> MessageFlag.EPHEMERAL;
                case SILENT -> MessageFlag.SUPPRESS_NOTIFICATIONS;
            };
        else if (value instanceof canaryprism.discordbridge.api.server.permission.PermissionType type)
            return switch (type) {
                case UNKNOWN -> throw new UnsupportedValueException(type);
            
                case VIEW_CHANNEL -> PermissionType.VIEW_CHANNEL;
                case MANAGE_CHANNEL -> PermissionType.MANAGE_CHANNELS;
                case MANAGE_ROLES -> PermissionType.MANAGE_ROLES;
                case CREATE_EXPRESSIONS -> throw new UnsupportedValueException(type);
                case MANAGE_EXPRESSIONS -> PermissionType.MANAGE_EMOJIS;
                case VIEW_AUDIT_LOG -> PermissionType.VIEW_AUDIT_LOG;
                case VIEW_SERVER_INSIGHTS -> PermissionType.VIEW_SERVER_INSIGHTS;
                case MANAGE_WEBHOOKS -> PermissionType.MANAGE_WEBHOOKS;
                case MANAGE_SERVER -> PermissionType.MANAGE_SERVER;
                
                case CREATE_INSTANT_INVITE -> PermissionType.CREATE_INSTANT_INVITE;
                case CHANGE_NICKNAME -> PermissionType.CHANGE_NICKNAME;
                case MANAGE_NICKNAMES -> PermissionType.MANAGE_NICKNAMES;
                case KICK_MEMBERS -> PermissionType.KICK_MEMBERS;
                case BAN_MEMBERS -> PermissionType.BAN_MEMBERS;
                case MODERATE_MEMBERS -> PermissionType.MODERATE_MEMBERS;
                
                case SEND_MESSAGES -> PermissionType.SEND_MESSAGES;
                case SEND_MESSAGES_IN_THREADS -> PermissionType.SEND_MESSAGES_IN_THREADS;
                case CREATE_PUBLIC_THREADS -> PermissionType.CREATE_PUBLIC_THREADS;
                case CREATE_PRIVATE_THREADS -> PermissionType.CREATE_PRIVATE_THREADS;
                case EMBED_LINKS -> PermissionType.EMBED_LINKS;
                case ATTACH_FILE -> PermissionType.ATTACH_FILE;
                case ADD_REACTIONS -> PermissionType.ADD_REACTIONS;
                case USE_EXTERNAL_EMOJIS -> PermissionType.USE_EXTERNAL_EMOJIS;
                case USE_EXTERNAL_STICKERS -> PermissionType.USE_EXTERNAL_STICKERS;
                case MENTION_ANYONE -> PermissionType.MENTION_EVERYONE;
                case MANAGE_MESSAGES -> PermissionType.MANAGE_MESSAGES;
                case MANAGE_THREADS -> PermissionType.MANAGE_THREADS;
                case READ_MESSAGE_HISTORY -> PermissionType.READ_MESSAGE_HISTORY;
                case SEND_TTS_MESSAGES -> PermissionType.SEND_TTS_MESSAGES;
                case SEND_VOICE_MESSAGES -> throw new UnsupportedValueException(type);
                case CREATE_POLLS -> throw new UnsupportedValueException(type);
                
                case CONNECT_VOICE -> PermissionType.CONNECT;
                case SPEAK -> PermissionType.SPEAK;
                case VIDEO -> PermissionType.STREAM;
                case USE_SOUNDBOARD -> throw new UnsupportedValueException(type);
                case USE_EXTERNAL_SOUNDBOARD -> throw new UnsupportedValueException(type);
                case USE_VOICE_ACTIVITY -> PermissionType.USE_VOICE_ACTIVITY;
                case PRIORITY_SPEAKER -> PermissionType.PRIORITY_SPEAKER;
                case MUTE_MEMBERS -> PermissionType.MUTE_MEMBERS;
                case DEAFEN_MEMBERS -> PermissionType.DEAFEN_MEMBERS;
                case MOVE_MEMBERS -> PermissionType.MOVE_MEMBERS;
                case SET_CHANNEL_STATUS -> throw new UnsupportedValueException(type);
                
                case USE_APPLICATION_COMMANDS -> PermissionType.USE_APPLICATION_COMMANDS;
                case START_EMBEDDED_ACTIVITIES -> PermissionType.START_EMBEDDED_ACTIVITIES;
                case USE_EXTERNAL_APPS -> throw new UnsupportedValueException(type);
                
                case REQUEST_TO_SPEAK -> PermissionType.REQUEST_TO_SPEAK;
                
                case CREATE_EVENTS -> throw new UnsupportedValueException(type);
                case MANAGE_EVENTS -> throw new UnsupportedValueException(type);
                
                case ADMINISTRATOR -> PermissionType.ADMINISTRATOR;
                
                case VIEW_MONETIZATION_ANALYTICS -> throw new UnsupportedValueException(type);
            };
        
        throw new IllegalArgumentException(String.format("Unreachable; Unknown Value %s", value));

    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T extends DiscordBridgeEnum> @NotNull T convertInternalObject(@NotNull Class<T> type, @NotNull Object value) {

        conversion_attempt: {
            if (type == canaryprism.discordbridge.api.channel.ChannelType.class) {
                if (!(value instanceof ChannelType e)) break conversion_attempt;
                return (T) switch (e) {
                    case SERVER_TEXT_CHANNEL -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_TEXT;
                    case PRIVATE_CHANNEL -> canaryprism.discordbridge.api.channel.ChannelType.PRIVATE;
                    case SERVER_VOICE_CHANNEL -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_VOICE;
                    case GROUP_CHANNEL -> canaryprism.discordbridge.api.channel.ChannelType.GROUP;
                    case CHANNEL_CATEGORY -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_CATEGORY;
                    case SERVER_NEWS_CHANNEL -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_NEWS;
                    case SERVER_STORE_CHANNEL -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_SHOP;
                    case SERVER_NEWS_THREAD -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_THREAD_NEWS;
                    case SERVER_PUBLIC_THREAD -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_THREAD_PUBLIC;
                    case SERVER_PRIVATE_THREAD -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_THREAD_PRIVATE;
                    case SERVER_STAGE_VOICE_CHANNEL -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_STAGE;
                    case SERVER_DIRECTORY_CHANNEL -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_DIRECTORY;
                    case SERVER_FORUM_CHANNEL -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_FORUM;
                    case UNKNOWN -> canaryprism.discordbridge.api.channel.ChannelType.UNKNOWN;
                };
            } else if (type == canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType.class) {
                if (!(value instanceof SlashCommandOptionType e)) break conversion_attempt;
                return (T) switch (e) {
                    case SUB_COMMAND -> canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType.SUBCOMMAND;
                    case SUB_COMMAND_GROUP -> canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType.SUBCOMMAND_GROUP;
                    case STRING -> canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType.STRING;
                    case LONG -> canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType.INTEGER;
                    case BOOLEAN -> canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType.BOOLEAN;
                    case USER -> canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType.USER;
                    case CHANNEL -> canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType.CHANNEL;
                    case ROLE -> canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType.ROLE;
                    case MENTIONABLE -> canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType.MENTIONABLE;
                    case DECIMAL -> canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType.NUMBER;
                    case ATTACHMENT -> canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType.ATTACHMENT;
                    case UNKNOWN -> canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType.UNKNOWN;
                };
            } else if (type == canaryprism.discordbridge.api.message.MessageFlag.class) {
                if (!(value instanceof MessageFlag e)) break conversion_attempt;
                return (T) switch (e) {
                    case EPHEMERAL -> canaryprism.discordbridge.api.message.MessageFlag.EPHEMERAL;
                    case SUPPRESS_NOTIFICATIONS -> canaryprism.discordbridge.api.message.MessageFlag.SILENT;
                    default -> canaryprism.discordbridge.api.message.MessageFlag.UNKNOWN;
                };
            } else if (type == canaryprism.discordbridge.api.server.permission.PermissionType.class) {
                if (!(value instanceof PermissionType e)) break conversion_attempt;
                return (T) switch (e) {
                    case CREATE_INSTANT_INVITE -> canaryprism.discordbridge.api.server.permission.PermissionType.CREATE_INSTANT_INVITE;
                    case KICK_MEMBERS -> canaryprism.discordbridge.api.server.permission.PermissionType.KICK_MEMBERS;
                    case BAN_MEMBERS -> canaryprism.discordbridge.api.server.permission.PermissionType.BAN_MEMBERS;
                    case ADMINISTRATOR -> canaryprism.discordbridge.api.server.permission.PermissionType.ADMINISTRATOR;
                    case MANAGE_CHANNELS -> canaryprism.discordbridge.api.server.permission.PermissionType.MANAGE_CHANNEL;
                    case MANAGE_SERVER -> canaryprism.discordbridge.api.server.permission.PermissionType.MANAGE_SERVER;
                    case ADD_REACTIONS -> canaryprism.discordbridge.api.server.permission.PermissionType.ADD_REACTIONS;
                    case VIEW_AUDIT_LOG -> canaryprism.discordbridge.api.server.permission.PermissionType.VIEW_AUDIT_LOG;
                    case VIEW_SERVER_INSIGHTS -> canaryprism.discordbridge.api.server.permission.PermissionType.VIEW_SERVER_INSIGHTS;
                    case VIEW_CHANNEL -> canaryprism.discordbridge.api.server.permission.PermissionType.VIEW_CHANNEL;
                    case SEND_MESSAGES -> canaryprism.discordbridge.api.server.permission.PermissionType.SEND_MESSAGES;
                    case SEND_TTS_MESSAGES -> canaryprism.discordbridge.api.server.permission.PermissionType.SEND_TTS_MESSAGES;
                    case MANAGE_MESSAGES -> canaryprism.discordbridge.api.server.permission.PermissionType.MANAGE_MESSAGES;
                    case EMBED_LINKS -> canaryprism.discordbridge.api.server.permission.PermissionType.EMBED_LINKS;
                    case ATTACH_FILE -> canaryprism.discordbridge.api.server.permission.PermissionType.ATTACH_FILE;
                    case READ_MESSAGE_HISTORY -> canaryprism.discordbridge.api.server.permission.PermissionType.READ_MESSAGE_HISTORY;
                    case MENTION_EVERYONE -> canaryprism.discordbridge.api.server.permission.PermissionType.MENTION_ANYONE;
                    case USE_EXTERNAL_EMOJIS -> canaryprism.discordbridge.api.server.permission.PermissionType.USE_EXTERNAL_EMOJIS;
                    case USE_EXTERNAL_STICKERS -> canaryprism.discordbridge.api.server.permission.PermissionType.USE_EXTERNAL_STICKERS;
                    case CONNECT -> canaryprism.discordbridge.api.server.permission.PermissionType.CONNECT_VOICE;
                    case SPEAK -> canaryprism.discordbridge.api.server.permission.PermissionType.SPEAK;
                    case MUTE_MEMBERS -> canaryprism.discordbridge.api.server.permission.PermissionType.MUTE_MEMBERS;
                    case DEAFEN_MEMBERS -> canaryprism.discordbridge.api.server.permission.PermissionType.DEAFEN_MEMBERS;
                    case MOVE_MEMBERS -> canaryprism.discordbridge.api.server.permission.PermissionType.MOVE_MEMBERS;
                    case USE_VOICE_ACTIVITY -> canaryprism.discordbridge.api.server.permission.PermissionType.USE_VOICE_ACTIVITY;
                    case PRIORITY_SPEAKER -> canaryprism.discordbridge.api.server.permission.PermissionType.PRIORITY_SPEAKER;
                    case STREAM -> canaryprism.discordbridge.api.server.permission.PermissionType.VIDEO;
                    case REQUEST_TO_SPEAK -> canaryprism.discordbridge.api.server.permission.PermissionType.REQUEST_TO_SPEAK;
                    case START_EMBEDDED_ACTIVITIES -> canaryprism.discordbridge.api.server.permission.PermissionType.START_EMBEDDED_ACTIVITIES;
                    case MANAGE_THREADS -> canaryprism.discordbridge.api.server.permission.PermissionType.MANAGE_THREADS;
                    case CREATE_PUBLIC_THREADS -> canaryprism.discordbridge.api.server.permission.PermissionType.CREATE_PUBLIC_THREADS;
                    case CREATE_PRIVATE_THREADS -> canaryprism.discordbridge.api.server.permission.PermissionType.CREATE_PRIVATE_THREADS;
                    case SEND_MESSAGES_IN_THREADS -> canaryprism.discordbridge.api.server.permission.PermissionType.SEND_MESSAGES_IN_THREADS;
                    case CHANGE_NICKNAME -> canaryprism.discordbridge.api.server.permission.PermissionType.CHANGE_NICKNAME;
                    case MANAGE_NICKNAMES -> canaryprism.discordbridge.api.server.permission.PermissionType.MANAGE_NICKNAMES;
                    case MANAGE_ROLES -> canaryprism.discordbridge.api.server.permission.PermissionType.MANAGE_ROLES;
                    case MANAGE_WEBHOOKS -> canaryprism.discordbridge.api.server.permission.PermissionType.MANAGE_WEBHOOKS;
                    case MANAGE_EMOJIS -> canaryprism.discordbridge.api.server.permission.PermissionType.MANAGE_EXPRESSIONS;
                    case USE_APPLICATION_COMMANDS -> canaryprism.discordbridge.api.server.permission.PermissionType.USE_APPLICATION_COMMANDS;
                    case MODERATE_MEMBERS -> canaryprism.discordbridge.api.server.permission.PermissionType.MODERATE_MEMBERS;
                };
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
                    ChannelImpl.class, TextChannelImpl.class, ServerChannelImpl.class, ServerTextChannelImpl.class,
                    UserImpl.class,
                    SlashCommandAutocompleteEventImpl.class, SlashCommandInvokeEventImpl.class,
                    FollowupResponderImpl.class, ImmediateResponderImpl.class, ResponseUpdaterImpl.class,
                    SlashCommandAutocompleteInteractionImpl.class,
                    SlashCommandImpl.class,
                    SlashCommandInteractionImpl.class,
                    SlashCommandInteractionOptionImpl.class,
                    SlashCommandOptionChoiceImpl.class,
                    SlashCommandOptionImpl.class,
                    AttachmentImpl.class,
                    RoleImpl.class,
                    ServerImpl.class,
                    DiscordApiImpl.class)
                    .map((e) -> Map.entry(
                            ((Class<? extends DiscordBridgeApi>) e.getInterfaces()[0]),
                            e.getConstructors()[0].getParameterTypes()[1]
                    ))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        
        return Optional.ofNullable(Holder.map.get(type));
    }
    
    @Override
    public @NotNull String toString() {
        return "DiscordBridge Javacord 3.8.0 Implementation";
    }
    
    public static canaryprism.discordbridge.api.misc.DiscordLocale convertLocale(@NotNull DiscordLocale locale) {
        return canaryprism.discordbridge.api.misc.DiscordLocale.fromLocale(Locale.forLanguageTag(locale.getLocaleCode()))
                .orElse(canaryprism.discordbridge.api.misc.DiscordLocale.UNKNOWN);
    }
    
    public static DiscordLocale convertLocale(@NotNull canaryprism.discordbridge.api.misc.DiscordLocale locale) {
        return DiscordLocale.fromLocaleCode(locale.locale.toLanguageTag());
    }
    
    public @NotNull SlashCommandBuilder convertData(@NotNull SlashCommandData data) {
        var builder = new SlashCommandBuilder()
                .setName(data.getName())
                .setDescription(data.getDescription())
                .setOptions(data.getOptions()
                        .stream()
                        .map(this::convertData)
                        .toList())
                .setEnabledInDms(data.isEnabledInDMs())
                .setNsfw(data.isNSFW());
        
        if (data.getAllowedContexts().isPresent())
            throw new UnsupportedOperationException(String.format("%s does not support contexts", this));
        
        if (data.isDefaultDisabled())
            builder.setDefaultDisabled();
        
        data.getRequiredPermissions()
                .map((e) -> e.stream()
                        .map(this::getImplementationValue)
                        .map(PermissionType.class::cast)
                        .collect(Collectors.toCollection(() -> EnumSet.noneOf(PermissionType.class))))
                .ifPresent(builder::setDefaultEnabledForPermissions);
        
        for (var e : data.getNameLocalizations().entrySet()) {
            if (convertLocale(e.getKey()) != DiscordLocale.UNKNOWN)
                builder.addNameLocalization(convertLocale(e.getKey()), e.getValue());
        }
        for (var e : data.getDescriptionLocalizations().entrySet()) {
            if (convertLocale(e.getKey()) != DiscordLocale.UNKNOWN)
                builder.addDescriptionLocalization(convertLocale(e.getKey()), e.getValue());
        }
        
        return builder;
    }
    
    public SlashCommandOption convertData(@NotNull SlashCommandOptionData data) {
        var builder = new SlashCommandOptionBuilder()
                .setName(data.getName())
                .setDescription(data.getDescription())
                .setType(((SlashCommandOptionType) getImplementationValue(data.getType())))
                .setOptions(data.getOptions()
                        .stream()
                        .map(this::convertData)
                        .toList())
                .setChoices(data.getChoices()
                        .stream()
                        .map(this::convertData)
                        .toList())
                .setRequired(data.isRequired())
                .setAutocompletable(data.isAutocompletable())
                .setChannelTypes(data.getChannelTypeBounds()
                        .stream()
                        .map(this::getImplementationValue)
                        .map(ChannelType.class::cast)
                        .collect(Collectors.toUnmodifiableSet()));
        
        data.getIntegerBoundsMin().ifPresent(builder::setLongMinValue);
        data.getIntegerBoundsMax().ifPresent(builder::setLongMaxValue);
        data.getNumberBoundsMin().ifPresent(builder::setDecimalMinValue);
        data.getNumberBoundsMax().ifPresent(builder::setDecimalMaxValue);
        data.getStringLengthBoundsMin().ifPresent(builder::setMinLength);
        data.getStringLengthBoundsMax().ifPresent(builder::setMaxLength);
        
        for (var e : data.getNameLocalizations().entrySet()) {
            if (convertLocale(e.getKey()) != DiscordLocale.UNKNOWN)
                builder.addNameLocalization(convertLocale(e.getKey()), e.getValue());
        }
        for (var e : data.getDescriptionLocalizations().entrySet()) {
            if (convertLocale(e.getKey()) != DiscordLocale.UNKNOWN)
                builder.addDescriptionLocalization(convertLocale(e.getKey()), e.getValue());
        }
        
        return builder.build();
    }
    
    public SlashCommandOptionChoice convertData(@NotNull SlashCommandOptionChoiceData data) {
        var builder = new SlashCommandOptionChoiceBuilder()
                .setName(data.getName());
        
        switch (data.getType()) {
            case INTEGER -> builder.setValue(((Long) data.getValue()));
            case STRING -> builder.setValue(((String) data.getValue()));
            default -> throw new UnsupportedOperationException(
                    String.format("%s doesn't support option choices for type %s", this, data.getType()));
        }
        
        for (var e : data.getNameLocalizations().entrySet()) {
            if (convertLocale(e.getKey()) != DiscordLocale.UNKNOWN)
                builder.addNameLocalization(convertLocale(e.getKey()), e.getValue());
        }
        
        return builder.build();
    }
}
