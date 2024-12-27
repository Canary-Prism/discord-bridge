plugins {
    `java-library`
}

description = "The Javacord implementation of discord-bridge"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":discord-bridge-api"))
    compileOnly("org.javacord:javacord:3.8.0")

    testImplementation(project(":discord-bridge-api"))
}