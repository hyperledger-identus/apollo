import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension
import java.util.Locale

plugins {
    alias(libs.plugins.dokka)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kover)
    alias(libs.plugins.nexus.publish)
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    signing
}

group = "org.hyperledger.identus"

allprojects {
    group = rootProject.group

    repositories {
        google()
        mavenCentral()
    }

    rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin> {
        rootProject.the<YarnRootExtension>().reportNewYarnLock = true
        rootProject.the<YarnRootExtension>().yarnLockAutoReplace = true
    }
}

subprojects {
    if (project.name == "apollo" || project.name == "bip32-ed25519") {
        apply(plugin = "com.vanniktech.maven.publish")

        mavenPublishing {
            publishToMavenCentral()
            signAllPublications()

            val effectiveDescription =
                when {
                    !project.description.isNullOrBlank() -> project.description
                    project.name == "apollo" -> "Collection of cryptographic methods used across Identus platform."
                    project.name == "bip32-ed25519" -> "Identus Bip32 HD Keys in Ed25519"
                    else -> "Identus Library"
                }

            pom {
                name.set(
                    "Identus " +
                        project
                            .name
                            .replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                            }
                )
                description.set(effectiveDescription)
                url.set("https://docs.atalaprism.io/")

                organization {
                    name.set("Hyperledger")
                    url.set("https://www.hyperledger.org/")
                }

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("hamada147")
                        name.set("Ahmed Moussa")
                        email.set("ahmed.moussa@iohk.io")
                        organization.set("IOG")
                        roles.add("developer")
                    }
                    developer {
                        id.set("amagyar-iohk")
                        name.set("Allain Magyar")
                        email.set("allain.magyar@iohk.io")
                        organization.set("IOG")
                        roles.add("qc")
                    }
                    developer {
                        id.set("antonbaliasnikov")
                        name.set("Anton Baliasnikov")
                        email.set("anton.baliasnikov@iohk.io")
                        organization.set("IOG")
                        roles.add("qc")
                    }
                    developer {
                        id.set("elribonazo")
                        name.set("Javier Ribó")
                        email.set("javier.ribo@iohk.io")
                        organization.set("IOG")
                        roles.add("developer")
                    }
                    developer {
                        id.set("goncalo-frade-iohk")
                        name.set("Gonçalo Frade")
                        email.set("goncalo.frade@iohk.io")
                        organization.set("IOG")
                        roles.add("developer")
                    }
                    developer {
                        id.set("curtis-h")
                        name.set("Curtis Harding")
                        email.set("curtis.harding@iohk.io")
                        organization.set("IOG")
                        roles.add("developer")
                    }
                    developer {
                        id.set("cristianIOHK")
                        name.set("Cristian Gonzalez")
                        email.set("cristian.castro@iohk.io")
                        organization.set("IOG")
                        roles.add("developer")
                    }
                    developer {
                        id.set("yshyn-iohk")
                        name.set("Yurii Shynbuiev")
                        email.set("yurii.shynbuiev@iohk.io")
                        organization.set("IOG")
                        roles.add("developer")
                    }
                }

                scm {
                    connection.set("scm:git:git://git@github.com/hyperledger/identus-apollo.git")
                    developerConnection.set("scm:git:ssh://git@github.com/hyperledger/identus-apollo.git")
                    url.set("https://github.com/hyperledger/identus-apollo")
                }
            }
        }
    } else {
        // Explicitly disable publishing tasks for other modules
        tasks.withType<PublishToMavenRepository>().configureEach {
            enabled = false
        }
        tasks.withType<PublishToMavenLocal>().configureEach {
            enabled = false
        }
    }

    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "org.jetbrains.kotlinx.kover")

    ktlint {
        verbose.set(true)
        outputToConsole.set(true)
        filter {
            exclude { it.file.path.contains("external") }
            exclude { it.file.path.contains("generated") }
        }
    }

    koverReport {
        filters {
            excludes {
                classes("org.hyperledger.identus.apollo.utils.bip39.wordlists.*")
            }
        }
    }

    afterEvaluate {
        if (plugins.hasPlugin("maven-publish")) {
            extensions.findByType<PublishingExtension>()?.publications?.withType<MavenPublication>()?.configureEach {
                extensions.configure<SigningExtension> {
                    useInMemoryPgpKeys(
                        findProperty("signing.keyId") as String?,
                        findProperty("signing.key") as String?,
                        findProperty("signing.password") as String?
                    )
                    sign(this@configureEach)
                }
            }
        }
    }
}

/**
 * The `javadocJar` variable is used to register a `Jar` task to generate a Javadoc JAR file.
 * The Javadoc JAR file is created with the classifier "javadoc" and it includes the HTML documentation generated
 * by the `dokkaHtml` task.
 */
val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml)
}

if (tasks.findByName(":apollo:publishAndroidDebugPublicationToSonatypeRepository") != null) {
    tasks.named(":apollo:publishAndroidDebugPublicationToSonatypeRepository").configure {
        dependsOn(":apollo:signAndroidReleasePublication")
    }
}

listOf(
    ":bip32-ed25519:androidDebugSourcesJar"
).forEach {
    if (tasks.findByName(it) != null) {
        tasks.named(it).configure {
            dependsOn(":bip32-ed25519:copyGeneratedKotlin")
        }
    }
}

if (tasks.findByName(":bip32-ed25519:publishAndroidDebugPublicationToSonatypeRepository") != null) {
    tasks.named(":bip32-ed25519:publishAndroidDebugPublicationToSonatypeRepository").configure {
        listOf(
            ":apollo:signJvmPublication",
            ":apollo:signMacosArm64Publication",
            ":apollo:signKotlinMultiplatformPublication",
            ":apollo:signJsPublication",
            ":apollo:signIosX64Publication"
        ).forEach {
            if (tasks.findByName(it) != null) {
                dependsOn(it)
            }
        }
    }
}

listOf(
    ":apollo:signMacosArm64Publication",
    ":apollo:signKotlinMultiplatformPublication",
    ":apollo:signJsPublication",
    ":apollo:signIosX64Publication",
).forEach {
    if (tasks.findByName(it) != null) {
        tasks.named(it).configure {
            enabled = false
        }
    }
}

tasks.wrapper {
    gradleVersion = "8.13"
    distributionType = Wrapper.DistributionType.ALL
}
