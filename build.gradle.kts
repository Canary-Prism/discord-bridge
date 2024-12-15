plugins {
    `java-library`
    `maven-publish`
    signing
}

description = "A Unified api for Discord api wrappers"

allprojects {

    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    group = "io.github.canary-prism"
    version = "0.0.2"

    repositories {
        mavenCentral()
    }

    publishing {
        repositories {
            maven {
                name = "Sonatype"
                url = uri("https://repo1.maven.org/maven2")

                credentials {
                    username = findProperty("maven.username") as String?
                    password = findProperty("maven.password") as String?
                }
            }
        }

        publications {
            create<MavenPublication>(name) {
                from(components["java"])

                pom {
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
        }
    }

    java {
        modularity.inferModulePath = true
        withSourcesJar()
        withJavadocJar()
    }

    signing {
        useGpgCmd()
        sign(publishing.publications[name])
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

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }

    dependencies {
        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        compileOnly("org.jetbrains:annotations:24.0.0")
    }

    tasks.test {
        useJUnitPlatform()
    }

}