plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.dokka)
}

val secp256k1Dir = rootDir.resolve("secp256k1-kmp")

kotlin {
    iosX64 {
        compilations["main"].compilerOptions.configure {
            freeCompilerArgs.addAll(
                "-include-binary",
                secp256k1Dir
                    .resolve("native")
                    .resolve("build")
                    .resolve("ios")
                    .resolve("x86_x64-iphonesimulator")
                    .resolve("libsecp256k1.a")
                    .absolutePath
            )
        }
    }
    iosArm64 {
        compilations["main"].compilerOptions.configure {
            freeCompilerArgs.addAll(
                "-include-binary",
                secp256k1Dir
                    .resolve("native")
                    .resolve("build")
                    .resolve("ios")
                    .resolve("arm64-iphoneos")
                    .resolve("libsecp256k1.a")
                    .absolutePath
            )
        }
    }
    iosSimulatorArm64 {
        compilations["main"].compilerOptions.configure {
            freeCompilerArgs.addAll(
                "-include-binary",
                secp256k1Dir
                    .resolve("native")
                    .resolve("build")
                    .resolve("ios")
                    .resolve("arm64-iphonesimulator")
                    .resolve("libsecp256k1.a")
                    .absolutePath
            )
        }
    }
    macosArm64 {
        compilations["main"].compilerOptions.configure {
            freeCompilerArgs.addAll(
                "-include-binary",
                secp256k1Dir
                    .resolve("native")
                    .resolve("build")
                    .resolve("ios")
                    .resolve("arm64-x86_x64-macosx")
                    .resolve("libsecp256k1.a")
                    .absolutePath
            )
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.bignum)
            }
        }

        val nativeMain by creating {
            dependsOn(commonMain)
        }

        val iosArm64Main by getting { dependsOn(nativeMain) }
        val iosX64Main by getting { dependsOn(nativeMain) }
        val iosSimulatorArm64Main by getting { dependsOn(nativeMain) }
        val macosArm64Main by getting { dependsOn(nativeMain) }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
    }

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().configureEach {
        val currentTargetName = this.name
        compilations["main"].apply {
            cinterops.create("libsecp256k1") {
                includeDirs.headerFilterOnly(project.file("native/secp256k1/include/"))
                tasks[interopProcessingTaskName].dependsOn(
                    when (currentTargetName) {
                        "iosX64", "iosArm64" -> ":secp256k1-kmp:native:buildSecp256k1Ios"
                        "iosSimulatorArm64" -> ":secp256k1-kmp:native:buildSecp256k1IosSimulatorArm64"
                        "macosX64" -> ":secp256k1-kmp:native:buildSecp256k1MacosX64"
                        "macosArm64" -> ":secp256k1-kmp:native:buildSecp256k1MacosArm64"
                        else -> ":secp256k1-kmp:native:buildSecp256k1Host"
                    }
                )
            }

            val binaryPath =
                when (currentTargetName) {
                    "iosArm64" -> "ios/arm64-iphoneos/libsecp256k1.a"
                    "iosX64" -> "ios/x86_x64-iphonesimulator/libsecp256k1.a"
                    "iosSimulatorArm64" -> "ios/arm64-iphonesimulator/libsecp256k1.a"
                    "macosX64" -> "ios/x86_64-macosx/libsecp256k1.a"
                    "macosArm64" -> "ios/arm64-macosx/libsecp256k1.a"
                    else -> "host/libsecp256k1.a"
                }

            compilerOptions.configure {
                freeCompilerArgs.addAll(
                    "-include-binary",
                    secp256k1Dir.resolve("native/build/$binaryPath").absolutePath
                )
            }
        }
    }
}

tasks.withType<PublishToMavenRepository>().configureEach {
    enabled = false
}

tasks.withType<PublishToMavenLocal>().configureEach {
    enabled = false
}
