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
           url: "https://github.com/hyperledger-identus/apollo/releases/download/v2.1.0/ApolloLibrary.xcframework.zip",
           checksum: "475be7f961b47deee9323833c327f418170b3e1ddf363ed130fa00a96e3cb8f2"
       )
    ]
)
