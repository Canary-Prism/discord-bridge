plugins {
    `java-library`
}

description = "The Javacord implementation of discord-bridge"

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    compileOnly(project(":discord-bridge-api"))
    compileOnly("com.github.discord-jar:b-1.2")

    testImplementation(project(":discord-bridge-api"))
}