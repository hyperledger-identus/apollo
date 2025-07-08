plugins {
    alias(libs.plugins.kotlin.multiplatform)
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
