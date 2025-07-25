plugins {
    `java-library`
}

description = "The Javacord implementation of discord-bridge"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":discord-bridge-api"))
    compileOnly("net.dv8tion:JDA:5.3.0")
    implementation("io.github.canary-prism:commons-event:1.0.0")
    implementation("org.slf4j:slf4j-api")
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.2")
    compileOnly("com.google.code.findbugs:annotations:3.0.1u2")
}