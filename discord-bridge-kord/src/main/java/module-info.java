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

/// Module for Kord implementation of discord-bridge
module canaryprism.discordbridge.kord {
    requires static org.jetbrains.annotations;
    requires canaryprism.discordbridge.api;
    requires core.jvm;
    requires common.jvm;
    requires rest.jvm;
    requires kotlinx.serialization.json;
    requires kotlinx.coroutines.core;
    requires io.ktor.client.core;
    
    provides canaryprism.discordbridge.api.DiscordBridge
            with canaryprism.discordbridge.kord.DiscordBridgeKord;
}