plugins {
    base
}

val bash = "bash"

val currentOs = org.gradle.internal.os.OperatingSystem.current()

val buildDirPath = projectDir.resolve("build")

val iosPlatforms = listOf("ios", "iossimulator")

val macosArchitecture = listOf("macosX64", "macosArm64")

fun outputMissing(dir: File) = !dir.exists() || dir.listFiles().isNullOrEmpty()

// Host build task
tasks.register<Exec>("buildSecp256k1Host") {
    group = "native build"
    workingDir = projectDir
    environment(
        "TARGET",
        when {
            currentOs.isLinux -> "linux"
            currentOs.isMacOsX -> "darwin"
            currentOs.isWindows -> "mingw"
            else -> error("Unsupported OS $currentOs")
        }
    )
    commandLine(bash, "build.sh")
    inputs.file("build.sh")
    outputs.dir(buildDirPath)
}

tasks.register<Exec>(
    "buildSecp256k1MacosArm64"
) {
    group = "native build"
    workingDir = projectDir
    commandLine(bash, "build-ios.sh", "macosx")
    inputs.file("build-ios.sh")
    outputs.dir(buildDirPath.resolve("ios"))
    onlyIf { currentOs.isMacOsX }
    onlyIf { outputMissing(buildDirPath.resolve("ios").resolve("arm64-x86_x64-macosx")) }
}

tasks.register<Exec>("buildSecp256k1Ios") {
    group = "native build"
    workingDir = projectDir
    commandLine(bash, "build-ios.sh", *iosPlatforms.toTypedArray())
    inputs.file("build-ios.sh")
    outputs.dir(buildDirPath.resolve("ios"))
    onlyIf { currentOs.isMacOsX }
    onlyIf { outputMissing(buildDirPath.resolve("ios").resolve("arm64-iphoneos")) }
}

tasks.register<Exec>("buildSecp256k1IosSimulatorArm64") {
    group = "native build"
    workingDir = projectDir
    commandLine(bash, "build-ios.sh", "iossimulator")
    inputs.file("build-ios.sh")
    outputs.dir(buildDirPath.resolve("ios"))
    onlyIf { currentOs.isMacOsX }
}

// Aggregate all builds task
tasks.register("buildSecp256k1") {
    group = "native build"
    dependsOn("buildSecp256k1Host")
    if (currentOs.isMacOsX) {
        dependsOn("buildSecp256k1Ios")
        dependsOn("buildSecp256k1IosSimulatorArm64")
        dependsOn("buildSecp256k1MacosArm64")
    }
}

// Proper clean-up
tasks.register<Delete>("deleteNativeBuild") {
    group = "build"
    delete(buildDirPath)
}

// Attach clean to Gradle's standard clean
tasks.named("clean") {
    dependsOn("deleteNativeBuild")
}

// Disable publishing for this module
tasks.withType<PublishToMavenRepository>().configureEach {
    enabled = false
}
tasks.withType<PublishToMavenLocal>().configureEach {
    enabled = false
}
