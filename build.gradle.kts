import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

plugins {
    alias(libs.plugins.dokka)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kover)
    alias(libs.plugins.nexus.publish)
    id("maven-publish")
    id("signing")
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
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
    if (project.name == "apollo") {
        apply(plugin = "org.gradle.maven-publish")
        apply(plugin = "org.gradle.signing")

        publishing {
            publications.withType<MavenPublication> {
                groupId = rootProject.group.toString()
                artifactId = project.name
                version = project.version.toString()
                artifact(javadocJar)

                pom {
                    name.set("Identus Apollo")
                    description.set("Collection of cryptographic methods used across Identus platform.")
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

                signing {
                    val signingKey =
                        project
                            .findProperty("signing.signingSecretKey") as String?
                            ?: System.getenv("OSSRH_GPG_SECRET_KEY")
                    val signingPassword =
                        project
                            .findProperty("signing.signingSecretKeyPassword") as String?
                            ?: System.getenv("OSSRH_GPG_SECRET_KEY_PASSWORD")

                    if (!signingKey.isNullOrEmpty() && !signingPassword.isNullOrEmpty()) {
                        useInMemoryPgpKeys(signingKey, signingPassword)

                        sign(
                            publishing.publications.matching {
                                gradle.startParameter.taskNames.none { it.contains("publishToMavenLocal") }
                            }
                        )
                    } else {
                        println("Signing skipped: signing keys not configured.")
                    }
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
}

nexusPublishing {
    repositories {
        // see https://central.sonatype.org/publish/publish-portal-ossrh-staging-api/#configuration
        sonatype {
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
            username.set(System.getenv("OSSRH_USERNAME"))
            password.set(System.getenv("OSSRH_PASSWORD"))
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

tasks.wrapper {
    gradleVersion = "8.13"
    distributionType = Wrapper.DistributionType.ALL
}
