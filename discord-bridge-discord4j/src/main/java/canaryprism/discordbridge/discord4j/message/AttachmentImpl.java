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

package canaryprism.discordbridge.discord4j.message;

import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.message.Attachment;
import canaryprism.discordbridge.discord4j.DiscordBridgeDiscord4J;
import org.jetbrains.annotations.NotNull;

public record AttachmentImpl(DiscordBridgeDiscord4J bridge, discord4j.core.object.entity.Attachment attachment) implements Attachment {
    
    @Override
    public @NotNull String getIdAsString() {
        return attachment.getId().asString();
    }
    
    @Override
    public long getId() {
        return attachment.getId().asLong();
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return attachment;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
}
