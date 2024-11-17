package edu.grinnell.csc207.blockchains;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 * Blocks to be stored in blockchains.
 *
 * @author Tiffany Tang
 * @author Andrew Fargo
 * @author Samuel A. Rebelsky
 */
public class Block {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  private static MessageDigest md;
  private static Random rd = new Random();

  static {
    try {
      md = MessageDigest.getInstance("sha-256");
    } catch (NoSuchAlgorithmException e) {
      // Algorithm not supported
      throw new RuntimeException(e);
    } // try/catch
  } // static

  private int blockNum;
  private Transaction data;
  private Hash previousHash;
  private long nonceVal;
  private Hash blockHash;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Common constructor code between public methods of construction.
   * Does not guarantee that the block is valid.
   *
   * @param num The number of this block on the chain.
   * @param transaction The transaction data.
   * @param prevHash The previous block's hash.
   */
  private Block(int num, Transaction transaction, Hash prevHash) {
    this.blockNum = num;
    this.data = transaction;
    this.previousHash = prevHash;
  } // Block(int, Transaction, Hash)
  
  /**
   * Create a new block from the specified block number, transaction, and
   * previous hash, mining to choose a nonce that meets the requirements
   * of the validator.
   *
   * @param num
   *   The number of the block.
   * @param transaction
   *   The transaction for the block.
   * @param prevHash
   *   The hash of the previous block.
   * @param check
   *   The validator used to check the block.
   */
  Block(int num, Transaction transaction, Hash prevHash, HashValidator check) {
    this(num, transaction, prevHash);
    this.mine(check);
  } // Block(int, Transaction, Hash, HashValidator)

  /**
   * Create a new block, computing the hash for the block.
   *
   * @param num
   *   The number of the block.
   * @param transaction
   *   The transaction for the block.
   * @param prevHash
   *   The hash of the previous block.
   * @param nonce
   *   The nonce of the block.
   */
  Block(int num, Transaction transaction, Hash prevHash, long nonce) {
    this(num, transaction, prevHash);
    this.nonceVal = nonce;
    this.computeHash();
  } // Block(int, Transaction, Hash, long)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Compute a new nonce by repeatedly checking random values.
   */
  private void mine(HashValidator check) {
    do {
      this.nonceVal = rd.nextLong();
      this.computeHash();
    } while (check.isValid(this.blockHash));
  } // mine()
  
  /**
   * Compute the hash of the block given all the other info already
   * stored in the block.
   */
  private void computeHash() {
    md.reset();
    md.update(ByteBuffer.allocate(Integer.BYTES).putInt(this.blockNum));
    md.update(ByteBuffer.allocate(Integer.BYTES).putInt(this.data.hashcode()));
    md.update(this.prevHash.getBytes());
    md.update(ByteBuffer.allocate(Long.BYTES).putLong(this.nonceVal));
    this.blockHash = new Hash(md.digest());
  } // computeHash()

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Get the number of the block.
   *
   * @return the number of the block.
   */
  public int getNum() {
    return this.blockNum;
  } // getNum()

  /**
   * Get the transaction stored in this block.
   *
   * @return the transaction.
   */
  public Transaction getTransaction() {
    return new Transaction("Here", "There", 0); // STUB
  } // getTransaction()

  /**
   * Get the nonce of this block.
   *
   * @return the nonce.
   */
  public long getNonce() {
    return this.nonce;
  } // getNonce()

  /**
   * Get the hash of the previous block.
   *
   * @return the hash of the previous block.
   */
  Hash getPrevHash() {
    return this.prevHash;
  } // getPrevHash

  /**
   * Get the hash of the current block.
   *
   * @return the hash of the current block.
   */
  Hash getHash() {
    return this.blockHash;
  } // getHash

  /**
   * Get a string representation of the block.
   *
   * @return a string representation of the block.
   */
  public String toString() {
    return "";  // STUB
  } // toString()
} // class Block
