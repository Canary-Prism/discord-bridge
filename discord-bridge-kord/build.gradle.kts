plugins {
    `java-library`
}

description = "The Kord implementation of discord-bridge"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":discord-bridge-api"))
    compileOnly("dev.kord:kord-core:0.15.0")
    implementation("io.github.canary-prism:commons-event:1.0.0")
    implementation("org.slf4j:slf4j-api:2.0.16")
}