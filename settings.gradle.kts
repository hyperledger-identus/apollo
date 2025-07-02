pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    versionCatalogs {
        create("libs") {
//            from(files("gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "apollo"

include(":apollo")
include(":iOSLibs")
include(":secp256k1-kmp")
include(":secp256k1-kmp:native")
include(":bip32-ed25519")
