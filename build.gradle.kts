
plugins {
    `java-library`
}

allprojects {
    group = "io.github.canary-prism"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    apply(plugin = "java-library")

    tasks.javadoc {
        javadocTool = javaToolchains.javadocToolFor {
            languageVersion = JavaLanguageVersion.of(23)
        }
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