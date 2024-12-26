## discord-bridge-discord4j

the [Discord4J](https://docs.discord4j.com/) implementation module for Discord Bridge

this implementation does not support Interaction Contexts, you must utilise the deprecated `enabledInDMs` property

Discord4J's reactor system is cool and all but unfortunately the api uses CompletableFutures so a lot of the interactions
using reactive patterns had to be gutted

it's a real shame too, however, a problem i have with how it uses reactive patterns is how it doesn't really communicate
what is just a getter and what's a request anymore? like if some getter returns a Mono or Flux you really can't tell
whether it's just being quirky and using them instead of Optionals or Streams or because those result in actual api requests
that justify using a data type that can handle concurrency

SlashCommandInteractionOption::isAutocompleteTarget will always be present because Discord4J doesn't communicate that difference