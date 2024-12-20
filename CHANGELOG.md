# Changelog

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