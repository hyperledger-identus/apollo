// swift-tools-version:5.7
import PackageDescription

let package = Package(
    name: "ApolloLibrary",
    platforms: [
        .iOS(.v13),
        .macOS(.v11)
    ],
    products: [
        .library(
            name: "ApolloLibrary",
            targets: ["ApolloBinary"]
        ),
    ],

    targets: [
        // LOCAL
//          .binaryTarget(
//              name: "ApolloBinary",
//              path: "./apollo/build/packages/ApolloSwift/Apollo.xcframework.zip"
//          ),

        // RELEASE
       .binaryTarget(
           name: "ApolloBinary",
           url: "https://github.com/hyperledger-identus/apollo/releases/download/v1.7.1/Apollo.xcframework.zip",
           checksum: "0ce47f2c72a9561abcb9c9edd43a5442731938d48e99a6032fea9ae466795e0b"
       )
    ]
)
