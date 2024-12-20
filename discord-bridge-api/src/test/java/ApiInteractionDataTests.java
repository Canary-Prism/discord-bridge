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

import canaryprism.discordbridge.api.channel.ChannelType;
import canaryprism.discordbridge.api.data.interaction.slash.SlashCommandData;
import canaryprism.discordbridge.api.data.interaction.slash.SlashCommandOptionChoiceData;
import canaryprism.discordbridge.api.data.interaction.slash.SlashCommandOptionData;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType.*;
import static org.junit.jupiter.api.Assertions.*;

/// Tests for the concrete classes in package [canaryprism.discordbridge.api.data]
public class ApiInteractionDataTests {
    @Test
    void slashCommandDataCreationConstructorShouldAtLeastWorkPlease() {
        new SlashCommandData("name", "desscription");
    }
    
    @Test
    void slashCommandWithInvalidNameShouldThrowIllegalArgumentException() {
        try {
            new SlashCommandData("", "description");
            
            fail();
        } catch (IllegalArgumentException e) {
            // do nothing
        }
    }
    
    @Test
    void moreComplicatedSlashCommandDataWithOptionsAndWhateverInside() {
        new SlashCommandData("mewo", "woah description")
                .setOptions(List.of(
                        new SlashCommandOptionData("str", "mrrp", STRING)
                                .setAutocompletable(true),
                        new SlashCommandOptionData("str2", "mrrp2", STRING),
                        new SlashCommandOptionData("mrr", "meow", STRING)
                                .setChoices(List.of(
                                        new SlashCommandOptionChoiceData("mrrp", "nya"),
                                        new SlashCommandOptionChoiceData("mrrp", "mewo")
                                ))
                ));
    }
    
    @SuppressWarnings("MagicNumber")
    @Test
    void complicatedSlashCommand2() {
        new SlashCommandData("mewo", "mewo")
                .setOptions(List.of(
                        new SlashCommandOptionData("one", "one", STRING)
                                .setStringLengthBoundsMin(0L)
                                .setStringLengthBoundsMax(6000L),
                        new SlashCommandOptionData("two", "two", NUMBER)
                                .setNumberBoundsMin(-10d)
                                .setNumberBoundsMax(10d),
                        new SlashCommandOptionData("three", "three", INTEGER)
                                .setIntegerBoundsMin(-10L)
                                .setIntegerBoundsMax(10L),
                        new SlashCommandOptionData("four", "four", CHANNEL)
                                .setChannelTypeBounds(Set.of(ChannelType.SERVER_TEXT))
                ));
    }
}
