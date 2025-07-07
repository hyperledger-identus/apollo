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
//            url: "https://github.com/hyperledger-identus/apollo/releases/download/v1.7.0/Apollo.xcframework.zip",
//            checksum: "61404fdcce61867f33b53dc4d1ad0821f67b99ec324e728fc2a621853631ddd7"
//        )
    ]
)
