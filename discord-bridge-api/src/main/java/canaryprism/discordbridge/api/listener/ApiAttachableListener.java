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

package canaryprism.discordbridge.api.listener;

import canaryprism.discordbridge.api.listener.interaction.SlashCommandAutocompleteListener;
import canaryprism.discordbridge.api.listener.interaction.SlashCommandInvokeListener;

import java.util.EventListener;

/// A listener that can be attached to a [canaryprism.discordbridge.api.DiscordApi]
public sealed interface ApiAttachableListener extends EventListener permits SlashCommandAutocompleteListener, SlashCommandInvokeListener {
}
