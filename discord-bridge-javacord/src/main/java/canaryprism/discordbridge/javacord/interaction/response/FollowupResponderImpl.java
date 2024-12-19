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

package canaryprism.discordbridge.javacord.interaction.response;

import canaryprism.discordbridge.api.DiscordBridge;
import canaryprism.discordbridge.api.interaction.response.FollowupResponder;
import org.javacord.api.interaction.callback.InteractionFollowupMessageBuilder;
import org.javacord.api.interaction.callback.InteractionMessageBuilderBase;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public record FollowupResponderImpl(DiscordBridge bridge, InteractionFollowupMessageBuilder responder) implements FollowupResponder, ResponseBuilderImpl<FollowupResponder> {
    
    @Override
    public @NotNull CompletableFuture<?> send() {
        return responder.send();
    }
    
    @Override
    public @NotNull CompletableFuture<?> update(long message_id) {
        return responder.update(message_id);
    }
    
    @Override
    public InteractionMessageBuilderBase<?> getInteractionMessageBuilderBase() {
        return responder;
    }
    
    @Override
    public @NotNull Object getImplementation() {
        return responder;
    }
    
    @Override
    public @NotNull DiscordBridge getBridge() {
        return bridge;
    }
}
