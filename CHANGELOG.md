# Changelog

## v3.2.1
- fixed bug where depending on the root project didn't bring in dependencies for `discord-bridge-discord4j` and `discord-bridge-kord`

## v3.2.0
- fixed bug where class loading errors in service providers caused errors that would crash the service loader instead of just trying another provider
- added default convenience methods for removing listeners

## v3.1.2
- added `Automatic-Module-Name` to META-INF/MANIFEST.MF of `discord-bridge-kord`

## v3.1.1
- removed `discord-bridge-discord4j`, `discord-bridge-jda`, and `discord-bridge-kord`'s dependency on `java.desktop` module

## v3.1.0
- added Kord implementation `discord-bridge-kord`

## v3.0.1
- fixed Channel implementations and ChannelDirector just not working at all

## v3.0.0
- changed Option Choices to not return optionals since they must always have values
- added `ContextType`s
- deprecated methods related to commands and enabledInDMs stuff
- added version information to DiscordBridge implementation `toString`
- changed how event listeners work, you must now specify the type of event listener to remove in `DiscordApi::removeListener`
- added Discord4J implementation `discord-bridge-discord4j`
- renamed `canaryprism.discordbridge.entities` to `canaryprism.discordbridge.entity`
- renamed `TextChannel` and related to `MessageChannel` since a TextChannel or ServerTextChannel means specifically the SERVER_TEXT channel type ONLY rather than any channel you can send messages in

## v2.0.5
- fixed `discord-bridge-javacord` and `discord-bridge-jda` having a bug with their `DiscordBridge::getImplementationType` methods that makes them never load
- added a feature where `DiscordBridge.load()` will return the passed parameter untouched if the parameter is already a `DiscordApi` object

## v2.0.4
- fixed `SlashCommandOptionData` bounds validation just being wrong

## v2.0.3
- allow passing any arbitrary set even with bounded wildcards in the sets for `SlashCommandOptionData::setChannelTypeBounds`

## v2.0.2
- added a missing `@Unmodifiable` annotation
- fixed a broken javadoc link
- allow even bounded wildcards in the sets for `SlashCommandData::setRequiredPermissions`

## v2.0.1
- allow passing any arbitrary set to `SlashCommandData::setRequiredPermissions`

## v2.0.0
- added `DiscordLocale` enum and moved around some stuff
- fixed root project defaulting to Java version 23

## v1.3.0
- added a couple new channel type interfaces

## v1.2.0
- fixed discord-bridge-jda's `SlashCommandInteractionOption::getValue` not returning compliant values
- added `getImplementationType()` to `DiscordBridge`

## v1.1.0
- added full autocomplete listener event and response support

## v1.0.2
- fixed `discord-bridge` packaging not being `pom` meaning it couldn't be used as an aggregator

## v1.0.1
- fixed `discord-bridge-jda` not having META-INF services defined for non-modular projects

## v1.0.0
- meow meow meow