package edu.grinnell.csc207.blockchains;

/**
 * A singly linked Block node.
 *
 * @author Andrew Fargo
 * @author Tiffany Tang
 */
public class BlockNode {
  /** a block which stores the value for the node itself. */
  Block data;
  /** a block node which the node points to. */
  BlockNode next;
  /**
  * Create a BlockNode.
  * @param block
  * @param node
  *
  */
  public BlockNode(Block block, BlockNode node) {
    this.data = block;
    this.next = node;
  } // BlockNode (Block, BlockNode)
} // class BlockNode
