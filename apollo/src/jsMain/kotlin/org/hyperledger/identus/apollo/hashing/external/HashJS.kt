package org.hyperledger.identus.apollo.hashing.external

/**
 * The `hash` variable is an instance of the `Hash` interface, which provides access to various hashing algorithms such as HMAC, Ripemd, and SHA.
 * You can use this variable to perform cryptographic hashing operations.
 *
 * @property hmac An instance of the `HmacConstructor` interface, which is used to create HMAC objects for a specified hash algorithm.
 * @property ripemd An instance of the `RipemdSet` interface, which provides access to the Ripemd160 hash algorithm.
 * @property ripemd160 An instance of the `Ripemd160Constructor` interface, which is used to create Ripemd160 hash objects.
 * @property sha An instance of the `ShaSet` interface, which provides access to the SHA hash algorithms (SHA1, SHA224, SHA256, SHA384, SHA512).
 * @property sha1 An instance of the `Sha1Constructor` interface, which is used to create SHA1 hash objects.
 * @property sha224 An instance of the `Sha224Constructor` interface, which is used to create SHA224 hash objects.
 * @property sha256 An instance of the `Sha256Constructor` interface, which is used to create SHA256 hash objects.
 * @property sha384 An instance of the `Sha384Constructor` interface, which is used to create SHA384 hash objects.
 * @property sha512 An instance of the `Sha512Constructor` interface, which is used to create SHA512 hash objects.
 * @property utils An instance of the `Utils` interface, which provides utility methods for converting input and output formats of hash values.
 */
@JsModule("hash.js")
internal external val hash: Hash

/**
 * This interface represents a block hash algorithm.
 *
 * @param T the type of the block hash algorithm
 */
internal external interface BlockHash<T> {
    var hmacStrength: Number
    var padLength: Number
    var endian: String // "big" | "little"
}

/**
 * The `MessageDigest` interface represents a cryptographic hash function that can be used to digest
 * a message and produce a fixed-length output. It provides methods to update the message, retrieve
 * the digest as an array of numbers, and retrieve the digest as a hex string.
 *
 * @param T the type of the implementing class
 */
internal external interface MessageDigest<T> {
    var blockSize: Number
    var outSize: Number

    fun update(msg: Any, enc: String /* "hex" */ = definedExternally): T

    fun digest(): Array<Number>

    fun digest(enc: String /* "hex" */): String
}

/**
 * Interface representing a Hash object.
 */
internal external interface Hash {
    var hmac: HmacConstructor
    var ripemd: RipemdSet
    var ripemd160: Ripemd160Constructor
    var sha: ShaSet
    var sha1: Sha1Constructor
    var sha224: Sha224Constructor
    var sha256: Sha256Constructor
    var sha384: Sha384Constructor
    var sha512: Sha512Constructor
    var utils: Utils
}

/**
 * The Utils interface provides utility functions for converting data formats.
 */
internal external interface Utils {
    fun toArray(msg: Any, enc: String /* "hex" */): Array<Number>

    fun toHex(msg: Any): String
}

/**
 * This interface represents a set of functions related to the RIPEMD-160 hashing algorithm.
 */
internal external interface RipemdSet {
    var ripemd160: Ripemd160Constructor
}

/**
 * A set of SHA hash constructors.
 */
internal external interface ShaSet {
    var sha1: Sha1Constructor
    var sha224: Sha224Constructor
    var sha256: Sha256Constructor
    var sha384: Sha384Constructor
    var sha512: Sha512Constructor
}

/**
 * The interface HmacConstructor represents a constructor for Hmac object.
 */
internal external interface HmacConstructor {
    @nativeInvoke
    operator fun invoke(hash: BlockHash<Any>, key: Any, enc: String /* "hex" */ = definedExternally): Hmac
}

/**
 * An external interface representing the constructor for the RIPEMD-160 hash function.
 *
 * This interface allows creating instances of the `Ripemd160` interface, which is both a `BlockHash` and a `MessageDigest`.
 *
 * The `Ripemd160` interface extends the `BlockHash` interface, which provides properties for the HMAC strength, padding length, and endianess.
 *
 * The `Ripemd160` interface also extends the `MessageDigest` interface, which provides properties for the block size and output size of the hash function.
 *
 * @see Ripemd160
 * @see BlockHash
 * @see MessageDigest
 */
internal external interface Ripemd160Constructor {
    @nativeInvoke
    operator fun invoke(): Ripemd160
}

/**
 * The `Sha1Constructor` interface represents a constructor function for creating instances of the `Sha1` interface.
 */
internal external interface Sha1Constructor {
    @nativeInvoke
    operator fun invoke(): Sha1
}

/**
 * This interface represents a constructor for the SHA-224 algorithm.
 */
internal external interface Sha224Constructor {
    @nativeInvoke
    operator fun invoke(): Sha224
}

/**
 * An external interface representing a constructor for the SHA256 algorithm.
 */
internal external interface Sha256Constructor {
    @nativeInvoke
    operator fun invoke(): Sha256
}

/**
 * The Sha384Constructor interface represents a constructor function for the SHA384 interface,
 * which is an implementation of the BlockHash and MessageDigest interfaces.
 */
internal external interface Sha384Constructor {
    @nativeInvoke
    operator fun invoke(): Sha384
}

/**
 * The `Sha512Constructor` interface represents a constructor function for creating instances of the `SHA512` interface.
 */
internal external interface Sha512Constructor {
    @nativeInvoke
    operator fun invoke(): Sha512
}

/**
 * Represents an HMAC (Hash-based Message Authentication Code).
 * The HMAC class is an internal external interface that extends the MessageDigest interface.
 * It provides methods for updating the digest with input data, generating the digest, and encoding the digest as a string.
 */
internal external interface Hmac : MessageDigest<Hmac> {
    override var blockSize: Number // 512
    override var outSize: Number // 160
}

/**
 * The Ripemd160 interface represents the RIPEMD-160 hash function.
 */
internal external interface Ripemd160 : BlockHash<Ripemd160>, MessageDigest<Ripemd160> {
    override var blockSize: Number // 512
    override var hmacStrength: Number // 192
    override var outSize: Number // 160
    override var padLength: Number // 64
    override var endian: String // "little"
}

/**
 * The interface for the Sha1 class.
 * This interface extends the [BlockHash] and [MessageDigest] interfaces.
 * It provides methods and properties specific to the Sha1 algorithm.
 *
 * @see BlockHash
 * @see MessageDigest
 */
internal external interface Sha1 : BlockHash<Sha1>, MessageDigest<Sha1> {
    override var blockSize: Number // 512
    override var hmacStrength: Number // 80
    override var outSize: Number // 160
    override var padLength: Number // 64
    override var endian: String // "big"
}

/**
 * The `SHA224` class represents the SHA-224 hash algorithm. It implements the `BlockHash` and `MessageDigest` interfaces.
 *
 * @see BlockHash
 * @see MessageDigest
 */
internal external interface Sha224 : BlockHash<Sha224>, MessageDigest<Sha224> {
    override var blockSize: Number // 512
    override var hmacStrength: Number // 192
    override var outSize: Number // 224
    override var padLength: Number // 64
    override var endian: String // "big"
}

/**
 * Represents the SHA256 algorithm for hashing and message digest operations.
 */
internal external interface Sha256 : BlockHash<Sha256>, MessageDigest<Sha256> {
    override var blockSize: Number // 512
    override var hmacStrength: Number // 192
    override var outSize: Number // 256
    override var padLength: Number // 64
    override var endian: String // "big"
}

/**
 * Represents an implementation of the SHA-384 hash function.
 */
internal external interface Sha384 : BlockHash<Sha384>, MessageDigest<Sha384> {
    override var blockSize: Number // 1024
    override var hmacStrength: Number // 192
    override var outSize: Number // 384
    override var padLength: Number // 128
    override var endian: String // "big"
}

/**
 * SHA512 is an internal external interface that extends both BlockHash and MessageDigest interfaces.
 * It represents the SHA-512 hash algorithm.
 *
 * Please note that this documentation only focuses on the specific properties and methods provided by the SHA512 interface.
 *
 * @see BlockHash
 * @see MessageDigest
 */
internal external interface Sha512 : BlockHash<Sha512>, MessageDigest<Sha512> {
    override var blockSize: Number // 1024
    override var hmacStrength: Number // 192
    override var outSize: Number // 512
    override var padLength: Number // 128
    override var endian: String // "big"
}
