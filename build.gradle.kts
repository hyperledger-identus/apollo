plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.ktlint)
}

group = "dev.allain"

subprojects {
    group = rootProject.group
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    ktlint {
        verbose.set(true)
        outputToConsole.set(true)
        filter {
            exclude { it.file.path.contains("external") }
            exclude { it.file.path.contains("generated") }
        }
    }
}

tasks.wrapper {
    gradleVersion = "8.13"
    distributionType = Wrapper.DistributionType.ALL
}
