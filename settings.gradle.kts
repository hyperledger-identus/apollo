pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "apollo"

include(":apollo")
include(":iOSLibs")
include(":secp256k1-kmp")
include(":secp256k1-kmp:native")
include(":bip32-ed25519")
