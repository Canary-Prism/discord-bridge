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

package canaryprism.discordbridge.discord4j;

import canaryprism.discordbridge.api.DiscordApi;
import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.DiscordBridgeApi;
import canaryprism.discordbridge.api.channel.ChannelType;
import canaryprism.discordbridge.api.data.interaction.slash.SlashCommandData;
import canaryprism.discordbridge.api.data.interaction.slash.SlashCommandOptionChoiceData;
import canaryprism.discordbridge.api.data.interaction.slash.SlashCommandOptionData;
import canaryprism.discordbridge.api.enums.DiscordBridgeEnum;
import canaryprism.discordbridge.api.enums.PartialSupport;
import canaryprism.discordbridge.api.enums.TypeValue;
import canaryprism.discordbridge.api.exceptions.UnsupportedImplementationException;
import canaryprism.discordbridge.api.exceptions.UnsupportedValueException;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType;
import canaryprism.discordbridge.api.message.MessageFlag;
import canaryprism.discordbridge.api.misc.DiscordLocale;
import canaryprism.discordbridge.api.server.permission.PermissionType;
import canaryprism.discordbridge.discord4j.channel.ChannelImpl;
import canaryprism.discordbridge.discord4j.channel.MessageChannelImpl;
import canaryprism.discordbridge.discord4j.channel.ServerChannelImpl;
import canaryprism.discordbridge.discord4j.channel.ServerMessageChannelImpl;
import canaryprism.discordbridge.discord4j.entity.user.UserImpl;
import canaryprism.discordbridge.discord4j.event.interaction.SlashCommandAutocompleteEventImpl;
import canaryprism.discordbridge.discord4j.event.interaction.SlashCommandInvokeEventImpl;
import canaryprism.discordbridge.discord4j.interaction.response.FollowupResponderImpl;
import canaryprism.discordbridge.discord4j.interaction.response.ImmediateResponderImpl;
import canaryprism.discordbridge.discord4j.interaction.response.ResponseUpdaterImpl;
import canaryprism.discordbridge.discord4j.interaction.slash.*;
import canaryprism.discordbridge.discord4j.message.AttachmentImpl;
import canaryprism.discordbridge.discord4j.server.ServerImpl;
import canaryprism.discordbridge.discord4j.server.permission.RoleImpl;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.*;
import discord4j.core.object.entity.channel.*;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/// The Discord4J implementation of [DiscordBridge]
public final class DiscordBridgeDiscord4J implements DiscordBridge {
    
    private static final Logger log = LoggerFactory.getLogger(DiscordBridgeDiscord4J.class);
    
    @Override
    public boolean canLoadApi(@NotNull Object o) {
        return o instanceof GatewayDiscordClient;
    }
    
    @Override
    public @NotNull DiscordApi loadApi(@NotNull Object api) {
        log.trace("loading '{}'", api);
        try {
            return new DiscordApiImpl(this, ((GatewayDiscordClient) api));
        } catch (ClassCastException e) {
            throw new UnsupportedImplementationException(
                    String.format("discord-bridge-discord4j implementation can't load object %s", api));
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T extends PartialSupport> @NotNull Set<? extends @NotNull T> getSupportedValues(Class<T> type) {
        class SupportedValues {
            static final Set<?> slash_command_option_type = getSupported(SlashCommandOptionType.class);
            
            static final Set<?> channel_type = getSupported(ChannelType.class);
            
            static final Set<?> message_flag_type = getSupported(MessageFlag.class);
            
            static final Set<?> permission_type = getSupported(PermissionType.class);
        }
        
        if (type == SlashCommandOptionType.class)
            return (Set<? extends T>) SupportedValues.slash_command_option_type;
        
        if (type == ChannelType.class)
            return (Set<? extends T>) SupportedValues.channel_type;
        
        if (type == MessageFlag.class)
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
    
    @SuppressWarnings({ "DuplicateBranchesInSwitch" })
    @Override
    public @NotNull Type getInternalTypeRepresentation(@NotNull TypeValue<?> value) {
        
        if (value instanceof ChannelType type)
            return switch (((Channel.Type) getImplementationValue(type))) {
                case GUILD_TEXT -> TextChannel.class;
                case DM -> PrivateChannel.class;
                case GUILD_VOICE -> VoiceChannel.class;
                case GROUP_DM -> MessageChannel.class;
                case GUILD_CATEGORY -> Category.class;
                case GUILD_NEWS -> NewsChannel.class;
                case GUILD_STAGE_VOICE -> VoiceChannel.class;
                case GUILD_STORE -> StoreChannel.class;
                case UNKNOWN -> Channel.class;
            };
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
    
    @SuppressWarnings({ "DuplicateBranchesInSwitch" })
    private static @NotNull Object staticGetImplementationValue(@NotNull DiscordBridgeEnum value) {
        if (value instanceof ChannelType type)
            return switch (type) {
                case UNKNOWN -> throw new UnsupportedValueException(type);
                case PRIVATE -> Channel.Type.DM;
                case GROUP -> Channel.Type.GROUP_DM;
                case SERVER_TEXT -> Channel.Type.GUILD_TEXT;
                case SERVER_VOICE -> Channel.Type.GUILD_VOICE;
                case SERVER_CATEGORY -> Channel.Type.GUILD_CATEGORY;
                case SERVER_NEWS -> Channel.Type.GUILD_NEWS;
                case SERVER_STAGE -> Channel.Type.GUILD_STAGE_VOICE;
                case SERVER_THREAD_NEWS -> throw new UnsupportedValueException(type);
                case SERVER_THREAD_PUBLIC -> throw new UnsupportedValueException(type);
                case SERVER_THREAD_PRIVATE -> throw new UnsupportedValueException(type);
                case SERVER_FORUM -> throw new UnsupportedValueException(type);
                case SERVER_MEDIA -> throw new UnsupportedValueException(type);
                case SERVER_SHOP -> Channel.Type.GUILD_STORE;
                case SERVER_DIRECTORY -> throw new UnsupportedValueException(type);
            };
        else if (value instanceof SlashCommandOptionType type)
            return switch (type) {
                case UNKNOWN -> ApplicationCommandOption.Type.UNKNOWN;
                case SUBCOMMAND -> ApplicationCommandOption.Type.SUB_COMMAND;
                case SUBCOMMAND_GROUP -> ApplicationCommandOption.Type.SUB_COMMAND_GROUP;
                case STRING -> ApplicationCommandOption.Type.STRING;
                case INTEGER -> ApplicationCommandOption.Type.INTEGER;
                case NUMBER -> ApplicationCommandOption.Type.NUMBER;
                case BOOLEAN -> ApplicationCommandOption.Type.BOOLEAN;
                case USER -> ApplicationCommandOption.Type.USER;
                case CHANNEL -> ApplicationCommandOption.Type.CHANNEL;
                case ROLE -> ApplicationCommandOption.Type.ROLE;
                case MENTIONABLE -> ApplicationCommandOption.Type.MENTIONABLE;
                case ATTACHMENT -> ApplicationCommandOption.Type.ATTACHMENT;
            };
        else if (value instanceof MessageFlag flag)
            return switch (flag) {
                case UNKNOWN -> throw new UnsupportedValueException(flag);
                case EPHEMERAL -> Message.Flag.EPHEMERAL;
                case SILENT -> Message.Flag.SUPPRESS_NOTIFICATIONS;
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
                case CREATE_PUBLIC_THREADS -> Permission.CREATE_PUBLIC_THREADS;
                case CREATE_PRIVATE_THREADS -> Permission.CREATE_PRIVATE_THREADS;
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
    
    @SuppressWarnings({ "unchecked" })
    @Override
    public <T extends DiscordBridgeEnum> @NotNull T convertInternalObject(@NotNull Class<T> type, @NotNull Object value) {
        conversion_attempt: {
            if (type == ChannelType.class) {
                if (!(value instanceof Channel.Type e)) break conversion_attempt;
                return (T) switch (e) {
                    case UNKNOWN -> ChannelType.UNKNOWN;
                    case GUILD_TEXT -> ChannelType.SERVER_TEXT;
                    case DM -> ChannelType.PRIVATE;
                    case GUILD_VOICE -> ChannelType.SERVER_VOICE;
                    case GROUP_DM -> ChannelType.GROUP;
                    case GUILD_CATEGORY -> ChannelType.SERVER_CATEGORY;
                    case GUILD_NEWS -> ChannelType.SERVER_NEWS;
                    case GUILD_STORE -> ChannelType.SERVER_SHOP;
                    case GUILD_STAGE_VOICE -> ChannelType.SERVER_STAGE;
                };
            } else if (type == SlashCommandOptionType.class) {
                if (!(value instanceof ApplicationCommandOption.Type e)) break conversion_attempt;
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
            } else if (type == MessageFlag.class) {
                if (!(value instanceof Message.Flag e)) break conversion_attempt;
                return (T) switch (e) {
                    case EPHEMERAL -> MessageFlag.EPHEMERAL;
                    case SUPPRESS_NOTIFICATIONS -> MessageFlag.SILENT;
                    default -> {
                        log.debug("unsupported Discord4J value '{}', converting to UNKNOWN", e);
                        yield MessageFlag.UNKNOWN;
                    }
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
                    case MANAGE_THREADS -> PermissionType.MANAGE_THREADS;
                    case CREATE_PUBLIC_THREADS -> PermissionType.CREATE_PUBLIC_THREADS;
                    case CREATE_PRIVATE_THREADS -> PermissionType.CREATE_PRIVATE_THREADS;
                    case SEND_MESSAGES_IN_THREADS -> PermissionType.SEND_MESSAGES_IN_THREADS;
                    case CHANGE_NICKNAME -> PermissionType.CHANGE_NICKNAME;
                    case MANAGE_NICKNAMES -> PermissionType.MANAGE_NICKNAMES;
                    case MANAGE_ROLES -> PermissionType.MANAGE_ROLES;
                    case MANAGE_WEBHOOKS -> PermissionType.MANAGE_WEBHOOKS;
                    //noinspection deprecation
                    case MANAGE_EMOJIS, MANAGE_EMOJIS_AND_STICKERS, MANAGE_GUILD_EXPRESSIONS -> PermissionType.MANAGE_EXPRESSIONS;
                    //noinspection deprecation
                    case USE_SLASH_COMMANDS, USE_APPLICATION_COMMANDS -> PermissionType.USE_APPLICATION_COMMANDS;
                    case MODERATE_MEMBERS -> PermissionType.MODERATE_MEMBERS;
                    case MANAGE_EVENTS -> PermissionType.MANAGE_EVENTS;
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
        return "DiscordBridge Discord4J 3.2.7 Implementation";
    }
    
    public @Unmodifiable Set<PermissionType> parsePermissionBitFlag(long bitflag) {
        return Collections.unmodifiableSet(Arrays.stream(Permission.values())
                .filter((e) -> (e.getValue() & bitflag) != 0)
                .map((e) -> convertInternalObject(PermissionType.class, e))
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(PermissionType.class))));
    }
    
    public static DiscordLocale convertLocale(@NotNull String locale) {
        return DiscordLocale.fromLocale(Locale.forLanguageTag(locale))
                .orElseGet(() -> {
                    log.debug("unsupported Discord4J locale '{}', converting to UNKNOWN", locale);
                    return DiscordLocale.UNKNOWN;
                });
    }
    
    public static String convertLocale(@NotNull DiscordLocale locale) {
        return locale.locale.toLanguageTag();
    }
    
    public @NotNull ApplicationCommandRequest convertData(@NotNull SlashCommandData data) {
        var builder = ApplicationCommandRequest.builder()
                .name(data.getName())
                .description(data.getDescription())
                .nameLocalizationsOrNull(
                        data.getNameLocalizations()
                                .entrySet()
                                .stream()
                                .map((e) -> Map.entry(
                                        convertLocale(e.getKey()), e.getValue()
                                ))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                )
                .descriptionLocalizationsOrNull(
                        data.getDescriptionLocalizations()
                                .entrySet()
                                .stream()
                                .map((e) -> Map.entry(
                                        convertLocale(e.getKey()), e.getValue()
                                ))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                )
                .options(data.getOptions()
                        .stream()
                        .map(this::convertData)
                        .toList())
                .dmPermission(data.isEnabledInDMs())
                .defaultPermission(!data.isDefaultDisabled())
                .defaultMemberPermissions(
                        data.getRequiredPermissions()
                                .map((set) -> set.stream()
                                        .map(this::getImplementationValue)
                                        .map(Permission.class::cast)
                                        .map(Permission::getValue)
                                        .reduce(0L, (a, b) -> a | b)
                                )
                                .map(String::valueOf)
                );
        
        if (data.getAllowedContexts().isPresent())
            throw new UnsupportedOperationException(String.format("%s does not support contexts", this));
        
        if (data.isNSFW())
            throw new IllegalArgumentException(String.format("%s doesn't support nsfw commands", this));
        
        return builder.build();
    }
    
    public ApplicationCommandOptionData convertData(@NotNull SlashCommandOptionData data) {
        var builder = ApplicationCommandOptionData.builder()
                .name(data.getName())
                .description(data.getDescription())
                .nameLocalizationsOrNull(
                        data.getNameLocalizations()
                                .entrySet()
                                .stream()
                                .map((e) -> Map.entry(
                                        convertLocale(e.getKey()), e.getValue()
                                ))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                )
                .descriptionLocalizationsOrNull(
                        data.getDescriptionLocalizations()
                                .entrySet()
                                .stream()
                                .map((e) -> Map.entry(
                                        convertLocale(e.getKey()), e.getValue()
                                ))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                )
                .type(((ApplicationCommandOption.Type) getImplementationValue(data.getType())).getValue())
                .options(data.getOptions()
                        .stream()
                        .map(this::convertData)
                        .toList())
                .choices(data.getChoices()
                        .stream()
                        .map(this::convertData)
                        .toList())
                .required(data.isRequired())
                .autocomplete(data.isAutocompletable())
                .channelTypes(data.getChannelTypeBounds()
                        .stream()
                        .map(this::getImplementationValue)
                        .map(Channel.Type.class::cast)
                        .map(Channel.Type::getValue)
                        .collect(Collectors.toSet()));
        
        data.getIntegerBoundsMin().map(Long::doubleValue).ifPresent(builder::minValue);
        data.getIntegerBoundsMax().map(Long::doubleValue).ifPresent(builder::maxValue);
        data.getNumberBoundsMin().ifPresent(builder::minValue);
        data.getNumberBoundsMax().ifPresent(builder::maxValue);
        data.getStringLengthBoundsMin().map(Long::intValue).ifPresent(builder::minLength);
        data.getStringLengthBoundsMax().map(Long::intValue).ifPresent(builder::maxLength);
        
        return builder.build();
    }
    
    public ApplicationCommandOptionChoiceData convertData(@NotNull SlashCommandOptionChoiceData data) {
        return ApplicationCommandOptionChoiceData.builder()
                .name(data.getName())
                .nameLocalizationsOrNull(
                        data.getNameLocalizations()
                                .entrySet()
                                .stream()
                                .map((e) -> Map.entry(
                                        convertLocale(e.getKey()), e.getValue()
                                ))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                )
                .value(data.getValue())
                .build();
    }
}
