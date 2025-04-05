// --== CS400 Spring 2023 File Header Information ==--
// Name: Sai Gungurthi
// Email: sgungurthi@wisc.edu
// Team: AK
// TA: Gary Dahl
// Lecturer: Gary Dahl
// Notes to Grader: None

import java.util.LinkedList;
import java.util.Stack;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Red-Black Tree implementation with a Node inner class for representing
 * the nodes of the tree. Currently, this implements a Binary Search Tree that
 * we will turn into a red black tree by modifying the insert functionality.
 */
public class RedBlackTree<T extends Comparable<T>> implements SortedCollectionInterface<T> {

    /**
     * This class represents a node holding a single value within a binary tree.
     */
    protected static class Node<T> {
        public T data;

        // tracks the black height only for the current node: 0 = red, 1 = black, and 2 = double-black
        public int blackHeight;

        // The context array stores the context of the node in the tree:
        // - context[0] is the parent reference of the node,
        // - context[1] is the left child reference of the node,
        // - context[2] is the right child reference of the node.
        // The @SuppressWarning("unchecked") annotation is used to suppress an unchecked
        // cast warning. Java only allows us to instantiate arrays without generic
        // type parameters, so we use this cast here to avoid future casts of the
        // node type's data field.
        @SuppressWarnings("unchecked")
        public Node<T>[] context = (Node<T>[])new Node[3];
        public Node(T data) {
            this.data = data;
            blackHeight = 0; // every newly instantiated Node object has a blackHeight of 0 (aka red) by default
        }

        /**
         * @return true when this node has a parent and is the right child of
         * that parent, otherwise return false
         */
        public boolean isRightChild() {
            return context[0] != null && context[0].context[2] == this;
        }

        /**
         * I created this method for readability and convenience.
         *
         * @return true when this node has a parent and is the left child of
         * that parent, otherwise return false
         */
        public boolean isLeftChild() {
            return context[0] != null && context[0].context[1] == this;
        }

    }

    protected Node<T> root; // reference to root node of tree, null when empty
    protected int size = 0; // the number of values in the tree

    /**
     * Resolves any red-black tree property violations that are introduced by inserting each new red node into a
     * red-black tree.
     *
     * @param redNode node being inserted into RBT, or in case 3, a node being recursively called
     */
    protected void enforceRBTreePropertiesAfterInsert(Node<T> redNode) {
        // case 1: parent's sibling is black and parent and child are on same side
        // solution: rotate and color swap parent and grandparent
        if (parent(redNode) != null) {
            enforceCaseOneHelper(redNode);

            // case 2: parent's sibling is black and parent and child are NOT on same side
            // solution: rotate red nodes, then do case 1
            if ((redNode.isRightChild() && parent(redNode).isLeftChild()) &&
                    (uncle(redNode) == null || uncle(redNode).blackHeight == 1)) {
                Node<T> tempParent = parent(redNode);
                rotate(redNode, parent(redNode));
                enforceCaseOneHelper(tempParent);
            }

            if ((redNode.isLeftChild() && parent(redNode).isRightChild()) &&
                    (uncle(redNode) == null || uncle(redNode).blackHeight == 1)) {
                Node<T> tempParent = parent(redNode);
                rotate(redNode, parent(redNode));
                enforceCaseOneHelper(tempParent);
            }

            // case 3: parent's sibling is red
            // solution: toggle color of parent, parent's sibling, and grandparent; check for violations further up tree
            if ((parent(redNode).isRightChild() || parent(redNode).isLeftChild()) &&
                    (uncle(redNode) != null && uncle(redNode).blackHeight == 0)) {
                toggleColorHelper(parent(redNode));
                toggleColorHelper(grandparent(redNode));
                toggleColorHelper(uncle(redNode));
                if (!root.equals(grandparent(redNode)) && grandparent(grandparent(redNode)) != null) {
                    if (grandparent(redNode).blackHeight == 0 && parent(grandparent(redNode)).blackHeight == 0) {
                        enforceRBTreePropertiesAfterInsert(grandparent(redNode));
                    }
                }
            }
        }

        root.blackHeight = 1;
    }

    /**
     * Implements the algorithm for RBT insertion case one. It is a helper method to make the code look cleaner.
     *
     * @param redNode
     */
    private void enforceCaseOneHelper(Node<T> redNode) {
        if (parent(redNode).isRightChild() && (uncle(redNode) == null || uncle(redNode).blackHeight == 1)) {
            if (redNode.isRightChild()) {
                int parentBlackHeight = parent(redNode).blackHeight;
                int grandparentBlackHeight = grandparent(redNode).blackHeight;
                Node<T> tempNode = grandparent(redNode);
                rotate(parent(redNode), grandparent(redNode));
                tempNode.blackHeight = parentBlackHeight;
                parent(redNode).blackHeight = grandparentBlackHeight;
            }
        }

        if (parent(redNode).isLeftChild() && (uncle(redNode) == null || uncle(redNode).blackHeight == 1)) {
            if (redNode.isLeftChild()) {
                int parentBlackHeight = parent(redNode).blackHeight;
                int grandparentBlackHeight = grandparent(redNode).blackHeight;
                Node<T> tempNode = grandparent(redNode);
                rotate(parent(redNode), grandparent(redNode));
                tempNode.blackHeight = parentBlackHeight;
                parent(redNode).blackHeight = grandparentBlackHeight;
            }
        }
    }

    private void toggleColorHelper(Node<T> node) {
        if (node != null && node.blackHeight == 0) {
            node.blackHeight = 1;
        } else if (node != null && node.blackHeight == 1) {
            node.blackHeight = 0;
        }
    }

    private Node<T> parent(Node<T> node) {
        return node.context[0];
    }

    private Node<T> grandparent(Node<T> node) {
        return node.context[0].context[0];
    }

    private Node<T> uncle(Node<T> node) {
        if (parent(node).isLeftChild()) {
            return grandparent(node).context[2];
        }
        else {
            return grandparent(node).context[1];
        }
    }

    /**
     * Performs a naive insertion into a binary search tree: adding the input
     * data value to a new node in a leaf position within the tree. After
     * this insertion, a method is called to restructure and balance the tree.
     * This tree will not hold null references, nor duplicate data values.
     * @param data to be added into this binary search tree
     * @return true if the value was inserted, false if not
     * @throws NullPointerException when the provided data argument is null
     * @throws IllegalArgumentException when data is already contained in the tree
     */
    public boolean insert(T data) throws NullPointerException, IllegalArgumentException {
        // null references cannot be stored within this tree
        if(data == null) throw new NullPointerException(
                "This RedBlackTree cannot store null references.");

        Node<T> newNode = new Node<>(data);
        if (this.root == null) {
            // add first node to an empty tree
            root = newNode;
            size++;
            enforceRBTreePropertiesAfterInsert(newNode);
            return true;
        } else {
            // insert into subtree
            Node<T> current = this.root;
            while (true) {
                int compare = newNode.data.compareTo(current.data);
                if (compare == 0) {
                    throw new IllegalArgumentException("This RedBlackTree already contains value " + data.toString());
                } else if (compare < 0) {
                    // insert in left subtree
                    if (current.context[1] == null) {
                        // empty space to insert into
                        current.context[1] = newNode;
                        newNode.context[0] = current;
                        this.size++;
                        enforceRBTreePropertiesAfterInsert(newNode);
                        return true;
                    } else {
                        // no empty space, keep moving down the tree
                        current = current.context[1];
                    }
                } else {
                    // insert in right subtree
                    if (current.context[2] == null) {
                        // empty space to insert into
                        current.context[2] = newNode;
                        newNode.context[0] = current;
                        this.size++;
                        enforceRBTreePropertiesAfterInsert(newNode);
                        return true;
                    } else {
                        // no empty space, keep moving down the tree
                        current = current.context[2];
                    }
                }
            }
        }
    }

    /**
     * Performs the rotation operation on the provided nodes within this tree.
     * When the provided child is a left child of the provided parent, this
     * method will perform a right rotation. When the provided child is a
     * right child of the provided parent, this method will perform a left rotation.
     * When the provided nodes are not related in one of these ways, this method
     * will throw an IllegalArgumentException.
     * @param child is the node being rotated from child to parent position
     *      (between these two node arguments)
     * @param parent is the node being rotated from parent to child position
     *      (between these two node arguments)
     * @throws IllegalArgumentException when the provided child and parent
     *      node references are not initially (pre-rotation) related that way
     */
    private void rotate(Node<T> child, Node<T> parent) throws IllegalArgumentException {
        if (child == null || parent == null) {
            throw new IllegalArgumentException("The provided child and parent node references are not related that way.");
        }

        if (!(parent.context[1] == child || parent.context[2] == child)) {
            throw new IllegalArgumentException("The provided child and parent node references are not related that way.");
        }

        // left child, right rotation
        if (child.isLeftChild()) {
            // when the parent is the root
            if (root == parent) {
                Node<T> rightChildOfChild = null;
                if (child.context[2] != null) {
                    rightChildOfChild = child.context[2]; // saved in a temp variable because reassigned later on
                }
                child.context[2] = parent;
                root = child; // important step
                child.context[0] = null; // roots don't have parents
                parent.context[0] = child;
                parent.context[1] = rightChildOfChild; // right child of the "child" becomes the left child of the "parent"
                if (rightChildOfChild != null) {
                    rightChildOfChild.context[0] = parent;
                }
            } else { // slightly different code for when the parent is not the root
                Node<T> rightChildOfChild = null;
                Node<T> grandparent = parent.context[0]; // need grandparent variable because parent is no longer the root
                if (child.context[2] != null) {
                    rightChildOfChild = child.context[2];
                }
                child.context[2] = parent;
                if (parent.isLeftChild()) {
                    grandparent.context[1] = child;
                } else if (parent.isRightChild()) {
                    grandparent.context[2] = child;
                }
                child.context[0] = grandparent; // assigning child's new parent to parent's old parent
                parent.context[0] = child;
                parent.context[1] = rightChildOfChild;
                if (rightChildOfChild != null) {
                    rightChildOfChild.context[0] = parent;
                }
            }
        }

        // right child, left rotation
        else if (child.isRightChild()) { // same idea as a right rotation, but the other way around
            if (root == parent) {
                Node<T> leftChildOfChild = null;
                if (child.context[1] != null) {
                    leftChildOfChild = child.context[1];
                }
                child.context[1] = parent;
                root = child;
                child.context[0] = null;
                parent.context[0] = child;
                parent.context[2] = leftChildOfChild;
                if (leftChildOfChild != null) {
                    leftChildOfChild.context[0] = parent;
                }
            } else {
                Node<T> leftChildOfChild = null;
                Node<T> grandparent = parent.context[0];
                if (child.context[1] != null) {
                    leftChildOfChild = child.context[1];
                }
                child.context[1] = parent;
                if (parent.isLeftChild()) {
                    grandparent.context[1] = child;
                } else if (parent.isRightChild()) {
                    grandparent.context[2] = child;
                }
                child.context[0] = grandparent;
                parent.context[0] = child;
                parent.context[2] = leftChildOfChild;
                if (leftChildOfChild != null) {
                    leftChildOfChild.context[0] = parent;
                }
            }
        }
    }

    /**
     * Get the size of the tree (its number of nodes).
     * @return the number of nodes in the tree
     */
    public int size() {
        return size;
    }

    /**
     * Method to check if the tree is empty (does not contain any node).
     * @return true of this.size() return 0, false if this.size() > 0
     */
    public boolean isEmpty() {
        return this.size() == 0;
    }

    /**
     * Removes the value data from the tree if the tree contains the value.
     * This method will not attempt to rebalance the tree after the removal and
     * should be updated once the tree uses Red-Black Tree insertion.
     * @return true if the value was remove, false if it didn't exist
     * @throws NullPointerException when the provided data argument is null
     * @throws IllegalArgumentException when data is not stored in the tree
     */
    public boolean remove(T data) throws NullPointerException, IllegalArgumentException {
        // null references will not be stored within this tree
        if (data == null) {
            throw new NullPointerException("This RedBlackTree cannot store null references.");
        } else {
            Node<T> nodeWithData = this.findNodeWithData(data);
            // throw exception if node with data does not exist
            if (nodeWithData == null) {
                throw new IllegalArgumentException("The following value is not in the tree and cannot be deleted: " + data.toString());
            }
            boolean hasRightChild = (nodeWithData.context[2] != null);
            boolean hasLeftChild = (nodeWithData.context[1] != null);
            if (hasRightChild && hasLeftChild) {
                // has 2 children
                Node<T> successorNode = this.findMinOfRightSubtree(nodeWithData);
                // replace value of node with value of successor node
                nodeWithData.data = successorNode.data;
                // remove successor node
                if (successorNode.context[2] == null) {
                    // successor has no children, replace with null
                    this.replaceNode(successorNode, null);
                } else {
                    // successor has a right child, replace successor with its child
                    this.replaceNode(successorNode, successorNode.context[2]);
                }
            } else if (hasRightChild) {
                // only right child, replace with right child
                this.replaceNode(nodeWithData, nodeWithData.context[2]);
            } else if (hasLeftChild) {
                // only left child, replace with left child
                this.replaceNode(nodeWithData, nodeWithData.context[1]);
            } else {
                // no children, replace node with a null node
                this.replaceNode(nodeWithData, null);
            }
            this.size--;
            return true;
        }
    }

    /**
     * Checks whether the tree contains the value *data*.
     * @param data the data value to test for
     * @return true if *data* is in the tree, false if it is not in the tree
     */
    public boolean contains(T data) {
        // null references will not be stored within this tree
        if (data == null) {
            throw new NullPointerException("This RedBlackTree cannot store null references.");
        } else {
            Node<T> nodeWithData = this.findNodeWithData(data);
            // return false if the node is null, true otherwise
            return (nodeWithData != null);
        }
    }

    /**
     * Helper method that will replace a node with a replacement node. The replacement
     * node may be null to remove the node from the tree.
     * @param nodeToReplace the node to replace
     * @param replacementNode the replacement for the node (may be null)
     */
    protected void replaceNode(Node<T> nodeToReplace, Node<T> replacementNode) {
        if (nodeToReplace == null) {
            throw new NullPointerException("Cannot replace null node.");
        }
        if (nodeToReplace.context[0] == null) {
            // we are replacing the root
            if (replacementNode != null)
                replacementNode.context[0] = null;
            this.root = replacementNode;
        } else {
            // set the parent of the replacement node
            if (replacementNode != null)
                replacementNode.context[0] = nodeToReplace.context[0];
            // do we have to attach a new left or right child to our parent?
            if (nodeToReplace.isRightChild()) {
                nodeToReplace.context[0].context[2] = replacementNode;
            } else {
                nodeToReplace.context[0].context[1] = replacementNode;
            }
        }
    }

    /**
     * Helper method that will return the inorder successor of a node with two children.
     * @param node the node to find the successor for
     * @return the node that is the inorder successor of node
     */
    protected Node<T> findMinOfRightSubtree(Node<T> node) {
        if (node.context[1] == null && node.context[2] == null) {
            throw new IllegalArgumentException("Node must have two children");
        }
        // take a step to the right
        Node<T> current = node.context[2];
        while (true) {
            // then go left as often as possible to find the successor
            if (current.context[1] == null) {
                // we found the successor
                return current;
            } else {
                current = current.context[1];
            }
        }
    }

    /**
     * Helper method that will return the node in the tree that contains a specific
     * value. Returns null if there is no node that contains the value.
     * @return the node that contains the data, or null of no such node exists
     */
    protected Node<T> findNodeWithData(T data) {
        Node<T> current = this.root;
        while (current != null) {
            int compare = data.compareTo(current.data);
            if (compare == 0) {
                // we found our value
                return current;
            } else if (compare < 0) {
                // keep looking in the left subtree
                current = current.context[1];
            } else {
                // keep looking in the right subtree
                current = current.context[2];
            }
        }
        // we're at a null node and did not find data, so it's not in the tree
        return null;
    }

    /**
     * This method performs an inorder traversal of the tree. The string
     * representations of each data value within this tree are assembled into a
     * comma separated string within brackets (similar to many implementations
     * of java.util.Collection, like java.util.ArrayList, LinkedList, etc).
     * @return string containing the ordered values of this tree (in-order traversal)
     */
    public String toInOrderString() {
        // generate a string of all values of the tree in (ordered) in-order
        // traversal sequence
        StringBuffer sb = new StringBuffer();
        sb.append("[ ");
        if (this.root != null) {
            Stack<Node<T>> nodeStack = new Stack<>();
            Node<T> current = this.root;
            while (!nodeStack.isEmpty() || current != null) {
                if (current == null) {
                    Node<T> popped = nodeStack.pop();
                    sb.append(popped.data.toString());
                    if(!nodeStack.isEmpty() || popped.context[2] != null) sb.append(", ");
                    current = popped.context[2];
                } else {
                    nodeStack.add(current);
                    current = current.context[1];
                }
            }
        }
        sb.append(" ]");
        return sb.toString();
    }

    /**
     * This method performs a level order traversal of the tree. The string
     * representations of each data value
     * within this tree are assembled into a comma separated string within
     * brackets (similar to many implementations of java.util.Collection).
     * This method will be helpful as a helper for the debugging and testing
     * of your rotation implementation.
     * @return string containing the values of this tree in level order
     */
    public String toLevelOrderString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[ ");
        if (this.root != null) {
            LinkedList<Node<T>> q = new LinkedList<>();
            q.add(this.root);
            while(!q.isEmpty()) {
                Node<T> next = q.removeFirst();
                if(next.context[1] != null) q.add(next.context[1]);
                if(next.context[2] != null) q.add(next.context[2]);
                sb.append(next.data.toString());
                if(!q.isEmpty()) sb.append(", ");
            }
        }
        sb.append(" ]");
        return sb.toString();
    }

    public String toString() {
        return "level order: " + this.toLevelOrderString() +
                "\nin order: " + this.toInOrderString();
    }

    /**
     * The toLevelOrderString method but includes the colors of the nodes.
     *
     * @return
     */
    public String toLevelOrderStringWithColor() {
        StringBuffer sb = new StringBuffer();
        sb.append("[ ");
        if (this.root != null) {
            LinkedList<Node<T>> q = new LinkedList<>();
            q.add(this.root);
            while(!q.isEmpty()) {
                Node<T> next = q.removeFirst();
                if(next.context[1] != null) q.add(next.context[1]);
                if(next.context[2] != null) q.add(next.context[2]);
                sb.append(next.data.toString() + "(" + next.blackHeight + ")");
                if(!q.isEmpty()) sb.append(", ");
            }
        }
        sb.append(" ]");
        return sb.toString();
    }

    /**
     * The toInOrderString method but includes the colors of the nodes.
     *
     * @return
     */
    public String toInOrderStringWithColor() {
        // generate a string of all values of the tree in (ordered) in-order
        // traversal sequence
        StringBuffer sb = new StringBuffer();
        sb.append("[ ");
        if (this.root != null) {
            Stack<Node<T>> nodeStack = new Stack<>();
            Node<T> current = this.root;
            while (!nodeStack.isEmpty() || current != null) {
                if (current == null) {
                    Node<T> popped = nodeStack.pop();
                    sb.append(popped.data.toString() + "(" + popped.blackHeight + ")");
                    if(!nodeStack.isEmpty() || popped.context[2] != null) sb.append(", ");
                    current = popped.context[2];
                } else {
                    nodeStack.add(current);
                    current = current.context[1];
                }
            }
        }
        sb.append(" ]");
        return sb.toString();
    }

    /**
     * Tests RBT insertion case one taught in lecture. This is when the uncle is black and the redNode and its parent
     * make a line (the simplest case).
     */
    @Test
    public void testCaseOne() {
        RedBlackTree<Integer> actual = new RedBlackTree<>();
        actual.insert(20);
        actual.insert(10);
        actual.insert(30);
        actual.insert(40);
        actual.insert(50);
        String actualLevelOrderString = actual.toLevelOrderStringWithColor().trim();
        String actualInOrderString = actual.toInOrderStringWithColor().trim();
        assertEquals("[ 20(1), 10(1), 40(1), 30(0), 50(0) ]", actualLevelOrderString);
        assertEquals("[ 10(1), 20(1), 30(0), 40(1), 50(0) ]", actualInOrderString);

    }

    /**
     * Tests RBT insertion case two taught in lecture. This is when the uncle is black, but the redNode and its parent
     * make a triangle (not a line).
     */
    @Test
    public void testCaseTwo() {
        RedBlackTree<Integer> actual = new RedBlackTree<>();
        actual.insert(20);
        actual.insert(10);
        actual.insert(30);
        actual.insert(50);
        actual.insert(40);
        String actualLevelOrderString = actual.toLevelOrderStringWithColor().trim();
        String actualInOrderString = actual.toInOrderStringWithColor().trim();
        assertEquals("[ 20(1), 10(1), 40(1), 30(0), 50(0) ]", actualLevelOrderString);
        assertEquals("[ 10(1), 20(1), 30(0), 40(1), 50(0) ]", actualInOrderString);
    }

    /**
     * Tests RBT insertion case three taught in lecture. This is when the uncle is red.
     */
    @Test
    public void testCaseThree() {
        RedBlackTree<Integer> actual = new RedBlackTree<>();
        actual.insert(40);
        actual.insert(20);
        actual.insert(60);
        actual.insert(80);
        String actualLevelOrderString = actual.toLevelOrderStringWithColor().trim();
        String actualInOrderString = actual.toInOrderStringWithColor().trim();
        assertEquals("[ 40(1), 20(1), 60(1), 80(0) ]", actualLevelOrderString);
        assertEquals("[ 20(1), 40(1), 60(1), 80(0) ]", actualInOrderString);
    }

    /**
     * Main method
     * @param args
     */
    public static void main(String[] args) {

    }
}