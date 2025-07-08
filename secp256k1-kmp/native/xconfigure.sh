#!/usr/bin/env bash

#
# Build for iOS 64bit-ARM variants and iOS Simulator
# - Place the script at project root
# - Customize MIN_IOS_VERSION and other flags as needed
#
# Test Environment
# - macOS 10.14.6
# - iOS 13.1
# - Xcode 11.1
#

Build() {
    # Ensure -fembed-bitcode builds, as workaround for libtool macOS bug
    export MACOSX_DEPLOYMENT_TARGET="10.4"
    # Get the correct toolchain for target platforms
    export CC=$(xcrun --find --sdk "${SDK}" clang)
    export CXX=$(xcrun --find --sdk "${SDK}" clang++)
    export CPP=$(xcrun --find --sdk "${SDK}" cpp)
    export CFLAGS="${HOST_FLAGS} ${OPT_FLAGS}"
    export CXXFLAGS="${HOST_FLAGS} ${OPT_FLAGS}"
    export LDFLAGS="${HOST_FLAGS}"

    # Create a unique, isolated build directory for this specific architecture
    BUILD_DIR_ARCH="${PLATFORMS}/${PLATFORM}/build"
    mkdir -p "${BUILD_DIR_ARCH}"
    cd "${BUILD_DIR_ARCH}" # Enter the isolated directory
    
    ../../../../configure \
        --host="${CHOST}" \
        --prefix="${PREFIX}" \
        --exec-prefix="${PLATFORMS}/${PLATFORM}" \
        --enable-static \
        --disable-shared \
        "$@"

    make -j"${MAKE_JOBS}"
    make install
    cd ../../../..
}

# Locations
ScriptDir="$( cd "$( dirname "$0" )" && pwd )"
cd - &> /dev/null
PREFIX="${ScriptDir}"/_build
PLATFORMS="${PREFIX}"/platforms
UNIVERSAL="${PREFIX}"/universal

# Compiler options
OPT_FLAGS="-O3 -g3 -fembed-bitcode"
MAKE_JOBS=8
MIN_IOS_VERSION=15.6
MIN_MACOS_VERSION=13.5
MIN_WATCHOS_VERSION=8
MIN_TVOS_VERSION=15.6

# Create universal binary
mkdir -p "${UNIVERSAL}" &> /dev/null

# Get target platforms from environment variable
TARGET_PLATFORMS=${TARGET_PLATFORMS}

# Convert comma-separated platforms to array
IFS=', ' read -r -a array <<< "$TARGET_PLATFORMS"

for platform in "${array[@]}"
do
    echo "Building for ${platform}"
    case ${platform} in
        "macosx")
            # Build for platforms
            ## Build for macosx platform
            SDK="macosx"
            PLATFORM="arm64-macosx"
            PLATFORM_MACOS=${PLATFORM}
            ARCH_FLAGS="-arch arm64"
            HOST_FLAGS="${ARCH_FLAGS} -mmacosx-version-min=${MIN_MACOS_VERSION} -isysroot $(xcrun --sdk ${SDK} --show-sdk-path)"
            CHOST="arm-apple-darwin"
            Build "$@"

            # Build for platforms
            ## Build for macosx platform x86-64
            SDK="macosx"
            PLATFORM="x86_64-macosx" # Updated this to target x86_64 architecture
            PLATFORM_MACOS_X86=${PLATFORM}
            ARCH_FLAGS="-arch x86_64" # Updated this to target x86_64 architecture
            HOST_FLAGS="${ARCH_FLAGS} -mmacosx-version-min=${MIN_MACOS_VERSION} -isysroot $(xcrun --sdk ${SDK} --show-sdk-path)"
            CHOST="x86_64-apple-darwin" # Updated this to specify x86_64 architecture
            Build "$@"

            mkdir -p "${UNIVERSAL}/arm64-x86_x64-macosx" &> /dev/null
            mkdir -p "${UNIVERSAL}/x86_x64-macosx" &> /dev/null
            mkdir -p "${UNIVERSAL}/arm64-macosx" &> /dev/null

            cp "${PLATFORMS}/${PLATFORM_MACOS_X86}/lib/libsecp256k1.a" "${UNIVERSAL}/x86_x64-macosx/libsecp256k1.a"
            cp "${PLATFORMS}/${PLATFORM_MACOS}/lib/libsecp256k1.a" "${UNIVERSAL}/arm64-macosx/libsecp256k1.a"

            lipo -create -output "${UNIVERSAL}/arm64-x86_x64-macosx/libsecp256k1.a" "${PLATFORMS}/${PLATFORM_MACOS_X86}/lib/libsecp256k1.a" "${PLATFORMS}/${PLATFORM_MACOS}/lib/libsecp256k1.a"
        ;;
        "ios")
            ## Build for iphoneos platform
            SDK="iphoneos"
            PLATFORM="arm64-iphoneos"
            PLATFORM_IOS=${PLATFORM}
            ARCH_FLAGS="-arch arm64"
            HOST_FLAGS="${ARCH_FLAGS} -miphoneos-version-min=${MIN_IOS_VERSION} -isysroot $(xcrun --sdk ${SDK} --show-sdk-path)"
            CHOST="arm-apple-darwin"
            Build "$@"

            mkdir -p "${UNIVERSAL}/${PLATFORM_IOS}" &> /dev/null
            lipo -create -output "${UNIVERSAL}/${PLATFORM_IOS}/libsecp256k1.a" "${PLATFORMS}/${PLATFORM_IOS}/lib/libsecp256k1.a"
        ;;
        "iossimulator")
            ## Build for iphone intel simulators
            SDK="iphonesimulator"
            PLATFORM="x86_64-sim"
            PLATFORM_SIM_X86=${PLATFORM}
            ARCH_FLAGS="-arch x86_64"
            HOST_FLAGS="${ARCH_FLAGS} -mios-simulator-version-min=${MIN_IOS_VERSION} -isysroot $(xcrun --sdk ${SDK} --show-sdk-path)"
            CHOST="x86_64-apple-darwin"
            Build "$@"

            ## Build for iphone M1/M2/Mx simulators
            SDK="iphonesimulator"
            PLATFORM="arm64-sim"
            PLATFORM_SIM_ARM=${PLATFORM}
            ARCH_FLAGS="-arch arm64"
            HOST_FLAGS="${ARCH_FLAGS} -mios-simulator-version-min=${MIN_IOS_VERSION} -isysroot $(xcrun --sdk ${SDK} --show-sdk-path)"
            CHOST="arm-apple-darwin"
            Build "$@"

            mkdir -p "${UNIVERSAL}/arm64_x86_x64-iphonesimulator" &> /dev/null
            mkdir -p "${UNIVERSAL}/x86_x64-iphonesimulator" &> /dev/null
            mkdir -p "${UNIVERSAL}/arm64-iphonesimulator" &> /dev/null

            cp "${PLATFORMS}/${PLATFORM_SIM_X86}/lib/libsecp256k1.a" "${UNIVERSAL}/x86_x64-iphonesimulator/libsecp256k1.a"
            cp "${PLATFORMS}/${PLATFORM_SIM_ARM}/lib/libsecp256k1.a" "${UNIVERSAL}/arm64-iphonesimulator/libsecp256k1.a"

            lipo -create -output "${UNIVERSAL}/arm64_x86_x64-iphonesimulator/libsecp256k1.a" "${PLATFORMS}/${PLATFORM_SIM_ARM}/lib/libsecp256k1.a" "${PLATFORMS}/${PLATFORM_SIM_X86}/lib/libsecp256k1.a"
        ;;
        "tvos")
            ## Build for tvOS device
            SDK="appletvos"
            PLATFORM="arm64-appletvos"
            PLATFORM_TV_ARM=${PLATFORM}
            ARCH_FLAGS="-arch arm64"
            HOST_FLAGS="${ARCH_FLAGS} -mappletvos-version-min=${MIN_TVOS_VERSION} -isysroot $(xcrun --sdk ${SDK} --show-sdk-path)"
            CHOST="arm-apple-darwin"
            Build "$@"

            mkdir -p "${UNIVERSAL}/${PLATFORM_TV_ARM}" &> /dev/null

            lipo -create -output "${UNIVERSAL}/${PLATFORM_TV_ARM}/libsecp256k1.a" "${PLATFORMS}/${PLATFORM_TV_ARM}/lib/libsecp256k1.a"
        ;;
        "watchos")
            ## Build for watchOS device
            SDK="watchos"
            PLATFORM="arm64-watchos"
            PLATFORM_WATCH_ARM=${PLATFORM}
            ARCH_FLAGS="-arch arm64"
            HOST_FLAGS="${ARCH_FLAGS} -mwatchos-version-min=${MIN_WATCHOS_VERSION} -isysroot $(xcrun --sdk ${SDK} --show-sdk-path)"
            CHOST="arm-apple-darwin"
            Build "$@"

            mkdir -p "${UNIVERSAL}/${PLATFORM_WATCH_ARM}" &> /dev/null

            lipo -create -output "${UNIVERSAL}/${PLATFORM_WATCH_ARM}/libsecp256k1.a" "${PLATFORMS}/${PLATFORM_WATCH_ARM}/lib/libsecp256k1.a"
        ;;
        *)
            echo "Unsupported platform: ${TARGET_PLATFORM}"
        ;;
    esac
done


### Build for watchOS M1/M2/Mx simulator
#SDK="watchsimulator"
#PLATFORM="arm64-watchos-sim"
#PLATFORM_WATCH_SIM_ARM=${PLATFORM}
#ARCH_FLAGS="-arch arm64"
#HOST_FLAGS="${ARCH_FLAGS} -mwatchos-simulator-version-min=${MIN_WATCHOS_VERSION} -isysroot $(xcrun --sdk ${SDK} --show-sdk-path)"
#CHOST="arm-apple-darwin"
#Build "$@"
#
### Build for watchOS intel simulator
#SDK="watchsimulator"
#PLATFORM="x86_64-watchos-sim"
#PLATFORM_WATCH_SIM_X86=${PLATFORM}
#ARCH_FLAGS="-arch x86_64"
#HOST_FLAGS="${ARCH_FLAGS} -mwatchos-simulator-version-min=${MIN_WATCHOS_VERSION} -isysroot $(xcrun --sdk ${SDK} --show-sdk-path)"
#CHOST="x86_64-apple-darwin"
#Build "$@"

## Build for tvOS M1/M2/Mx simulator
#SDK="appletvsimulator"
#PLATFORM="arm64-appletvos-sim"
#PLATFORM_TV_SIM_ARM=${PLATFORM}
#ARCH_FLAGS="-arch arm64"
#HOST_FLAGS="${ARCH_FLAGS} -mappletvos-simulator-version-min=${MIN_TVOS_VERSION} -isysroot $(xcrun --sdk ${SDK} --show-sdk-path)"
#CHOST="arm-apple-darwin"
#Build "$@"
#
### Build for tvOS intel simulator
#SDK="appletvsimulator"
#PLATFORM="x86_64-appletvos-sim"
#PLATFORM_TV_SIM_X86=${PLATFORM}
#ARCH_FLAGS="-arch x86_64"
#HOST_FLAGS="${ARCH_FLAGS} -mappletvos-simulator-version-min=${MIN_TVOS_VERSION} -isysroot $(xcrun --sdk ${SDK} --show-sdk-path)"
#CHOST="x86_64-apple-darwin"
#Build "$@"




#mkdir -p "${UNIVERSAL}/arm64_x86_x64-watchossimulator" &> /dev/null
# A universal library can only have 1 of each arch inside.
# Uneeded step but iphone device library


# Uneeded step but watchos library


# Uneeded step but watchos library


# Macos universal library

# ios simulators universal library


# watchos simulators universal library
#lipo -create -output "${UNIVERSAL}/arm64_x86_x64-watchossimulator/libsecp256k1.a" "${PLATFORMS}/${PLATFORM_WATCH_SIM_ARM}/lib/libsecp256k1.a" "${PLATFORMS}/${PLATFORM_WATCH_SIM_X86}/lib/libsecp256k1.a"

## tvos simulators universal library
#lipo -create -output "${UNIVERSAL}/arm64_x86_x64-tvossimulator.a" "${PLATFORMS}/${PLATFORM_TV_SIM_ARM}/lib/libsecp256k1.a" "${PLATFORMS}/${PLATFORM_TV_SIM_X86}/lib/libsecp256k1.a"
