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
           url: "https://github.com/hyperledger-identus/apollo/releases/download/v1.7.2/Apollo.xcframework.zip",
           checksum: "e956cb7e32abdb0233d5351c87e0a4bc30a06c87d9c687ba197cd8d916454e49"
       )
    ]
)
