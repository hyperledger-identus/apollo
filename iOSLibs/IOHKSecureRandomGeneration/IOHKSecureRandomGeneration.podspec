#
# Be sure to run `pod lib lint IOHKSecureRandomGeneration.podspec' to ensure this is a
# valid spec before submitting.
#
# Any lines starting with a # are optional, but their use is encouraged
# To learn more about a Podspec see https://guides.cocoapods.org/syntax/podspec.html
#

Pod::Spec.new do |s|
    s.name             = 'IOHKSecureRandomGeneration'
    s.version          = '1.0.0'
    s.summary          = 'IOHKSecureRandomGeneration contains Secure Random.'
    
    # This description is used to generate tags and improve search results.
    #   * Think: What does it do? Why did you write it? What is the focus?
    #   * Try to keep it short, snappy and to the point.
    #   * Write the description between the DESC delimiters below.
    #   * Finally, don't worry about the indent, CocoaPods strips it!
    
    s.description      = <<-DESC
    IOHKSecureRandomGeneration contains Secure Random Generation for IV random generation.
    DESC
    
    s.homepage         = 'https://github.com/input-output-hk/atala-prism-apollo'
    s.author           = { 'Ahmed Moussa' => 'moussa.ahmed95@gmail.com' }
    s.source           = { :git => 'git+https://github.com/hyperledger/identus-apollo.git', :tag => s.version.to_s }
    s.swift_version = '5.7'
    s.cocoapods_version = '>= 1.10.0'
    # s.social_media_url = 'https://twitter.com/<TWITTER_USERNAME>'
    
    s.ios.deployment_target = '13.0'
    s.osx.deployment_target  = '12.0'
    s.tvos.deployment_target = '13.0'
    s.watchos.deployment_target = '8.0'
    
    s.source_files = 'IOHKSecureRandomGeneration/**/*.swift'
    
    # s.resource_bundles = {
    #   'IOHKSecureRandomGeneration' => ['IOHKSecureRandomGeneration/Assets/*.png']
    # }
    
    # s.public_header_files = 'Pod/Classes/**/*.h'
    # s.frameworks = 'UIKit', 'MapKit'
    # s.dependency 'AFNetworking', '~> 2.3'
end
