#!/usr/bin/env bash
set -e

# Get target platforms from command line arguments
TARGET_PLATFORMS="$@"

cp xconfigure.sh secp256k1

cd secp256k1

./autogen.sh
TARGET_PLATFORMS="${TARGET_PLATFORMS}" sh xconfigure.sh --enable-experimental --enable-module_ecdh --enable-module-recovery --enable-module-schnorrsig --enable-benchmark=no --enable-shared=no --enable-exhaustive-tests=no --enable-tests=no

mkdir -p ../build/ios
cp -R _build/universal/* ../build/ios/