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

package canaryprism.discordbridge.discordjar;

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
import canaryprism.discordbridge.api.interaction.Command;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType;
import canaryprism.discordbridge.api.misc.DiscordLocale;
import canaryprism.discordbridge.api.server.permission.PermissionType;
import com.seailz.discordjar.DiscordJar;
import com.seailz.discordjar.command.CommandChoice;
import com.seailz.discordjar.command.CommandOption;
import com.seailz.discordjar.command.CommandOptionType;
import com.seailz.discordjar.command.CommandType;
import com.seailz.discordjar.command.listeners.slash.SlashSubCommand;
import com.seailz.discordjar.model.channel.utils.ChannelType;
import com.seailz.discordjar.model.message.MessageFlag;
import com.seailz.discordjar.utils.permission.Permission;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DiscordBridgeDiscordJar implements DiscordBridge {
    
    @Override
    public boolean canLoadApi(@NotNull Object o) {
        return o instanceof DiscordJar;
    }
    
    @Override
    public @NotNull DiscordApi loadApi(@NotNull Object api) {
        try {
            return new DiscordApiImpl(this, ((DiscordJar) api));
        } catch (ClassCastException e) {
            throw new UnsupportedImplementationException(
                    String.format("discord-bridge-discord-jar implementation can't load object %s", api));
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
        else if (value instanceof SlashCommandOptionType type)
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
                case UNKNOWN -> throw new UnsupportedValueException(type);
                case PRIVATE -> ChannelType.DM;
                case GROUP -> ChannelType.GROUP_DM;
                case SERVER_TEXT -> ChannelType.GUILD_TEXT;
                case SERVER_VOICE -> ChannelType.GUILD_VOICE;
                case SERVER_CATEGORY -> ChannelType.GUILD_CATEGORY;
                case SERVER_NEWS -> ChannelType.GUILD_ANNOUNCEMENT;
                case SERVER_STAGE -> ChannelType.GUILD_STAGE_VOICE;
                case SERVER_THREAD_NEWS -> ChannelType.ANNOUNCEMENT_THREAD;
                case SERVER_THREAD_PUBLIC -> ChannelType.PUBLIC_THREAD;
                case SERVER_THREAD_PRIVATE -> ChannelType.PRIVATE_THREAD;
                case SERVER_FORUM -> ChannelType.GUILD_FORUM;
                case SERVER_MEDIA -> ChannelType.GUILD_MEDIA;
                case SERVER_SHOP -> throw new UnsupportedValueException(type);
                case SERVER_DIRECTORY -> ChannelType.GUILD_DIRECTORY;
            };
        else if (value instanceof SlashCommandOptionType type)
            return switch (type) {
                case UNKNOWN -> throw new UnsupportedValueException(type);
                case SUBCOMMAND -> CommandOptionType.SUB_COMMAND;
                case SUBCOMMAND_GROUP -> CommandOptionType.SUB_COMMAND_GROUP;
                case STRING -> CommandOptionType.STRING;
                case INTEGER -> CommandOptionType.INTEGER;
                case NUMBER -> CommandOptionType.NUMBER;
                case BOOLEAN -> CommandOptionType.BOOLEAN;
                case USER -> CommandOptionType.USER;
                case CHANNEL -> CommandOptionType.CHANNEL;
                case ROLE -> CommandOptionType.ROLE;
                case MENTIONABLE -> CommandOptionType.MENTIONABLE;
                case ATTACHMENT -> CommandOptionType.ATTACHMENT;
            };
        else if (value instanceof canaryprism.discordbridge.api.message.MessageFlag flag)
            return switch (flag) {
                case UNKNOWN -> throw new UnsupportedValueException(flag);
                case EPHEMERAL -> MessageFlag.EPHEMERAL;
                case SILENT -> MessageFlag.SUPPRESS_NOTICICATIONS;
            };
        else if (value instanceof PermissionType type)
            return switch (type) {
                case UNKNOWN -> throw new UnsupportedValueException(type);
                
                case VIEW_CHANNEL -> Permission.VIEW_CHANNEL;
                case MANAGE_CHANNEL -> Permission.MANAGE_CHANNELS;
                case MANAGE_ROLES -> Permission.MANAGE_ROLES;
                case CREATE_EXPRESSIONS -> throw new UnsupportedValueException(type);
                case MANAGE_EXPRESSIONS -> Permission.MANAGE_GUILD_EXPRESSIONS;
                case VIEW_AUDIT_LOG -> Permission.VIEW_AUDIT_LOG;
                case VIEW_SERVER_INSIGHTS -> Permission.VIEW_GUILD_INSIGHTS;
                case MANAGE_WEBHOOKS -> Permission.MANAGE_WEBHOOKS;
                case MANAGE_SERVER -> Permission.MANAGE_GUILD;
                
                case CREATE_INSTANT_INVITE -> Permission.CREATE_INSTANT_INVITE;
                case CHANGE_NICKNAME -> Permission.CHANGE_NICKNAME;
                case MANAGE_NICKNAMES -> Permission.MANAGE_NICKNAMES;
                case KICK_MEMBERS -> Permission.KICK_MEMBERS;
                case BAN_MEMBERS -> Permission.BAN_MEMBERS;
                case MODERATE_MEMBERS -> Permission.MODERATE_MEMBERS;
                
                case SEND_MESSAGES -> Permission.SEND_MESSAGES;
                case SEND_MESSAGES_IN_THREADS -> Permission.SEND_MESSAGES_IN_THREADS;
                case CREATE_PUBLIC_THREADS -> Permission.USE_PUBLIC_THREADS;
                case CREATE_PRIVATE_THREADS -> Permission.USE_PRIVATE_THREADS;
                case EMBED_LINKS -> Permission.EMBED_LINKS;
                case ATTACH_FILE -> Permission.ATTACH_FILES;
                case ADD_REACTIONS -> Permission.ADD_REACTIONS;
                case USE_EXTERNAL_EMOJIS -> Permission.USE_EXTERNAL_EMOJIS;
                case USE_EXTERNAL_STICKERS -> Permission.USE_EXTERNAL_STICKERS;
                case MENTION_ANYONE -> Permission.MENTION_EVERYONE;
                case MANAGE_MESSAGES -> Permission.MANAGE_MESSAGES;
                case MANAGE_THREADS -> Permission.MANAGE_THREADS;
                case READ_MESSAGE_HISTORY -> Permission.READ_MESSAGE_HISTORY;
                case SEND_TTS_MESSAGES -> Permission.SEND_TTS_MESSAGES;
                case SEND_VOICE_MESSAGES -> Permission.SEND_VOICE_MESSAGES;
                case CREATE_POLLS -> throw new UnsupportedValueException(type);
                
                case CONNECT_VOICE -> Permission.CONNECT;
                case SPEAK -> Permission.SPEAK;
                case VIDEO -> Permission.STREAM;
                case USE_SOUNDBOARD -> Permission.USE_SOUNDBOARD;
                case USE_EXTERNAL_SOUNDBOARD -> Permission.USE_EXTERNAL_SOUNDS;
                case USE_VOICE_ACTIVITY -> Permission.USE_VAD;
                case PRIORITY_SPEAKER -> Permission.PRIORITY_SPEAKER;
                case MUTE_MEMBERS -> Permission.MUTE_MEMBERS;
                case DEAFEN_MEMBERS -> Permission.DEAFEN_MEMBERS;
                case MOVE_MEMBERS -> Permission.MOVE_MEMBERS;
                case SET_CHANNEL_STATUS -> throw new UnsupportedValueException(type);
                
                case USE_APPLICATION_COMMANDS -> Permission.USE_APPLICATION_COMMANDS;
                case START_EMBEDDED_ACTIVITIES -> Permission.USE_EMBEDDED_ACTIVITIES;
                case USE_EXTERNAL_APPS -> throw new UnsupportedValueException(type);
                
                case REQUEST_TO_SPEAK -> Permission.REQUEST_TO_SPEAK;
                
                case CREATE_EVENTS -> throw new UnsupportedValueException(type);
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
                    case GUILD_TEXT -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_TEXT;
                    case DM -> canaryprism.discordbridge.api.channel.ChannelType.PRIVATE;
                    case GUILD_VOICE -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_VOICE;
                    case GROUP_DM -> canaryprism.discordbridge.api.channel.ChannelType.GROUP;
                    case GUILD_CATEGORY -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_CATEGORY;
                    case GUILD_ANNOUNCEMENT -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_NEWS;
                    case ANNOUNCEMENT_THREAD -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_THREAD_NEWS;
                    case PUBLIC_THREAD -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_THREAD_PUBLIC;
                    case PRIVATE_THREAD -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_THREAD_PRIVATE;
                    case GUILD_STAGE_VOICE -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_STAGE;
                    case GUILD_DIRECTORY -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_DIRECTORY;
                    case GUILD_FORUM -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_FORUM;
                    case GUILD_MEDIA -> canaryprism.discordbridge.api.channel.ChannelType.SERVER_MEDIA;
                };
            } else if (type == SlashCommandOptionType.class) {
                if (!(value instanceof CommandOptionType e)) break conversion_attempt;
                return (T) switch (e) {
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
                    case SUPPRESS_NOTICICATIONS -> canaryprism.discordbridge.api.message.MessageFlag.SILENT;
                    default -> canaryprism.discordbridge.api.message.MessageFlag.UNKNOWN;
                };
            } else if (type == PermissionType.class) {
                if (!(value instanceof Permission e)) break conversion_attempt;
                return (T) switch (e) {
                    case CREATE_INSTANT_INVITE -> PermissionType.CREATE_INSTANT_INVITE;
                    case KICK_MEMBERS -> PermissionType.KICK_MEMBERS;
                    case BAN_MEMBERS -> PermissionType.BAN_MEMBERS;
                    case ADMINISTRATOR -> PermissionType.ADMINISTRATOR;
                    case MANAGE_CHANNELS -> PermissionType.MANAGE_CHANNEL;
                    case MANAGE_GUILD -> PermissionType.MANAGE_SERVER;
                    case ADD_REACTIONS -> PermissionType.ADD_REACTIONS;
                    case VIEW_AUDIT_LOG -> PermissionType.VIEW_AUDIT_LOG;
                    case VIEW_GUILD_INSIGHTS -> PermissionType.VIEW_SERVER_INSIGHTS;
                    case VIEW_CHANNEL -> PermissionType.VIEW_CHANNEL;
                    case SEND_MESSAGES -> PermissionType.SEND_MESSAGES;
                    case SEND_TTS_MESSAGES -> PermissionType.SEND_TTS_MESSAGES;
                    case MANAGE_MESSAGES -> PermissionType.MANAGE_MESSAGES;
                    case EMBED_LINKS -> PermissionType.EMBED_LINKS;
                    case ATTACH_FILES -> PermissionType.ATTACH_FILE;
                    case READ_MESSAGE_HISTORY -> PermissionType.READ_MESSAGE_HISTORY;
                    case MENTION_EVERYONE -> PermissionType.MENTION_ANYONE;
                    case USE_EXTERNAL_EMOJIS -> PermissionType.USE_EXTERNAL_EMOJIS;
                    case USE_EXTERNAL_STICKERS -> PermissionType.USE_EXTERNAL_STICKERS;
                    case CONNECT -> PermissionType.CONNECT_VOICE;
                    case SPEAK -> PermissionType.SPEAK;
                    case MUTE_MEMBERS -> PermissionType.MUTE_MEMBERS;
                    case DEAFEN_MEMBERS -> PermissionType.DEAFEN_MEMBERS;
                    case MOVE_MEMBERS -> PermissionType.MOVE_MEMBERS;
                    case USE_VAD -> PermissionType.USE_VOICE_ACTIVITY;
                    case PRIORITY_SPEAKER -> PermissionType.PRIORITY_SPEAKER;
                    case STREAM -> PermissionType.VIDEO;
                    case REQUEST_TO_SPEAK -> PermissionType.REQUEST_TO_SPEAK;
                    case USE_EMBEDDED_ACTIVITIES -> PermissionType.START_EMBEDDED_ACTIVITIES;
                    case MANAGE_EVENTS -> PermissionType.MANAGE_EVENTS;
                    case MANAGE_THREADS -> PermissionType.MANAGE_THREADS;
                    case USE_PUBLIC_THREADS -> PermissionType.CREATE_PUBLIC_THREADS;
                    case USE_PRIVATE_THREADS -> PermissionType.CREATE_PRIVATE_THREADS;
                    case SEND_MESSAGES_IN_THREADS -> PermissionType.SEND_MESSAGES_IN_THREADS;
                    case CHANGE_NICKNAME -> PermissionType.CHANGE_NICKNAME;
                    case MANAGE_NICKNAMES -> PermissionType.MANAGE_NICKNAMES;
                    case MANAGE_ROLES -> PermissionType.MANAGE_ROLES;
                    case MANAGE_WEBHOOKS -> PermissionType.MANAGE_WEBHOOKS;
                    case MANAGE_GUILD_EXPRESSIONS -> PermissionType.MANAGE_EXPRESSIONS;
                    case USE_APPLICATION_COMMANDS -> PermissionType.USE_APPLICATION_COMMANDS;
                    case MODERATE_MEMBERS -> PermissionType.MODERATE_MEMBERS;
                    case VIEW_CREATOR_MONETIZATION_ANALYTICS -> PermissionType.VIEW_MONETIZATION_ANALYTICS;
                    case USE_SOUNDBOARD -> PermissionType.USE_SOUNDBOARD;
                    case USE_EXTERNAL_SOUNDS -> PermissionType.USE_EXTERNAL_SOUNDBOARD;
                    case SEND_VOICE_MESSAGES -> PermissionType.SEND_VOICE_MESSAGES;
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
                            ChannelImpl.class, MessageChannelImpl.class, ServerChannelImpl.class, ServerMessageChannelImpl.class,
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
        return "DiscordBridge Discord.jar Beta 1.2 Implementation";
    }
    
    public static DiscordLocale convertLocale(@NotNull String locale) {
        return DiscordLocale.fromLocale(Locale.forLanguageTag(locale))
                .orElse(DiscordLocale.UNKNOWN);
    }
    
    public static String convertLocale(@NotNull DiscordLocale locale) {
        return locale.locale.toLanguageTag();
    }
    
    public @NotNull Command convertData(@NotNull SlashCommandData data) {
        var builder = new com.seailz.discordjar.command.Command(
                data.getName(),
                CommandType.SLASH_COMMAND,
                data.getDescription(),
                
        )
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
    
    public SlashSubCommand convertDataSubcommands(@NotNull SlashCommandOptionData data) {
        return new SlashSubCommand(
                data.getName(),
                data.getDescription(),
                data.getOptions()
                        .stream()
                        .map(this::convertData)
                        .toList()
        );
    }
    
    public CommandOption convertData(@NotNull SlashCommandOptionData data) {
        var builder = new CommandOption(
                data.getName(),
                data.getDescription(),
                ((CommandOptionType) getImplementationValue(data.getType())),
                data.isRequired(),
                data.getChoices()
                        .stream()
                        .map(this::convertData)
                        .toList(),
                data.getOptions()
                        .stream()
                        .filter((e) -> e.getType().getTypeRepresentation() == Void.class)
                        .map(this::convertDataSubcommands)
                        .toList(),
                data.getOptions()
                        .stream()
                        .filter((e) -> e.getType().getTypeRepresentation() != Void.class)
                        .map(this::convertData)
                        .toList(),
                Optional.<Number>empty()
                        .or(data::getIntegerBoundsMin)
                        .or(data::getNumberBoundsMin)
                        .map(Number::intValue)
                        .orElse(0)
        );
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
    
    public CommandChoice convertData(@NotNull SlashCommandOptionChoiceData data) {
        if (!data.getNameLocalizations().isEmpty()) {
            throw new UnsupportedOperationException(String.format("%s doesn't support Slash Comand option choice name localizations", this));
        }
        return new CommandChoice(
                data.getName(),
                data.getValue().toString()
        );
    }
}
