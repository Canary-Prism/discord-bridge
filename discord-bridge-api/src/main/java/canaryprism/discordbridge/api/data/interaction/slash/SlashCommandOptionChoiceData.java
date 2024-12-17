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

package canaryprism.discordbridge.api.data.interaction.slash;

import canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/// Represents data for a SlashCommandOptionChoice
///
/// This class checks values passed to setter methods and requires they conform to Discord's requirements
///
/// As such an instance of this class can be submitted to discord with low chance of failure
public final class SlashCommandOptionChoiceData {
    
    private volatile @Nullable SlashCommandOptionData parent;
    
    private volatile @NotNull SlashCommandOptionType type;
    private volatile @NotNull String name;
    private volatile @NotNull Map<Locale, @NotNull String> name_localizations = Map.of();
    private volatile @NotNull Object value;
    
    /// Constructs a new SlashCommandOptionChoiceData with the given type, name, and value
    ///
    /// These three are required immediately because option choices must have them
    ///
    /// Checks performed on the passed values are the same as [#setType(SlashCommandOptionType)], [#setName(String)] and [#setValue(Object)]
    ///
    /// Name localizations will be empty
    ///
    /// @param type the type of the choice
    /// @param name the name of the choice
    /// @param value the value of the choice
    /// @throws IllegalArgumentException if the type, name or value is invalid
    /// @throws NullPointerException if the type, name or value is null
    /// @see #setType(SlashCommandOptionType)
    /// @see #setName(String)
    /// @see #setValue(Object)
    public SlashCommandOptionChoiceData(@NotNull SlashCommandOptionType type, @NotNull String name, @NotNull Object value) {
        this.type = type;
        this.name = name;
        this.value = value;
        this.setType(type);
        this.setName(name);
        this.setValue(value);
    }
    
    /// Constructs a new SlashCommandOptionChoiceData with the given name and value, inferring the type
    ///
    /// This is a convenience constructor to infer the type of the value passed for
    /// [#SlashCommandOptionChoiceData(SlashCommandOptionType, String, Object)]
    ///
    /// @param name the name of the choice
    /// @param value the value of the choice
    /// @throws IllegalArgumentException if the type can't be inferred or the name or value is invalid
    /// @throws NullPointerException if the name or value is null
    /// @see #SlashCommandOptionChoiceData(SlashCommandOptionType, String, Object)
    public SlashCommandOptionChoiceData(@NotNull String name, @NotNull Object value) {
        this(infer(Objects.requireNonNull(value, "value can't be null")), name, value);
    }
    
    private static SlashCommandOptionType infer(@NotNull Object value) {
        var types = Arrays.stream(SlashCommandOptionType.values())
                .filter(SlashCommandOptionType::canBeChoices)
                .filter((e) -> e.getTypeRepresentation().isInstance(value))
                .toList();
        return types.stream()
                .max(Comparator.comparing((type) ->
                        types.stream()
                                .map(SlashCommandOptionType::getTypeRepresentation)
                                .filter((e) -> e.isAssignableFrom(type.getTypeRepresentation()))
                                .count()))
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("can't infer type for value '%s'", value)));
    }
    
    /// Gets the [SlashCommandOptionType] of this option choice data
    ///
    /// @return the SlashCommandOptionType
    public @NotNull SlashCommandOptionType getType() {
        return type;
    }
    
    /// Sets the [SlashCommandOptionType] of this option data
    ///
    /// If this option choice has values they must be compatible with the type,
    /// otherwise this method will throw [IllegalStateException]
    ///
    /// If this option choice belongs to an option,
    /// this also checks to make sure the type stays consistent
    ///
    /// @param type the SlashCommandOptionType to set to
    /// @return this
    /// @throws IllegalStateException if the option data has other data incompatible with the setting type
    /// @throws IllegalArgumentException if `SlashCommandOptionType.UNKNOWN` is passed
    /// @throws NullPointerException if type is `null`
    public SlashCommandOptionChoiceData setType(@NotNull SlashCommandOptionType type) {
        var parent = this.parent;
        if (parent != null)
            parent.checkChoiceType(type);
        
        if (!type.getTypeRepresentation().isInstance(value))
            throw new IllegalStateException(String.format("type incompatible with value %s", value));
        
        this.type = type;
        return this;
    }
    
    /// Gets the name of this option choice data
    ///
    /// @return the name
    public @NotNull String getName() {
        return name;
    }
    
    /// Sets the name of this option choice data
    ///
    /// Choice names have the same requirements as [command descriptions][SlashCommandData#setDescription(String)]
    ///
    /// If this option data belongs to a command or other option,
    /// this also checks to make sure the name is still unique
    ///
    /// @param name the name to set
    /// @return this
    /// @throws IllegalArgumentException if the name is invalid
    /// @throws NullPointerException if the name is null
    /// @see SlashCommandData#setDescription(String)
    public @NotNull SlashCommandOptionChoiceData setName(@NotNull String name) {
        SlashCommandData.checkStringLength(name, SlashCommandData.MAX_DESCRIPTION_LENGTH, "name");

        this.name = name;
        
        return this;
    }
    
    /// Gets the name localizations of this option choice data
    ///
    /// @return the name localizations
    public @NotNull @Unmodifiable Map<Locale, @NotNull String> getNameLocalizations() {
        return name_localizations;
    }
    
    /// Sets the name localizations of this option choice data
    ///
    /// Localised names are subject to the same requirements as the choice name
    ///
    /// @param name_localizations a map of name localizations to set
    /// @return this
    /// @throws IllegalArgumentException if any localized name is invalid
    /// @throws NullPointerException if the map or any of the values in the map is null
    /// @see #setName(String)
    public SlashCommandOptionChoiceData setNameLocalizations(@NotNull Map<Locale, @NotNull String> name_localizations) {
        this.name_localizations = Map.copyOf(name_localizations);
        return this;
    }
    
    /// Gets the value of this option choice
    ///
    /// @return the option
    public @NotNull Object getValue() {
        return value;
    }
    
    /// Sets the value of this option choice
    ///
    /// The value must be assignable to the class returned when calling [SlashCommandOptionType#getTypeRepresentation()]
    /// on this choice's type
    ///
    /// @param value the value to set
    /// @return this
    /// @throws IllegalStateException if the value is incompatible with this choice's type
    public SlashCommandOptionChoiceData setValue(@NotNull Object value) {
        if (!type.getTypeRepresentation().isInstance(value))
            throw new IllegalStateException(String.format(
                    "can't set value %s for type %s", value, type));
        
        this.value = value;
        
        return this;
    }
}
