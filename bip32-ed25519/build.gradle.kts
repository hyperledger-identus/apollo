import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackOutput
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
}

val appleBinaryName = "ApolloLibrary"
val rustModuleDir = layout.projectDirectory.dir("rust-ed25519-bip32")
val wrapperDir = rustModuleDir.dir("wrapper")
val wasmDir = rustModuleDir.dir("wasm")

val wrapperOutputDir = wrapperDir.dir("build/generated")
val wasmOutputDir = wasmDir.dir("build")
val currentModuleName: String = "Bip32Ed25519"

val generatedResourcesDir =
    project.layout.buildDirectory.asFile
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

        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
            systemProperty(
                "jna.library.path",
                generatedJvmLibsDirs.joinToString(File.pathSeparator) { it.absolutePath }
            )
            jvmArgs("-Djna.library.path=${generatedJvmLibsDirs.joinToString(File.pathSeparator) { it.absolutePath }}")
            jvmArgs("-Djava.library.path=${generatedJvmLibsDirs.joinToString(File.pathSeparator) { it.absolutePath }}")
        }
    }
    androidTarget {
        publishLibraryVariants()
    }
    iosArm64 {
        binaries.framework {
            baseName = appleBinaryName
        }
    }
    iosX64 {
        binaries.framework {
            baseName = appleBinaryName
        }
    }
    iosSimulatorArm64 {
        binaries.framework {
            baseName = appleBinaryName
        }
    }
    macosArm64 {
        binaries.framework {
            baseName = appleBinaryName
        }
    }
    js(IR) {
        this.binaries.library()
        this.useCommonJs()
        generateTypeScriptDefinitions()
        browser {
            webpackTask {
                output.library = currentModuleName
                output.libraryTarget = KotlinWebpackOutput.Target.VAR
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        nodejs {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.js.ExperimentalJsExport")
    }

    sourceSets {
        // Keep CommonMain without Uniffi-generated code
        val commonMain by getting {
            dependencies {
                implementation(libs.atomicfu)
                implementation(libs.serialization.json)
                implementation(libs.okio)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        // New sourceSet for Uniffi-enabled targets
        val uniffiMain by creating {
            dependsOn(commonMain)
            kotlin.srcDir(layout.buildDirectory.dir("generated/commonMain/kotlin"))
        }

        val uniffiTest by creating {
            dependsOn(commonTest)
        }

        // JVM, Android, Native all use Uniffi
        val jvmMain by getting {
            dependsOn(uniffiMain)
            kotlin.srcDir(layout.buildDirectory.dir("generated/jvmMain/kotlin"))

            val generatedResources =
                project.layout.buildDirectory.asFile
                    .get()
                    .resolve("generatedResources")
                    .resolve("jvmMain")
                    .resolve("libs")
            resources.srcDir(generatedResources)

            dependencies {
                implementation(libs.jna)
            }
        }

        val jvmTest by getting {
            dependsOn(uniffiTest)
            dependencies {
                implementation(libs.bignum)
                implementation(libs.junit)
            }
        }

        val androidMain by getting {
            dependsOn(uniffiMain)
            kotlin.srcDir(layout.buildDirectory.dir("generated/androidMain/kotlin"))
            dependencies {
                implementation("net.java.dev.jna:jna:5.13.0@aar")
            }
        }

        val nativeMain by creating {
            dependsOn(uniffiMain)
            kotlin.srcDir(layout.buildDirectory.dir("generated/nativeMain/kotlin"))
        }

        val iosArm64Main by getting { dependsOn(nativeMain) }
        val iosX64Main by getting { dependsOn(nativeMain) }
        val iosSimulatorArm64Main by getting { dependsOn(nativeMain) }
        val macosArm64Main by getting { dependsOn(nativeMain) }

        // JS Main is separate and does NOT depend on Uniffi code
        val jsMain by getting {
            dependencies {
                implementation(npm("@noble/hashes", "1.3.1"))
                implementation(libs.kotlin.web)
                implementation(libs.kotlin.node)
            }
        }

        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
            }
        }
    }

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().configureEach {
        val currentTarget = this.name

        // Mapping of Kotlin Native targets to their corresponding Rust architecture directories
        val archMapping =
            mapOf(
                "macosArm64" to "aarch64-apple-darwin",
                "iosX64" to "x86_64-apple-ios",
                "iosArm64" to "aarch64-apple-ios",
                "iosSimulatorArm64" to "aarch64-apple-ios-sim"
            )

        val rustArch =
            archMapping[currentTarget]
                ?: error("Unsupported target $currentTarget for Rust arch mapping.")

        compilations["main"].cinterops.create("ed25519_bip32_wrapper") {
            val interopDir =
                layout.buildDirectory
                    .dir("generated/nativeInterop/cinterop/headers/ed25519_bip32_wrapper")
                    .get()
                    .asFile

            val nativeLibDir =
                layout.buildDirectory
                    .dir("generatedResources/${currentTarget}Main/libs/$rustArch")
                    .get()
                    .asFile

            packageName("ed25519_bip32_wrapper.cinterop")

            header(interopDir.resolve("ed25519_bip32_wrapper.h"))

            defFile(project.file("src/nativeInterop/cinterop/ed25519_bip32_wrapper.def"))

            compilerOpts("-I${interopDir.absolutePath}")

            extraOpts(
                "-libraryPath",
                nativeLibDir.absolutePath,
                "-staticLibrary",
                "libuniffi_ed25519_bip32_wrapper.a"
            )

            tasks[interopProcessingTaskName].dependsOn("prepareRustLibs")
        }
    }
}

// === Group: Android Setup ===
android {
    namespace = "org.hyperledger.identus.apollo.bip32ed25519"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    lint {
        disable += "NewApi"
        checkGeneratedSources = false
        abortOnError = false
    }
}

// === Group: Rust tasks Tasks ===
tasks.register<Exec>("buildRustWrapper") {
    group = "build"
    description = "Builds Rust binaries for Kotlin multiplatform."
    workingDir(wrapperDir)
    commandLine("bash", "./build-kotlin-library.sh")
    inputs.file(wrapperDir.file("build-kotlin-library.sh"))
    outputs.dirs(wrapperDir.dir("target"), wrapperOutputDir)
}

tasks.register<Exec>("buildRustWasm") {
    group = "rust"
    description = "Builds Rust Wasm binaries for Kotlin multiplatform."
    workingDir = wasmDir.asFile
    commandLine("./build_kotlin_library.sh")
    inputs.file(wasmDir.file("build_kotlin_library.sh"))
    outputs.dir(wasmOutputDir)
}

tasks.register<Copy>("copyGeneratedKotlin") {
    group = "rust"
    description = "Copies Rust-generated Kotlin wrappers."
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    dependsOn("buildRustWrapper")
    from(wrapperOutputDir)
    into(layout.buildDirectory.dir("generated"))
}

tasks.register<Copy>("copyWasmOutput") {
    group = "rust"
    description = "Copies Rust-generated Wasm."
    dependsOn("buildRustWasm")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(wasmOutputDir)
    into(
        rootDir
            .resolve("build")
            .resolve("js")
            .resolve("packages")
            .resolve("Apollo")
            .resolve("kotlin")
    )
}

tasks.register<Copy>("copyWasmOutputTest") {
    group = "rust"
    description = "Copies Rust-generated Wasm for testing."
    dependsOn("buildRustWasm")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(wasmOutputDir)
    into(
        rootDir
            .resolve("build")
            .resolve("js")
            .resolve("packages")
            .resolve("Apollo-test")
            .resolve("kotlin")
    )
}

tasks.register("copyNativeLibs") {
    group = "rust"
    description = "Copies native Rust binaries into architecture-specific subdirectories."
    dependsOn("buildRustWrapper")

    val archMapping =
        mapOf(
            "x86_64-unknown-linux-gnu" to "linux-x86-64",
            "aarch64-unknown-linux-gnu" to "linux-aarch64"
        )

    val targetDirs =
        mapOf(
            "jvmMain" to
                listOf(
                    "x86_64-apple-darwin",
                    "aarch64-apple-darwin",
                    "aarch64-unknown-linux-gnu",
                    "x86_64-unknown-linux-gnu"
                ),
            "androidMain" to
                listOf(
                    "aarch64-linux-android",
                    "x86_64-linux-android",
                    "i686-linux-android",
                    "armv7-linux-androideabi"
                ),
            "macosArm64Main" to listOf("aarch64-apple-darwin"),
            "macosX64Main" to listOf("x86_64-apple-darwin"),
            "iosX64Main" to listOf("x86_64-apple-ios"),
            "iosSimulatorArm64Main" to listOf("aarch64-apple-ios-sim"),
            "iosArm64Main" to listOf("aarch64-apple-ios")
        )

    inputs.dir(wrapperDir.dir("target"))
    outputs.dir(layout.buildDirectory.dir("generatedResources"))

    doLast {
        targetDirs.forEach { (target, architectures) ->
            architectures.forEach { archDir ->
                val outputArchDir = archMapping[archDir] ?: archDir
                val outputDir = layout.buildDirectory.dir("generatedResources/$target/libs/$outputArchDir")
                copy {
                    from(wrapperDir.dir("target/$archDir/release"))
                    into(outputDir)
                    include("*.so", "*.dylib", "*.a")
                }
            }
        }
    }
}

tasks.register("prepareRustLibs") {
    group = "rust"
    description = "Aggregate task for building Rust wrappers and copying outputs."
    dependsOn(
        "buildRustWrapper",
        "buildRustWasm",
        "copyGeneratedKotlin",
        "copyWasmOutput",
        "copyWasmOutputTest",
        "copyNativeLibs"
    )
}

// === Group: Main Lifecycle Tasks ===
listOf(
    "assemble",
    "build",
    "prepareAndroidMainArtProfile",
    "jvmProcessResources",
    "jsProcessResources",
    "iosArm64SourcesJar",
    "iosSimulatorArm64SourcesJar",
    "iosX64SourcesJar",
    "jvmSourcesJar",
    "macosArm64SourcesJar"
).forEach {
    if (tasks.findByName(it) != null) {
        tasks.named(it).configure {
            dependsOn("prepareRustLibs", "copyGeneratedKotlin")
        }
    }
}

// === Group: Kotlin Compilation Tasks ===
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    dependsOn("prepareRustLibs")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompileCommon>().configureEach {
    dependsOn("prepareRustLibs")
}

tasks.withType<KotlinJsCompile>().configureEach {
    dependsOn("prepareRustLibs")
    compilerOptions {
        target = "es2015"
    }
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.add("-opt-in=kotlin.ExperimentalStdlibApi")
    }
}

// === Group: Packaging and Assets Tasks ===
tasks
    .matching {
        val name = it.name
        name.startsWith("package") &&
            name.endsWith("Resources") ||
            name.startsWith("package") &&
            name.endsWith("Assets") ||
            name.startsWith("extractDeepLinksForAar") ||
            name.startsWith("packageDebugAssets") ||
            name.startsWith("mergeReleaseResources")
    }.configureEach {
        dependsOn("prepareRustLibs")
    }

// === Group: KtLint Tasks ===
tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.KtLintCheckTask>().configureEach {
    dependsOn("prepareRustLibs")
}

// === Group: Publishing Tasks (Disabled) ===
tasks.withType<PublishToMavenRepository>().configureEach {
    enabled = false
}

tasks.withType<PublishToMavenLocal>().configureEach {
    enabled = false
}
