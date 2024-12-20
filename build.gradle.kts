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
    version = "2.0.1"

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
                url = "https://github.com/Canary-Prism/slavacord"
                connection = "scm:git:git://github.com/Canary-Prism/slavacord.git"
                developerConnection = "scm:git:ssh://git@github.com:Canary-Prism/slavacord.git"
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
}

subprojects {
    apply(plugin = "java-library")

    dependencies {
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
}

mavenPublishing {
    pom {
        packaging = "pom"
    }
}