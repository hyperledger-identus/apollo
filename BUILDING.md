
## Bulding Apollo

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
