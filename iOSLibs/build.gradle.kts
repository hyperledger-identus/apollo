plugins {
    base
}

val libraries = listOf("IOHKSecureRandomGeneration", "IOHKCryptoKit")
val sdks = listOf("iphoneos", "iphonesimulator", "macosx")

libraries.forEach { library ->
    sdks.forEach { sdk ->
        tasks.register<Exec>("build${library.replaceFirstChar(Char::uppercase)}${sdk.replaceFirstChar(Char::uppercase)}") {
            group = "build swift"
            description = "Build $library for $sdk."

            // FIX 1: Revert to direct assignment, which is what your Gradle version expects.
            workingDir = layout.projectDirectory.asFile

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

            onlyIf {
                System.getProperty("os.name").contains("Mac OS X", ignoreCase = true)
            }

            // FIX 2: Use `project.fileTree` with a directory from the layout API.
            inputs.files(
                project.fileTree(layout.projectDirectory.dir("$library/$library.xcodeproj")) {
                    exclude("**/xcuserdata")
                },
                project.fileTree(layout.projectDirectory.dir("$library/$library"))
            )

            outputs.dir(
                layout.projectDirectory.dir(
                    when (sdk) {
                        "iphoneos" -> "$library/build/Release-iphoneos/"
                        "iphonesimulator" -> "$library/build/Release-iphonesimulator/"
                        "macosx" -> "$library/build/Release/"
                        else -> error("Unsupported SDK: $sdk")
                    }
                )
            )
        }
    }
}

tasks.register<Delete>("deleteBuildFolder") {
    group = "build"
    // FIX 3: Use `layout.buildDirectory` to avoid the deprecation warning.
    delete(layout.buildDirectory)
    libraries.forEach { library ->
        delete(layout.projectDirectory.dir("$library/build"))
    }
}

tasks.named("clean") {
    dependsOn("deleteBuildFolder")
}
