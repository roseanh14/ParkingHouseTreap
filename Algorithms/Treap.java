package Algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Treap<K extends Comparable<K>, V> {

    @SuppressWarnings("ClassCanBeRecord")
    public static final class ViewNode<K, V> {
        public final K key;
        public final V value;
        public final int priority;
        public final ViewNode<K, V> left;
        public final ViewNode<K, V> right;

        public ViewNode(K key, V value, int priority, ViewNode<K, V> left, ViewNode<K, V> right) {
            this.key = key;
            this.value = value;
            this.priority = priority;
            this.left = left;
            this.right = right;
        }
    }

    private class Node {
        K key;
        V value;
        int priority;
        Node left;
        Node right;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.priority = random.nextInt(1000);
        }
    }

    private Node root;
    private final Random random = new Random();
    private final StringBuilder rotationLog = new StringBuilder();

    public boolean contains(K key) {
        return search(key) != null;
    }

    public V search(K key) {
        Node current = root;

        while (current != null) {
            int cmp = key.compareTo(current.key);

            if (cmp == 0) {
                return current.value;
            } else if (cmp < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        return null;
    }

    public boolean insert(K key, V value) {
        if (contains(key)) {
            log("Insert skipped: key " + key + " already exists.");
            return false;
        }

        log("INSERT key=" + key);
        root = insertNode(root, new Node(key, value));
        return true;
    }

    private Node insertNode(Node current, Node newNode) {
        if (current == null) {
            log("Inserted node key=" + newNode.key + ", priority=" + newNode.priority);
            return newNode;
        }

        int cmp = newNode.key.compareTo(current.key);

        if (cmp < 0) {
            current.left = insertNode(current.left, newNode);

            if (current.left != null && current.left.priority > current.priority) {
                log("Heap violation after insert: child key=" + current.left.key
                        + " has higher priority than parent key=" + current.key);
                current = rotateRight(current);
            }
        } else {
            current.right = insertNode(current.right, newNode);

            if (current.right != null && current.right.priority > current.priority) {
                log("Heap violation after insert: child key=" + current.right.key
                        + " has higher priority than parent key=" + current.key);
                current = rotateLeft(current);
            }
        }

        return current;
    }

    public boolean delete(K key) {
        if (!contains(key)) {
            log("Delete skipped: key " + key + " does not exist.");
            return false;
        }

        log("DELETE key=" + key);
        root = deleteNode(root, key);
        return true;
    }

    private Node deleteNode(Node node, K key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            node.left = deleteNode(node.left, key);
        } else if (cmp > 0) {
            node.right = deleteNode(node.right, key);
        } else {
            log("Found node to delete: key=" + node.key + ", priority=" + node.priority);

            if (node.left == null && node.right == null) {
                log("Deleting leaf node key=" + node.key);
                return null;
            }

            if (node.left == null) {
                log("Deleting node key=" + node.key + " with only right child.");
                return node.right;
            }

            if (node.right == null) {
                log("Deleting node key=" + node.key + " with only left child.");
                return node.left;
            }

            if (node.left.priority > node.right.priority) {
                log("Delete rebalance: left child has higher priority, rotate RIGHT on key=" + node.key);
                node = rotateRight(node);
                node.right = deleteNode(node.right, key);
            } else {
                log("Delete rebalance: right child has higher priority, rotate LEFT on key=" + node.key);
                node = rotateLeft(node);
                node.left = deleteNode(node.left, key);
            }
        }

        return node;
    }

    private Node rotateLeft(Node root) {
        log("LEFT ROTATION on key=" + root.key);

        Node newRoot = root.right;
        Node movedSubtree = newRoot.left;

        newRoot.left = root;
        root.right = movedSubtree;

        log("After LEFT rotation: new parent key=" + newRoot.key + ", moved down key=" + root.key);
        return newRoot;
    }

    private Node rotateRight(Node root) {
        log("RIGHT ROTATION on key=" + root.key);

        Node newRoot = root.left;
        Node movedSubtree = newRoot.right;

        newRoot.right = root;
        root.left = movedSubtree;

        log("After RIGHT rotation: new parent key=" + newRoot.key + ", moved down key=" + root.key);
        return newRoot;
    }

    public List<String> inorder() {
        List<String> result = new ArrayList<>();
        inorderTraversal(root, result);
        return result;
    }

    private void inorderTraversal(Node node, List<String> result) {
        if (node == null) {
            return;
        }

        inorderTraversal(node.left, result);
        result.add("Spot " + node.key + " -> " + node.value);
        inorderTraversal(node.right, result);
    }

    public K predecessor(K key) {
        Node current = root;
        Node result = null;

        while (current != null) {
            if (key.compareTo(current.key) <= 0) {
                current = current.left;
            } else {
                result = current;
                current = current.right;
            }
        }

        return result == null ? null : result.key;
    }

    public K successor(K key) {
        Node current = root;
        Node result = null;

        while (current != null) {
            if (key.compareTo(current.key) < 0) {
                result = current;
                current = current.left;
            } else {
                current = current.right;
            }
        }

        return result == null ? null : result.key;
    }

    public boolean validateTreap() {
        return isBST(root, null, null) && isHeap(root);
    }

    private boolean isBST(Node node, K min, K max) {
        if (node == null) {
            return true;
        }

        if (min != null && node.key.compareTo(min) <= 0) {
            return false;
        }

        if (max != null && node.key.compareTo(max) >= 0) {
            return false;
        }

        return isBST(node.left, min, node.key)
                && isBST(node.right, node.key, max);
    }

    private boolean isHeap(Node node) {
        if (node == null) {
            return true;
        }

        if (node.left != null && node.left.priority > node.priority) {
            return false;
        }

        if (node.right != null && node.right.priority > node.priority) {
            return false;
        }

        return isHeap(node.left) && isHeap(node.right);
    }

    public String printTreap() {
        StringBuilder builder = new StringBuilder();

        if (root == null) {
            return "Treap is empty.";
        }

        printNode(root, builder, 0);
        return builder.toString();
    }

    private void printNode(Node node, StringBuilder builder, int depth) {
        if (node == null) {
            return;
        }

        printNode(node.right, builder, depth + 1);

        builder.append("    ".repeat(depth))
                .append("[spot=")
                .append(node.key)
                .append(", priority=")
                .append(node.priority)
                .append(", value=")
                .append(node.value)
                .append("]")
                .append("\n");

        printNode(node.left, builder, depth + 1);
    }

    public ViewNode<K, V> getViewRoot() {
        return buildView(root);
    }

    private ViewNode<K, V> buildView(Node node) {
        if (node == null) {
            return null;
        }

        return new ViewNode<>(
                node.key,
                node.value,
                node.priority,
                buildView(node.left),
                buildView(node.right)
        );
    }

    public String getRotationLog() {
        return rotationLog.toString();
    }

    public void clearRotationLog() {
        rotationLog.setLength(0);
    }

    private void log(String message) {
        rotationLog.append(message).append("\n");
        System.out.println(message);
    }
}