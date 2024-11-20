package edu.grinnell.csc207.blockchains;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.HashSet;


/**
 * A full blockchain.
 *
 * @author Your Name Here
 */
public class BlockChain implements Iterable<Transaction> {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  BlockNode first;
  BlockNode last;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new blockchain using a validator to check elements.
   *
   * @param check
   *   The validator used to check elements.
   */
  public BlockChain(HashValidator check) {
    // STUB
  } // BlockChain(HashValidator)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  // We may consider adding a Hashmap<String, Integer> for user/balance methods?
  // Not sure if it would complicate things, maybe we could add the map
  // as a field and return a boolean if something goes wrong...
  // I'd need to sketch it out. - Andrew
  
  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Mine for a new valid block for the end of the chain, returning that
   * block.
   *
   * @param t
   *   The transaction that goes in the block.
   *
   * @return a new block with correct number, hashes, and such.
   */
  public Block mine(Transaction t) {
    return new Block(10, t, new Hash(new byte[] {7}), 11);       // STUB
  } // mine(Transaction)

  /**
   * Get the number of blocks curently in the chain.
   *
   * @return the number of blocks in the chain, including the initial block.
   */
  public int getSize() {
    return 2;   // STUB
  } // getSize()

  /**
   * Add a block to the end of the chain.
   *
   * @param blk
   *   The block to add to the end of the chain.
   *
   * @throws IllegalArgumentException if (a) the hash is not valid, (b)
   *   the hash is not appropriate for the contents, or (c) the previous
   *   hash is incorrect.
   */
  public void append(Block blk) {
    // STUB
  } // append()

  /**
   * Attempt to remove the last block from the chain.
   *
   * @return false if the chain has only one block (in which case it's
   *   not removed) or true otherwise (in which case the last block
   *   is removed).
   */
  public boolean removeLast() {
    return true;        // STUB
  } // removeLast()

  /**
   * Get the hash of the last block in the chain.
   *
   * @return the hash of the last sblock in the chain.
   */
  public Hash getHash() {
    return new Hash(new byte[] {2, 0, 7});   // STUB
  } // getHash()

  /**
   * Determine if the blockchain is correct in that (a) the balances are
   * legal/correct at every step, (b) that every block has a correct
   * previous hash field, (c) that every block has a hash that is correct
   * for its contents, and (d) that every block has a valid hash.
   *
   * @return true if the blockchain is correct and false otherwise.
   */
  public boolean isCorrect() {
    return true;        // STUB
  } // isCorrect()

  /**
   * Determine if the blockchain is correct in that (a) the balances are
   * legal/correct at every step, (b) that every block has a correct
   * previous hash field, (c) that every block has a hash that is correct
   * for its contents, and (d) that every block has a valid hash.
   *
   * @throws Exception
   *   If things are wrong at any block.
   */
  public void check() throws Exception {
    // STUB
  } // check()

  /**
   * Return an iterator of all the people who participated in the
   * system.
   *
   * @return an iterator of all the people in the system.
   */
  public Iterator<String> users() {
    HashSet<String> users;
    this.iterator().forEachRemaining((t) -> {
	if (!t.getSource().equals("")) {
	  users.add(t.getSource());
	} // if
	users.add(t.getTarget());
      });
    return users.iterator();
  } // users()

  /**
   * Find one user's balance.
   *
   * @param user
   *   The user whose balance we want to find.
   *
   * @return that user's balance (or 0, if the user is not in the system).
   */
  public int balance(String user) {
    return 0;   // STUB
  } // balance()

  /**
   * Get an interator for all the blocks in the chain.
   *
   * @return an iterator for all the blocks in the chain.
   */
  public Iterator<Block> blocks() {
    return new Iterator<Block>() {
      /** Tracker for the next node to visit. */
      private BlockNode nextNode = BlockChain.this.first;

      /**
       * Sees if there are any nodes left to give.
       */
      public boolean hasNext() {
        return nextNode != null;
      } // hasNext()

      /**
       * Returns the next node's block.
       * @return The next block.
       */
      public Block next() {
        Block ret = this.nextNode.data;
	if (ret == null) {
	  throw new NoSuchElementException();
	} // if
	this.nextNode = this.nextNode.next;
	return ret;
      } // next()
    };
  } // blocks()

  /**
   * Get an interator for all the transactions in the chain.
   *
   * @return an iterator for all the blocks in the chain.
   */
  public Iterator<Transaction> iterator() {
    return new Iterator<Transaction>() {
      /** The underlying block iterator. */
      private Iterator<Block> blockIterator;

      // FIXME This may be required, but may throw a style error.
      {
	this.blockIterator = BlockChain.this.blocks();
      }

      /**
       * Do we have another block to get a transaction from?
       * @return true if so, false if not.
       */
      public boolean hasNext() {
	return this.blockIterator.hasNext();
      } // hasNext()

      /**
       * Get the next transaction.
       * @return The next transaction.
       */
      public Transaction next() {
	Block ret = this.blockIterator.next();
	if (ret == null) {
	  throw new NoSuchElementException();
	} // if
	return ret.getTransaction();
      } // next()
    };
  } // iterator()

} // class BlockChain
