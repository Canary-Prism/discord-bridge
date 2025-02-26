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

/// Module for Discord4J implementation of discord-bridge
module canaryprism.discordbridge.discord4j {
    requires static org.jetbrains.annotations;
    requires canaryprism.discordbridge.api;
    requires discord4j.common;
    requires discord4j.core;
    requires discord4j.discordjson;
    requires discord4j.rest;
    requires reactor.core;
    requires org.reactivestreams;
    requires canaryprism.commons.event;
    requires org.slf4j;
    requires discord4j.discordjson.api;
    
    provides canaryprism.discordbridge.api.DiscordBridge
            with canaryprism.discordbridge.discord4j.DiscordBridgeDiscord4J;
}