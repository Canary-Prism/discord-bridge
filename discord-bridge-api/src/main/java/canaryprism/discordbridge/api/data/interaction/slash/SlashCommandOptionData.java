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

import canaryprism.discordbridge.api.channel.ChannelType;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOption;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

import static canaryprism.discordbridge.api.data.interaction.slash.SlashCommandData.MAX_DESCRIPTION_LENGTH;
import static canaryprism.discordbridge.api.data.interaction.slash.SlashCommandData.MAX_OPTION_COUNT;
import static canaryprism.discordbridge.api.interaction.slash.SlashCommandOption.*;

/// Represents data for a SlashCommandOption
///
/// This class checks values passed to setter methods and requires they conform to Discord's requirements,
/// mostly length limits and such
///
/// As such an instance of this class can be submitted to discord with low chance of failure
public final class SlashCommandOptionData {
    
    private volatile @Nullable SlashCommandData command_parent;
    private volatile @Nullable SlashCommandOptionData option_parent;
    
    synchronized void removeParent() {
        command_parent = null;
        option_parent = null;
    }
    synchronized void setParent(@NotNull SlashCommandData command) {
        command_parent = command;
        option_parent = null;
    }
    synchronized void setParent(@NotNull SlashCommandOptionData option) {
        command_parent = null;
        option_parent = option;
    }
    
    private volatile @NotNull String name;
    private volatile @NotNull String description;
    private volatile @NotNull Map<Locale, @NotNull String> name_localizations = Map.of();
    private volatile @NotNull Map<Locale, @NotNull String> description_localizations = Map.of();
    
    private volatile @NotNull SlashCommandOptionType type;
    private volatile boolean required = true;
    private volatile boolean autocompletable = false;
    private volatile @NotNull List<@NotNull SlashCommandOptionChoiceData> choices = List.of();
    private volatile @NotNull List<@NotNull SlashCommandOptionData> options = List.of();
    
    private volatile @NotNull EnumSet<ChannelType> channel_type_bounds = EnumSet.noneOf(ChannelType.class);
    
    private volatile @Nullable @Range(from = MIN_NUMBER, to = MAX_NUMBER) Long min_integer;
    private volatile @Nullable @Range(from = MIN_NUMBER, to = MAX_NUMBER) Long max_integer;

    private volatile @Nullable @Range(from = MIN_NUMBER, to = MAX_NUMBER) Double min_number;
    private volatile @Nullable @Range(from = MIN_NUMBER, to = MAX_NUMBER) Double max_number;

    private volatile @Nullable @Range(from = 0, to = MAX_STRING_LENGTH) Long min_length;
    private volatile @Nullable @Range(from = 1, to = MAX_STRING_LENGTH) Long max_length;
    
    /// Constructs a new SlashCommandData instance with the given name, description, and type
    ///
    /// These three are required immediately because command options must have them
    ///
    /// Checks performed on the passed values are the same as [#setName(String)], [#setDescription(String)] and [#setType(SlashCommandOptionType)]
    ///
    /// Other fields have default values as follows:
    ///  - [#name_localizations] and [#description_localizations] are empty maps
    ///  - [#options] and [#choices] are empty lists
    ///  - [#required] is `true`
    ///  - [#autocompletable] is `false`
    ///  - all bounds are unset
    ///
    /// @param name the name of the option
    /// @param description the description of the option
    /// @param type the type of the option
    /// @throws IllegalArgumentException if the name, description or type is invalid
    /// @throws NullPointerException if the name, description or type is null
    /// @see #setName(String)
    /// @see #setDescription(String)
    /// @see #setType(SlashCommandOptionType)
    public SlashCommandOptionData(@NotNull String name, @NotNull String description, @NotNull SlashCommandOptionType type) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.setName(name);
        this.setDescription(description);
        this.setType(type);
    }
    
    /// Gets the name of this option data
    ///
    /// @return the name
    public @NotNull String getName() {
        return name;
    }
    
    /// Sets the name of this option data
    ///
    /// Option names have the same requirements as [command names][SlashCommandData#setName(String)]
    ///
    /// If this option data belongs to a command or other option,
    /// this also checks to make sure the name is still unique
    ///
    /// @param name the name to set
    /// @return this
    /// @throws IllegalArgumentException if the name is invalid
    /// @throws NullPointerException if the name is null
    /// @see SlashCommandData#setName(String)
    public @NotNull SlashCommandOptionData setName(@NotNull String name) {
        SlashCommandData.checkName(name, "name");
        synchronized (this) {
            var command_parent = this.command_parent;
            var option_parent = this.option_parent;
            
            if (command_parent != null)
                command_parent.ensureUnique(this, name);
            
            if (option_parent != null)
                option_parent.ensureUnique(this, name);
        }
        
        this.name = name;
        
        return this;
    }
    
    /// Gets the description of this option data
    ///
    /// @return the description
    public @NotNull String getDescription() {
        return description;
    }
    
    /// Sets the description of this option data
    ///
    /// like [command descriptions][SlashCommandData#setDescription(String)], option descriptions must be between
    /// 1 and {@value SlashCommandData#MAX_DESCRIPTION_LENGTH} characters long
    ///
    /// @param description the description to set
    /// @return this
    /// @throws IllegalArgumentException if the description is invalid
    /// @throws NullPointerException if the description is null
    /// @see SlashCommandData#setDescription(String)
    public @NotNull SlashCommandOptionData setDescription(@NotNull String description) {
        this.description = description;
        return this;
    }
    
    /// Gets the name localizations of this option data
    ///
    /// @return the name localizations
    public @NotNull @Unmodifiable Map<Locale, @NotNull String> getNameLocalizations() {
        return name_localizations;
    }
    
    /// Sets the name localizations of this option data
    ///
    /// Localised names are subject to the same requirements as the option name,
    /// which itself has the same requirements as [command names][SlashCommandData#setName(String)],
    /// except localised names don't have to be mutually unique
    ///
    /// @param name_localizations a map of name localizations to set
    /// @return this
    /// @throws IllegalArgumentException if any localized name is invalid
    /// @throws NullPointerException if the map or any of the values in the map is null
    /// @see #setName(String)
    /// @see SlashCommandData#setName(String)
    public @NotNull SlashCommandOptionData setNameLocalizations(@NotNull Map<Locale, @NotNull String> name_localizations) {
        name_localizations = Map.copyOf(name_localizations);
        name_localizations.forEach((locale, name) ->
                SlashCommandData.checkName(name, String.format("name for locale %s", locale)));
        this.name_localizations = name_localizations;
        return this;
    }
    
    /// Gets the description localizations of this slash command data
    ///
    /// @return the description localizations
    public @NotNull @Unmodifiable Map<Locale, @NotNull String> getDescriptionLocalizations() {
        return description_localizations;
    }
    
    /// Sets the description localizations of this option data
    ///
    /// Localised description are subject to the same requirements as the option description,
    /// which itself has the same requirements as [command desscriptions][SlashCommandData#setDescription(String)]
    ///
    /// @param description_localizations a map of description localizations to set
    /// @return this
    /// @throws IllegalArgumentException if any localized description is invalid
    /// @throws NullPointerException if the map or any of the values in the map is null
    /// @see #setDescription(String)
    /// @see SlashCommandData#setDescription(String)
    public @NotNull SlashCommandOptionData setDescriptionLocalizations(@NotNull Map<Locale, @NotNull String> description_localizations) {
        description_localizations = Map.copyOf(description_localizations);
        description_localizations.forEach((locale, description) ->
                SlashCommandData.checkStringLength(description, MAX_DESCRIPTION_LENGTH, String.format("description for locale %s", locale)));
        this.description_localizations = description_localizations;
        return this;
    }
    
    /// Gets the [SlashCommandOptionType] of this option data
    ///
    /// @return the SlashCommandOptionType
    public @NotNull SlashCommandOptionType getType() {
        return type;
    }
    
    /// Sets the [SlashCommandOptionType] of this option data
    ///
    /// If this option data has bounds on it of any kind, options, or choices and you wish to set this option data's type
    /// to a different type which doesn't support them you must first unset them by either:
    ///  - setting bounds to `null`
    ///  - setting options or choices to empty lists
    ///
    /// Otherwise this method may throw [IllegalStateException]
    ///
    /// If this option data belongs to a command or other option,
    /// this also checks to make sure the entire hierarchy is still valid
    ///
    /// @param type the SlashCommandOptionType to set to
    /// @return this
    /// @throws IllegalStateException if the option data has other data incompatible with the setting type
    /// @throws IllegalArgumentException if `SlashCommandOptionType.UNKNOWN` is passed
    /// @throws NullPointerException if type is `null`
    public @NotNull SlashCommandOptionData setType(@NotNull SlashCommandOptionType type) {
        Objects.requireNonNull(type, "type must not be null");
        
        if (type == SlashCommandOptionType.UNKNOWN)
            throw new IllegalArgumentException("UNKNOWN disallowed here");
        
        if (type != SlashCommandOptionType.SUBCOMMAND && type != SlashCommandOptionType.SUBCOMMAND_GROUP && !options.isEmpty())
            throw new IllegalStateException("options are only allowed with type SUBCOMMAND and SUBCOMMAND_GROUP");
        
        synchronized (this) {
            // if the type is SUBCOMMAND_GROUP
            if (type == SlashCommandOptionType.SUBCOMMAND_GROUP) {
                // skip checking for SUBCOMMAND_GROUP options (those are never valid as options in options anyway)
                // check for *not* SUBCOMMAND options
                if (options.stream().anyMatch((e) -> e.getType() != SlashCommandOptionType.SUBCOMMAND))
                    throw new IllegalStateException("SUBCOMMAND_GROUP can only have SUBCOMMAND options");
                
                if (option_parent != null)
                    throw new IllegalStateException("SUBCOMMAND_GROUP options can only belong to commands, not options");
                

                var command_parent = this.command_parent;
                if (command_parent != null)
                    command_parent.checkOptionTypes(this, type);
                
            }
            // if the type is SUBCOMMAND
            if (type == SlashCommandOptionType.SUBCOMMAND) {
                // skip checking for SUBCOMMAND_GROUP options (those are never valid as options in options anyway)
                // check for SUBCOMMAND options
                if (options.stream().anyMatch((e) -> e.getType() == SlashCommandOptionType.SUBCOMMAND))
                    throw new IllegalStateException("SUBCOMMAND can't have SUBCOMMAND options");
                

                var option_parent = this.option_parent;
                if (option_parent != null)
                    option_parent.checkOptionTypes(type);
                

                var command_parent = this.command_parent;
                if (command_parent != null)
                    command_parent.checkOptionTypes(this, type);
            }
        }
        
        if (type == SlashCommandOptionType.SUBCOMMAND_GROUP || type == SlashCommandOptionType.SUBCOMMAND)
            if (!required)
                throw new IllegalStateException("SUBCOMMAND_GROUP and SUBCOMMAND may not be set as not required");
        
        if (!type.can_be_choices) {
            if (!choices.isEmpty())
                throw new IllegalStateException(String.format("%s type cannot have option choices", type));
            if (autocompletable)
                throw new IllegalStateException(String.format("%s type cannot be autocompletable", type));
        }
        
        
        if (type != SlashCommandOptionType.STRING) {
            if (min_length != null)
                throw new IllegalStateException("String minimum length bounds only allowed with type STRING");
            if (max_length != null)
                throw new IllegalStateException("String maximum length bounds only allowed with type STRING");
        }
        
        if (type != SlashCommandOptionType.INTEGER) {
            if (min_integer != null)
                throw new IllegalStateException("minimum integer bounds only allowed with type INTEGER");
            if (max_integer != null)
                throw new IllegalStateException("maximum integer bounds only allowed with type INTEGER");
        }
        
        if (type != SlashCommandOptionType.NUMBER) {
            if (min_number != null)
                throw new IllegalStateException("minimum number bounds only allowed with type NUMBER");
            if (max_number != null)
                throw new IllegalStateException("maximum number bounds only allowed with type NUMBER");
        }
        
        if (type != SlashCommandOptionType.CHANNEL && !channel_type_bounds.isEmpty())
            throw new IllegalStateException("channel type bounds only allowed with type CHANNEL");
        
        if (!choices.stream().allMatch((e) -> e.getType() == type))
            throw new IllegalStateException(String.format(
                    "option has choices incompatible with type %s", type));
        
        this.type = type;

        return this;
    }
    
    /// Gets whether this option data is required or not
    ///
    /// @return whether this option is required or not
    public boolean isRequired() {
        return required;
    }
    
    /// Sets whether this option data is required or not
    ///
    /// Options may only be set to be not required if they're after all other required options.
    /// As such if this option data belongs to either a command data or another option data,
    /// it will check to make sure that you're not causing any required options to go after non required ones,
    /// and throw [IllegalStateException] if that is not the case
    ///
    /// @param required whether this option data is required
    /// @return this
    /// @throws IllegalStateException if setting this option to the specified state
    ///                               causes required options to appear after non required ones
    public @NotNull SlashCommandOptionData setRequired(boolean required) {
        synchronized (this) {
            var command_parent = this.command_parent;
            var option_parent = this.option_parent;
            
            if (command_parent != null)
                command_parent.checkOptionRequirements(this, required);
            if (option_parent != null)
                option_parent.checkOptionRequirements(this, required);
        }
        
        this.required = required;
        
        return this;
    }
    
    /// Gets whether this option data is autocompletable or not
    ///
    /// @return whether this option data is autocompletable
    public boolean isAutocompletable() {
        return autocompletable;
    }
    
    /// Sets whether this option data is autocompletable or not
    ///
    /// Autocompletable has the same restrictions as [option choices][#setChoices(List)] and are incompatible with them
    ///
    /// @param autocompletable whether this option data is autocompletable
    /// @return this
    /// @throws IllegalStateException if the option's current set type doesn't support autocomplete/choices
    ///                               or option choices are already set
    public @NotNull SlashCommandOptionData setAutocompletable(boolean autocompletable) {
        var type = this.type;
        if (autocompletable) {
            if (!type.can_be_choices)
                throw new IllegalStateException(String.format("option type %s doesn't support autocomplete", type));
            if (!choices.isEmpty())
                throw new IllegalStateException(
                        "autocompletable can't be set to true because this option already has static option choices");
        }
        
        this.autocompletable = autocompletable;
        return this;
    }
    
    /// Gets the choices of this option data
    ///
    /// @return the choices
    public @NotNull @Unmodifiable List<@NotNull SlashCommandOptionChoiceData> getChoices() {
        return choices;
    }
    
    /// Sets the choices of this option data
    ///
    /// Option choices may only be applied to options with types where [SlashCommandOptionType#can_be_choices] is true
    ///
    /// Option choices are incompatible with [autocompletable][#setAutocompletable(boolean)]
    ///
    /// Option choice list must have at most {@value SlashCommandData#MAX_OPTION_COUNT} elements
    ///
    /// @param choices the choices to set
    /// @return this
    /// @throws IllegalStateException if the option's current set type doesn't support autocomplete/choices
    ///                               or this option is already set to autocompletable
    /// @throws NullPointerException if the list or any of the elements in the list is null
    public @NotNull SlashCommandOptionData setChoices(@NotNull List<@NotNull SlashCommandOptionChoiceData> choices) {
        choices = List.copyOf(choices);
        
        if (choices.size() > MAX_OPTION_COUNT)
            throw new IllegalArgumentException(String.format("choices list may only have up to %s options", MAX_OPTION_COUNT));
        
        
        var type = this.type;
        if (autocompletable) {
            if (!type.can_be_choices)
                throw new IllegalStateException(String.format("option type %s doesn't support choices", type));
            if (autocompletable)
                throw new IllegalStateException(
                        "option choices can't be set because this option already has autocompletable set to true");
        }
        
        checkChoiceType(choices);
        
        this.choices = choices;
        return this;
    }
    
    synchronized void checkChoiceType(@NotNull List<@NotNull SlashCommandOptionChoiceData> choices) {
        for (var e : choices) {
            if (type != e.getType())
                throw new IllegalStateException(String.format("option choice type %s doesn't match option type %s",
                        e.getType(), type));
        }
    }
    
    synchronized void checkChoiceType(@NotNull SlashCommandOptionType type) {
        if (type != this.type)
            throw new IllegalStateException(String.format("option choice type %s doesn't match option type %s",
                    type, this.type));
    }
    
    /// Gets the options of this option data
    ///
    /// @return the options
    public @NotNull @Unmodifiable List<@NotNull SlashCommandOptionData> getOptions() {
        return options;
    }
    
    /// Sets the options of this option data
    ///
    /// Options may only be set if this option's type is either
    /// [SlashCommandOptionType#SUBCOMMAND_GROUP] or [SlashCommandOptionType#SUBCOMMAND]:
    ///  - if the type is [SlashCommandOptionType#SUBCOMMAND_GROUP], its options must all be of type
    ///    [SlashCommandOptionType#SUBCOMMAND]
    ///  - if the type is [SlashCommandOptionType#SUBCOMMAND], its options must not have any be of type
    ///    [SlashCommandOptionType#SUBCOMMAND]
    ///
    /// ### The rest of the requirements are the same as [command options][SlashCommandData#setOptions(List)]:
    ///
    /// Option list must have at most {@value SlashCommandData#MAX_OPTION_COUNT} elements
    ///
    /// All options in the provided list must have unique names
    ///
    /// [SlashCommandOptionType#SUBCOMMAND] and [SlashCommandOptionType#SUBCOMMAND_GROUP]
    /// may not appear alongside other types
    ///
    /// All required options must appear before all non required options
    ///
    /// @param options the list of option data to set
    /// @return this
    /// @throws IllegalStateException if this option's type cannot support the provided option list
    /// @throws IllegalArgumentException if the specified option list is invalid
    /// @throws NullPointerException if the list or any of the elements in the list is null
    public @NotNull SlashCommandOptionData setOptions(@NotNull List<@NotNull SlashCommandOptionData> options) {
        options = List.copyOf(options);
        
        if (options.size() > MAX_OPTION_COUNT)
            throw new IllegalArgumentException(String.format("options list may only have up to %s options", MAX_OPTION_COUNT));
        
        if (options.stream().anyMatch((e) -> e.type == SlashCommandOptionType.SUBCOMMAND_GROUP))
            throw new IllegalArgumentException("SUBCOMMAND_GROUP options cannot be nested in options");
        
        if (type == SlashCommandOptionType.SUBCOMMAND_GROUP) {
            if (options.stream().anyMatch((e) -> e.type != SlashCommandOptionType.SUBCOMMAND))
                throw new IllegalStateException("SUBCOMMAND_GROUP option's options must all be of type SUBCOMMAND");
        } else if (type == SlashCommandOptionType.SUBCOMMAND) {
            if (options.stream().anyMatch((e) -> e.type == SlashCommandOptionType.SUBCOMMAND))
                throw new IllegalStateException("SUBCOMMAND option's options must not be SUBCOMMAND");
        } else {
            if (!options.isEmpty())
                throw new IllegalStateException("option type doesn't support nested options");
        }
        
        SlashCommandData.ensureUnique(options);
        SlashCommandData.checkOptionTypes(options);
        
        var old_options = this.options;
        this.options = options;
        
        for (var option : old_options) {
            option.removeParent();
        }
        for (var option : options) {
            option.setParent(this);
        }
        
        return this;
    }
    
    
    
    /// invoked by this's options to check if it can change name
    synchronized void ensureUnique(@NotNull SlashCommandOptionData option, @NotNull String name) {
        var set = new HashSet<String>();
        for (var e : options) {
            var n = (e == option) ? name : e.getName();
            if (!set.add(n))
                throw new IllegalStateException(String.format("options list contains duplicate name '%s'", option.getName()));
        }
    }
    
    /// invoked by this's options to check if it can change types
    synchronized void checkOptionTypes(SlashCommandOptionType type) {
        var this_type = this.type;
        if (!canNestType(this_type, type))
            throw new IllegalStateException(
                    String.format("can't set option type to %s because it is nested in option of incompatible type %s",
                            type, this_type));
    }
    
    boolean canNestType(SlashCommandOptionType outer, SlashCommandOptionType inner) {
        return (outer == SlashCommandOptionType.SUBCOMMAND_GROUP && inner == SlashCommandOptionType.SUBCOMMAND)
                || (outer == SlashCommandOptionType.SUBCOMMAND
                    && inner != SlashCommandOptionType.SUBCOMMAND_GROUP && inner != SlashCommandOptionType.SUBCOMMAND);
    }
    
    synchronized void checkOptionRequirements(@NotNull SlashCommandOptionData option, boolean required) {
        boolean was_required = true;
        for (var e : options) {
            var is_required = (e == option) ? required : option.isRequired();
            if (!was_required && is_required)
                if (required)
                    throw new IllegalStateException("option can't be set to required because options before it are not required");
                else
                    throw new IllegalStateException("option can't be set to not required because options after it are required");
            
            was_required = is_required;
        }
    }
    
    private void expectType(SlashCommandOptionType type, String message) {
        if (this.type != type)
            throw new IllegalStateException(message);
    }
    
    private void limitNumber(double number, long min, long max, String name) {
        if (number < min)
            throw new IllegalArgumentException(String.format("%s must not be less than %s", name, min));
        if (number > max)
            throw new IllegalArgumentException(String.format("%s must not be greater than %s", name, min));
    }
    
    /// Gets the [ChannelType] bounds of this option data
    ///
    /// @return the channel type bounds
    public @NotNull EnumSet<ChannelType> getChannelTypeBounds() {
        return EnumSet.copyOf(channel_type_bounds);
    }
    
    /// Sets the [ChannelType] bounds of this option data
    ///
    /// Channel type bounds limit the user into only choosing channels that match one of the specified ChannelTypes
    ///
    /// Channel type bounds may only be set if option is of type [SlashCommandOptionType#CHANNEL]
    ///
    /// @param channel_type_bounds the ChannelType bounds to set to or empty to allow all types
    /// @return this
    /// @throws IllegalStateException if option isn't of type `SlashCommandOptionType.CHANNEL`
    public @NotNull SlashCommandOptionData setChannelTypeBounds(@NotNull EnumSet<ChannelType> channel_type_bounds) {
        expectType(SlashCommandOptionType.CHANNEL, "channel type bounds may only be set on CHANNEL options");
        this.channel_type_bounds = channel_type_bounds;
        return this;
    }
    
    /// Gets the minimum allowed INTEGER value of this option data
    ///
    /// @return the minimum of the integer bounds
    public @NotNull Optional<@Range(from = MIN_NUMBER, to = MAX_NUMBER) Long> getIntegerBoundsMin() {
        return Optional.ofNullable(min_integer);
    }
    
    /// Sets the minimum allowed INTEGER value of this option data
    ///
    /// Discord INTEGER values must be between {@value SlashCommandOption#MIN_NUMBER} and {@value SlashCommandOption#MAX_NUMBER}
    /// and bounds follow the same restrictions
    ///
    /// May only be set if option is of type [SlashCommandOptionType#INTEGER]
    ///
    /// @param min_integer the minimum integer bounds to set or `null` to remove the bounds
    /// @return this
    /// @throws IllegalStateException if option isn't of type `SlashCommandOptionType.INTEGER`
    /// @throws IllegalArgumentException if the specified bounds is invalid
    public synchronized @NotNull SlashCommandOptionData setIntegerBoundsMin(@Nullable @Range(from = MIN_NUMBER, to = MAX_NUMBER) Long min_integer) {
        expectType(SlashCommandOptionType.INTEGER, "integer bounds may only be set on INTEGER options");
        if (min_integer != null)
            limitNumber(min_integer, MIN_NUMBER, MIN_NUMBER, "min integer");
        
        var max_integer = this.max_integer;
        if (min_integer != null && max_integer != null)
            if (min_integer > max_integer)
                throw new IllegalStateException("min can't be greater than max");
            
        this.min_integer = min_integer;
        return this;
    }
    
    /// Gets the maximum allowed INTEGER value of this option data
    ///
    /// @return the maximum of the integer bounds
    public @NotNull Optional<@Range(from = MIN_NUMBER, to = MAX_NUMBER) Long> getIntegerBoundsMax() {
        return Optional.ofNullable(max_integer);
    }
    
    /// Sets the maximum allowed INTEGER value of this option data
    ///
    /// Discord INTEGER values must be between {@value SlashCommandOption#MIN_NUMBER} and {@value SlashCommandOption#MAX_NUMBER}
    /// and bounds follow the same restrictions
    ///
    /// May only be set if option is of type [SlashCommandOptionType#INTEGER]
    ///
    /// @param max_integer the minimum integer bounds to set or `null` to remove the bounds
    /// @return this
    /// @throws IllegalStateException if option isn't of type `SlashCommandOptionType.INTEGER`
    /// @throws IllegalArgumentException if the specified bounds is invalid
    public synchronized @NotNull SlashCommandOptionData setIntegerBoundsMax(@Nullable @Range(from = MIN_NUMBER, to = MAX_NUMBER) Long max_integer) {
        expectType(SlashCommandOptionType.INTEGER, "integer bounds may only be set on INTEGER options");
        if (max_integer != null)
            limitNumber(max_integer, MIN_NUMBER, MIN_NUMBER, "max integer");
        
        var min_integer = this.min_integer;
        if (min_integer != null && max_integer != null)
            if (min_integer > max_integer)
                throw new IllegalStateException("max can't be smaller than min");
        
        this.max_integer = max_integer;
        return this;
    }
    
    /// Gets the minimum allowed NUMBER value of this option data
    ///
    /// @return the minimum of the number bounds
    public @NotNull Optional<@Range(from = MIN_NUMBER, to = MAX_NUMBER) Double> getNumberBoundsMin() {
        return Optional.ofNullable(min_number);
    }
    
    /// Sets the minimum allowed NUMBER value of this option data
    ///
    /// Discord NUMBER values must be between {@value SlashCommandOption#MIN_NUMBER} and {@value SlashCommandOption#MAX_NUMBER}
    /// and bounds follow the same restrictions
    ///
    /// May only be set if option is of type [SlashCommandOptionType#NUMBER]
    ///
    /// @param min_number the minimum number bounds to set or `null` to remove the bounds
    /// @return this
    /// @throws IllegalStateException if option isn't of type `SlashCommandOptionType.NUMBER`
    /// @throws IllegalArgumentException if the specified bounds is invalid
    public @NotNull SlashCommandOptionData setNumberBoundsMin(@Nullable @Range(from = MIN_NUMBER, to = MAX_NUMBER) Double min_number) {
        expectType(SlashCommandOptionType.NUMBER, "number bounds may only be set on NUMBER options");
        if (min_number != null)
            limitNumber(min_number, MIN_NUMBER, MIN_NUMBER, "min number");
        
        var max_number = this.max_number;
        if (min_number != null && max_number != null)
            if (min_number > max_number)
                throw new IllegalStateException("min can't be greater than max");
        
        this.min_number = min_number;
        return this;
    }
    
    /// Gets the maximum allowed NUMBER value of this option data
    ///
    /// @return the maximum of the number bounds
    public @NotNull Optional<@Range(from = MIN_NUMBER, to = MAX_NUMBER) Double> getNumberBoundsMax() {
        return Optional.ofNullable(max_number);
    }
    
    /// Sets the maximum allowed NUMBER value of this option data
    ///
    /// Discord NUMBER values must be between {@value SlashCommandOption#MIN_NUMBER} and {@value SlashCommandOption#MAX_NUMBER}
    /// and bounds follow the same restrictions
    ///
    /// May only be set if option is of type [SlashCommandOptionType#NUMBER]
    ///
    /// @param max_number the maximum number bounds to set or `null` to remove the bounds
    /// @return this
    /// @throws IllegalStateException if option isn't of type `SlashCommandOptionType.NUMBER`
    /// @throws IllegalArgumentException if the specified bounds is invalid
    public @NotNull SlashCommandOptionData setNumberBoundsMax(@Nullable @Range(from = MIN_NUMBER, to = MAX_NUMBER) Double max_number) {
        expectType(SlashCommandOptionType.NUMBER, "number bounds may only be set on NUMBER options");
        if (max_number != null)
            limitNumber(max_number, MIN_NUMBER, MIN_NUMBER, "min number");
        
        var min_number = this.min_number;
        if (min_number != null && max_number != null)
            if (min_number > max_number)
                throw new IllegalStateException("max can't be smaller than min");
        
        this.max_number = max_number;
        return this;
    }
    
    /// Gets the minimum allowed STRING length of this option data
    ///
    /// @return the minimum of the string length bounds
    public @NotNull Optional<@Range(from = 0, to = MAX_STRING_LENGTH) Long> getStringLengthBoundsMin() {
        return Optional.ofNullable(min_length);
    }
    
    /// Sets the minimum allowed STRING length of this option data
    ///
    /// Discord minimum STRING length bounds must be between 0 and {@value SlashCommandOption#MAX_STRING_LENGTH}
    ///
    /// May only be set if option is of type [SlashCommandOptionType#STRING]
    ///
    /// @param min_length the minimum string length bounds to set or `null` to remove the bounds
    /// @return this
    /// @throws IllegalStateException if option isn't of type `SlashCommandOptionType.STRING`
    /// @throws IllegalArgumentException if the specified bounds is invalid
    public @NotNull SlashCommandOptionData setStringLengthBoundsMin(@Nullable @Range(from = 0, to = MAX_STRING_LENGTH) Long min_length) {
        expectType(SlashCommandOptionType.STRING, "string length bounds may only be set on STRING options");
        if (min_length != null)
            limitNumber(min_length, MIN_NUMBER, MIN_NUMBER, "min length");
        
        var max_length = this.max_length;
        if (min_length != null && max_length != null)
            if (min_length > max_length)
                throw new IllegalStateException("min can't be greater than max");
        
        this.min_length = min_length;
        return this;
    }
    
    /// Gets the minimum allowed STRING length of this option data
    ///
    /// @return the minimum of the string length bounds
    public @NotNull Optional<@Range(from = 1, to = MAX_STRING_LENGTH) Long> getStringLengthBoundsMax() {
        return Optional.ofNullable(max_length);
    }
    
    /// Sets the maximum allowed STRING length of this option data
    ///
    /// Discord maximum STRING length bounds must be between 1 and {@value SlashCommandOption#MAX_STRING_LENGTH}
    ///
    /// May only be set if option is of type [SlashCommandOptionType#STRING]
    ///
    /// @param max_length the maximum string length bounds to set or `null` to remove the bounds
    /// @return this
    /// @throws IllegalStateException if option isn't of type `SlashCommandOptionType.STRING`
    /// @throws IllegalArgumentException if the specified bounds is invalid
    public @NotNull SlashCommandOptionData setStringLengthBoundsMax(@Nullable @Range(from = 1, to = MAX_STRING_LENGTH) Long max_length) {
        expectType(SlashCommandOptionType.STRING, "string length bounds may only be set on STRING options");
        if (max_length != null)
            limitNumber(max_length, MIN_NUMBER, MIN_NUMBER, "min length");
        
        var min_length = this.min_length;
        if (min_length != null && max_length != null)
            if (min_length > max_length)
                throw new IllegalStateException("min can't be greater than max");
        
        this.max_length = max_length;
        return this;
    }
}
