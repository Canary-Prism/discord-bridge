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

    implementation("org.slf4j:slf4j-api")

    implementation("org.apache.logging.log4j:log4j-to-slf4j:2.24.3")

    testImplementation(project(":discord-bridge-api"))
}