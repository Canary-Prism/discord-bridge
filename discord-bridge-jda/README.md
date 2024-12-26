## discord-bridge-jda

the [JDA](https://jda.wiki/) implementation module for Discord Bridge

this implementation does not support Interaction Contexts, you must utilise the deprecated `enabledInDMs` property

JDA is the library with as of writing this the most up to date support of the discord api  
but i honestly don't really like the api design

using nullable values as returns results in a less elegant way of dealing with the absence of the value than using Optional imo  
obviously you can always just wrap it by `Optional.ofNullable()` but it's always more clunky than just returning Optional straight up  
and inventing your own data type to deal with async requests and chaining and callbacks is a bit unnecessary
when CompletableFuture already exists innit?

