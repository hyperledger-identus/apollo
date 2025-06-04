import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

val currentOs = OperatingSystem.current()

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
}

fun KotlinNativeTarget.secp256k1CInterop(target: String) {
    compilations["main"].cinterops {
        val libsecp256k1 by creating {
            includeDirs.headerFilterOnly(project.file("native/secp256k1/include/"))
            tasks[interopProcessingTaskName].dependsOn(":secp256k1-kmp:native:buildSecp256k1${target.replaceFirstChar(Char::uppercase)}")
        }
    }
}

kotlin {
    if (currentOs.isLinux) {
        linuxX64 {
            secp256k1CInterop("host")
            // https://youtrack.jetbrains.com/issue/KT-39396
            compilations["main"].kotlinOptions.freeCompilerArgs +=
                listOf(
                    "-include-binary",
                    "$rootDir/secp256k1-kmp/native/build/linux/libsecp256k1.a"
                )
        }
    }
    if (currentOs.isMacOsX) {
        iosX64 {
            secp256k1CInterop("ios")
            // https://youtrack.jetbrains.com/issue/KT-39396
            compilations["main"].kotlinOptions.freeCompilerArgs +=
                listOf(
                    "-include-binary",
                    "$rootDir/secp256k1-kmp/native/build/ios/x86_x64-iphonesimulator/libsecp256k1.a"
                )
        }
        iosArm64 {
            secp256k1CInterop("ios")
            // https://youtrack.jetbrains.com/issue/KT-39396
            compilations["main"].kotlinOptions.freeCompilerArgs +=
                listOf(
                    "-include-binary",
                    "$rootDir/secp256k1-kmp/native/build/ios/arm64-iphoneos/libsecp256k1.a"
                )
        }
        iosSimulatorArm64 {
            secp256k1CInterop("iosSimulatorArm64")
            // https://youtrack.jetbrains.com/issue/KT-39396
            compilations["main"].kotlinOptions.freeCompilerArgs +=
                listOf(
                    "-include-binary",
                    "$rootDir/secp256k1-kmp/native/build/ios/arm64-iphonesimulator/libsecp256k1.a"
                )
        }
        macosX64 {
            secp256k1CInterop("macosX64")
            // https://youtrack.jetbrains.com/issue/KT-39396
            compilations["main"].kotlinOptions.freeCompilerArgs +=
                listOf(
                    "-include-binary",
                    "$rootDir/secp256k1-kmp/native/build/ios/x86_x64-macosx/libsecp256k1.a"
                )
        }
        macosArm64 {
            secp256k1CInterop("macosArm64")
            // https://youtrack.jetbrains.com/issue/KT-39396
            compilations["main"].kotlinOptions.freeCompilerArgs +=
                listOf(
                    "-include-binary",
                    "$rootDir/secp256k1-kmp/native/build/ios/arm64-macosx/libsecp256k1.a"
                )
        }
    }
    applyDefaultHierarchyTemplate()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("com.ionspin.kotlin:bignum:0.3.9")
            }
        }
        val nativeMain by getting
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
    }

//    watchosArm64 {
//        secp256k1CInterop("watchOSArm64")
//        compilations["main"].defaultSourceSet.dependsOn(nativeMain)
//        // https://youtrack.jetbrains.com/issue/KT-39396
//        compilations["main"].kotlinOptions.freeCompilerArgs += listOf("-include-binary", "$rootDir/secp256k1-kmp/native/build/ios/arm64-watchos/libsecp256k1.a")
//    }
//
//    watchosSimulatorArm64 {
//        secp256k1CInterop("ios")
//        compilations["main"].defaultSourceSet.dependsOn(nativeMain)
//        // https://youtrack.jetbrains.com/issue/KT-39396
//        compilations["main"].kotlinOptions.freeCompilerArgs += listOf("-include-binary", "$rootDir/native/build/ios/arm64_x86_x64-watchossimulator.a")
//    }

//    tvosArm64 {
//        secp256k1CInterop("tvOSArm64")
//        compilations["main"].defaultSourceSet.dependsOn(nativeMain)
//        // https://youtrack.jetbrains.com/issue/KT-39396
//        compilations["main"].kotlinOptions.freeCompilerArgs += listOf("-include-binary", "$rootDir/secp256k1-kmp/native/build/ios/arm64-appletvos/libsecp256k1.a")
//    }
}

afterEvaluate {
    tasks.withType<PublishToMavenRepository>().configureEach {
        enabled = false
    }
}
