Pod::Spec.new do |spec|
  spec.name         = 'ApolloLibrary'
  spec.version      = '1.7.2'
  spec.summary      = 'Apollo XCFramework distributed as a CocoaPod.'
  spec.homepage     = 'https://github.com/hyperledger-identus/apollo'
  spec.license      = { :type => 'MIT', :file => 'LICENSE' }
  spec.author       = 'Hyperledger Identus'
  spec.source       = { :http => 'https://github.com/hyperledger-identus/apollo/releases/download/v1.7.2/ApolloLibrary.xcframework.zip' }
  spec.vendored_frameworks = 'ApolloLibrary.xcframework'
  spec.platform     = :ios, '13.0'
  spec.ios.deployment_target = '13.0'
  spec.osx.deployment_target = '11.0'
end
