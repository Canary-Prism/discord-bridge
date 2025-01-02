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
    implementation("io.github.canary-prism:commons-event:1.0.0")
    implementation("org.slf4j:slf4j-api")
    compileOnly("com.google.code.findbugs:annotations:3.0.1u2")
}