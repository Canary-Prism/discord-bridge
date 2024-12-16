plugins {
    `java-library`
}

description = "The Javacord implementation of discord-bridge"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":discord-bridge-api"))
    compileOnly("net.dv8tion:JDA:5.0.1")
    compileOnly("com.google.code.findbugs:annotations:3.0.1u2")
}