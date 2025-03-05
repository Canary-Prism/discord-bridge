import com.vanniktech.maven.publish.SonatypeHost

plugins {
    `java-library`
    id("com.vanniktech.maven.publish") version "0.30.0"
}

description = "A Unified api for Discord api wrappers"

allprojects {

    apply(plugin = "java-library")
    plugins.apply("com.vanniktech.maven.publish")

    group = "io.github.canary-prism"
    version = "6.0.2"

    mavenPublishing {
        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

        signAllPublications()

        pom {

            name = project.name
            description = project.description

            url = "https://github.com/Canary-Prism/discord-bridge"

            licenses {
                license {
                    name = "The Apache License, Version 2.0"
                    url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                }
            }

            developers {
                developer {
                    id = "Canary-Prism"
                    name = "Canary Prism"
                    email = "canaryprsn@gmail.com"
                }
            }

            scm {
                url = "https://github.com/Canary-Prism/discord-bridge"
                connection = "scm:git:git://github.com/Canary-Prism/discord-bridge.git"
                developerConnection = "scm:git:ssh://git@github.com:Canary-Prism/discord-bridge.git"
            }
        }
    }

    repositories {
        mavenCentral()
    }

    java {
        modularity.inferModulePath = true
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }


    tasks.javadoc {
        javadocTool = javaToolchains.javadocToolFor {
            languageVersion = JavaLanguageVersion.of(23)
        }

        (options as StandardJavadocDocletOptions).tags(
            "apiNote:a:API Note:",
            "implSpec:a:Implementation Requirements:",
            "implNote:a:Implementation Note:"
        )
    }

    dependencies {
        implementation(platform("org.slf4j:slf4j-bom:2.0.16"))
        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        compileOnly("org.jetbrains:annotations:24.0.0")
    }

    tasks.test {
        useJUnitPlatform()
    }

}

dependencies {
    api(project(":discord-bridge-api"))
    runtimeOnly(project(":discord-bridge-javacord"))
    runtimeOnly(project(":discord-bridge-jda"))
    runtimeOnly(project(":discord-bridge-discord4j"))
    runtimeOnly(project(":discord-bridge-kord"))
}

mavenPublishing {
    pom {
        packaging = "pom"
    }
}