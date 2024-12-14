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
/// the [canaryprism.discordbridge.api.DiscordBridge#getSupportedPermissionTypes()] method should be used to obtain
/// supported permission types instead of [#values()]
public enum PermissionType implements PartialSupport {
    
    // General Server Permissions
    VIEW_CHANNEL("View Channel"), // plurality depends on whether it's a role permission or channel override
    MANAGE_CHANNELS("Manage Channels"),
    MANAGE_ROLES("Manage Roles"),
    CREATE_EXPRESSIONS("Create Expressions"),
    MANAGE_EXPRESSIONS("Manage Expressions"),
    VIEW_AUDIT_LOG("View Audit Log"),
    VIEW_SERVER_INSIGHTS("View Server Insights"),
    MANAGE_WEBHOOKS("Manage Webhooks"),
    MANAGE_SERVER("Manage Server"),
    
    
    // Membership Permissions
    CREATE_INSTANT_INVITE("Create Invite"),
    CHANGE_NICKNAME("Change Nickname"),
    MANAGE_NICKNAMES("Manage Nickname"),
    KICK_MEMBERS("Kick Members"),
    BAN_MEMBERS("Ban Members"),
    MODERATE_MEMBERS("Time out Members"),
    
    // Text Channel Permissions
    SEND_MESSAGES("Send Messages"),
    SEND_MESSAGES_IN_THREADS("Send Messages in Threads"),
    CREATE_PUBLIC_THREADS("Create Public Threads"),
    CREATE_PRIVATE_THREADS("Create Private Threads"),
    EMBED_LINKS("Embed Links"),
    ATTACH_FILE("Attach Files"),
    ADD_REACTIONS("Add Reactions"),
    USE_EXTERNAL_EMOJIS("Use External Emojis"),
    USE_EXTERNAL_STICKERS("Use External Stickers"),
    MENTION_ANYONE("Mention @everyone, @here and All Roles"),
    MANAGE_MESSAGES("Manage Messages"),
    MANAGE_THREADS("Manage Threads"),
    READ_MESSAGE_HISTORY("Read Message History"),
    SEND_TTS_MESSAGES("Send TTS Messages"),
    SEND_VOICE_MESSAGES("Send Voice Messages"),
    CREATE_POLLS("Create Polls"),
    
    // Voice Channel Permissions
    CONNECT_VOICE("Connect"),
    SPEAK("Speak"),
    /// Includes camera and streaming
    VIDEO("Video"),
    USE_SOUNDBOARD("Use Soundboard"),
    USE_EXTERNAL_SOUNDBOARD("Use External Sounds"),
    USE_VOICE_ACTIVITY("Use Voice Activity"), //USE_VAD,
    PRIORITY_SPEAKER("Priority Speaker"),
    MUTE_MEMBERS("Mute Members"),
    DEAFEN_MEMBERS("Deafen Members"),
    MOVE_MEMBERS("Move Members"),
    SET_CHANNEL_STATUS("Set Voice Channel Status"),
    
    // Application Permissions
    USE_APPLICATION_COMMANDS("Use Application Commands"),
    START_EMBEDDED_ACTIVITIES("Use Activities"),
    USE_EXTERNAL_APPS("Use External Apps"),
    
    // Stage Channel Permissions
    REQUEST_TO_SPEAK("Request to Speak"),
    
    // Events Permissions
    CREATE_EVENTS("Create Events"),
    MANAGE_EVENTS("Manage Events"),
    
    
    ADMINISTRATOR("Administrator"),
    ;
    
    /// The readable name of this permission
    public final String name;
    
    PermissionType(String name) {
        this.name = name;
    }
}
