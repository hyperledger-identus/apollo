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
         .binaryTarget(
             name: "ApolloBinary",
             path: "./apollo/build/packages/ApolloSwift/Apollo.xcframework.zip"
         ),

        // RELEASE
//        .binaryTarget(
//            name: "ApolloBinary",
//            url: "https://github.com/hyperledger-identus/apollo/releases/download/v1.6.0/Apollo.xcframework.zip",
//            checksum: "78c2dd302041d5a92707ca218b95c339ba01b07bca926db52e5ea0fc8cd4ad5c"
//        )
    ]
)
