# Discord Bridge

Discord Bridge is a Discord api wrapper wrapper  
it's a unified api for other Discord api wrappers for Java 

i mostly made this so i could make [Slavacord](https://github.com/Canary-Prism/slavacord) api implementation agnostic  
but i figured other people might find this project interesting

currently this repo provides implementations for [Javacord](https://javacord.org/) and [JDA](https://jda.wiki/), 
i plan on adding support for [Discord4J](https://docs.discord4j.com/) and [Kord](https://github.com/kordlib/kord) in the future

## adding as dependency and whatever

### Gradle:
```declarative
repositories {
    mavenCentral()
}

dependencies {
    // this depends on discord-bridge-api and all the provided implementations in this repository by default
    implementation("io.github.canary-prism:discord-bridge:1.2.0") {
        // you can optionally exclude unneeded implementations
        exclude(module="discord-bridge-javacord") // change this to the module you want to disable if you want
    }
}
```

### Maven:
```xml
<dependency>
  <groupId>io.github.canary-prism</groupId>
  <artifactId>discord-bridge</artifactId>
  <version>1.2.0</version>
</dependency>
```

## how use

### DiscordBridge

the central interface representing any given discord-bridge implementation is the `DiscordBridge` service

an instance of DiscordBridge represents an implementation and all of the classes can be obtained starting from that point

DiscordBridge also defines static methods to load DiscordApis

### DiscordApi

after you add a dependency for the discord bridge implementation you want you must create some discord api object or equivalent in the specific implementation  
for example, if you're using `discord-bridge-javacord`, create a `DiscordApi` object, if you're using `discord-bridge-jda`, create a `JDA` object  
in any case, obtain the object in the specific implementation that represents your discord application (bot) session, and can be used to perform global application operations

once you have one, pass it into `DiscordBridge.load(Object)`
```java
var internal_api = getImplementationSpecificApiObject();
var api = DiscordBridge.load(internal_api);
```

this loads the object and turns it into an instance of discord-bridge-api's `DiscordApi` object, unlocking most of the rest of the api

the style of this api is largely inspired by Javacord, with heavy use of CompletableFuture and Optionals

after you've obtained the object you can use it to perform basic operations like getting a server by its ID
```java
var server = api.getServerById(1234567890L).orElseThrow();
```

you can also upload slash commands (though only bulk overwriting is supported at this point)
```java
server.bulkUpdateServerCommands(Set.of(
        new SlashCommandData("command_name", "description")
)).join();
```

the api itself is pretty bare bones unfortunately as my main goal was still to expose the minimum amount of the api required to get Slavacord to work

### internal implementation

#### DiscordBridgeApi

all of the api interfaces have a method `getImplementation()` which can be used to get the internal object backing any given api object  
this can be useful if you know the exact implementation backing the api object and need to perform a specific operation that discord-bridge-api doesn't support

they also define a `getBridge()` method which can be used to get the specific DiscordBridge object (and therefore implementation) that this particular object belongs to

#### PartialSupport

some of the enums in the api implement `PartialSupport`, which means that implementations are free to only support part of, or indeed none of the values in the enum  
users should make sure to check that any given enum value of a PartialSupport enum is actually supported by any given implementation using either `PartialSupport::isSupported` or `DiscordBridge::getSupportedValues`