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

package canaryprism.discordbridge.api.exceptions;

import canaryprism.discordbridge.api.enums.PartialSupport;
import org.jetbrains.annotations.NotNull;

/// Exception to indicate that the value of the [PartialSupport] enum is not supported by
/// this implementation
public class UnsupportedValueException extends IllegalArgumentException {
    
    /// Creates a new UnsupportedValueException with the specified value
    ///
    /// @param value the unsupported value
    public UnsupportedValueException(@NotNull PartialSupport value) {
        super(String.format("Value %s.%s Unsupported by this implementation",
                value.getClass().getSimpleName(), value));
    }
}
