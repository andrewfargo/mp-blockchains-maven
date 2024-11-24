package edu.grinnell.csc207.main;

import edu.grinnell.csc207.blockchains.Block;
import edu.grinnell.csc207.blockchains.BlockChain;
import edu.grinnell.csc207.blockchains.HashValidator;
import edu.grinnell.csc207.blockchains.Transaction;

import edu.grinnell.csc207.util.IOUtils;

import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * A simple UI for our BlockChain class.
 *
 * @author Tiffany Tang,
 * @author Andrew Fargo
 * @author Samuel A. Rebelsky
 */
public class BlockChainUI {
  // +-----------+---------------------------------------------------
  // | Constants |
  // +-----------+

  /**
   * The number of bytes we validate. Should be set to 3 before submitting.
   */
  static final int VALIDATOR_BYTES = 3;

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Print out the instructions.
   *
   * @param pen
   *   The pen used for printing instructions.
   */
  public static void instructions(PrintWriter pen) {
    pen.println("""
      Valid commands:
        mine: discovers the nonce for a given transaction
        append: appends a new block onto the end of the chain
        remove: removes the last block from the end of the chain
        check: checks that the block chain is valid
        users: prints a list of users
        balance: finds a user's balance
        transactions: prints out the chain of transactions
        blocks: prints out the chain of blocks (for debugging only)
        help: prints this list of commands
        quit: quits the program""");
  } // instructions(PrintWriter)

  // +------+--------------------------------------------------------
  // | Main |
  // +------+

  /**
   * Run the UI.
   *
   * @param args
   *   Command-line arguments (currently ignored).
   */
  public static void main(String[] args) throws Exception {
    PrintWriter pen = new PrintWriter(System.out, true);
    BufferedReader eyes = new BufferedReader(new InputStreamReader(System.in));

    // Set up our blockchain.
    HashValidator validator =
        (h) -> {
          if (h.length() < VALIDATOR_BYTES) {
            return false;
          } // if
          for (int v = 0; v < VALIDATOR_BYTES; v++) {
            if (h.get(v) != 0) {
              return false;
            } // if
          } // for
          return true;
        };
    BlockChain chain = new BlockChain(validator);

    instructions(pen);

    boolean done = false;

    String source;
    String target;
    int amount;
    long nonce;

    while (!done) {
      pen.print("\nCommand: ");
      pen.flush();
      String command = eyes.readLine();
      if (command == null) {
        command = "quit";
      } // if

      switch (command.toLowerCase()) {
        case "append":
          source = IOUtils.readLine(pen, eyes, "\nSource: ");
          target = IOUtils.readLine(pen, eyes, "Target: ");
          amount = IOUtils.readInt(pen, eyes, "Amount: ");
          nonce = IOUtils.readLong(pen, eyes, "Nonce: ");

          Transaction appendedTransaction = new Transaction(source, target, amount);
          Block appendedBlock = new Block(chain.getSize(), appendedTransaction,
                                chain.getHash(), nonce);
          try {
            chain.append(appendedBlock);
          } catch (IllegalArgumentException e) {
            pen.println("Could not append: " + e.getMessage());
          } //try/catch
          break;

        case "balance":
          String user = IOUtils.readLine(pen, eyes, "\nUser: ");
          try {
            int bal = chain.balance(user);
            pen.printf("%s's balance is %d", user, bal);
          } catch (Exception e) {
            pen.printf("Something wrong in the chain: " + e.getMessage());
          } // try/catch
          break;

        case "blocks":
          chain.blocks().forEachRemaining(b -> pen.println(b.toString()));
          break;

        case "check":
          try {
            chain.check();
          } catch (Exception e) {
            pen.println(e.getMessage());
            break;
          } // try/catch
          pen.println("Chain checks out.");
          break;

        case "help":
          instructions(pen);
          break;

        case "mine":
          source = IOUtils.readLine(pen, eyes, "Source (return for deposit): ");
          target = IOUtils.readLine(pen, eyes, "Target: ");
          amount = IOUtils.readInt(pen, eyes, "Amount: ");

          /*A new block created with the information provided by the user. */
          Block b = chain.mine(new Transaction(source, target, amount));
          pen.println("Nonce: " + b.getNonce());
          break;

        case "quit":
          done = true;
          break;

        case "remove":
          if (chain.removeLast()) {
            pen.printf("Removed last element.");
          } else {
            pen.printf("Could not remove last element.");
          } //if-else
          break;

        case "transactions":
          chain.iterator().forEachRemaining(t -> pen.println(t.toString()));
          break;

        case "users":
          chain.users().forEachRemaining(u -> pen.println(u));
          break;

        default:
          pen.printf("invalid command: '%s'. Try again.\n", command);
          break;
      } // switch
    } // while

    pen.printf("\nGoodbye\n");
    eyes.close();
    pen.close();
  } // main(String[])
} // class BlockChainUI
