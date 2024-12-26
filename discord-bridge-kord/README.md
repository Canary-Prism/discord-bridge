## discord-bridge-kord

the [Kord](https://github.com/kordlib/kord) implementation of Discord Bridge

this implementation does not support Interaction Contexts, you must utilise the deprecated `enabledInDMs` property

since Kord is a Kotlin thing and uses a burdensome amount of coroutines the Java interop is,,,, painful  
manual continuations were created with EmptyCoroutineContexts which then manually completed CompletableFutures  
i have no idea what this means for any Kotlin users crazy enough to use Discord Bridge, how the coroutine system will behave,
but when i tested it it still seemed to be async enough so that's fine i suppose

a lot of the functions are also named really weird things that aren't what the discord api calls them and imo are even worse
and more confusing than what discord api calls them  
a lot of the stuff is also split between Behavior types and the actual real type where the behaviour only does most of 
what i need it to be able to do so that's fun

additionally they felt the need to invent their own Optional type so that's... great.