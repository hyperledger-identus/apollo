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
//              path: "./apollo/build/packages/ApolloSwift/ApolloBinary.xcframework.zip"
//          ),

        // RELEASE
       .binaryTarget(
           name: "ApolloBinary",
           url: "https://github.com/hyperledger-identus/apollo/releases/download/v1.8.7/ApolloLibrary.xcframework.zip",
           checksum: "e07f2a15ab97f6a05a656a7907cdb92f95a3cfcf585c28fe761b083899e9cccf"
       )
    ]
)
