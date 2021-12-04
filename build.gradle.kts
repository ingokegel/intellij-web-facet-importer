import org.gradle.api.tasks.wrapper.Wrapper.DistributionType

plugins {
    kotlin("jvm") version "1.6.0"
    id("org.jetbrains.intellij") version "1.3.0"
}

group = "com.ejt"
version = "0.1"

repositories {
    mavenCentral()
}

intellij {
    version.set("IU-2021.3")
    plugins.set(listOf("com.intellij.javaee.web", "Spring", "gradle"))
}

tasks {
    wrapper {
        gradleVersion = "7.3.1"
        distributionType = DistributionType.ALL
    }

    runPluginVerifier {
        ideVersions.set(listOf("IU-2021.3"))
    }
}