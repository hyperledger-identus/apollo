//
//  IOHKRSA.swift
//  IOHKRSA
//
//  Created by Ahmed Moussa on 15/12/2022.
//

import Foundation
import CryptoKit

@objc
public class IOHKRSA: NSObject {
    private override init() {}
    
    @objc
    public class func signRSA(key: SecKey, type: RSASignatureMessageType, data: Data) -> Data? {
        let signer = RSASigner(key: key, type: type)
        return signer.sign(data: data)
    }
    
    @objc
    public class func verifyRSA(key: SecKey, type: RSASignatureMessageType, data: Data, signedData: Data) -> Bool {
        let signer = RSASigner(key: key, type: type)
        return signer.verify(data: data, signedData: signedData)
    }
    
    @objc
    public class func generateKeyPair(keySize: RSAKeySize) -> KeyPair? {
        let tag = "org.hyperledger.identus.apollo.keys".data(using: .utf8)!
        let attributes: [String : Any] = [
            kSecAttrKeyType as String: kSecAttrKeyTypeRSA,
            kSecAttrKeySizeInBits as String: keySize.rawValue as CFNumber,
            kSecPrivateKeyAttrs as String: [
                kSecAttrIsPermanent as String: false,
                kSecAttrApplicationTag as String: tag
            ]
        ]
        var error: Unmanaged<CFError>?
        guard let privateKey = SecKeyCreateRandomKey(attributes as CFDictionary, &error) else {
            return nil
        }
        guard let publicKey = SecKeyCopyPublicKey(privateKey) else {
            return nil
        }
        return KeyPair(privateKey: privateKey, publicKey: publicKey)
    }

    @objc
    public class func generatePublicKeyFrom(privateKey: SecKey) -> SecKey? {
        guard let publicKey = SecKeyCopyPublicKey(privateKey) else {
            return nil
        }
        return publicKey
    }

    @objc
    public class func exportKeyToBase64(_ key: SecKey) -> String? {
        var error: Unmanaged<CFError>?
        let base64Key: String
        if let cfdata = SecKeyCopyExternalRepresentation(key, &error) {
            let data: Data = cfdata as Data
            base64Key = data.base64EncodedString()
        } else {
            return nil
        }
        return base64Key
    }

    @objc
    public class func importPrivateKeyFrom(base64Key: String, keySize: RSAKeySize) -> SecKey? {
        guard let keyData = Data(base64Encoded: base64Key) else {
            return nil
        }

        let keyDict: [NSObject : NSObject] = [
            kSecAttrKeyType: kSecAttrKeyTypeRSA,
            kSecAttrKeyClass: kSecAttrKeyClassPrivate,
            kSecAttrKeySizeInBits: keySize.rawValue as CFNumber,
            kSecReturnPersistentRef: true as NSObject
        ]

        guard let key = SecKeyCreateWithData(keyData as CFData, keyDict as CFDictionary, nil) else {
            return nil
        }
        return key
    }
}
