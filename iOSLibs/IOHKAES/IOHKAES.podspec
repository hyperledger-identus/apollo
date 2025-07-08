#
# Be sure to run `pod lib lint IOHKAES.podspec' to ensure this is a
# valid spec before submitting.
#
# Any lines starting with a # are optional, but their use is encouraged
# To learn more about a Podspec see https://guides.cocoapods.org/syntax/podspec.html
#

Pod::Spec.new do |s|
    s.name             = 'IOHKAES'
    s.version          = '1.0.0'
    s.summary          = 'IOHKAES contains AES for KMM'
    
    # This description is used to generate tags and improve search results.
    #   * Think: What does it do? Why did you write it? What is the focus?
    #   * Try to keep it short, snappy and to the point.
    #   * Write the description between the DESC delimiters below.
    #   * Finally, don't worry about the indent, CocoaPods strips it!
    
    s.description      = <<-DESC
    IOHKAES contains AES Encryption & Decryption
    DESC

    s.license          = 'MIT'
    s.homepage         = 'https://github.com/hyperledger-identus/apollo'
    s.author           = { 'Ahmed Moussa' => 'moussa.ahmed95@gmail.com' }
    s.source           = { :git => 'https://github.com/hyperledger/identus-apollo.git', :tag => s.version.to_s }
    s.swift_version = '5.9'
    s.cocoapods_version = '>= 1.10.0'
    # s.social_media_url = 'https://twitter.com/<TWITTER_USERNAME>'
    
    s.ios.deployment_target = '15.6'
    s.osx.deployment_target  = '13.5'
    s.tvos.deployment_target = '15.6'
    s.watchos.deployment_target = '8.0'

    s.source_files = 'IOHKAES/**/*.swift'
    
    # s.resource_bundles = {
    #   'IOHKAES' => ['IOHKAES/Assets/*.png']
    # }
    
    # s.public_header_files = 'Pod/Classes/**/*.h'
    # s.frameworks = 'UIKit', 'MapKit'
    # s.dependency 'AFNetworking', '~> 2.3'
end
