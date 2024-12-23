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

package canaryprism.discordbridge.kord.interaction.slash;

import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionChoice;
import canaryprism.discordbridge.api.misc.DiscordLocale;
import canaryprism.discordbridge.kord.DiscordBridgeKord;
import dev.kord.core.cache.data.ApplicationCommandOptionChoiceData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

public record SlashCommandOptionChoiceImpl(DiscordBridgeKord bridge, ApplicationCommandOptionChoiceData choice) implements SlashCommandOptionChoice {
    
    @Override
    public @NotNull Object getValue() {
        return choice.getValue();
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return choice;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
    
    @Override
    public @NotNull @Unmodifiable Map<DiscordLocale, @NotNull String> getNameLocalizations() {
        throw new UnsupportedOperationException(String.format("localization data inaccessible by %s", bridge));
    }
    
    @Override
    public @NotNull String getName() {
        return choice.getName();
    }
}
