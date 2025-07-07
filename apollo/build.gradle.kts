import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.io.ByteArrayOutputStream

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.dokka)
    alias(libs.plugins.npm.publish)
    alias(libs.plugins.swiftpackage)
}

val currentModuleName = "Apollo"
val appleBinaryName = "ApolloLibrary"
val minimumIosVersion = "15.0"
val minimumMacOSVersion = "13.0"
val generatedResourcesDir =
    project(":bip32-ed25519")
        .layout
        .buildDirectory
        .asFile
        .get()
        .resolve("generatedResources")
val generatedJvmLibsDirs =
    listOf(
        generatedResourcesDir.resolve("jvmMain/libs/aarch64-apple-darwin"),
        generatedResourcesDir.resolve("jvmMain/libs/linux-aarch64"),
        generatedResourcesDir.resolve("jvmMain/libs/x86_64-apple-darwin"),
        generatedResourcesDir.resolve("jvmMain/libs/linux-x86-64")
    )

kotlin {
    jvm {
        withSourcesJar()
        compilations.all {
            compilerOptions.configure {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }

        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
            systemProperty(
                "jna.library.path",
                generatedJvmLibsDirs.joinToString(File.pathSeparator) { it.absolutePath }
            )
            jvmArgs("-Djava.library.path=${generatedJvmLibsDirs.joinToString(File.pathSeparator) { it.absolutePath }}")
        }
    }

    androidTarget {
        publishLibraryVariants("release", "debug")
    }

    iosArm64 {
        swiftCinterop("IOHKSecureRandomGeneration", name)
        swiftCinterop("IOHKCryptoKit", name)

        binaries.framework {
            baseName = appleBinaryName
        }
    }
    iosX64 {
        swiftCinterop("IOHKSecureRandomGeneration", name)
        swiftCinterop("IOHKCryptoKit", name)

        binaries.framework {
            baseName = appleBinaryName
        }
    }
    iosSimulatorArm64 {
        swiftCinterop("IOHKSecureRandomGeneration", name)
        swiftCinterop("IOHKCryptoKit", name)

        binaries.framework {
            baseName = appleBinaryName
        }
    }
    macosArm64 {
        swiftCinterop("IOHKSecureRandomGeneration", name)
        swiftCinterop("IOHKCryptoKit", name)

        binaries.framework {
            baseName = appleBinaryName
        }
    }

    js(IR) {
        outputModuleName = currentModuleName
        binaries.library()
        useCommonJs()
        generateTypeScriptDefinitions()
        this.compilations["main"].packageJson {
            this.version = rootProject.version.toString()
        }
        this.compilations["test"].packageJson {
            this.version = rootProject.version.toString()
        }
        browser {
            webpackTask {
                output.library = currentModuleName
                output.libraryTarget = org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackOutput.Target.VAR
            }
            testTask {
                useKarma { useChromeHeadless() }
            }
        }
        nodejs {
            testTask {
                useKarma { useChromeHeadless() }
            }
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":bip32-ed25519"))
                implementation(libs.serialization.json)
                implementation(libs.bignum)
                implementation(libs.okio)
                implementation(libs.atomicfu)
                implementation(libs.macs.hmac.sha2)
                implementation(libs.hash.hmac.sha2)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                api(libs.secp256k1.kmp)
                implementation(libs.secp256k1.kmp.jvm)
                implementation(libs.secp256k1.kmp.android)
                implementation(libs.guava)
                implementation(libs.bouncycastle)
                implementation(libs.bitcoinjcore)
                implementation(libs.jna.android)
            }
        }

        val jvmMain by getting {
            dependsOn(commonMain)
            resources
                .srcDir(
                    project(":bip32-ed25519")
                        .layout
                        .buildDirectory
                        .dir("generatedResources/jvmMain/libs")
                )
            dependencies {
                api(libs.secp256k1.kmp)
                implementation(libs.secp256k1.kmp.jvm)
                implementation(libs.guava)
                implementation(libs.bouncycastle)
                implementation(libs.bitcoinjcore)
                implementation(libs.jna)
            }
        }

        val jvmTest by getting {
            dependsOn(commonTest)
            dependencies {
                implementation(libs.junit)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(npm("elliptic", "6.6.1"))
                implementation(npm("@types/elliptic", "6.4.18"))
                implementation(npm("@noble/curves", "1.2.0"))
                implementation(npm("@stablelib/x25519", "1.0.3"))
                implementation(npm("hash.js", "1.1.7"))
                implementation(npm("@noble/hashes", "1.3.1"))
                implementation(npm("stream-browserify", "3.0.0"))
                implementation(npm("buffer", "6.0.3"))
                implementation(libs.kotlin.web)
                implementation(libs.kotlin.node)
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(npm("url", "0.11.4"))
            }
        }

        val nativeMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(project(":bip32-ed25519"))
                implementation(project(":secp256k1-kmp"))
            }
        }

        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
            }
        }
    }

    multiplatformSwiftPackage {
        packageName("Apollo")
        swiftToolsVersion("5.9")
        targetPlatforms {
            iOS { v(minimumIosVersion) }
            macOS { v(minimumMacOSVersion) }
        }
        outputDirectory(File(rootDir, "apollo/build/packages/ApolloSwift"))
    }
}

android {
    namespace = "org.hyperledger.identus.apollo"
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = 21
    }

    kotlin {
        jvmToolchain(17)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    publishing {
        multipleVariants {
            withSourcesJar()
            withJavadocJar()
            allVariants()
        }
    }
}

npmPublish {
    organization.set("hyperledger")
    version.set(rootProject.version.toString())
    packages {
        named("js") {
            scope.set("hyperledger")
            packageName.set("identus-apollo")
            readme.set(rootDir.resolve("README.md"))
        }
    }
}

/**
 * Adds a Swift interop configuration for a library.
 *
 * @param library The name of the library.
 * @param platform The platform for which the interop is being configured.
 */
fun KotlinNativeTarget.swiftCinterop(library: String, platform: String) {
    compilations.getByName("main") {
        cinterops.create(library) {
            extraOpts = listOf("-compiler-option", "-DNS_FORMAT_ARGUMENT(A)=")
            when (platform) {
                "iosX64", "iosSimulatorArm64" -> {
                    includeDirs.headerFilterOnly("$rootDir/iOSLibs/$library/build/Release-iphonesimulator/include/")
                    tasks[interopProcessingTaskName].dependsOn(":iOSLibs:build${library.replaceFirstChar(Char::uppercase)}Iphonesimulator")
                }

                "iosArm64" -> {
                    includeDirs.headerFilterOnly("$rootDir/iOSLibs/$library/build/Release-iphoneos/include/")
                    tasks[interopProcessingTaskName].dependsOn(":iOSLibs:build${library.replaceFirstChar(Char::uppercase)}Iphoneos")
                }

                "macosX64", "macosArm64" -> {
                    includeDirs.headerFilterOnly("$rootDir/iOSLibs/$library/build/Release/include/")
                    tasks[interopProcessingTaskName].dependsOn(":iOSLibs:build${library.replaceFirstChar(Char::uppercase)}Macosx")
                }
            }
        }
    }
}

tasks.withType<Test>().configureEach {
    dependsOn(":bip32-ed25519:prepareRustLibs")
    doFirst {
        val paths = generatedJvmLibsDirs.joinToString(separator = File.pathSeparator) { it.absolutePath }
        systemProperty("jna.library.path", paths)
        jvmArgs("-Djava.library.path=$paths")
    }
}

// === Group: Resource and Test Task Dependencies ===
val tasksRequiringRustLibs =
    listOf(
        "jvmProcessResources",
        "jsBrowserTest",
        "jsNodeTest"
    )

tasksRequiringRustLibs.forEach {
    tasks.named(it).configure {
        dependsOn(":bip32-ed25519:prepareRustLibs")
    }
}

val tasksPublishingDisabled =
    listOf(
        "publishIosX64PublicationToSonatypeRepository",
        "publishIosArm64PublicationToSonatypeRepository",
        "publishIosSimulatorArm64PublicationToSonatypeRepository",
        "publishMacosArm64PublicationToSonatypeRepository",
        "publishJsPublicationToSonatypeRepository",
        "publishIosX64PublicationToMavenLocal",
        "publishIosArm64PublicationToMavenLocal",
        "publishIosSimulatorArm64PublicationToMavenLocal",
        "publishMacosArm64PublicationToMavenLocal",
        "publishJsPublicationToMavenLocal"
    )
tasksPublishingDisabled.forEach {
    if (tasks.findByName(it) != null) {
        tasks.named(it).configure {
            this.enabled = false
        }
    }
}

val swiftPackageUpdateMinOSVersion =
    tasks.register("updateMinOSVersion") {
        group = "multiplatform-swift-package"
        description = "Updates the minimum OS version of the plists in the xcframework, known issue of the KMP SwiftPackage plugin"
        dependsOn("createSwiftPackage")

        val xcframeworkDir = layout.projectDirectory.file("build/packages/ApolloSwift/Apollo.xcframework").asFile

        doLast {
            val frameworkPaths =
                mapOf(
                    "ios-arm64/ApolloLibrary.framework" to "ios-arm64/ApolloLibrary.framework/ApolloLibrary",
                    "ios-arm64_x86_64-simulator/ApolloLibrary.framework" to "ios-arm64_x86_64-simulator/ApolloLibrary.framework/ApolloLibrary"
                )

            frameworkPaths.forEach { (plistFolder, binaryRelativePath) ->
                val binaryFile = xcframeworkDir.resolve(binaryRelativePath)
                val plistFile = xcframeworkDir.resolve("$plistFolder/Info.plist")

                if (binaryFile.exists() && plistFile.exists()) {
                    val currentMinOS =
                        ByteArrayOutputStream().use { outputStream ->
                            exec {
                                commandLine("otool", "-l", binaryFile.absolutePath)
                                standardOutput = outputStream
                            }
                            outputStream
                                .toString()
                                .lines()
                                .firstOrNull { it.contains("minos") }
                                ?.trim()
                                ?.split(" ")
                                ?.lastOrNull()
                                ?: throw GradleException("Could not determine min OS version from binary")
                        }

                    exec {
                        commandLine(
                            "/usr/libexec/PlistBuddy",
                            "-c",
                            "Set :MinimumOSVersion $currentMinOS",
                            plistFile.absolutePath
                        )
                    }

                    println("Updated $plistFile with MinimumOSVersion = $currentMinOS")
                } else {
                    println("Required files not found: binary=$binaryFile, plist=$plistFile")
                }
            }
        }
    }

val logLinuxBinaries by tasks.registering {
    group = "verification"
    description = "Logs the files present in the Linux binaries folder (useful for CI debugging)."

    val targetDir = project.layout.buildDirectory.dir("generatedResources/jvmMain/libs/aarch64-unknown-linux-gnu")

    doLast {
        val dir = targetDir.get().asFile
        println("Logging contents of: ${dir.absolutePath}")

        if (dir.exists()) {
            val files = dir.walkTopDown().toList()
            if (files.isEmpty()) {
                println("No files found in ${dir.absolutePath}")
            } else {
                files.forEach {
                    println(" - ${it.relativeTo(dir)}")
                }
            }
        } else {
            println("Directory ${dir.absolutePath} does not exist.")
        }
    }
}

afterEvaluate {
    if (tasks.findByName("createSwiftPackage") != null) {
        tasks.named("createSwiftPackage").configure {
            finalizedBy(swiftPackageUpdateMinOSVersion)
        }
    }

    if (tasks.findByName("jvmTest") != null) {
        tasks.named("jvmTest") {
            dependsOn(logLinuxBinaries)
        }
    }
}

// Ensure copy tasks always include duplicates
tasks.withType<Copy>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

// Configure Dokka tasks uniformly
tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    moduleName.set(currentModuleName)
    moduleVersion.set(rootProject.version.toString())
}
