plugins {
    base
}

val os = org.gradle.internal.os.OperatingSystem.current()
val libraries = listOf("IOHKSecureRandomGeneration", "IOHKCryptoKit")
val sdks = listOf("iphoneos", "iphonesimulator", "macosx")

libraries.forEach { library ->
    sdks.forEach { sdk ->
        tasks.register<Exec>("build${library.replaceFirstChar(Char::uppercase)}${sdk.replaceFirstChar(Char::uppercase)}") {
            group = "build swift"
            description = "Build $library for $sdk."

            workingDir(projectDir)

            commandLine(
                "xcodebuild",
                "-project",
                "$library/$library.xcodeproj",
                "-target",
                when (sdk) {
                    "macosx" -> "${library}Macos"
                    else -> "${library}Iphoneos"
                },
                "-sdk",
                sdk,
                "-configuration",
                "Release"
            )

            onlyIf { os.isMacOsX }

            inputs.files(
                fileTree("$projectDir/$library.xcodeproj") { exclude("**/xcuserdata") },
                fileTree("$projectDir/$library/$library")
            )

            outputs.dir(
                when (sdk) {
                    "iphoneos" -> projectDir.resolve("$library/build/Release-iphoneos/")
                    "iphonesimulator" -> projectDir.resolve("$library/build/Release-iphonesimulator/")
                    "macosx" -> projectDir.resolve("$library/build/Release/")
                    else -> error("Unsupported SDK: $sdk")
                }
            )
        }
    }
}

tasks.register<Delete>("deleteBuildFolder") {
    group = "build"
    delete(buildDir)
    libraries.forEach {
        delete("$projectDir/$it/build")
    }
}

tasks.named("clean") {
    dependsOn("deleteBuildFolder")
}

tasks.withType<PublishToMavenRepository>().configureEach {
    enabled = false
}

tasks.withType<PublishToMavenLocal>().configureEach {
    enabled = false
}
