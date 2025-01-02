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
    implementation("com.discord4j:discord-json:1.6.22")
    implementation("io.github.canary-prism:commons-event:1.0.0")
    implementation("org.slf4j:slf4j-api")

    testImplementation(project(":discord-bridge-api"))
    testImplementation("com.discord4j:discord4j-core:3.2.7")
}
