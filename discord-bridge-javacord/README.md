## discord-bridge-javacord

the [Javacord](https://javacord.org/) implementation module for Discord Bridge

this implementation does not support Interaction Contexts, you must utilise the deprecated `enabledInDMs` property

since the api is heavily inspired by Javacord's a lot of things are 1 to 1 which is convenient
but also means that a lot and i mean a LOT of the identifiers clash which requires only importing one and using the
fully qualified name for the other which is annoying

this was probably the easiest implementation to make