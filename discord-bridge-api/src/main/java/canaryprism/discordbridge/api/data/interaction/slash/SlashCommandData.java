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

import canaryprism.discordbridge.api.data.interaction.CommandData;
import canaryprism.discordbridge.api.interaction.slash.SlashCommandOptionType;
import canaryprism.discordbridge.api.server.permission.PermissionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

/// Represents data for a SlashCommand
///
/// This class checks values passed to setter methods and requires they conform to Discord's requirements,
/// mostly length limits and such
///
/// As such an instance of this class can be submitted to discord with low chance of failure
public final class SlashCommandData implements CommandData {
    
    /// Max allowed length of slash command name
    public static final int MAX_NAME_LENGTH = 32;
    /// Pattern slash command names must match
    public static final Pattern NAME_PATTERN = Pattern.compile(
            "^[-_\\p{L}\\p{N}\\p{sc=Deva}\\p{sc=Thai}]{1,32}$", Pattern.MULTILINE | Pattern.UNICODE_CHARACTER_CLASS);
    /// Max allowed length of slash command description
    public static final int MAX_DESCRIPTION_LENGTH = 100;
    /// Max allowed option list length
    public static final int MAX_OPTION_COUNT = 25;
    
    private volatile @NotNull String name;
    private volatile @NotNull String description;
    private volatile @NotNull Map<Locale, @NotNull String> name_localizations = Map.of();
    private volatile @NotNull Map<Locale, @NotNull String> description_localizations = Map.of();
    
    private volatile @NotNull List<@NotNull SlashCommandOptionData> options = List.of();
    
    private volatile boolean default_disabled = false;
    private volatile @Nullable EnumSet<PermissionType> required_permissions = null;
    private volatile boolean enabled_in_DMs = true;
    private volatile boolean nsfw = false;
    
    /// Constructs a new SlashCommandData instance with the given name and description
    ///
    /// These two are required immediately because commands must have both
    ///
    /// Checks performed on the passed values are the same as [#setName(String)] and [#setDescription(String)]
    ///
    /// Other fields have default values as follows:
    ///  - [#name_localizations] and [#description_localizations] are empty maps
    ///  - [#options] is an empty list
    ///  - [#default_disabled] and [#nsfw] is `false`
    ///  - [#required_permissions] is `null`
    ///  - [#enabled_in_DMs] is `true`
    ///
    /// @param name the name of the command
    /// @param description the description of the command
    /// @throws IllegalArgumentException if the name or description is invalid
    /// @throws NullPointerException if the name or description is null
    /// @see #setName(String)
    /// @see #setDescription(String)
    public SlashCommandData(@NotNull String name, @NotNull String description) {
        this.name = name;
        this.description = description;
        this.setName(name);
        this.setDescription(description);
    }
    
    static @NotNull String checkStringLength(@Nullable String string, int max_length, @NotNull String name) {
        Objects.requireNonNull(string, name + " must not be null");
        
        if (name.isBlank())
            throw new IllegalArgumentException(String.format("%s must not be blank", name));
        
        if (name.length() > max_length)
            throw new IllegalArgumentException(String.format("%s must not exceed %s characters", name, max_length));
        
        return string;
    }
    
    /// Gets the name of this slash command data
    ///
    /// @return the name
    public @NotNull String getName() {
        return name;
    }
    
    /// Sets the name of this slash command data
    ///
    /// Slash command names must be between 1 and {@value MAX_NAME_LENGTH} characters long, and match [#NAME_PATTERN]
    ///
    /// They also must not have any uppercase characters, or specifically characters that have a lowercase equivalent
    ///
    /// @param name the name to set
    /// @return this
    /// @throws IllegalArgumentException if the name is invalid
    /// @throws NullPointerException if the name is null
    public @NotNull SlashCommandData setName(@NotNull String name) {
        this.name = checkName(name, "name");
        return this;
    }
    
    static @NotNull String checkName(@Nullable String str, @NotNull String name) {
        str = checkStringLength(str, MAX_NAME_LENGTH, name);
        
        if (!NAME_PATTERN.matcher(str).matches())
            throw new IllegalArgumentException(String.format("%s must match pattern %s", name, NAME_PATTERN));
        
        if (!str.toLowerCase().equals(str))
            throw new IllegalArgumentException(String.format("%s must not contain uppercase letters, or letters that have a lowercase variant", name));
        
        return str;
    }
    
    /// Gets the description of this slash command data
    ///
    /// @return the description
    public @NotNull String getDescription() {
        return description;
    }
    
    /// Sets the description of this slash command data
    ///
    /// Descriptions must be between 1 and {@value MAX_DESCRIPTION_LENGTH} characters long
    ///
    /// @param description the description to set
    /// @return this
    /// @throws IllegalArgumentException if the description is invalid
    /// @throws NullPointerException if the description is null
    public @NotNull SlashCommandData setDescription(@NotNull String description) {
        this.description = checkStringLength(description, MAX_DESCRIPTION_LENGTH, "description");
        return this;
    }
    
    /// Gets the name localizations of this slash command data
    ///
    /// @return the name localizations
    public @NotNull Map<Locale, @NotNull String> getNameLocalizations() {
        return name_localizations;
    }
    
    /// Sets the name localizations of this slash command data
    ///
    /// Localised names are subject to the same requirements as the command name itself
    ///
    /// @param name_localizations a map of name localizations to set
    /// @return this
    /// @throws IllegalArgumentException if any localized name is invalid
    /// @throws NullPointerException if the map or any of the values in the map is null
    /// @see #setName(String)
    public @NotNull SlashCommandData setNameLocalizations(@NotNull Map<Locale, @NotNull String> name_localizations) {
        name_localizations = Map.copyOf(name_localizations);
        name_localizations.forEach((locale, name) ->
                checkName(name, String.format("name for locale %s", locale)));
        this.name_localizations = name_localizations;
        return this;
    }
    
    /// Gets the description localizations of this slash command data
    ///
    /// @return the description localizations
    public @NotNull Map<Locale, @NotNull String> getDescriptionLocalizations() {
        return description_localizations;
    }
    
    /// Sets the description localizations of this slash command data
    ///
    /// Localised descriptions are subject to the same requirements as the command description itself
    ///
    /// @param description_localizations a map of description localizations to set
    /// @return this
    /// @throws IllegalArgumentException if any localized description is invalid
    /// @throws NullPointerException if the map or any of the values in the map is null
    /// @see #setDescription(String)
    public @NotNull SlashCommandData setDescriptionLocalizations(@NotNull Map<Locale, @NotNull String> description_localizations) {
        description_localizations = Map.copyOf(description_localizations);
        description_localizations.forEach((locale, name) ->
                checkStringLength(name, MAX_DESCRIPTION_LENGTH, String.format("description for locale %s", locale)));
        this.description_localizations = description_localizations;
        return this;
    }
    
    /// Gets the list of option data of this slash command data
    ///
    /// @return the list of option data
    public synchronized @NotNull List<@NotNull SlashCommandOptionData> getOptions() {
        return options;
    }
    
    /// Sets the list of option data for this slash command data
    ///
    /// Option list must have at most {@value #MAX_OPTION_COUNT} elements
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
    /// @throws IllegalArgumentException if the options list is invalid
    /// @throws NullPointerException if the list or any of the elements in the list is null
    public synchronized @NotNull SlashCommandData setOptions(@NotNull List<@NotNull SlashCommandOptionData> options) {
        options = List.copyOf(options);
        
        if (options.size() > MAX_OPTION_COUNT)
            throw new IllegalArgumentException(String.format("options list may only have up to %s options", MAX_OPTION_COUNT));
        
        ensureUnique(options);
        checkOptionTypes(options);
        
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
    
    static void ensureUnique(@NotNull List<@NotNull SlashCommandOptionData> options) {
        var set = new HashSet<String>();
        for (var option : options) {
            if (!set.add(option.getName()))
                throw new IllegalArgumentException(String.format("options list contains duplicate name '%s'", option.getName()));
        }
    }
    
    static void checkOptionTypes(@NotNull List<@NotNull SlashCommandOptionData> options) {
        SlashCommandOptionType first_type = null;
        for (var e : options) {
            var type = e.getType();
            
            if (first_type == null)
                first_type = type;
            
            if (isIncompatibleTypes(first_type, type))
                throw new IllegalArgumentException(
                        String.format("option can't be set to type %s because it's incompatible with sibling option's type %s",
                                first_type, type));
        }
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
    synchronized void checkOptionTypes(@NotNull SlashCommandOptionData option, @NotNull SlashCommandOptionType type) {
        for (var e : options) {
            var t = (e == option) ? type : e.getType();
            if (isIncompatibleTypes(t, type))
                throw new IllegalStateException(
                        String.format("option can't be set to type %s because it's incompatible with sibling option's type %s",
                                type, t));
        }
    }
    
    static boolean isIncompatibleTypes(@NotNull SlashCommandOptionType left, @NotNull SlashCommandOptionType right) {
        return (left != SlashCommandOptionType.SUBCOMMAND_GROUP || right != SlashCommandOptionType.SUBCOMMAND_GROUP)
                && (left != SlashCommandOptionType.SUBCOMMAND || right != SlashCommandOptionType.SUBCOMMAND)
                && (left == SlashCommandOptionType.SUBCOMMAND_GROUP || left == SlashCommandOptionType.SUBCOMMAND
                || right == SlashCommandOptionType.SUBCOMMAND_GROUP || right == SlashCommandOptionType.SUBCOMMAND);
    }
    
    /// invoked by this's options to check if it can set required flag
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
    
    /// Gets whether this slash command data is disabled by default or not
    ///
    /// Commands that default disabled can only be used by server administrators by default
    ///
    /// @return whether this slash command data is default disabled
    public boolean isDefaultDisabled() {
        return default_disabled;
    }
    
    /// Sets the default disabled state of this slash command data
    ///
    /// Commands that default disabled can only be used by server administrators by default
    ///
    /// @param default_disabled the state to set
    /// @return this
    /// @deprecated Discord docs states "Not recommended for use as field will soon be deprecated."
    @Deprecated
    public @NotNull SlashCommandData setDefaultDisabled(boolean default_disabled) {
        this.default_disabled = default_disabled;
        return this;
    }
    
    /// Gets the required [PermissionType]s of this slash command data
    ///
    /// Required permissions require users to have all of the specified permissions or Administrator in order to use
    /// this command
    ///
    /// This is only a default. Server moderators may modify the requirement in their server. Bots cannot control this
    ///
    /// @return EnumSet of required PermissionTypes
    public @NotNull Optional<EnumSet<PermissionType>> getRequiredPermissions() {
        return Optional.ofNullable(required_permissions);
    }
    
    /// Sets the required [PermissionType]s of this slash command data
    ///
    /// Required permissions require users to have all of the specified permissions or Administrator in order to use
    /// this command
    ///
    /// This is only a default. Server moderators may modify the requirement in their server. Bots cannot control this
    ///
    /// @param required_permissions EnumSet of required PermissionTypes to set
    /// @return this
    /// @throws NullPointerException if the set is null
    public @NotNull SlashCommandData setRequiredPermissions(@Nullable EnumSet<PermissionType> required_permissions) {
        if (required_permissions != null)
            this.required_permissions = EnumSet.copyOf(required_permissions);
        else
            this.required_permissions = null;
        
        return this;
    }
    
    /// Gets whether this slash command data is enabled in DMs or not
    ///
    /// If false users will be forced to use this command only in servers
    ///
    /// If this SlashCommandData ends up being registered to a server this flag will be ignored and always be false
    ///
    /// @return whether this slash command data is enabled in DMs
    public boolean isEnabledInDMs() {
        return enabled_in_DMs;
    }
    
    /// Sets whether this slash command data is enabled in DMs or not
    ///
    /// If false users will be forced to use this command only in servers
    ///
    /// If this SlashCommandData ends up being registered to a server this flag will be ignored and always be false
    ///
    /// @param enabled_in_DMs whether this slash command data should be enabled in DMs
    /// @return this
    public @NotNull SlashCommandData setEnabledInDMs(boolean enabled_in_DMs) {
        this.enabled_in_DMs = enabled_in_DMs;
        return this;
    }
    
    /// Gets whether this slash command data is NSFW or not
    ///
    /// @return whether this slash command data is NSFW
    public boolean isNSFW() {
        return nsfw;
    }
    
    /// Sets the NSFW flag of this slash command data
    ///
    /// @param nsfw the state to set
    /// @return this
    public @NotNull SlashCommandData setNSFW(boolean nsfw) {
        this.nsfw = nsfw;
        return this;
    }
}
