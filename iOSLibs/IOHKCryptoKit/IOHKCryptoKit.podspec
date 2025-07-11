#
# Be sure to run `pod lib lint IOHKSecureRandomGeneration.podspec' to ensure this is a
# valid spec before submitting.
#
# Any lines starting with a # are optional, but their use is encouraged
# To learn more about a Podspec see https://guides.cocoapods.org/syntax/podspec.html
#

Pod::Spec.new do |s|
    s.name             = 'IOHKCryptoKit'
    s.version          = '1.0.0'
    s.summary          = 'IOHKCryptoKit contains Ed25519, X25519 CryptoKit functionalities.'
    
    # This description is used to generate tags and improve search results.
    #   * Think: What does it do? Why did you write it? What is the focus?
    #   * Try to keep it short, snappy and to the point.
    #   * Write the description between the DESC delimiters below.
    #   * Finally, don't worry about the indent, CocoaPods strips it!
    
    s.description      = <<-DESC
        IOHKCryptoKit contains Ed25519, X25519 CryptoKit functionalities.
    DESC
    
    s.homepage         = 'https://github.com/hyperledger-identus/apollo'
    s.author           = { 'Gonçalo Frade' => 'goncalo.frade@iohk.io' }
    s.source           = { :git => 'https://github.com/hyperledger/identus-apollo.git', :tag => s.version.to_s }
    s.swift_version = '5.9'
    s.cocoapods_version = '>= 1.10.0'
    
    s.ios.deployment_target = '15.6'
    s.osx.deployment_target  = '13.5'
    s.tvos.deployment_target = '15.6'
    s.watchos.deployment_target = '8.0'

    s.source_files = 'IOHKCryptoKit/**/*.swift'
end
