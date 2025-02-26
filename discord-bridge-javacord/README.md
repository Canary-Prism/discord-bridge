## discord-bridge-javacord

the [Javacord](https://javacord.org/) implementation module for Discord Bridge

this implementation does not support Interaction Contexts, you must utilise the deprecated `enabledInDMs` property

since the api is heavily inspired by Javacord's a lot of things are 1 to 1 which is convenient
but also means that a lot and i mean a LOT of the identifiers clash which requires only importing one and using the
fully qualified name for the other which is annoying

this was probably the easiest implementation to make

### welp. Javacord's dead

you could say that it was easy to tell.. it hadn't had an update in a long while and it was gonna happen eventually,
but i was still kinda bummed, it is my favourite one,,

as Discord's api evolves and more features get added to discord-bridge-api discord-bridge-javacord will just 
have to be left behind, there's no real solution to this

you might consider switching to a different library but that obviously requires getting used to  
sure discord-bridge *is* a facade thing but it was never meant as a replacement to interacting with the underlying library directly
as you can probably tell by how much emphasis is put on conversion between the facade and the underlying library
as opposed to.. like.. api design

anyways JDA and Discord4J both seem like decent replacements (it won't be the same api experience obviously)  
don't use Kord if you're using Java, the Kord implementation that i had to write entirely in Java was beyond painful
(but you probably wouldn't need *me* to tell you that, nobody would use a Kotlin library purely in Java)
