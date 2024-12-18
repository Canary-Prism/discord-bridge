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

package canaryprism.discordbridge.jda;

import canaryprism.discordbridge.api.DiscordApi;
import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.data.interaction.slash.SlashCommandData;
import canaryprism.discordbridge.api.data.interaction.slash.SlashCommandOptionChoiceData;
import canaryprism.discordbridge.api.data.interaction.slash.SlashCommandOptionData;
import canaryprism.discordbridge.api.enums.DiscordBridgeEnum;
import canaryprism.discordbridge.api.enums.PartialSupport;
import canaryprism.discordbridge.api.enums.TypeValue;
import canaryprism.discordbridge.api.exceptions.UnsupportedImplementationException;
import canaryprism.discordbridge.api.exceptions.UnsupportedValueException;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType;
import canaryprism.discordbridge.api.server.permission.PermissionType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.MessageFlag;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/// The JDA implementation of [DiscordBridge]
public final class DiscordBridgeJDA implements DiscordBridge {
    
    @Override
    public boolean canLoadApi(@NotNull Object o) {
        return o instanceof JDA;
    }
    
    @Override
    public @NotNull DiscordApi loadApi(@NotNull Object api) {
        try {
            return new DiscordApiImpl(this, ((JDA) api));
        } catch (ClassCastException e) {
            throw new UnsupportedImplementationException(
                    String.format("discord-bridge-javacord implementation can't load object %s", api));
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T extends PartialSupport> @NotNull Set<? extends @NotNull T> getSupportedValues(Class<T> type) {
        class SupportedValues {
            static final Set<?> slash_command_option_type = getSupported(SlashCommandOptionType.class);
            
            static final Set<?> channel_type = getSupported(canaryprism.discordbridge.api.channel.ChannelType.class);
            static final Set<?> permission_type = getSupported(PermissionType.class);
        }
        
        if (type == SlashCommandOptionType.class)
            return (Set<? extends T>) SupportedValues.slash_command_option_type;
        
        if (type == canaryprism.discordbridge.api.channel.ChannelType.class)
            return (Set<? extends T>) SupportedValues.channel_type;
        
        if (type == PermissionType.class)
            return (Set<? extends T>) SupportedValues.permission_type;
        
        return Set.of();
    }
    
    private static <T extends PartialSupport> Set<T> getSupported(Class<T> type) {
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
        if (value instanceof canaryprism.discordbridge.api.channel.ChannelType type)
            return ((ChannelType) getImplementationValue(type)).getInterface();
        else if (value instanceof SlashCommandOptionType type)
            return switch (type) {
                case UNKNOWN -> Object.class;
                case SUBCOMMAND, SUBCOMMAND_GROUP -> Void.class;
                case STRING -> String.class;
                case INTEGER -> Long.class;
                case NUMBER -> Double.class;
                case BOOLEAN -> Boolean.class;
                case USER -> User.class;
                case CHANNEL -> GuildChannelUnion.class;
                case ROLE -> Role.class;
                case MENTIONABLE -> IMentionable.class;
                case ATTACHMENT -> Message.Attachment.class;
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
                case PRIVATE -> ChannelType.PRIVATE;
                case GROUP -> ChannelType.GROUP;
                case SERVER_TEXT -> ChannelType.TEXT;
                case SERVER_VOICE -> ChannelType.VOICE;
                case SERVER_CATEGORY -> ChannelType.CATEGORY;
                case SERVER_NEWS -> ChannelType.NEWS;
                case SERVER_STAGE -> ChannelType.STAGE;
                case SERVER_THREAD_NEWS -> ChannelType.GUILD_NEWS_THREAD;
                case SERVER_THREAD_PUBLIC -> ChannelType.GUILD_PUBLIC_THREAD;
                case SERVER_THREAD_PRIVATE -> ChannelType.GUILD_PRIVATE_THREAD;
                case SERVER_FORUM -> ChannelType.FORUM;
                case SERVER_MEDIA -> ChannelType.MEDIA;
                case SERVER_SHOP -> throw new UnsupportedValueException(type);
                case SERVER_DIRECTORY -> throw new UnsupportedValueException(type);
            };
        else if (value instanceof SlashCommandOptionType type) 
            return switch (type) {
                case UNKNOWN -> OptionType.UNKNOWN;
                case SUBCOMMAND -> OptionType.SUB_COMMAND;
                case SUBCOMMAND_GROUP -> OptionType.SUB_COMMAND_GROUP;
                case STRING -> OptionType.STRING;
                case INTEGER -> OptionType.INTEGER;
                case NUMBER -> OptionType.NUMBER;
                case BOOLEAN -> OptionType.BOOLEAN;
                case USER -> OptionType.USER;
                case CHANNEL -> OptionType.CHANNEL;
                case ROLE -> OptionType.ROLE;
                case MENTIONABLE -> OptionType.MENTIONABLE;
                case ATTACHMENT -> OptionType.ATTACHMENT;
            };
        else if (value instanceof canaryprism.discordbridge.api.message.MessageFlag flag)
            return switch (flag) {
                case UNKNOWN -> throw new UnsupportedValueException(flag);
                case EPHEMERAL -> MessageFlag.EPHEMERAL;
                case SILENT -> MessageFlag.NOTIFICATIONS_SUPPRESSED;
            };
        else if (value instanceof PermissionType type)
            return switch (type) {
                case UNKNOWN -> Permission.UNKNOWN;
                case VIEW_CHANNEL -> Permission.VIEW_CHANNEL;
                case MANAGE_CHANNEL -> Permission.MANAGE_CHANNEL;
                case MANAGE_ROLES -> Permission.MANAGE_ROLES;
                case CREATE_EXPRESSIONS -> Permission.CREATE_GUILD_EXPRESSIONS;
                case MANAGE_EXPRESSIONS -> Permission.MANAGE_GUILD_EXPRESSIONS;
                case VIEW_AUDIT_LOG -> Permission.VIEW_AUDIT_LOGS;
                case VIEW_SERVER_INSIGHTS -> Permission.VIEW_GUILD_INSIGHTS;
                case MANAGE_WEBHOOKS -> Permission.MANAGE_WEBHOOKS;
                case MANAGE_SERVER -> Permission.MANAGE_SERVER;
                case CREATE_INSTANT_INVITE -> Permission.CREATE_INSTANT_INVITE;
                case CHANGE_NICKNAME -> Permission.NICKNAME_CHANGE;
                case MANAGE_NICKNAMES -> Permission.NICKNAME_MANAGE;
                case KICK_MEMBERS -> Permission.KICK_MEMBERS;
                case BAN_MEMBERS -> Permission.BAN_MEMBERS;
                case MODERATE_MEMBERS -> Permission.MODERATE_MEMBERS;
                case SEND_MESSAGES -> Permission.MESSAGE_SEND;
                case SEND_MESSAGES_IN_THREADS -> Permission.MESSAGE_SEND_IN_THREADS;
                case CREATE_PUBLIC_THREADS -> Permission.CREATE_PUBLIC_THREADS;
                case CREATE_PRIVATE_THREADS -> Permission.CREATE_PRIVATE_THREADS;
                case EMBED_LINKS -> Permission.MESSAGE_EMBED_LINKS;
                case ATTACH_FILE -> Permission.MESSAGE_ATTACH_FILES;
                case ADD_REACTIONS -> Permission.MESSAGE_ADD_REACTION;
                case USE_EXTERNAL_EMOJIS -> Permission.MESSAGE_EXT_EMOJI;
                case USE_EXTERNAL_STICKERS -> Permission.MESSAGE_EXT_STICKER;
                case MENTION_ANYONE -> Permission.MESSAGE_MENTION_EVERYONE;
                case MANAGE_MESSAGES -> Permission.MESSAGE_MANAGE;
                case MANAGE_THREADS -> Permission.MANAGE_THREADS;
                case READ_MESSAGE_HISTORY -> Permission.MESSAGE_HISTORY;
                case SEND_TTS_MESSAGES -> Permission.MESSAGE_TTS;
                case SEND_VOICE_MESSAGES -> Permission.MESSAGE_ATTACH_VOICE_MESSAGE;
                case CREATE_POLLS -> Permission.MESSAGE_SEND_POLLS;
                case CONNECT_VOICE -> Permission.VOICE_CONNECT;
                case SPEAK -> Permission.VOICE_SPEAK;
                case VIDEO -> Permission.VOICE_STREAM;
                case USE_SOUNDBOARD -> Permission.VOICE_USE_SOUNDBOARD;
                case USE_EXTERNAL_SOUNDBOARD -> Permission.VOICE_USE_EXTERNAL_SOUNDS;
                case USE_VOICE_ACTIVITY -> Permission.VOICE_USE_VAD;
                case PRIORITY_SPEAKER -> Permission.PRIORITY_SPEAKER;
                case MUTE_MEMBERS -> Permission.VOICE_MUTE_OTHERS;
                case DEAFEN_MEMBERS -> Permission.VOICE_DEAF_OTHERS;
                case MOVE_MEMBERS -> Permission.VOICE_MOVE_OTHERS;
                case SET_CHANNEL_STATUS -> Permission.VOICE_SET_STATUS;
                case USE_APPLICATION_COMMANDS -> Permission.USE_APPLICATION_COMMANDS;
                case START_EMBEDDED_ACTIVITIES -> Permission.USE_EMBEDDED_ACTIVITIES;
                case USE_EXTERNAL_APPS -> Permission.USE_EXTERNAL_APPLICATIONS;
                case REQUEST_TO_SPEAK -> Permission.REQUEST_TO_SPEAK;
                case CREATE_EVENTS -> Permission.CREATE_SCHEDULED_EVENTS;
                case MANAGE_EVENTS -> Permission.MANAGE_EVENTS;
                case ADMINISTRATOR -> Permission.ADMINISTRATOR;
                case VIEW_MONETIZATION_ANALYTICS -> Permission.VIEW_CREATOR_MONETIZATION_ANALYTICS;
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
                    case TEXT -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_TEXT;
                    case PRIVATE -> canaryprism.discordbridge.api.channel.ChannelType.PRIVATE;
                    case VOICE -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_VOICE;
                    case GROUP -> canaryprism.discordbridge.api.channel.ChannelType.GROUP;
                    case CATEGORY -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_CATEGORY;
                    case NEWS -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_NEWS;
                    case STAGE -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_STAGE;
                    case GUILD_NEWS_THREAD -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_THREAD_NEWS;
                    case GUILD_PUBLIC_THREAD -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_THREAD_PUBLIC;
                    case GUILD_PRIVATE_THREAD -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_THREAD_PRIVATE;
                    case FORUM -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_FORUM;
                    case MEDIA -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_MEDIA;
                    case UNKNOWN -> canaryprism.discordbridge.api.channel.ChannelType.UNKNOWN;
                };
            } else if (type == SlashCommandOptionType.class) {
                if (!(value instanceof OptionType e)) break conversion_attempt;
                return (T) switch (e) {
                    case UNKNOWN -> SlashCommandOptionType.UNKNOWN;
                    case SUB_COMMAND -> SlashCommandOptionType.SUBCOMMAND;
                    case SUB_COMMAND_GROUP -> SlashCommandOptionType.SUBCOMMAND_GROUP;
                    case STRING -> SlashCommandOptionType.STRING;
                    case INTEGER -> SlashCommandOptionType.INTEGER;
                    case BOOLEAN -> SlashCommandOptionType.BOOLEAN;
                    case USER -> SlashCommandOptionType.USER;
                    case CHANNEL -> SlashCommandOptionType.CHANNEL;
                    case ROLE -> SlashCommandOptionType.ROLE;
                    case MENTIONABLE -> SlashCommandOptionType.MENTIONABLE;
                    case NUMBER -> SlashCommandOptionType.NUMBER;
                    case ATTACHMENT -> SlashCommandOptionType.ATTACHMENT;
                };
            } else if (type == canaryprism.discordbridge.api.message.MessageFlag.class) {
                if (!(value instanceof MessageFlag e)) break conversion_attempt;
                return (T) switch (e) {
                    case EPHEMERAL -> canaryprism.discordbridge.api.message.MessageFlag.EPHEMERAL;
                    case NOTIFICATIONS_SUPPRESSED -> canaryprism.discordbridge.api.message.MessageFlag.SILENT;
                    default -> canaryprism.discordbridge.api.message.MessageFlag.UNKNOWN;
                };
            } else if (type == PermissionType.class) {
                if (!(value instanceof Permission e)) break conversion_attempt;
                return (T) switch (e) {
                    case MANAGE_CHANNEL -> PermissionType.MANAGE_CHANNEL;
                    case MANAGE_SERVER -> PermissionType.MANAGE_SERVER;
                    case VIEW_AUDIT_LOGS -> PermissionType.VIEW_AUDIT_LOG;
                    case VIEW_CHANNEL -> PermissionType.VIEW_CHANNEL;
                    case VIEW_GUILD_INSIGHTS -> PermissionType.VIEW_SERVER_INSIGHTS;
                    case MANAGE_ROLES, MANAGE_PERMISSIONS -> PermissionType.MANAGE_ROLES;
                    case MANAGE_WEBHOOKS -> PermissionType.MANAGE_WEBHOOKS;
                    case MANAGE_GUILD_EXPRESSIONS -> PermissionType.MANAGE_EXPRESSIONS;
                    case MANAGE_EVENTS -> PermissionType.MANAGE_EVENTS;
                    //noinspection deprecation
                    case USE_EMBEDDED_ACTIVITIES, VOICE_START_ACTIVITIES -> PermissionType.START_EMBEDDED_ACTIVITIES;
                    case VIEW_CREATOR_MONETIZATION_ANALYTICS -> PermissionType.VIEW_MONETIZATION_ANALYTICS;
                    case CREATE_GUILD_EXPRESSIONS -> PermissionType.CREATE_EXPRESSIONS;
                    case CREATE_SCHEDULED_EVENTS -> PermissionType.CREATE_EVENTS;
                    case CREATE_INSTANT_INVITE -> PermissionType.CREATE_INSTANT_INVITE;
                    case KICK_MEMBERS -> PermissionType.KICK_MEMBERS;
                    case BAN_MEMBERS -> PermissionType.BAN_MEMBERS;
                    case NICKNAME_CHANGE -> PermissionType.CHANGE_NICKNAME;
                    case NICKNAME_MANAGE -> PermissionType.MANAGE_NICKNAMES;
                    case MODERATE_MEMBERS -> PermissionType.MODERATE_MEMBERS;
                    case MESSAGE_ADD_REACTION -> PermissionType.ADD_REACTIONS;
                    case MESSAGE_SEND -> PermissionType.SEND_MESSAGES;
                    case MESSAGE_TTS -> PermissionType.SEND_TTS_MESSAGES;
                    case MESSAGE_MANAGE -> PermissionType.MANAGE_MESSAGES;
                    case MESSAGE_EMBED_LINKS -> PermissionType.EMBED_LINKS;
                    case MESSAGE_ATTACH_FILES -> PermissionType.ATTACH_FILE;
                    case MESSAGE_HISTORY -> PermissionType.READ_MESSAGE_HISTORY;
                    case MESSAGE_MENTION_EVERYONE -> PermissionType.MENTION_ANYONE;
                    case MESSAGE_EXT_EMOJI -> PermissionType.USE_EXTERNAL_EMOJIS;
                    case USE_APPLICATION_COMMANDS -> PermissionType.USE_APPLICATION_COMMANDS;
                    case MESSAGE_EXT_STICKER -> PermissionType.USE_EXTERNAL_STICKERS;
                    case MESSAGE_ATTACH_VOICE_MESSAGE -> PermissionType.SEND_VOICE_MESSAGES;
                    case MESSAGE_SEND_POLLS -> PermissionType.CREATE_POLLS;
                    case USE_EXTERNAL_APPLICATIONS -> PermissionType.USE_EXTERNAL_APPS;
                    case MANAGE_THREADS -> PermissionType.MANAGE_THREADS;
                    case CREATE_PUBLIC_THREADS -> PermissionType.CREATE_PUBLIC_THREADS;
                    case CREATE_PRIVATE_THREADS -> PermissionType.CREATE_PRIVATE_THREADS;
                    case MESSAGE_SEND_IN_THREADS -> PermissionType.SEND_MESSAGES_IN_THREADS;
                    case PRIORITY_SPEAKER -> PermissionType.PRIORITY_SPEAKER;
                    case VOICE_STREAM -> PermissionType.VIDEO;
                    case VOICE_CONNECT -> PermissionType.CONNECT_VOICE;
                    case VOICE_SPEAK -> PermissionType.SPEAK;
                    case VOICE_MUTE_OTHERS -> PermissionType.MUTE_MEMBERS;
                    case VOICE_DEAF_OTHERS -> PermissionType.DEAFEN_MEMBERS;
                    case VOICE_MOVE_OTHERS -> PermissionType.MOVE_MEMBERS;
                    case VOICE_USE_VAD -> PermissionType.USE_VOICE_ACTIVITY;
                    case VOICE_USE_SOUNDBOARD -> PermissionType.USE_SOUNDBOARD;
                    case VOICE_USE_EXTERNAL_SOUNDS -> PermissionType.USE_EXTERNAL_SOUNDBOARD;
                    case VOICE_SET_STATUS -> PermissionType.SET_CHANNEL_STATUS;
                    case REQUEST_TO_SPEAK -> PermissionType.REQUEST_TO_SPEAK;
                    case ADMINISTRATOR -> PermissionType.ADMINISTRATOR;
                    case UNKNOWN -> PermissionType.UNKNOWN;
                };
            }
        }
        
        throw new ClassCastException(String.format("Can't convert %s to %s", value, type));
    }
    
    @Override
    public @NotNull String toString() {
        return "DiscordBridge JDA Implementation";
    }
    
    public static Locale convertLocale(@NotNull DiscordLocale locale) {
        return Locale.forLanguageTag(locale.getLocale());
    }
    
    public static DiscordLocale convertLocale(@NotNull Locale locale) {
        return DiscordLocale.from(locale);
    }
    
    public @NotNull net.dv8tion.jda.api.interactions.commands.build.SlashCommandData convertData(@NotNull SlashCommandData data) {
        var builder = Commands.slash(data.getName(), data.getDescription())
                .setNameLocalizations(data.getNameLocalizations()
                        .entrySet()
                        .stream()
                        .map((e) -> Map.entry(
                                convertLocale(e.getKey()), e.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .setDescriptionLocalizations(data.getDescriptionLocalizations()
                        .entrySet()
                        .stream()
                        .map((e) -> Map.entry(
                                convertLocale(e.getKey()), e.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .addOptions(data.getOptions()
                        .stream()
                        .map(this::convertData)
                        .map(OptionData.class::cast)
                        .toList())
                .setGuildOnly(!data.isEnabledInDMs())
                .setNSFW(data.isNSFW());
        
        if (data.isDefaultDisabled())
            builder.setDefaultPermissions(DefaultMemberPermissions.DISABLED);
        
        data.getRequiredPermissions()
                .map((e) -> e.stream()
                        .map(this::getImplementationValue)
                        .map(Permission.class::cast)
                        .collect(Collectors.toSet()))
                .ifPresent((e) -> builder.setDefaultPermissions(DefaultMemberPermissions.enabledFor(e)));
        
        return builder;
    }
    
    public Object convertData(@NotNull SlashCommandOptionData data) {
        return switch (data.getType()) {
            case SUBCOMMAND_GROUP -> new SubcommandGroupData(data.getName(), data.getDescription())
                    .setNameLocalizations(data.getNameLocalizations()
                            .entrySet()
                            .stream()
                            .map((e) -> Map.entry(
                                    convertLocale(e.getKey()), e.getValue()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                    .setDescriptionLocalizations(data.getDescriptionLocalizations()
                            .entrySet()
                            .stream()
                            .map((e) -> Map.entry(
                                    convertLocale(e.getKey()), e.getValue()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                    .addSubcommands(data.getOptions()
                            .stream()
                            .map(this::convertData)
                            .map(SubcommandData.class::cast)
                            .toList());
            
            case SUBCOMMAND -> new SubcommandData(data.getName(), data.getDescription())
                    .setNameLocalizations(data.getNameLocalizations()
                            .entrySet()
                            .stream()
                            .map((e) -> Map.entry(
                                    convertLocale(e.getKey()), e.getValue()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                    .setDescriptionLocalizations(data.getDescriptionLocalizations()
                            .entrySet()
                            .stream()
                            .map((e) -> Map.entry(
                                    convertLocale(e.getKey()), e.getValue()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                    .addOptions(data.getOptions()
                            .stream()
                            .map(this::convertData)
                            .map(OptionData.class::cast)
                            .toList());
            
            default -> {
                var builder = new OptionData(((OptionType) getImplementationValue(data.getType())), data.getName(), data.getDescription())
                        .setNameLocalizations(data.getNameLocalizations()
                                .entrySet()
                                .stream()
                                .map((e) -> Map.entry(
                                        convertLocale(e.getKey()), e.getValue()))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                        .setDescriptionLocalizations(data.getDescriptionLocalizations()
                                .entrySet()
                                .stream()
                                .map((e) -> Map.entry(
                                        convertLocale(e.getKey()), e.getValue()))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                        .addChoices(data.getChoices()
                                .stream()
                                .map(this::convertData)
                                .toList())
                        .setRequired(data.isRequired())
                        .setAutoComplete(data.isAutocompletable())
                        .setChannelTypes(data.getChannelTypeBounds()
                                .stream()
                                .map(this::getImplementationValue)
                                .map(ChannelType.class::cast)
                                .collect(Collectors.toUnmodifiableSet()));
                
                data.getIntegerBoundsMin().ifPresent(builder::setMinValue);
                data.getIntegerBoundsMax().ifPresent(builder::setMaxValue);
                data.getNumberBoundsMin().ifPresent(builder::setMinValue);
                data.getNumberBoundsMax().ifPresent(builder::setMaxValue);
                data.getStringLengthBoundsMin().map(Long::intValue).ifPresent(builder::setMinLength);
                data.getStringLengthBoundsMax().map(Long::intValue).ifPresent(builder::setMaxLength);
                
                yield builder;
            }
        };
    }
    
    public Command.Choice convertData(@NotNull SlashCommandOptionChoiceData data) {
        return (switch (data.getType()) {
            case INTEGER -> new Command.Choice(data.getName(), ((Long) data.getValue()));
            case NUMBER -> new Command.Choice(data.getName(), ((Double) data.getValue()));
            case STRING -> new Command.Choice(data.getName(), ((String) data.getValue()));
            default -> throw new UnsupportedOperationException(
                    String.format("%s doesn't support option choices for type %s", this, data.getType()));
        }).setNameLocalizations(data.getNameLocalizations()
                .entrySet()
                .stream()
                .map((e) -> Map.entry(
                        convertLocale(e.getKey()), e.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }
}