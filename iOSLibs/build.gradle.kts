import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

val os: OperatingSystem = DefaultNativePlatform.getCurrentOperatingSystem()
val libraries = listOf("IOHKSecureRandomGeneration", "IOHKCryptoKit")
val sdks = listOf("iphoneos", "iphonesimulator", "macosx")

libraries.forEach { library ->
    sdks.forEach { sdk ->
        tasks.create<Exec>("build${library.replaceFirstChar(Char::uppercase)}${sdk.replaceFirstChar(Char::uppercase)}") {
            group = "build swift"

            if (os.isMacOsX) {
                when (sdk) {
                    "iphoneos", "iphonesimulator" -> {
                        commandLine(
                            "xcodebuild",
                            "-project",
                            "$library/$library.xcodeproj",
                            "-target",
                            "${library}Iphoneos",
                            "-sdk",
                            sdk
                        )
                    }
                    "macosx" -> {
                        commandLine(
                            "xcodebuild",
                            "-project",
                            "$library/$library.xcodeproj",
                            "-target",
                            "${library}Macos",
                            "-sdk",
                            sdk
                        )
                    }
                }
            } else {
                commandLine("echo", "Unsupported platform.")
            }

            workingDir(projectDir)

            inputs.files(
                fileTree("$projectDir/$library.xcodeproj") { exclude("**/xcuserdata") },
                fileTree("$projectDir/$library/$library")
            )
            when (sdk) {
                "iphoneos" -> {
                    outputs.files(
                        fileTree(projectDir.resolve("$library/build/Release-iphoneos/"))
                    )
                }
                "iphonesimulator" -> {
                    outputs.files(
                        fileTree(projectDir.resolve("$library/build/Release-iphonesimulator/"))
                    )
                }
                "macosx" -> {
                    outputs.files(
                        fileTree(projectDir.resolve("$library/build/Release"))
                    )
                }
            }
        }
    }
}

val deleteBuildFolder by tasks.register<Delete>("deleteBuildFolder") {
    group = "build"
    delete("$projectDir/build")
    libraries.forEach {
        delete("$projectDir/$it/build")
    }
}

afterEvaluate {
    tasks.named("clean") {
        dependsOn(deleteBuildFolder)
    }
    tasks.withType<PublishToMavenRepository>().configureEach {
        enabled = false
    }
    tasks.withType<PublishToMavenLocal>().configureEach {
        enabled = false
    }
}
