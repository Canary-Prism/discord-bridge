plugins {
    `java-library`
}

description = "The Javacord implementation of discord-bridge"

dependencies {
    compileOnly(project(":discord-bridge-api"))
    compileOnly("org.javacord:javacord:3.8.0")
}