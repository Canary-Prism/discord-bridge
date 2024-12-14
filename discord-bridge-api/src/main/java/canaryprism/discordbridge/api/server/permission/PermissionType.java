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

package canaryprism.discordbridge.api.server.permission;

import canaryprism.discordbridge.api.PartialSupport;

/// Enum of Discord permissions
///
/// Implementations may not support all of these permission types, thus directly referencing these enum cases should be avoided
///
/// the [canaryprism.discordbridge.api.DiscordBridge#getSupportedValues(Class)] method should be used to obtain
/// supported permission types instead of [#values()]
public enum PermissionType implements PartialSupport {
    
    // General Server Permissions
    
    /// See this channel or all channels by default
    VIEW_CHANNEL("View Channel"), // plurality depends on whether it's a role permission or channel override
    /// Manage the channel or all channels by default
    MANAGE_CHANNEL("Manage Channel"),
    /// Manage Roles
    MANAGE_ROLES("Manage Roles"),
    /// Create Expressiosn (Emojis, Stickers, and Soundboard)
    CREATE_EXPRESSIONS("Create Expressions"),
    /// Manage Expressions (Emojis, Stickers, and Soundboard)
    MANAGE_EXPRESSIONS("Manage Expressions"),
    /// View the Audit Log
    VIEW_AUDIT_LOG("View Audit Log"),
    /// View Server Insights
    VIEW_SERVER_INSIGHTS("View Server Insights"),
    /// Manage Webhooks of this server
    MANAGE_WEBHOOKS("Manage Webhooks"),
    /// Manage general things about this server
    /// (server name, Integrations, etc)
    MANAGE_SERVER("Manage Server"),
    
    
    // Membership Permissions
    
    /// Create an Invite to this server
    CREATE_INSTANT_INVITE("Create Invite"),
    /// Change your own nickname
    CHANGE_NICKNAME("Change Nickname"),
    /// Change others' nicknames
    MANAGE_NICKNAMES("Manage Nickname"),
    /// Kick Members
    KICK_MEMBERS("Kick Members"),
    /// Ban Members
    BAN_MEMBERS("Ban Members"),
    /// Timeout Members
    MODERATE_MEMBERS("Time out Members"),
    
    // Text Channel Permissions
    
    /// Send Messages
    SEND_MESSAGES("Send Messages"),
    /// Send Messages in Threads
    SEND_MESSAGES_IN_THREADS("Send Messages in Threads"),
    /// Create public threads
    CREATE_PUBLIC_THREADS("Create Public Threads"),
    /// Create private threads
    CREATE_PRIVATE_THREADS("Create Private Threads"),
    /// Send Embeds with their messages
    EMBED_LINKS("Embed Links"),
    /// Attach files to their messages
    ATTACH_FILE("Attach Files"),
    /// Add reactions to messages
    /// (users are always able to add to existing reactions)
    ADD_REACTIONS("Add Reactions"),
    /// Use custom emojis from other servers
    USE_EXTERNAL_EMOJIS("Use External Emojis"),
    /// Use custom stickers from other servers
    USE_EXTERNAL_STICKERS("Use External Stickers"),
    /// Mention @everyone, @here, and all roles, regardless of if this role allows others to ping them normally
    MENTION_ANYONE("Mention @everyone, @here and All Roles"),
    /// Manage Messages
    MANAGE_MESSAGES("Manage Messages"),
    /// Manage Threads
    MANAGE_THREADS("Manage Threads"),
    /// Read Message History of text channels
    ///
    /// If not granted means users can't request existing messages in a channel
    /// and can only see messages that are sent while they have the channel open
    READ_MESSAGE_HISTORY("Read Message History"),
    /// Send TTS Messages
    SEND_TTS_MESSAGES("Send TTS Messages"),
    /// Send Voice Messages
    SEND_VOICE_MESSAGES("Send Voice Messages"),
    /// Create polls
    CREATE_POLLS("Create Polls"),
    
    // Voice Channel Permissions
    
    /// Connect to the voice channel
    CONNECT_VOICE("Connect"),
    /// Speak in the voice channel
    SPEAK("Speak"),
    /// Turn on their camera or stream in the voice channel
    VIDEO("Video"),
    /// Use Soundboard in the voice channel
    USE_SOUNDBOARD("Use Soundboard"),
    /// Use Soundboard sounds from other servers in the voice channel
    USE_EXTERNAL_SOUNDBOARD("Use External Sounds"),
    /// Use Voice Activity in the voice channel
    ///
    /// if not granted users will be forced to use push to talk
    USE_VOICE_ACTIVITY("Use Voice Activity"),
    /// Use Priority push to talk in the voice channel
    PRIORITY_SPEAKER("Priority Speaker"),
    /// Server Mute members in the voice channel
    MUTE_MEMBERS("Mute Members"),
    /// Server Deafen members in the voice channel
    DEAFEN_MEMBERS("Deafen Members"),
    /// Move members between voice channels
    MOVE_MEMBERS("Move Members"),
    /// Set the voice channel status
    SET_CHANNEL_STATUS("Set Voice Channel Status"),
    
    // Application Permissions
    
    /// Use Application Commands
    USE_APPLICATION_COMMANDS("Use Application Commands"),
    /// Use Activities in the voice channel
    START_EMBEDDED_ACTIVITIES("Use Activities"),
    /// Use User Installed applications
    USE_EXTERNAL_APPS("Use External Apps"),
    
    // Stage Channel Permissions
    /// Request to speak in the stage voice channel
    REQUEST_TO_SPEAK("Request to Speak"),
    
    // Events Permissions
    /// Create Events
    CREATE_EVENTS("Create Events"),
    /// Manage Events
    MANAGE_EVENTS("Manage Events"),
    
    /// Administrator grants every **effective** permission, but does not mean every permission is *marked* as granted
    ///
    /// You should take care to specifically check for the existence of Administrator permissions when determining
    /// whether a user can perform a given action
    ADMINISTRATOR("Administrator"),
    ;
    
    /// The readable name of this permission
    public final String name;
    
    PermissionType(String name) {
        this.name = name;
    }
}
