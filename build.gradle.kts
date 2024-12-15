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
    version = "0.0.1"

    repositories {
        mavenCentral()
    }

    publishing {
        repositories {
            mavenCentral()
        }

        publications {
            create<MavenPublication>(name) {
                from(components["java"])
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