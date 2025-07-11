import dev.petuska.npm.publish.extension.domain.NpmAccess
import dev.petuska.npm.publish.task.NpmPublishTask
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.net.URI

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.dokka)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.npm.publish)
    alias(libs.plugins.swiftpackage)
    alias(libs.plugins.kover) apply false // https://github.com/Kotlin/kotlinx-kover/issues/747
}

val appleBinaryName = "ApolloLibrary"
val minimumIosVersion = "15.0"
val minimumMacOSVersion = "13.0"

kotlin {
    applyDefaultHierarchyTemplate()
    compilerOptions {
        freeCompilerArgs.addAll("-Xexpect-actual-classes")
    }
    jvm()
    androidLibrary {
        namespace = "dev.allain"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
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
    macosX64 {
        swiftCinterop("IOHKSecureRandomGeneration", name)
        swiftCinterop("IOHKCryptoKit", name)
        binaries.framework {
            baseName = appleBinaryName
        }
    }
    js(IR) {
        outputModuleName = "apollo"
        binaries.library()
        useCommonJs()
        generateTypeScriptDefinitions()
        browser {
            webpackTask {
                output.library = "apollo"
                output.libraryTarget = org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackOutput.Target.VAR
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        nodejs {
            testTask {
                useMocha {
                    timeout = "30s"
                }
            }
        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(project(":bip32-ed25519"))
            implementation(libs.serialization.json)
            implementation(libs.bignum)
            implementation(libs.okio)
            implementation(libs.atomicfu)
            implementation(libs.macs.hmac.sha2)
            implementation(libs.hash.hmac.sha2)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        androidMain.dependencies {
            api(libs.secp256k1.kmp)
            implementation(libs.secp256k1.kmp.jvm)
            implementation(libs.secp256k1.kmp.android)
            implementation(libs.guava)
            implementation(libs.bouncycastle)
            implementation(libs.bitcoinjcore)
            implementation(libs.jna.android)
        }
        jvmMain.dependencies {
            api(libs.secp256k1.kmp)
            implementation(libs.secp256k1.kmp.jvm)
            implementation(libs.guava)
            implementation(libs.bouncycastle)
            implementation(libs.bitcoinjcore)
            implementation(libs.jna)
        }
        jvmTest.dependencies {
            implementation(libs.junit)
        }
        jsMain.dependencies {
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
        jsTest.dependencies {
            implementation(npm("url", "0.11.4"))
        }
        nativeMain.dependencies {
            implementation(project(":bip32-ed25519"))
            implementation(project(":secp256k1-kmp"))
        }
        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
            }
        }
    }
    multiplatformSwiftPackage {
        packageName(appleBinaryName)
        swiftToolsVersion("5.9")
        targetPlatforms {
            iOS { v(minimumIosVersion) }
            macOS { v(minimumMacOSVersion) }
        }
        outputDirectory(File(rootDir, "apollo/build/packages/ApolloSwift"))
    }
}

tasks.withType<DokkaTask>().configureEach {
    moduleName.set("Apollo")
    moduleVersion.set(rootProject.version.toString())
    description = "This is a Kotlin Multiplatform Library for cryptography"
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
                remoteUrl.set(URI("https://github.com/hyperledger-identus/apollo/tree/main/src").toURL())
                remoteLineSuffix.set("#L")
            }
            externalDocumentationLink {
                url.set(URI("https://kotlinlang.org/api/latest/jvm/stdlib/").toURL())
            }
            externalDocumentationLink {
                url.set(URI("https://kotlinlang.org/api/kotlinx.serialization/").toURL())
            }
            externalDocumentationLink {
                url.set(URI("https://api.ktor.io/").toURL())
            }
            externalDocumentationLink {
                url.set(URI("https://kotlinlang.org/api/kotlinx-datetime/").toURL())
                packageListUrl.set(URI("https://kotlinlang.org/api/kotlinx-datetime/").toURL())
            }
            externalDocumentationLink {
                url.set(URI("https://kotlinlang.org/api/kotlinx.coroutines/").toURL())
            }
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
            val iosLibsDir = rootProject.layout.projectDirectory.dir("iOSLibs")

            when (platform) {
                "iosX64", "iosSimulatorArm64" -> {
                    val includeDir = iosLibsDir.dir("$library/build/Release-iphonesimulator/include/")
                    includeDirs.headerFilterOnly(includeDir)
                    tasks[interopProcessingTaskName].dependsOn(":iOSLibs:build${library.replaceFirstChar(Char::uppercase)}Iphonesimulator")
                }

                "iosArm64" -> {
                    val includeDir = iosLibsDir.dir("$library/build/Release-iphoneos/include/")
                    includeDirs.headerFilterOnly(includeDir)
                    tasks[interopProcessingTaskName].dependsOn(":iOSLibs:build${library.replaceFirstChar(Char::uppercase)}Iphoneos")
                }

                "macosX64", "macosArm64" -> {
                    val includeDir = iosLibsDir.dir("$library/build/Release/include/")
                    includeDirs.headerFilterOnly(includeDir)
                    tasks[interopProcessingTaskName].dependsOn(":iOSLibs:build${library.replaceFirstChar(Char::uppercase)}Macosx")
                }
            }
        }
    }
}

/* JS Wasm */
tasks.register<Copy>("copyBip32Wasm") {
    group = "js-build"
    description = "Copy ed25519_bip32_wasm.js to apollo js build directory."
    val buildRustWasmTaskProvider = project(":bip32-ed25519").tasks.named("buildRustWasm")
    dependsOn(buildRustWasmTaskProvider)
    from(project(":bip32-ed25519").layout.projectDirectory.dir("rust-ed25519-bip32/wasm/build"))
    into(rootProject.layout.buildDirectory.dir("js/packages/apollo/kotlin"))
}
tasks.named("jsBrowserProductionLibraryDistribution") {
    dependsOn("copyBip32Wasm")
}
tasks.named("jsNodeProductionLibraryDistribution") {
    dependsOn("copyBip32Wasm")
}

/* JS Wasm + Testing */
tasks.register<Copy>("copyBip32WasmTest") {
    group = "js-build"
    description = "Copy ed25519_bip32_wasm.js to apollo js test build directory."
    val buildRustWasmTaskProvider = project(":bip32-ed25519").tasks.named("buildRustWasm")
    dependsOn(buildRustWasmTaskProvider)
    from(project(":bip32-ed25519").layout.projectDirectory.dir("rust-ed25519-bip32/wasm/build"))
    into(rootProject.layout.buildDirectory.dir("js/packages/apollo-test/kotlin"))
}
tasks.named("jsBrowserTest") {
    dependsOn("copyBip32WasmTest")
}
tasks.named("jsNodeTest") {
    dependsOn("copyBip32WasmTest")
}

/* NPM Publication Wasm */
val npmBip32Wasm by tasks.registering(Copy::class) {
    group = "js-build"
    description = "Copy ed25519_bip32_wasm.js to npm publication directory."
    val buildRustWasmTaskProvider = project(":bip32-ed25519").tasks.named("buildRustWasm")
    dependsOn(buildRustWasmTaskProvider)
    from(project(":bip32-ed25519").layout.projectDirectory.dir("rust-ed25519-bip32/wasm/build"))
    into(layout.buildDirectory.dir("packages/js"))
}
tasks.withType<NpmPublishTask>().configureEach {
    dependsOn("npmBip32Wasm")
}

val swiftPackageUpdateMinOSVersion =
    tasks.register("updateMinOSVersion") {
        group = "multiplatform-swift-package"
        description =
            "Updates the minimum OS version of the plists in the xcframework, known issue of the KMP SwiftPackage plugin"
        dependsOn("createSwiftPackage")

        val xcframeworkDir =
            layout.projectDirectory.file("build/packages/ApolloSwift/$appleBinaryName.xcframework").asFile

        doLast {
            val frameworkPaths =
                mapOf(
                    "ios-arm64/$appleBinaryName.framework" to "ios-arm64/$appleBinaryName.framework/$appleBinaryName",
                    "ios-arm64_x86_64-simulator/$appleBinaryName.framework" to "ios-arm64_x86_64-simulator/$appleBinaryName.framework/$appleBinaryName"
                )

            frameworkPaths.forEach { (plistFolder, binaryRelativePath) ->
                val binaryFile = xcframeworkDir.resolve(binaryRelativePath)
                val plistFile = xcframeworkDir.resolve("$plistFolder/Info.plist")

                if (binaryFile.exists() && plistFile.exists()) {
                    val otoolOutput = providers.exec {
                        isIgnoreExitValue = true
                        commandLine("otool", "-l", binaryFile.absolutePath)
                    }
                    val currentMinOS = otoolOutput.standardOutput.asText.get()
                        .lines()
                        .firstOrNull { it.contains("minos") }
                        ?.trim()
                        ?.split(" ")
                        ?.lastOrNull()
                        ?: throw GradleException("Could not determine min OS version from binary")
                    providers.exec {
                        isIgnoreExitValue = true
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

afterEvaluate {
    if (tasks.findByName("createSwiftPackage") != null) {
        tasks.named("createSwiftPackage").configure {
            finalizedBy(swiftPackageUpdateMinOSVersion)
        }
    }
}

mavenPublishing {
    val shouldAutoRelease = project.findProperty("autoRelease")?.toString()?.toBoolean() ?: false
    publishToMavenCentral(automaticRelease = shouldAutoRelease)
    signAllPublications()
    coordinates(group.toString(), "apollo", rootProject.version.toString())
    pom {
        name.set("Identus Apollo")
        description.set("Collection of cryptographic methods used across Identus platform.")
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

npmPublish {
    organization.set("amagyar-iohk")
    version.set(rootProject.version.toString())
    access.set(NpmAccess.PUBLIC)
    packages {
        access.set(NpmAccess.PUBLIC)
        named("js") {
            scope.set("amagyar-iohk")
            packageName.set("identus-apollo")
            readme.set(rootProject.layout.projectDirectory.file("README.md"))
            packageJson {
                author {
                    name.set("IOG")
                }
                repository {
                    type.set("git")
                    url.set("https://github.com/hyperledger-identus/apollo")
                }
                license.set("Apache-2.0")
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
