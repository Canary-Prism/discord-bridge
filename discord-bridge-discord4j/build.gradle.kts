plugins {
    `java-library`
}

description = "The Discord4J implementation of discord-bridge"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":discord-bridge-api"))
    compileOnly("com.discord4j:discord4j-core:3.2.7")
    implementation("io.github.canary-prism:commons-event:1.0.0")

    testImplementation(project(":discord-bridge-api"))
    testImplementation("com.discord4j:discord4j-core:3.2.7")
}
