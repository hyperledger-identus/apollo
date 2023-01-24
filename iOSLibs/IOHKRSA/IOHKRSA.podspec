#
# Be sure to run `pod lib lint IOHKRSA.podspec' to ensure this is a
# valid spec before submitting.
#
# Any lines starting with a # are optional, but their use is encouraged
# To learn more about a Podspec see https://guides.cocoapods.org/syntax/podspec.html
#

Pod::Spec.new do |s|
    s.name             = 'IOHKRSA'
    s.version          = '1.0.0'
    s.summary          = 'IOHKRSA contains RSA signature for KMM'
    
    # This description is used to generate tags and improve search results.
    #   * Think: What does it do? Why did you write it? What is the focus?
    #   * Try to keep it short, snappy and to the point.
    #   * Write the description between the DESC delimiters below.
    #   * Finally, don't worry about the indent, CocoaPods strips it!
    
    s.description      = <<-DESC
    IOHKRSA contains RSA signature for KMM
    DESC

    s.license          = 'MIT'
    s.homepage         = 'https://github.com/input-output-hk/atala-prism-apollo'
    s.author           = { 'Ahmed Moussa' => 'moussa.ahmed95@gmail.com' }
    s.source           = { :git => 'https://github.com/input-output-hk/atala-prism-apollo.git', :tag => s.version.to_s }
    s.swift_version = '5.7'
    s.cocoapods_version = '>= 1.10.0'
    # s.social_media_url = 'https://twitter.com/<TWITTER_USERNAME>'
    
    s.ios.deployment_target = '13.0'
    s.osx.deployment_target  = '12.0'
    s.tvos.deployment_target = '13.0'
    s.watchos.deployment_target = '8.0'
    
    s.source_files = 'IOHKRSA/**/*.swift'
    
    # s.resource_bundles = {
    #   'IOHKRSA' => ['IOHKRSA/Assets/*.png']
    # }
    
    # s.public_header_files = 'Pod/Classes/**/*.h'
    # s.frameworks = 'UIKit', 'MapKit'
    # s.dependency 'AFNetworking', '~> 2.3'
end
