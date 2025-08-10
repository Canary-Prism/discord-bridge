module canaryprism.discordbridge.identity {
    requires canaryprism.discordbridge.api;
    requires static org.jetbrains.annotations;
    
    provides canaryprism.discordbridge.api.DiscordBridge
            with canaryprism.discordbridge.identity.IdentityBridge;
}