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

import canaryprism.discordbridge.api.interaction.response.ResponseBuilder;
import canaryprism.discordbridge.api.message.MessageFlag;
import org.javacord.api.interaction.callback.InteractionMessageBuilderBase;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

@SuppressWarnings("unchecked")
public interface ResponseBuilderImpl<T extends ResponseBuilder<T>> extends ResponseBuilder<T> {
    
    InteractionMessageBuilderBase<?> getInteractionMessageBuilderBase();
    
    @Override
    default @NotNull T setContent(@NotNull String text) {
        getInteractionMessageBuilderBase().setContent(text);
        return ((T) this);
    }
    
    @Override
    default @NotNull T setFlags(EnumSet<MessageFlag> flags) {
        getInteractionMessageBuilderBase().setFlags(flags.stream()
                .map(getBridge()::getImplementationValue)
                .map(org.javacord.api.entity.message.MessageFlag.class::cast)
                .toArray(org.javacord.api.entity.message.MessageFlag[]::new)
        );
        return ((T) this);
    }
}
