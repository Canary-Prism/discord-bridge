## discord-bridge-jda

the [JDA](https://jda.wiki/) implementation module for Discord Bridge

~~this implementation does not support Interaction Contexts, you must utilise the deprecated `enabledInDMs` property~~
as of at most version 5.3.0 JDA does support contexts now :D yippeeeeeeeeeee

actually discovering that it does now while playing around with JDA (bc Javacord is dead :c) is the reason i found out
about this and made this update ehehe

JDA is the library with as of writing this the most up to date support of the discord api  
but i honestly don't really like the api design

using nullable values as returns results in a less elegant way of dealing with the absence of the value than using Optional imo  
obviously you can always just wrap it by `Optional.ofNullable()` but it's always more clunky than just returning Optional straight up  
and inventing your own data type to deal with async requests and chaining and callbacks is a bit unnecessary
when CompletableFuture already exists innit?

a couple of methods in this implementation actually block despite returning Optionals as specified in discord-bridge-api,
this makes JDA probably the slowest implementation if you're using slavacord which uses a lot of the data in order to lookup
the method it should invoke
as a sorta quick fix to the blocking issue i cached the data which may result in more memory usage but ehhhh
with how commands are supposed to be used i don't think it'd be that bad