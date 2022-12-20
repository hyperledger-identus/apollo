//
//  AESEncryptor.swift
//  IOHKAES
//
//  Created by Ahmed Moussa on 14/10/2022.
//

import Foundation
import CommonCrypto
import CryptoKit

public class AESEncryptor: AESBase, Encryptor {
    ///
    /// Context obtained. True if we have it, false otherwise.
    ///
    private var haveContext: Bool = false
    
    public private (set) var algorithm: AESAlgorithm
    public private (set) var options: AESOptions
    public private (set) var mode: BlockMode
    public private (set) var key: [UInt8]
    public private (set) var iv: UnsafePointer<UInt8>
    public private (set) var keyData: Data = Data()
    public private (set) var ivData: Data = Data()
    public private (set) var padding: Padding
    
    ///
    /// Cleanup
    ///
    deinit {
        // Ensure we've got a context before attempting to get rid of it...
        if haveContext == false {
            return
        }
        
        // Ensure we've got a context before attempting to get rid of it...
        if context.pointee == nil {
            return
        }
        
        let rawStatus = CCCryptorRelease(context.pointee)
        guard rawStatus == kCCSuccess else { fatalError("CCCryptorUpdate returned unexpected status.") }
        
        context.deallocate()
        haveContext = false
    }
    
    private init(algorithm: AESAlgorithm, options: AESOptions, mode: BlockMode, padding: Padding, key: [UInt8], keyLength: Int, iv: UnsafePointer<UInt8>, ivLength: Int) throws {
        guard algorithm.isValidKeySize(keySize: keyLength) else {
            throw Error.invalidKeySize
        }
        guard options == .ecbMode || ivLength == algorithm.blockSize else {
            throw Error.invalidIVSizeOrLength
        }
        
        self.algorithm = algorithm
        self.options = options
        self.mode = mode
        self.key = key
        self.iv = iv
        self.padding = padding
        
        super.init()
        
        if self.mode != .gcm {
            let rawStatus = CCCryptorCreateWithMode(
                CCOperation(kCCEncrypt),
                mode.rawValue,
                algorithm.nativeValue(),
                padding.nativeValue(),
                iv,
                key,
                keyLength,
                nil,
                0,
                0,
                options.nativeValue,
                context
            )
            
            guard rawStatus == kCCSuccess else { throw Error.cryptoFailed(status: rawStatus) }
            self.haveContext = true
        }
    }
    
    public convenience init(algorithm: AESAlgorithm, options: AESOptions, mode: BlockMode, padding: Padding, key: Data, iv: Data) throws {
        guard let paddedKeySize = algorithm.paddedKeySize(keySize: key.count) else {
            throw Error.invalidKeySize
        }
        
        let keyBytes = [UInt8](key)
        let ivBytes = [UInt8](iv)
        
        try self.init(
            algorithm: algorithm,
            options: options,
            mode: mode,
            padding: padding,
            key: AESBase.zeroPad(byteArray: keyBytes, blockSize: paddedKeySize),
            keyLength: paddedKeySize,
            iv: ivBytes,
            ivLength: iv.count
        )
        
        self.keyData = key
        self.ivData = iv
    }
    
    // MARK: - Encryptor
    public func encrypt(str: String, encoding: String.Encoding = .utf8) -> String? {
        switch mode {
        case .ecb, .cbc, .cfb, .ctr, .ofb, .rc4, .cfb8:
            update(string: str)
            if let result = final() {
                accumulator = []
                return Data(bytes: result, count: result.count).base64EncodedString()
            } else {
                return nil
            }
        case .gcm:
            if let strData = str.data(using: encoding) {
                if let encryptedData = encryptAESGCM(input: strData) {
                    return String(data: encryptedData, encoding: encoding) //Utilities.hexString(from: encryptedData)
                } else {
                    return nil
                }
            } else {
                return nil
            }
        }
    }
    
    public func encrypt(str: String, encoding: String.Encoding = .utf8) -> Data? {
        switch mode {
        case .ecb, .cbc, .cfb, .ctr, .ofb, .rc4, .cfb8:
            update(string: str)
            if let result = final() {
                accumulator = []
                return Data(bytes: result, count: result.count)
            } else {
                return nil
            }
        case .gcm:
            if let strData = str.data(using: encoding) {
                return encryptAESGCM(input: strData)
            } else {
                return nil
            }
        }
    }
    
    public func encrypt(data: Data) -> Data? {
        switch mode {
        case .ecb, .cbc, .cfb, .ctr, .ofb, .rc4, .cfb8:
            update(data: data)
            if let result = final() {
                accumulator = []
                return Data(bytes: result, count: result.count)
            } else {
                return nil
            }
        case .gcm:
            return encryptAESGCM(input: data)
        }
    }
    
    private func encryptAESGCM(input: Data) -> Data? {
        do {
            accumulator = []
            let key = SymmetricKey(data: keyData)
            let sealedBox = try AES.GCM.seal(input, using: key, nonce: AES.GCM.Nonce(data: ivData))
            return sealedBox.ciphertext + sealedBox.tag
        } catch {
            return nil
        }
    }
    
    // MARK: - Obj-c Helper function
    public func encrypt(str: String) -> String? {
        return encrypt(str: str, encoding: .utf8)
    }
}
