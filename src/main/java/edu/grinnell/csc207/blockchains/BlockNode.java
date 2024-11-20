package edu.grinnell.csc207.blockchains;

/**
 * A singly linked Block node.
 *
 * @author Andrew Fargo
 * @author Tiffany Tang
 */
public class BlockNode {
  Block data;
  BlockNode next;

  public BlockNode(Block block, BlockNode node) {
    this.data = block;
    this.next = node;
  } // BlockNode (Block, BlockNode)
} // class BlockNode
