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

/**
 * Module for discord-bridge-api
 */
module canaryprism.discordbridge.api {
    requires org.jetbrains.annotations;
    exports canaryprism.discordbridge.api;
    exports canaryprism.discordbridge.api.interaction;
    exports canaryprism.discordbridge.api.interaction.response;
    exports canaryprism.discordbridge.api.interaction.slash;
    exports canaryprism.discordbridge.api.server;
    exports canaryprism.discordbridge.api.exceptions;
    exports canaryprism.discordbridge.api.entities;
    exports canaryprism.discordbridge.api.entities.user;
    exports canaryprism.discordbridge.api.channel;
    exports canaryprism.discordbridge.api.listener;
    exports canaryprism.discordbridge.api.listener.interaction;
    exports canaryprism.discordbridge.api.event;
    exports canaryprism.discordbridge.api.event.interaction;
    exports canaryprism.discordbridge.api.server.permission;
    exports canaryprism.discordbridge.api.message;
    
    uses canaryprism.discordbridge.api.DiscordBridge;
}