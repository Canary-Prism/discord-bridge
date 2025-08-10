plugins {
    `java-library`
}

description = "The identity (nop) implementation of discord-bridge"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":discord-bridge-api"))
    
    testImplementation(project(":discord-bridge-api"))
}