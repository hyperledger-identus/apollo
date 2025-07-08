# Package org.hyperledger.identus.apollo.securerandom

Apollo Secure Random is Kotlin Multiplatform library to generate secure random bytes

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

```kotlin
val iv: ByteArray = SecureRandom().nextBytes(16)
val seed: ByteArray = SecureRandom.generateSeed(10)
```
