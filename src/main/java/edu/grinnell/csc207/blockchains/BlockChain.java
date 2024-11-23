package edu.grinnell.csc207.blockchains;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.HashSet;
import java.util.HashMap;


/**
 * A full blockchain.
 *
 * @author Tiffany Tang
 * @author Andrew Fargo
 */
public class BlockChain implements Iterable<Transaction> {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  BlockNode first;
  BlockNode last;
  int size;
  HashValidator validator;

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
    this.size = 0; 
    this.first = null;
    this.validator = check;
    Transaction firstT = new Transaction("", "", 0);
    Hash firstHash = new Hash(new byte[0]);
    Block firstBlock =  new Block(this.size, firstT, firstHash, check);
    this.first = new BlockNode(firstBlock, null);
    this.last = this.first;
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
    return new Block(this.size, t, last.data.getHash(), this.validator );
  } // mine(Transaction)

  /**
   * Get the number of blocks curently in the chain.
   *
   * @return the number of blocks in the chain, including the initial block.
   */
  public int getSize() {
    return this.size;
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
    BlockNode newLast = new BlockNode(blk, null);
      this.last.next = newLast;
      this.last = newLast;
      this.size++;
  } // append()

  /**
   * Attempt to remove the last block from the chain.
   *
   * @return false if the chain has only one block (in which case it's
   *   not removed) or true otherwise (in which case the last block
   *   is removed).
   */
  public boolean removeLast() {
    if(this.size == 1) {
      return false;
    } //if
    else {
      // Get second to last.
      BlockNode current = this.first;
      while (current.next != this.last) {
        current = current.next;
      } // while
      this.last = current;
      this.last.next = null;
      this.size--;
      return true;
    } //else
  } // removeLast()

  /**
   * Get the hash of the last block in the chain.
   *
   * @return the hash of the last sblock in the chain.
   */
  public Hash getHash() {
    return this.last.data.getHash();
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
    try {
      check();
    } catch (Exception e) {
      return false;
    } //try-catch
    return true;
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
  balances();
  } // check()


  /**
   * Return an iterator of all the people who participated in the
   * system.
   *
   * @return an iterator of all the people in the system.
   */
  public Iterator<String> users() {
    HashSet<String> users = new HashSet<String>();
    this.iterator().forEachRemaining((t) -> {
	if (!t.getSource().equals("")) {
	  users.add(t.getSource());
	} // if
	users.add(t.getTarget());
      });
    return users.iterator();
  } // users()


 /**
  * Alter the balance differently based on if it is a deposit or a transfer.
  *
  * @param balances
  * @param person
  * @param amount
  * @param isSource
  * @param num
  * @throws Exception
  */
  private void alterAmount(HashMap<String, Integer> balances, 
  String person, Integer amount, boolean isSource, int num) throws Exception {
    Integer balance = balances.get(person);
    if (balance == null) {
      if (isSource) {
        throw new Exception("Unknown source in block" + num + ": " + person);
      } //if
      else {
        balance = 0;
      } //else
    } // if
    balance = isSource ? balance - amount : balance + amount;
    if (balance < 0) {
      throw new Exception("User " + person + 
      " doesn't have enough money for transaction " + num + ".");
    } // if
    balances.put(person, balance);
  } // alterAmount

  /**
   * Gets the balances of all users in the chain.
   * @return A hash map of all of the users and their balances.
   * @throws Exception if any transaction is invalid.
   */
  private HashMap<String, Integer> balances() throws Exception {
    HashMap<String, Integer> balances = new HashMap<String, Integer>();
    Iterator<Block> blocks = this.blocks();
    Block b = blocks.next();
    while (blocks.hasNext()) {
      b = blocks.next();
      Transaction t = b.getTransaction();
      if (t.getAmount() < 0) {
        throw new Exception("Negative amount in block " + b.getNum() + ".");
      } // if
      if (!t.getSource().isEmpty()) {
        alterAmount(balances, t.getSource(), t.getAmount(), true, b.getNum());
      } // if
      if (t.getTarget().isEmpty()) {
        throw new Exception("Target is empty.");
      }
      else {
        alterAmount(balances, t.getTarget(), t.getAmount(), false, b.getNum());
      } // if
    } // for
    return balances;
  }
  /**
   * Find one user's balance.
   *
   * @param user
   *   The user whose balance we want to find.
   *
   * @return that user's balance (or 0, if the user is not in the system).
   * @throws Exception 
   */
  public int balance(String user) throws Exception {
    Integer balanceOrNull = balances().get(user);
    if (balanceOrNull == null) {
      return 0;
    } //if
    else {
      return balanceOrNull;
    } //else
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
