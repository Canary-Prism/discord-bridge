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

import org.junit.jupiter.api.Test;

/// Tests loading the bridge implementations without their dependencies present, which should still work
public class LoadingBridgeTests {
    
    private void tryLoad(String class_name) {
        try {
            Class.forName(class_name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Test
    void javacord() {
        tryLoad("canaryprism.discordbridge.javacord.DiscordBridgeJavacord");
    }
    
    @Test
    void jda() {
        tryLoad("canaryprism.discordbridge.jda.DiscordBridgeJDA");
    }
    
    @Test
    void discord4j() {
        tryLoad("canaryprism.discordbridge.discord4j.DiscordBridgeDiscord4J");
    }
    
    @Test
    void kord() {
        tryLoad("canaryprism.discordbridge.kord.DiscordBridgeKord");
    }
}
