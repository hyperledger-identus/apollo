plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.maven.publish)
}

kotlin {
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosArm64()
    macosX64()
    applyDefaultHierarchyTemplate()
    sourceSets {
        commonMain {
            dependencies {
                api(libs.bignum)
            }
        }
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
    }

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().configureEach {
        val targetName = this.name

        compilations["main"].apply {
            cinterops.create("libsecp256k1") {
                // FIX 1: Use the safe 'layout' API instead of 'project.file()'
                val includeDir = layout.projectDirectory.dir("native/secp256k1/include/")
                includeDirs.headerFilterOnly(includeDir)

                val dependencyTaskName = when (targetName) {
                    "iosX64", "iosArm64", "iosSimulatorArm64" -> ":secp256k1-kmp:native:buildSecp256k1Ios"
                    "macosX64", "macosArm64" -> ":secp256k1-kmp:native:buildSecp256k1Macos"
                    else -> ":secp256k1-kmp:native:buildSecp256k1Host"
                }
                tasks[interopProcessingTaskName].dependsOn(dependencyTaskName)
            }

            val binaryPathString = when (targetName) {
                "iosArm64" -> "ios/arm64-iphoneos/libsecp256k1.a"
                "iosX64" -> "ios/x86_x64-iphonesimulator/libsecp256k1.a"
                "iosSimulatorArm64" -> "ios/arm64-iphonesimulator/libsecp256k1.a"
                "macosX64" -> "ios/x86_x64-macosx/libsecp256k1.a"
                "macosArm64" -> "ios/arm64-x86_x64-macosx/libsecp256k1.a"
                else -> "host/libsecp256k1.a"
            }

            val nativeBuildDir = project(":secp256k1-kmp:native").layout.buildDirectory
            val binaryProvider = nativeBuildDir.file(binaryPathString)

            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.addAll(
                        listOf("-include-binary", binaryProvider.get().asFile.absolutePath)
                    )
                }
            }
        }
    }
}

mavenPublishing {
    val shouldAutoRelease = project.findProperty("autoRelease")?.toString()?.toBoolean() ?: false
    publishToMavenCentral(automaticRelease = shouldAutoRelease)
    val hasSigningCredentials = project.hasProperty("signing.keyId") ||
        project.hasProperty("signingInMemoryKey") ||
        System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKey") != null ||
        System.getenv("GPG_KEY_ID") != null
    if (hasSigningCredentials) {
        signAllPublications()
    }
    coordinates(group.toString(), "secp256k1-kmp", rootProject.version.toString())
    pom {
        name.set("Identus secp256k1-kmp")
        description.set("Native secp256k1 Kotlin Multiplatform bindings used by Identus Apollo.")
        url.set("https://hyperledger-identus.github.io/docs/")
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
                id.set("hyperledger-identus")
                name.set("Hyperledger Identus")
                organization.set("Hyperledger")
                roles.add("developer")
            }
        }
        scm {
            connection.set("scm:git:git://github.com/hyperledger-identus/apollo.git")
            developerConnection.set("scm:git:ssh://git@github.com/hyperledger-identus/apollo.git")
            url.set("https://github.com/hyperledger-identus/apollo")
        }
    }
}
