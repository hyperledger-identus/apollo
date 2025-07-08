plugins {
    base
}

val bash = "bash"
val currentOs = org.gradle.internal.os.OperatingSystem.current()

// A single task for all macOS architectures
tasks.register<Exec>("buildSecp256k1Macos") {
    group = "native build"
    enabled = currentOs.isMacOsX
    workingDir = layout.projectDirectory.asFile
    commandLine(bash, "build-ios.sh", "macosx")
    inputs.file("build-ios.sh")
    inputs.file("xconfigure.sh")
    outputs.dir(layout.buildDirectory.dir("ios"))
}

// A single task for all iOS architectures
tasks.register<Exec>("buildSecp256k1Ios") {
    group = "native build"
    enabled = currentOs.isMacOsX
    workingDir = layout.projectDirectory.asFile
    commandLine(bash, "build-ios.sh", "ios", "iossimulator")
    inputs.file("build-ios.sh")
    inputs.file("xconfigure.sh")
    outputs.dir(layout.buildDirectory.dir("ios"))
}

// Aggregate and Clean-up Tasks
tasks.register("buildSecp256k1") {
    group = "native build"
    if (currentOs.isMacOsX) {
        dependsOn("buildSecp256k1Ios", "buildSecp256k1Macos")
    }
}

tasks.register<Delete>("deleteNativeBuild") {
    group = "build"
    delete(layout.buildDirectory)
}

tasks.named("clean") {
    dependsOn("deleteNativeBuild")
}
