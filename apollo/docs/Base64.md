# Package org.hyperledger.identus.apollo.base64

Apollo Base64 is Kotlin Multiplatform library containing Standard & URL safe

## Supported Targets

| Platform                             | Supported |
|--------------------------------------|-----------|
| iOS x86 64                           | ✔         |
| iOS Arm 64                           | ✔         |
| iOS Arm 32                           | ✔         |
| iOS Simulator Arm 64 (Apple Silicon) | ✔         |
| JVM                                  | ✔         | 
| Android                              | ✔         |
| JS Browser                           | ✔         |
| NodeJS Browser                       | ✔         |
| macOS Arm 64 (Apple Silicon)         | ✔         |

## Usage

### Base64 Standard

```kotlin
val helloWorld = "V2VsY29tZSB0byBJT0c=".base64Decoded // "Hello, world!"
println("Welcome to IOG".base64Encoded) // Prints "SGVsbG8sIHdvcmxkIQ=="
```

### Base64 URL

```kotlin
val helloWorld = "V2VsY29tZSB0byBJT0c".base64UrlDecoded // "Hello, world!"
println("Welcome to IOG".base64UrlEncoded) // Prints "SGVsbG8sIHdvcmxkIQ"
```
