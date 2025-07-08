//
//  AES256Tests.swift
//  IOHKRSATests
//
//  Created by Ahmed Moussa on 13/10/2022.
//

import XCTest
@testable import IOHKRSA
import CommonCrypto

class IOHKRSATests: XCTestCase {
    
    private let base64Privatekey = "MIIEowIBAAKCAQEAxWM6u/d5Ha+wl+jyJd9XKAQxSI7ea3tILnKxnh37w3OkkhH+qg2E+A4dDpPcFStqPutfcHk9X3+LtlLW5vJ+t2l6Ty71m9giN/lkJ8LOJ3i0VOzElqU7T7AXoPE81oQW/OXCCQA1BzhddRPhXQiUrwNbHv7eBs6YSk5suVAQvyAbRSnTcnyYhjr/q9sllrZX5fgbTyyutpTBC7S73ZeILd6yTx4yyPvS8kiTIUVmc5U45UJr6UUOhEB1uIRVZRuP1mCyyaUoAAoGWBetGXIFCxszAc3lZPsx8lIVJJyLJEwyf2MeMi5V2RZOcn9ISBh8/pgVZ7xv3bkh/rNQ8TX/2wIDAQABAoIBAF5nqbE52L8Ghtb47kuHyKcWbj3OOvm8AldYurOtknq2Bglouty5T5XWJjaRKEslsxB5wXFJQz24AmnGsArQAyQUug9CHe0WD7Omvyy3IqNFOzZ88T89sqLXzU1H47mTfm0hNE7avwH/hs9WnUxCLN6Ro9SswCMQT00GukBdV00ZJW6BbZn2/5eyefD2HTmprc8jVbJTFxTJmZSO2ESH8Wxs7221WOXOz1FQpGcFolsn5K9iHCKNZPiIDgFvTx1cVdgbXvPhcAduz9zGjfCdAb6oi6UM3UM7xGLljHBh5VVvYOtj64+2026bbK1xAv4wIS1SLKSBd2/y9fTESt7kED0CgYEA+B8jL/zGrGQoNb4geIlGZaazkJFd7xL+DE0bKlGobx4412kdFr/fcTLwj9RWTYJKHdipIDEblu9xlv/452+21aVJ6f3yGqAtj5DKC40s2aPAknAPDRBY6eJ2eyDxJkn8f6gZZEhhcUDqa5AbJhSLMx7Vy2l9Y1uHrqnFSdmUMV8CgYEAy6ezB2qMmGqp75J959i9EgHXHRxFuf4JN35bQvleuZDmkePH08hDzvaysVLvx8gdqw2F/PX0ntJaQtPcmIRZqbwwS2Bl90VNa2BSia+PwSdo6uML/JrZFDscxRXNYWMbmb6nEyXruStfNAPsruoIbqKvYMEC0+XJPIw3YIsOlwUCgYEAuAlQbi2Npxx6/rHRH9aiUo99LMPF6qgpqzId7hu3VQQToSMkIlWbBDmGWQhkIwV6t/yWJXyHPELJ+/hMxE0GTh1VX6uuQbWBVbVNR5glJiDhmzxmzYfMi14gB1/9viqukHk8rDdLn1dJCRNQL8SZIwfCCChsip+/Wfwui4JI+v0CgYAETpWgsIYsqwTRBj4XayQEf3l3KiPnXRIA1HFFp1RzhDb8Gt8dAX09oUYCLgjYJslJ1s1gnisAON6jzys5JoNm7Y3v6if95L9QaAcW1lmp8hVr8kVMoFG69c0T9fQT/S6WP8v0wbaMgfhPd03IHTb0cAhZeXt/07o/x49cQVSbqQKBgEVEmapcbETWGElj+5fXRMDpFok64ihIotAvT2AlkficO+23+EXNHrYCrXTqHBOqtT+Ajlkv1N/JSF3d7hrQUwRBZwy5V7Cl5lvPgpvwgwVottXFCCJCOar072V00DQn7/lv691+TV7PReReAjhMte8M9OufE8xZWl2OxRWdUMtq"
    
    var privateKey: SecKey!
    var publicKey: SecKey!
    
    override func setUp() {
        super.setUp()
        self.privateKey = try! importPrivateKeyFrom(base64Key: base64Privatekey)
        self.publicKey = try! generatePublicKeyFrom(privateKey: self.privateKey)
    }
    
    // MARK: - RSA SHA256
    func test_signRSASHA256() {
        let valueShouldBeBase64 = "BI4BnTHXumF0iOeahFIuYBavBm8sTEGxpNlaYhhthcNwx6/Au6ZfPX8tqAfsYjNTpZFJKsseLXWlpbYRcHLg29ccldof5smzk23AD3t4AtS/VVGRsezVVqMudSZ2/qXev6tEe6hRJSgFXa2yCuCYQLK+qHoJNtV/Q2OzJ2auR3gunAGPPNJFbpdzrVdS18SFWPnmQT9Sp81hTMhM2Oz05rB7yA9y9gOmTT4/pu1VNiv7CRrYZh9AP0PgvrU86v4fr7JYeEj5CYivNkkdxNZXE6jGMvvh6yCwglPWyvSIiJ92xQgMBe3hqJXuKXY/+u7Bg2PEDKRfPYtjy4IxgKBT6g=="
        let valueShouldBeData = valueShouldBeBase64.data(using: .utf8)!
        let valueShouldBe = Data(base64Encoded: valueShouldBeData)!
        
        let strToSign = "Hello IOG!"
        let dataToSign = strToSign.data(using: .utf8)!
        let signer = RSASigner(key: privateKey, type: .rsaSHA256)
        let signature = signer.sign(data: dataToSign)
        
        XCTAssertTrue(signature == valueShouldBe)
    }
    
    func test_verifyRSASHA256() {
        let valueShouldBeBase64 = "BI4BnTHXumF0iOeahFIuYBavBm8sTEGxpNlaYhhthcNwx6/Au6ZfPX8tqAfsYjNTpZFJKsseLXWlpbYRcHLg29ccldof5smzk23AD3t4AtS/VVGRsezVVqMudSZ2/qXev6tEe6hRJSgFXa2yCuCYQLK+qHoJNtV/Q2OzJ2auR3gunAGPPNJFbpdzrVdS18SFWPnmQT9Sp81hTMhM2Oz05rB7yA9y9gOmTT4/pu1VNiv7CRrYZh9AP0PgvrU86v4fr7JYeEj5CYivNkkdxNZXE6jGMvvh6yCwglPWyvSIiJ92xQgMBe3hqJXuKXY/+u7Bg2PEDKRfPYtjy4IxgKBT6g=="
        let valueShouldBeData = valueShouldBeBase64.data(using: .utf8)!
        let valueShouldBe = Data(base64Encoded: valueShouldBeData)!
        
        let strToSign = "Hello IOG!"
        let dataToSign = strToSign.data(using: .utf8)!
        let signer = RSASigner(key: publicKey, type: .rsaSHA256)
        
        XCTAssertTrue(signer.verify(data: dataToSign, signedData: valueShouldBe))
    }
    
    // MARK: - RSA/PSS SHA256
    // Note: RSA PSS variation will make the output different each time even after using the same keypair
    func test_signAndVerifyRSAPSSSHA256() {
        let strToSign = "Hello IOG!"
        let dataToSign = strToSign.data(using: .utf8)!
        
        let signer = RSASigner(key: privateKey, type: .rsaPSSSHA256)
        let verifier = RSASigner(key: publicKey, type: .rsaPSSSHA256)
        
        let signature = signer.sign(data: dataToSign)
    
        XCTAssertTrue(verifier.verify(data: dataToSign, signedData: signature!))
    }
    
    func test_verifyRSAPSSSHA256() {
        let signatureBase64 = "Z9gXjwmfzetTPalqe6uLMVJJ0tDkzkbZFvcRl3qVLF5oGQVz2T3/l1fNwr4tB9AQhPfQwGq7Iic+V6oKe+fMjsgIS+zxaKrcnNh6pQI4q+yPkvmNtmZAR6JLuLhid3HH330vFpgpmjf3GPFsaaT98E7gRRd9xtvsqSzEACQbJPBG122E6GjO7F9IeDcvtlgd8N38CND8pN7ff7O/K145YcpnXqc+A1DzCjYJcnfDn4GYWlX9pqHyS4GsE8tun+/aX5wOLiBX75wVRZFdZE8JGDt6E6CLHIBTVydxIWkITA7ncl7nYLnWpxaz+CVhJD6L7fu6gsi3CMJ1JPlI+WOdOQ=="
        let signatureData = Data(base64Encoded: signatureBase64)!
        
        let strToSign = "Hello IOG!"
        let dataToSign = strToSign.data(using: .utf8)!
        
        let verifier = RSASigner(key: publicKey, type: .rsaPSSSHA256)
        
        XCTAssertTrue(verifier.verify(data: dataToSign, signedData: signatureData))
    }
    
    // MARK: - Helper methods
    private func generateKeyPair() throws -> (privateKey: SecKey, publicKey: SecKey) {
        let tag = "org.hyperledger.identus.apollo.keys".data(using: .utf8)!
        let attributes: [String: Any] = [
            kSecAttrKeyType as String: kSecAttrKeyTypeRSA,
            kSecAttrKeySizeInBits as String: 2048 as CFNumber,
            kSecPrivateKeyAttrs as String: [
                kSecAttrIsPermanent as String: false,
                kSecAttrApplicationTag as String: tag
            ]
        ]
        var error: Unmanaged<CFError>?
        guard let privateKey = SecKeyCreateRandomKey(attributes as CFDictionary, &error) else {
            print(error!.takeRetainedValue())
            XCTFail("Failed to get private key")
            throw Error.keyGenerationFaild
        }
        guard let publicKey = SecKeyCopyPublicKey(privateKey) else {
            XCTFail("Failed to get public key")
            throw Error.keyGenerationFaild
        }
        return (privateKey, publicKey)
    }
    
    private func generatePublicKeyFrom(privateKey: SecKey) throws -> SecKey {
        guard let publicKey = SecKeyCopyPublicKey(privateKey) else {
            XCTFail("Failed to get public key")
            throw Error.keyGenerationFaild
        }
        return publicKey
    }
    
    private func exportKeyToBase64(_ key: SecKey) throws -> String {
        var error: Unmanaged<CFError>?
        let base64Key: String
        if let cfdata = SecKeyCopyExternalRepresentation(key, &error) {
            let data: Data = cfdata as Data
            base64Key = data.base64EncodedString()
        } else {
            throw Error.exportKeyFailed
        }
        return base64Key
    }
    
    private func importPrivateKeyFrom(base64Key: String) throws -> SecKey {
        guard let keyData = Data(base64Encoded: base64Key) else {
            throw Error.importKeyFailed
        }

        let keyDict:[NSObject:NSObject] = [
            kSecAttrKeyType: kSecAttrKeyTypeRSA,
            kSecAttrKeyClass: kSecAttrKeyClassPrivate,
            kSecAttrKeySizeInBits: 2048 as CFNumber,
            kSecReturnPersistentRef: true as NSObject
        ]

        guard let key = SecKeyCreateWithData(keyData as CFData, keyDict as CFDictionary, nil) else {
            throw Error.importKeyFailed
        }
        return key
    }
    
    private func importPublicKey(_ base64Key: String) throws -> SecKey {
        guard let keyData = Data(base64Encoded: base64Key) else {
            throw Error.importKeyFailed
        }

        let keyDict:[NSObject:NSObject] = [
            kSecAttrKeyType: kSecAttrKeyTypeRSA,
            kSecAttrKeyClass: kSecAttrKeyClassPublic,
            kSecAttrKeySizeInBits: 2048 as CFNumber,
            kSecReturnPersistentRef: true as NSObject
        ]

        guard let key = SecKeyCreateWithData(keyData as CFData, keyDict as CFDictionary, nil) else {
            throw Error.importKeyFailed
        }
        return key
    }
}
