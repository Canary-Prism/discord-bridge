## discord-bridge-identity

the identity (nop) implementation of Discord Bridge

this implementation does not do anything, it can't load any object as an api 
and all conversion methods just return the passed in value without modification

this implementation is meant for libraries that want to work with values and their internal representation
in all discord-bridge implementations so that the base values defined in `discord-bridge-api` can be interacted with
the same way values in real implementations can be

if you wish to make use of this implementation you must depend on `discord-bridge-identity` manually, 
depending on `discord-bridge` will *not* add this as a transitive dependency