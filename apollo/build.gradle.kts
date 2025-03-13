import dev.petuska.npm.publish.extension.domain.NpmAccess
import org.gradle.internal.os.OperatingSystem
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.BitcodeEmbeddingMode
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackOutput.Target
import org.jetbrains.kotlin.gradle.tasks.CInteropProcess
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.tasks.KtLintCheckTask
import java.net.URL
import java.time.Year

val currentModuleName: String = "Apollo"
val os: OperatingSystem = OperatingSystem.current()
val secp256k1Dir = rootDir.resolve("secp256k1-kmp")

val taskGroup = "build ed25519-bip32"
val ed25519bip32Dir = rootDir.resolve("rust-ed25519-bip32")
val generatedDir = project.layout.buildDirectory.asFile.get().resolve("generated")
val generatedResourcesDir = project.layout.buildDirectory.asFile.get().resolve("generatedResources")
val ed25519bip32BinariesDir = ed25519bip32Dir.resolve("wrapper").resolve("target")
val ANDROID_SDK = System.getenv("ANDROID_HOME")
val NDK = System.getenv("ANDROID_NDK_HOME")

plugins {
    kotlin("multiplatform")
    id("io.github.luca992.multiplatform-swiftpackage") version "2.2.2"
    id("com.android.library")
    id("org.jetbrains.dokka")
    id("dev.petuska.npm.publish") version "3.4.1"
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

/**
 * Configures the interop settings for the `secp256k1CInterop` method.
 *
 * @param target The target platform for which to configure the interop.
 * @throws IllegalStateException if the compilation is not found or if the interop processing task dependency is not properly set.
 */
fun KotlinNativeTarget.secp256k1CInterop(target: String) {
    compilations["main"].cinterops {
        val libsecp256k1 by creating {
            includeDirs.headerFilterOnly(
                secp256k1Dir
                    .resolve("native")
                    .resolve("secp256k1")
                    .resolve("include")
            )
            tasks[interopProcessingTaskName]
                .dependsOn(":secp256k1-kmp:native:buildSecp256k1${target.replaceFirstChar(Char::uppercase)}")
        }
    }
}

/**
 * Generates the cinterop configuration for the ed25519Bip32 library based on the target platform.
 *
 * @param target The target platform for which the cinterop configuration is generated.
 *               Supported values are "macosX64", "macosArm64", "iosArm64", "iosX64", and "iosSimulatorArm64".
 *
 * @throws GradleException if an unsupported target platform is specified.
 */
fun KotlinNativeTarget.ed25519Bip32CInterop(target: String) {
    compilations.getByName("main") {
        cinterops {
            val ed25519_bip32_wrapper by creating {
                val crate = this.name
                packageName("$crate.cinterop")
                header(
                    project.layout.buildDirectory.asFile.get()
                        .resolve("generated")
                        .resolve("nativeInterop")
                        .resolve("cinterop")
                        .resolve("headers")
                        .resolve(crate)
                        .resolve("$crate.h")
                )
                tasks.named(interopProcessingTaskName) {
                    dependsOn(":apollo:buildEd25519Bip32")
                }
                when (target) {
                    "macosX64" -> {
                        extraOpts(
                            "-libraryPath",
                            rootDir
                                .resolve("rust-ed25519-bip32")
                                .resolve("wrapper")
                                .resolve("target")
                                .resolve("x86_64-apple-darwin")
                                .resolve("release")
                                .absolutePath
                        )
                    }

                    "macosArm64" -> {
                        extraOpts(
                            "-libraryPath",
                            rootDir
                                .resolve("rust-ed25519-bip32")
                                .resolve("wrapper")
                                .resolve("target")
                                .resolve("aarch64-apple-darwin")
                                .resolve("release")
                                .absolutePath
                        )
                    }

                    "iosArm64" -> {
                        extraOpts(
                            "-libraryPath",
                            rootDir
                                .resolve("rust-ed25519-bip32")
                                .resolve("wrapper")
                                .resolve("target")
                                .resolve("aarch64-apple-ios")
                                .resolve("release")
                                .absolutePath
                        )
                    }

                    "iosX64" -> {
                        extraOpts(
                            "-libraryPath",
                            rootDir
                                .resolve("rust-ed25519-bip32")
                                .resolve("wrapper")
                                .resolve("target")
                                .resolve("x86_64-apple-ios")
                                .resolve("release")
                                .absolutePath
                        )
                    }

                    "iosSimulatorArm64" -> {
                        extraOpts(
                            "-libraryPath",
                            rootDir
                                .resolve("rust-ed25519-bip32")
                                .resolve("wrapper")
                                .resolve("target")
                                .resolve("aarch64-apple-ios-sim")
                                .resolve("release")
                                .absolutePath
                        )
                    }

                    else -> {
                        throw GradleException("Unsupported linking for $target")
                    }
                }
            }
        }
    }
}

/**
 * Creates a copy task with the specified parameters.
 *
 * @param name The name of the copy task.
 * @param fromDir The source directory from which files will be copied.
 * @param intoDir The destination directory where files will be copied into.
 */
fun createCopyTask(
    name: String,
    fromDir: File,
    intoDir: File
) = tasks.register<Copy>(name) {
    group = taskGroup
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    include("*.so", "*.a", "*.d", "*.dylib", "**/*.kt", "*.js", "**/*.h")
    from(fromDir)
    into(intoDir)
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

/**
 * Copy Generated Kotlin Code
 */
val copyEd25519Bip32GeneratedTask = createCopyTask(
    "copyEd25519Bip32Generated",
    ed25519bip32Dir.resolve("wrapper").resolve("build").resolve("generated"),
    generatedDir
)

/**
 * Copy JVM Code to Android folder
 */
val copyToAndroidSrc by tasks.register<Copy>("copyToAndroidSrc") {
    group = taskGroup
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    include("*.so", "*.a", "*.d", "*.dylib", "**/*.kt", "*.js", "**/*.h")
    from(project.layout.buildDirectory.asFile.get().resolve("generated").resolve("jvmMain"))
    into(project.layout.buildDirectory.asFile.get().resolve("generated").resolve("androidMain"))
    dependsOn(copyEd25519Bip32GeneratedTask)
    mustRunAfter(copyEd25519Bip32GeneratedTask)
}

val copyEd25519Bip32ForMacOSX8664Task = createCopyTask(
    "copyEd25519Bip32ForMacOSX86_64",
    ed25519bip32BinariesDir.resolve("x86_64-apple-darwin").resolve("release"),
    generatedResourcesDir.resolve("jvm").resolve("main").resolve("darwin-x86-64")
)

val copyEd25519Bip32ForMacOSArch64Task = createCopyTask(
    "copyEd25519Bip32ForMacOSArch64",
    ed25519bip32BinariesDir.resolve("aarch64-apple-darwin").resolve("release"),
    generatedResourcesDir.resolve("jvm").resolve("main").resolve("darwin-aarch64")
)

val copyEd25519Bip32ForLinuxX8664Task = createCopyTask(
    "copyEd25519Bip32ForLinuxX86_64",
    ed25519bip32BinariesDir.resolve("x86_64-unknown-linux-gnu").resolve("release"),
    generatedResourcesDir.resolve("jvm").resolve("main").resolve("linux-x86-64")
)

val copyEd25519Bip32ForLinuxArch64Task = createCopyTask(
    "copyEd25519Bip32ForLinuxArch64",
    ed25519bip32BinariesDir.resolve("aarch64-unknown-linux-gnu").resolve("release"),
    generatedResourcesDir.resolve("jvm").resolve("main").resolve("linux-aarch64")
)

/**
 * A task group that responsible for moving generated JVM binaries to correct folder.
 */
val copyEd25519Bip32ForJVMTargetTask by tasks.register("copyEd25519Bip32ForJVMTarget") {
    group = taskGroup
    dependsOn(
        copyEd25519Bip32ForMacOSX8664Task,
        copyEd25519Bip32ForMacOSArch64Task,
        copyEd25519Bip32ForLinuxX8664Task,
        copyEd25519Bip32ForLinuxArch64Task
    )
}

val copyEd25519Bip32ForAndroidX8664Task = createCopyTask(
    "copyEd25519Bip32ForAndroidX86_64",
    ed25519bip32BinariesDir.resolve("x86_64-linux-android").resolve("release"),
    generatedResourcesDir.resolve("android").resolve("main").resolve("jniLibs").resolve("x86_64")
)

val copyEd25519Bip32ForAndroidArch64Task = createCopyTask(
    "copyEd25519Bip32ForAndroidArch64",
    ed25519bip32BinariesDir.resolve("aarch64-linux-android").resolve("release"),
    generatedResourcesDir.resolve("android").resolve("main").resolve("jniLibs").resolve("arm64-v8a")
)

val copyEd25519Bip32ForAndroidI686Task = createCopyTask(
    "copyEd25519Bip32ForAndroidI686",
    ed25519bip32BinariesDir.resolve("i686-linux-android").resolve("release"),
    generatedResourcesDir.resolve("android").resolve("main").resolve("jniLibs").resolve("x86")
)

val copyEd25519Bip32ForAndroidArmv7aTask = createCopyTask(
    "copyEd25519Bip32ForAndroidArmv7a",
    ed25519bip32BinariesDir.resolve("armv7-linux-androideabi").resolve("release"),
    generatedResourcesDir.resolve("android").resolve("main").resolve("jniLibs").resolve("armeabi-v7a")
)

/**
 * A task group that responsible for moving generated Android binaries to correct folder.
 */
val copyEd25519Bip32ForAndroidTargetTask by tasks.register("copyEd25519Bip32ForAndroidTarget") {
    group = taskGroup
    dependsOn(
        copyEd25519Bip32ForAndroidX8664Task,
        copyEd25519Bip32ForAndroidArch64Task,
        copyEd25519Bip32ForAndroidI686Task,
        copyEd25519Bip32ForAndroidArmv7aTask
    )
}

val copyEd25519Bip32Wrapper by tasks.register("copyEd25519Bip32") {
    dependsOn(
        copyEd25519Bip32GeneratedTask,
        copyEd25519Bip32ForJVMTargetTask,
        copyEd25519Bip32ForAndroidTargetTask,
        copyToAndroidSrc
    )
    mustRunAfter(buildEd25519Bip32Wrapper)
}

val buildEd25519Bip32Wrapper by tasks.register<Exec>("buildEd25519Bip32Wrapper") {
    group = taskGroup
    workingDir = ed25519bip32Dir.resolve("wrapper")
    val localEnv = this.environment
    localEnv += mapOf(
        "ANDROID_HOME" to ANDROID_SDK,
        "ANDROID_NDK_HOME" to NDK
    )
    this.environment = localEnv
    commandLine("./build-kotlin-library.sh")
}

val copyEd25519Bip32Wasm = createCopyTask(
    "copyEd25519Bip32GeneratedWasm",
    ed25519bip32Dir.resolve("wasm").resolve("build"),
    rootDir.resolve("build").resolve("js").resolve("packages").resolve("Apollo").resolve("kotlin")
)
copyEd25519Bip32Wasm.configure {
    mustRunAfter(buildEd25519Bip32Wasm)
}

val copyEd25519Bip32WasmTest = createCopyTask(
    "copyEd25519Bip32GeneratedWasmTest",
    ed25519bip32Dir.resolve("wasm").resolve("build"),
    rootDir.resolve("build").resolve("js").resolve("packages").resolve("Apollo-test").resolve("kotlin")
)
copyEd25519Bip32WasmTest.configure {
    mustRunAfter(buildEd25519Bip32Wasm)
}

val buildEd25519Bip32Wasm by tasks.register<Exec>("buildEd25519Bip32Wasm") {
    group = taskGroup
    workingDir = ed25519bip32Dir.resolve("wasm")
    commandLine("./build_kotlin_library.sh")
}

val buildEd25519Bip32Task by tasks.register("buildEd25519Bip32") {
    group = taskGroup
    dependsOn(
        buildEd25519Bip32Wasm,
        copyEd25519Bip32Wasm,
        copyEd25519Bip32WasmTest,
        buildEd25519Bip32Wrapper,
        copyEd25519Bip32Wrapper
    )
}

val cleanEd25519Bip32 by tasks.register<Delete>("cleanEd25519Bip32") {
    group = taskGroup
    val wasmDir = ed25519bip32Dir.resolve("wasm")
    delete.add(wasmDir.resolve("build"))
    delete.add(wasmDir.resolve("pkg"))
    delete.add(wasmDir.resolve("node_modules"))
    delete.add(wasmDir.resolve("target"))

    val wrapperDir = ed25519bip32Dir.resolve("wrapper")
    delete.add(wrapperDir.resolve("build"))
    delete.add(wrapperDir.resolve("target"))
}

kotlin {
    androidTarget {
        publishAllLibraryVariants()
    }
    jvm {
        withSourcesJar()
        publishing {
            publications {
                withType<MavenPublication> {
                    artifact(javadocJar)
                }
            }
        }
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    iosArm64 {
        swiftCinterop("IOHKSecureRandomGeneration", name)
        swiftCinterop("IOHKCryptoKit", name)

        secp256k1CInterop("ios")
        ed25519Bip32CInterop(name)

        // https://youtrack.jetbrains.com/issue/KT-39396
        compilations["main"].kotlinOptions.freeCompilerArgs += listOf(
            "-include-binary",
            secp256k1Dir
                .resolve("native")
                .resolve("build")
                .resolve("ios")
                .resolve("arm64-iphoneos")
                .resolve("libsecp256k1.a")
                .absolutePath
        )

        binaries.framework {
            baseName = "ApolloLibrary"
            embedBitcode(BitcodeEmbeddingMode.DISABLE)
        }
    }
    iosX64 {
        swiftCinterop("IOHKSecureRandomGeneration", name)
        swiftCinterop("IOHKCryptoKit", name)

        secp256k1CInterop("ios")
        ed25519Bip32CInterop(name)

        // https://youtrack.jetbrains.com/issue/KT-39396
        compilations["main"].kotlinOptions.freeCompilerArgs += listOf(
            "-include-binary",
            secp256k1Dir
                .resolve("native")
                .resolve("build")
                .resolve("ios")
                .resolve("x86_x64-iphonesimulator")
                .resolve("libsecp256k1.a")
                .absolutePath
        )

        binaries.framework {
            baseName = "ApolloLibrary"
            embedBitcode(BitcodeEmbeddingMode.DISABLE)
            if (os.isMacOsX) {
                if (System.getenv()
                    .containsKey("XCODE_VERSION_MAJOR") && System.getenv("XCODE_VERSION_MAJOR") == "1500"
                ) {
                    linkerOpts += "-ld64"
                }
            }
        }
    }
    iosSimulatorArm64 {
        swiftCinterop("IOHKSecureRandomGeneration", name)
        swiftCinterop("IOHKCryptoKit", name)

        secp256k1CInterop("ios")
        ed25519Bip32CInterop(name)

        // https://youtrack.jetbrains.com/issue/KT-39396
        compilations["main"].kotlinOptions.freeCompilerArgs += listOf(
            "-include-binary",
            secp256k1Dir
                .resolve("native")
                .resolve("build")
                .resolve("ios")
                .resolve("arm64-iphonesimulator")
                .resolve("libsecp256k1.a")
                .absolutePath
        )

        binaries.framework {
            baseName = "ApolloLibrary"
            embedBitcode(BitcodeEmbeddingMode.DISABLE)
        }
    }
    macosArm64 {
        swiftCinterop("IOHKSecureRandomGeneration", name)
        swiftCinterop("IOHKCryptoKit", name)

        secp256k1CInterop("macosArm64")
        ed25519Bip32CInterop(name)

        // https://youtrack.jetbrains.com/issue/KT-39396
        compilations["main"].kotlinOptions.freeCompilerArgs += listOf(
            "-include-binary",
            secp256k1Dir
                .resolve("native")
                .resolve("build")
                .resolve("ios")
                .resolve("arm64-macosx")
                .resolve("libsecp256k1.a")
                .absolutePath
        )

        binaries.framework {
            baseName = "ApolloLibrary"
            embedBitcode(BitcodeEmbeddingMode.DISABLE)
        }
    }
    js(IR) {
        this.moduleName = currentModuleName
        this.binaries.library()
        this.useCommonJs()
        generateTypeScriptDefinitions()
        this.compilations["main"].packageJson {
            this.version = rootProject.version.toString()
        }
        this.compilations["test"].packageJson {
            this.version = rootProject.version.toString()
        }
        browser {
            this.webpackTask {
                this.output.library = currentModuleName
                this.output.libraryTarget = Target.VAR
            }
            this.testTask {
                this.useKarma {
                    this.useChromeHeadless()
                }
            }
        }
        nodejs {
            this.testTask {
                this.useKarma {
                    this.useChromeHeadless()
                }
            }
        }
    }
    applyDefaultHierarchyTemplate()

    multiplatformSwiftPackage {
        packageName("Apollo")
        swiftToolsVersion("5.3")
        targetPlatforms {
            iOS { v("13") }
            macOS { v("11") }
        }
        outputDirectory(File(rootDir, "apollo/build/packages/ApolloSwift"))
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
                implementation("com.ionspin.kotlin:bignum:0.3.9")
                implementation("org.kotlincrypto.macs:hmac-sha2:0.3.0")
                implementation("org.kotlincrypto.hash:sha2:0.4.0")
                implementation("com.squareup.okio:okio:3.7.0")
                implementation("org.jetbrains.kotlinx:atomicfu:0.23.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val allButJSMain by creating {
            dependsOn(commonMain)
            kotlin.srcDir(
                generatedDir
                    .resolve("commonMain")
                    .resolve("kotlin")
            )
        }
        val allButJSTest by creating {
            dependsOn(commonTest)
        }
        val androidMain by getting {
            dependsOn(allButJSMain)
            kotlin.srcDir(
                generatedDir
                    .resolve("androidMain")
                    .resolve("kotlin")
            )
            val generatedResources = project.layout.buildDirectory.asFile.get()
                .resolve("generatedResources")
                .resolve("android")
                .resolve("main")
                .resolve("jniLibs")
            resources.srcDir(generatedResources)
            dependencies {
                api("fr.acinq.secp256k1:secp256k1-kmp:0.14.0")
                implementation("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm:0.11.0")
                implementation("fr.acinq.secp256k1:secp256k1-kmp-jni-android:0.14.0")
                implementation("com.google.guava:guava:30.1-jre")
                implementation("org.bouncycastle:bcprov-jdk15on:1.68")
                implementation("org.bitcoinj:bitcoinj-core:0.16.2")
                implementation("net.java.dev.jna:jna:5.13.0@aar")
            }
        }
        val androidUnitTest by getting {
            dependsOn(allButJSTest)
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
        val jvmMain by getting {
            dependsOn(allButJSMain)
            kotlin.srcDir(
                generatedDir
                    .resolve("jvmMain")
                    .resolve("kotlin")
            )
            val generatedResources = project.layout.buildDirectory.asFile.get()
                .resolve("generatedResources")
                .resolve("jvm")
                .resolve("main")
            resources.srcDir(generatedResources)
            dependencies {
                api("fr.acinq.secp256k1:secp256k1-kmp:0.14.0")
                implementation("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm:0.11.0")
                implementation("com.google.guava:guava:30.1-jre")
                implementation("org.bouncycastle:bcprov-jdk15on:1.68")
                implementation("org.bitcoinj:bitcoinj-core:0.16.2")
                implementation("net.java.dev.jna:jna:5.13.0")
            }
        }
        val jvmTest by getting {
            dependsOn(allButJSTest)
            dependencies {
                implementation("junit:junit:4.13.2")
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

                // Polyfill dependencies
                implementation(npm("stream-browserify", "3.0.0"))
                implementation(npm("buffer", "6.0.3"))

                implementation("org.jetbrains.kotlin-wrappers:kotlin-web:1.0.0-pre.461")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-node:18.11.13-pre.461")
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(npm("url", "0.11.4"))
            }
        }
        val nativeMain by getting {
            dependsOn(allButJSMain)
            kotlin.srcDir(
                generatedDir
                    .resolve("nativeMain")
                    .resolve("kotlin")
            )
        }
        val appleMain by getting {
            kotlin.srcDirs(
                secp256k1Dir
                    .resolve("src")
                    .resolve("commonMain")
                    .resolve("kotlin"),
                secp256k1Dir
                    .resolve("src")
                    .resolve("nativeMain")
                    .resolve("kotlin")
            )
        }
        applyDefaultHierarchyTemplate()

        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
            }
        }
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    // Enable the export of KDoc (Experimental feature) to Generated Native targets (Apple, Linux, etc.)
    targets.withType<KotlinNativeTarget> {
        compilations.getByName("main") {
            compilerOptions.options.freeCompilerArgs.add("-Xexport-kdoc")
        }
    }

    if (os.isMacOsX) {
        if (tasks.findByName("iosX64Test") != null) {
            tasks.getByName<org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeSimulatorTest>("iosX64Test") {
                device.set("iPhone 15 Pro Max")
            }
        }
        if (tasks.findByName("iosSimulatorArm64Test") != null) {
            tasks.getByName<org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeSimulatorTest>("iosSimulatorArm64Test") {
                device.set("iPhone 15 Pro Max")
            }
        }
    }
}

android {
    namespace = "org.hyperledger.identus.apollo"
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].jniLibs {
        setSrcDirs(
            listOf(
                project.layout.buildDirectory.asFile.get()
                    .resolve("generatedResources")
                    .resolve("android")
                    .resolve("main")
                    .resolve("jniLibs")
            )
        )
    }

    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    /**
     * Because Software Components will not be created automatically for Maven publishing from
     * Android Gradle Plugin 8.0. To opt-in to the future behavior, set the Gradle property android.
     * disableAutomaticComponentCreation=true in the `gradle.properties` file or use the new
     * publishing DSL.
     */
    publishing {
        multipleVariants {
            withSourcesJar()
            withJavadocJar()
            allVariants()
        }
    }
}

ktlint {
    filter {
        exclude {
            it.file.toString().contains("generated")
        }
        exclude("**/external/*", "./src/jsMain/kotlin/io/iohk/atala/prism/apollo/utils/external/*")
        exclude {
            it.file.toString().contains("external")
        }
        exclude { projectDir.toURI().relativize(it.file.toURI()).path.contains("/external/") }
    }
}

// Dokka implementation
tasks.withType<DokkaTask>().configureEach {
    moduleName.set(currentModuleName)
    moduleVersion.set(rootProject.version.toString())
    description = "This is a Kotlin Multiplatform Library for cryptography"
    pluginConfiguration<org.jetbrains.dokka.base.DokkaBase, org.jetbrains.dokka.base.DokkaBaseConfiguration> {
        footerMessage = "(c) ${Year.now().value} HyberLedger Copyright"
    }
    dokkaSourceSets {
        configureEach {
            jdkVersion.set(17)
            languageVersion.set("1.9.22")
            apiVersion.set("2.0")
            includes.from(
                "docs/Apollo.md",
                "docs/Base64.md",
                "docs/SecureRandom.md"
            )
            sourceLink {
                localDirectory.set(projectDir.resolve("src"))
                remoteUrl.set(URL("https://github.com/hyperledger-identus/apollo/tree/main/src"))
                remoteLineSuffix.set("#L")
            }
            externalDocumentationLink {
                url.set(URL("https://kotlinlang.org/api/latest/jvm/stdlib/"))
            }
            externalDocumentationLink {
                url.set(URL("https://kotlinlang.org/api/kotlinx.serialization/"))
            }
            externalDocumentationLink {
                url.set(URL("https://api.ktor.io/"))
            }
            externalDocumentationLink {
                url.set(URL("https://kotlinlang.org/api/kotlinx-datetime/"))
                packageListUrl.set(URL("https://kotlinlang.org/api/kotlinx-datetime/"))
            }
            externalDocumentationLink {
                url.set(URL("https://kotlinlang.org/api/kotlinx.coroutines/"))
            }
        }
    }
}

npmPublish {
    organization.set("hyperledger")
    version.set(rootProject.version.toString())
    access.set(NpmAccess.PUBLIC)
    packages {
        access.set(NpmAccess.PUBLIC)
        named("js") {
            scope.set("hyperledger")
            packageName.set("identus-apollo")
            readme.set(rootDir.resolve("README.md"))
            files {
                from(
                    ed25519bip32Dir
                        .resolve("wasm")
                        .resolve("build")
                )
            }
            packageJson {
                author {
                    name.set("IOG")
                }
                repository {
                    type.set("git")
                    url.set("https://github.com/hyperledger-identus/apollo")
                }
            }
        }
    }
    registries {
        access.set(NpmAccess.PUBLIC)
        register("npmjs") {
            uri.set("https://registry.npmjs.org")
            authToken.set(System.getenv("NPM_TOKEN"))
        }
    }
}

// Workaround for a bug in Gradle
afterEvaluate {
    tasks.withType<KotlinCompile> {
        dependsOn(
            ":iOSLibs:buildIOHKCryptoKitIphoneos",
            ":iOSLibs:buildIOHKCryptoKitIphonesimulator",
            ":iOSLibs:buildIOHKCryptoKitMacosx",
            ":iOSLibs:buildIOHKSecureRandomGenerationIphoneos",
            ":iOSLibs:buildIOHKSecureRandomGenerationIphonesimulator",
            ":iOSLibs:buildIOHKSecureRandomGenerationMacosx",
            buildEd25519Bip32Task
        )
    }
    tasks.withType<ProcessResources> {
        dependsOn(
            ":iOSLibs:buildIOHKCryptoKitIphoneos",
            ":iOSLibs:buildIOHKCryptoKitIphonesimulator",
            ":iOSLibs:buildIOHKCryptoKitMacosx",
            ":iOSLibs:buildIOHKSecureRandomGenerationIphoneos",
            ":iOSLibs:buildIOHKSecureRandomGenerationIphonesimulator",
            ":iOSLibs:buildIOHKSecureRandomGenerationMacosx",
            buildEd25519Bip32Task
        )
    }
    tasks.withType<CInteropProcess> {
        dependsOn(
            ":iOSLibs:buildIOHKCryptoKitIphoneos",
            ":iOSLibs:buildIOHKCryptoKitIphonesimulator",
            ":iOSLibs:buildIOHKCryptoKitMacosx",
            ":iOSLibs:buildIOHKSecureRandomGenerationIphoneos",
            ":iOSLibs:buildIOHKSecureRandomGenerationIphonesimulator",
            ":iOSLibs:buildIOHKSecureRandomGenerationMacosx",
            copyEd25519Bip32GeneratedTask
        )
    }
    tasks.getByName<Delete>("clean") {
        dependsOn(cleanEd25519Bip32)
    }
    tasks.withType<KtLintCheckTask> {
        // dependsOn(buildEd25519Bip32Task)
    }
    tasks.withType<org.gradle.jvm.tasks.Jar> {
        dependsOn(copyEd25519Bip32GeneratedTask)
    }

    tasks.getByName("androidDebugSourcesJar").dependsOn(copyToAndroidSrc)
    tasks.getByName("androidReleaseSourcesJar").dependsOn(copyToAndroidSrc)

    tasks.getByName("mergeDebugJniLibFolders").dependsOn(buildEd25519Bip32Task)
    tasks.getByName("mergeReleaseJniLibFolders").dependsOn(buildEd25519Bip32Task)

    tasks.getByName("packageDebugResources").dependsOn(buildEd25519Bip32Task)
    tasks.getByName("packageReleaseResources").dependsOn(buildEd25519Bip32Task)

    tasks.getByName("extractDeepLinksForAarDebug").dependsOn(buildEd25519Bip32Task)
    tasks.getByName("extractDeepLinksForAarRelease").dependsOn(buildEd25519Bip32Task)

    tasks.getByName("mergeDebugResources").dependsOn(buildEd25519Bip32Task)
    tasks.getByName("mergeReleaseResources").dependsOn(buildEd25519Bip32Task)

    if (tasks.findByName("iosX64Test") != null) {
        tasks.named("iosX64Test") {
            this.enabled = false
        }
    }
    tasks.withType<PublishToMavenRepository> {
        dependsOn(tasks.withType<Sign>())
    }
    tasks.withType<PublishToMavenLocal> {
        dependsOn(tasks.withType<Sign>())
    }

    // Disable publish of targets
    if (tasks.findByName("publishIosX64PublicationToSonatypeRepository") != null) {
        tasks.named("publishIosX64PublicationToSonatypeRepository") {
            this.enabled = false
        }
    }
    if (tasks.findByName("publishIosArm64PublicationToSonatypeRepository") != null) {
        tasks.named("publishIosArm64PublicationToSonatypeRepository") {
            this.enabled = false
        }
    }
    if (tasks.findByName("publishIosSimulatorArm64PublicationToSonatypeRepository") != null) {
        tasks.named("publishIosSimulatorArm64PublicationToSonatypeRepository") {
            this.enabled = false
        }
    }
    if (tasks.findByName("publishMacosArm64PublicationToSonatypeRepository") != null) {
        tasks.named("publishMacosArm64PublicationToSonatypeRepository") {
            this.enabled = false
        }
    }
    if (tasks.findByName("publishJsPublicationToSonatypeRepository") != null) {
        tasks.named("publishJsPublicationToSonatypeRepository") {
            this.enabled = false
        }
    }
}
