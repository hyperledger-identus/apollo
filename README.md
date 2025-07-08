![License](https://img.shields.io/badge/license-Apache%20License%202.0-blue?logo=apache)
![Kotlin](https://img.shields.io/badge/kotlin-2.2.0-blue?logo=kotlin)
![Gradle](https://img.shields.io/badge/gradle-8.13-blue?logo=gradle)

# Apollo

A cryptography lib built with Kotlin Multiplatform with support for the following targets:

## Packages

[![Sonatype](https://img.shields.io/maven-central/v/org.hyperledger.identus/apollo?logo=sonatype&color=orange&label=maven&logoColor=1B1C30)](https://central.sonatype.com/artifact/org.hyperledger.identus/apollo)
[![NPM](https://img.shields.io/npm/v/%40hyperledger%2Fidentus-apollo?logo=npm&color=orange&label=npm&logoColor=CB3837)](https://www.npmjs.com/package/@hyperledger/identus-apollo)
[![Swift](https://img.shields.io/github/v/release/hyperledger-identus/apollo?logo=swift&color=orange&label=spm)](https://github.com/hyperledger-identus/apollo/releases)

## JVM Usage

In `build.gradle.kts` files include the dependency
```kotlin
repositories {
    mavenCentral()
}
```
For dependencies
```kotlin
dependencies {
    implementation("org.hyperledger.identus:apollo:<latest version>")
}
```

## Swift usage

### Using SPM

Inside your `Package.swift` file, add the following
```swift
dependencies: [
    .package(
        url: "git@github.com:hyperledger/identus-apollo.git",
        from: "<latest version>"
    )
]
```
### Using generated xcframework directly

The following instruction using Xcode 15
1. Go the [Release Page](https://github.com/hyperledger-identus/apollo/releases) and check the latest version and download the `ApolloBinary.xcframework.zip` file.
2. Uncompress the downloaded file.
3. Add the `ApolloBinary.xcframework` to your Xcode project.
4. When asked select Copy items if needed.
5. Then go to the project configuration page in Xcode and check the Frameworks and Libraries section and add the `ApolloBinary.xcframework` if not found then choose `Embed & Sign`.
6. Then go to the build phase page and mark the framework as required.

[!WARNING]
**For Intel iOS simulator**: You need to add the following flag as YES `EMBEDDED_CONTENT_CONTAINS_SWIFT=YES` on the target like so:

```swift
Package.swift

Package(
   ...
   targets: .testTarget(
      ...
      swiftSettings: [.define("EMBEDDED_CONTENT_CONTAINS_SWIFT=YES")]
      ...
   )
)

```

## Node.js usage

Inside the `package.json`
```json
{
    "dependencies": {
        "@hyperledger/identus-apollo": "<latest version>"
    }
}
```

## How to use for another KMP (Kotlin Multiplatform) project

### Using Groovy

In the project `build.gradle`
```groovy
allprojects {
    repositories {
        // along with all the other current existing repos add the following
        mavenCentral()
    }
}
```
In the module `build.gradle`
```groovy
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                // This following is just an example you can import it as per you needs
                implementation 'org.hyperledger.identus:apollo:<latest version>'
            }
        }
    }
}
```

### Using Kotlin DSL

In the project `build.gradle.kts`
```kotlin
allprojects {
    repositories {
        // along with all the other current existing repos add the following
        mavenCentral()
    }
}
```
```kotlin
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                // This following is just an example you can import it as per you needs
                implementation("org.hyperledger.identus:apollo:<latest version>")
            }
        }
    }
}
```

## How to use for Scala project

```scala
libraryDependencies += "org.hyperledger.identus" % "apollo-jvm" % "<latest version>"
```

## Usage

Please have a look at unit tests, more samples will be added soon.

## Building Apollo

See [BUILDING.md](./BUILDING.md) for instructions on how to build Apollo from source.

## Contributing to Apollo
See [CONTRIBUTING.md](./CONTRIBUTING.md) and [CODE_OF_CONDUCT.md](./CODE_OF_CONDUCT.md) for instructions on how to contribute

## Cryptography Notice

This distribution includes cryptographic software. The country in which you currently reside may 
have restrictions on the import, possession, use, and/or re-export to another country, of encryption 
software. BEFORE using any encryption software, please check your country's laws, regulations and policies 
concerning the import, possession, or use, and re-export of encryption software, to see if this is permitted. 
See [http://www.wassenaar.org/](http://www.wassenaar.org/) for more information.

## License

This software is provided 'as-is', without any express or implied warranty. In no event will the
authors be held liable for any damages arising from the use of this software. Permission is granted
to anyone to use this software for any purpose, including commercial applications, and to alter it
and redistribute it freely.
