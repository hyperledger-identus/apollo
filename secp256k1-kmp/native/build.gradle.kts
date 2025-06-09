import org.gradle.internal.os.OperatingSystem

val currentOs = OperatingSystem.current()
val bash = "bash"

val buildSecp256k1 by tasks.creating { group = "build native" }

val buildSecp256k1Host by tasks.creating(Exec::class) {
    group = "build native"
    buildSecp256k1.dependsOn(this)

    val target =
        when {
            currentOs.isLinux -> "linux"
            currentOs.isMacOsX -> "darwin"
            currentOs.isWindows -> "mingw"
            else -> error("Unsupported OS $currentOs")
        }

    inputs.files(projectDir.resolve("build.sh"))
    outputs.dir(projectDir.resolve("build/$target"))

    workingDir = projectDir
    environment("TARGET", target)
    commandLine(bash, "-l", "build.sh")
}

val buildSecp256k1Ios by tasks.creating(Exec::class) {
    group = "build native"
    buildSecp256k1.dependsOn(this)

    onlyIf { currentOs.isMacOsX }

    inputs.files(projectDir.resolve("build-ios.sh"))
    outputs.dir(projectDir.resolve("build/ios"))

    workingDir = projectDir
    if (!file("build/ios/arm64-iphoneos/libsecp256k1.a").exists() || !file("build/ios/x86_x64-iphonesimulator/libsecp256k1.a").exists()) {
        commandLine(bash, "build-ios.sh", "ios", "iossimulator")
    } else {
        commandLine("echo", "Skipping ios libsecp256k1.a build execution as the necessary file exists.")
    }
}

val buildSecp256k1IosSimulatorArm64 by tasks.creating(Exec::class) {
    group = "build native"
    buildSecp256k1.dependsOn(this)

    onlyIf { currentOs.isMacOsX }

    inputs.files(projectDir.resolve("build-ios.sh"))
    outputs.dir(projectDir.resolve("build/ios"))

    workingDir = projectDir
    if (!file("build/ios/arm64-iphonesimulator/libsecp256k1.a").exists()) {
        commandLine(bash, "build-ios.sh", "iossimulator")
    } else {
        commandLine("echo", "Skipping iosimulator arm64 libsecp256k1.a build execution as the necessary file exists.")
    }
}

val buildSecp256k1MacosArm64 by tasks.creating(Exec::class) {
    group = "build native"
    buildSecp256k1.dependsOn(this)

    onlyIf { currentOs.isMacOsX }

    inputs.files(projectDir.resolve("build-ios.sh"))
    outputs.dir(projectDir.resolve("build/ios"))

    workingDir = projectDir
    if (!file("build/ios/x86_x64-macosx/libsecp256k1.a").exists()) {
        commandLine(bash, "build-ios.sh", "macosx")
    } else {
        commandLine("echo", "Skipping macosx Arm64 libsecp256k1.a build execution as the necessary file exists.")
    }
}

val buildSecp256k1MacosX64 by tasks.creating(Exec::class) {
    group = "build native"
    buildSecp256k1.dependsOn(this)

    onlyIf { currentOs.isMacOsX }

    inputs.files(projectDir.resolve("build-ios.sh"))
    outputs.dir(projectDir.resolve("build/ios"))

    workingDir = projectDir
    if (!file("build/ios/arm64-macosx/libsecp256k1.a").exists()) {
        commandLine(bash, "build-ios.sh", "macosx")
    } else {
        commandLine("echo", "Skipping macosx X64 libsecp256k1.a build execution as the necessary file exists.")
    }
}

val buildSecp256k1WatchOSArm64 by tasks.creating(Exec::class) {
    group = "build native"
    buildSecp256k1.dependsOn(this)

    onlyIf { currentOs.isMacOsX }

    inputs.files(projectDir.resolve("build-ios.sh"))
    outputs.dir(projectDir.resolve("build/ios"))

    workingDir = projectDir
    if (!file("build/ios/arm64-watchos/libsecp256k1.a").exists()) {
        commandLine(bash, "build-ios.sh", "watchos")
    } else {
        commandLine("echo", "Skipping watchos libsecp256k1.a build execution as the necessary file exists.")
    }
}

val buildSecp256k1TvOSArm64 by tasks.creating(Exec::class) {
    group = "build native"
    buildSecp256k1.dependsOn(this)

    onlyIf { currentOs.isMacOsX }

    inputs.files(projectDir.resolve("build-ios.sh"))
    outputs.dir(projectDir.resolve("build/ios"))

    workingDir = projectDir
    if (!file("build/ios/arm64-tvos/libsecp256k1.a").exists()) {
        commandLine(bash, "build-ios.sh", "tvos")
    } else {
        commandLine("echo", "Skipping tvos libsecp256k1.a build execution as the necessary file exists.")
    }
}

val deleteBuildFolder by tasks.register<Delete>("deleteBuildFolder") {
    group = "build"
    delete(projectDir.resolve("build"))
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
