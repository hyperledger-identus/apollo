# Apollo

[![Kotlin](https://img.shields.io/badge/kotlin-1.9.22-blue.svg?logo=kotlin)](http://kotlinlang.org)
![badge-license]
![badge-latest-release]
[![semantic-release-kotlin]](https://github.com/semantic-release/semantic-release)

![badge-platform-android]
![badge-platform-ios]
![badge-platform-jvm]
![badge-platform-js]
![badge-platform-js-node]

A cryptography lib built with Kotlin Multiplatform with support for the following targets:

- JS
- iOS
- Android
- JVM

## How to build Apollo

### Set Environment Variables

Set variable `GITHUB_ACTOR` with your GitHub Username and set variable `GITHUB_TOKEN` with your GitHub Personal Access Token.

As an example we will go with `Bash`

1. Open CMD.
2. Run `sudo nano $HOME/.bash_profile`.
3. Insert `export GITHUB_ACTOR="YOUR GITHUB USERNAME"`
4. Insert `export GITHUB_TOKEN="YOUR GITHUB PERSONAL ACCESS TOKEN"`
5. Save profile and restart CMD to take effect.

### Install Homebrew (Mac Only)

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

### Install autoconf, automake & libtool (Mac Only)

```bash
brew install autoconf automake libtool
```

### Install JDK 11

```bash
cs java --jvm adopt:1.11.0-11 --setup
```

after that `java -version` should yield something like that

```text
openjdk version "11.0.11" 2021-04-20
OpenJDK Runtime Environment (build 11.0.11+9)
OpenJDK 64-Bit Server VM (build 11.0.11+9, mixed mode)
```

In case of using macOS with M chip, make sure to install the arch64 version of Java

### Install XCode (Mac Only)

Install XCode from App Store. 

Then approve xcodebuild license in your terminal. Like so:
```bash
$ sudo xcodebuild -license
```

### Install Android SDK

Install Android SDK from SDK Manager (via Android Studio). 

Then approve Android SDK license. Like so:
```bash
$ cd /Users/{{YOUR USER}}/Library/Android/sdk
$ tools/bin/sdkmanager --licenses
```
While there are many ways to install Android SDK this has proven to be the most reliable way. Standard IntelliJ with Android plugin may work. However, we've had several issues. Your mileage may vary.

For Ubuntu, 
```bash
sudo apt update && sudo apt install android-sdk
```
Leaving the SDK at `~/Android/Sdk`

### Create local.properties file

Create a file named `local.properties` in the root of Apollo.

Add your android sdk path to `local.properties file`. Like so:
```properties
sdk.dir = /Users/{{YOUR USER}}/Library/Android/sdk
```
This will indicate to your IDE which android SDK to use.

Alternatively, you can add the following environment variable into your shell profile file:
```bash
$ export ANDROID_HOME='/Users/{{YOUR USER}}/Library/Android/sdk
```

### Building the project

Install Rust packages:
```bash
$ ./scripts/install-rust-packages.sh
```

You should be able to import and build the project in IntelliJ IDEA now. 

#### Troubleshooting

Here is a list of common issues you might face and its solutions.

##### Environment Variables were added but not available

If you already added the environment variable to your CMD profile and still not being available.

**Solution**

* Restart your Device.

##### No binary for ChromeHeadless browser on your platform

If you get error:
```log
No binary for ChromeHeadless browser on your platform.
Please, set "CHROME_BIN" env variable.
java.lang.IllegalStateException: Errors occurred during launch of browser for testing.
- ChromeHeadless
```

**Solution**

* Install headless chrome or just Chrome browser

##### In case IntelliJ was building but was still showing syntax error in Gradle Script

**Solution**

* Go to preference/settings and make sure to select the correct Java version 11.

##### Could not find JNA native support

if you get this error on macOS with M chip:
```log
Could not find JNA native support
```
**Solution**

* Make sure that you are using Java version that is arch64.

## How to use for JVM/Android app

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

## How to use for Swift app

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
1. Go the [Release Page](https://github.com/hyperledger-identus/apollo/releases) and check the latest version and download the `Apollo.xcframework.zip` file.
2. Uncompress the downloaded file.
3. Add the `Apollo.xcframework` to your Xcode project.
4. When asked select Copy items if needed.
5. Then go to the project configuration page in Xcode and check the Frameworks and Libraries section and add the `Apollo.xcframework` if not found then choose `Embed & Sign`.
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

## How to use for Node.js app

Inside the `package.json`
```json
{
    "dependencies": {
        "@atala/apollo": "<latest version>"
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

<!-- TAG_VERSION -->
[badge-latest-release]: https://img.shields.io/badge/latest--release-1.2.10-blue.svg?style=flat
[badge-license]: https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat
[semantic-release-kotlin]: https://img.shields.io/badge/semantic--release-kotlin-blue?logo=semantic-release

<!-- TAG_PLATFORMS -->
[badge-platform-android]: http://img.shields.io/badge/-android-6EDB8D.svg?style=flat
[badge-platform-ios]: http://img.shields.io/badge/-ios-CDCDCD.svg?style=flat
[badge-platform-jvm]: http://img.shields.io/badge/-jvm-DB413D.svg?style=flat
[badge-platform-js]: http://img.shields.io/badge/-js-F8DB5D.svg?style=flat
[badge-platform-js-node]: https://img.shields.io/badge/-nodejs-68a063.svg?style=flat
