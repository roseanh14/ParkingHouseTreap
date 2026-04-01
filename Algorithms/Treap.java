package Algorithms;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.AbstractMap;

public class Treap<K extends Comparable<K>, V> {

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
    private final StringBuilder operationLog = new StringBuilder();

    public boolean contains(K key) {
        return findNode(key) != null;
    }

    public V search(K key) {
        Node node = findNode(key);
        return node == null ? null : node.value;
    }

    private Node findNode(K key) {
        Node current = root;

        while (current != null) {
            int cmp = key.compareTo(current.key);

            if (cmp == 0) {
                return current;
            } else if (cmp < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        return null;
    }

    public boolean insert(K key, V value) {
        clearOperationLog();

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
            log("Inserted key=" + newNode.key + ", priority=" + newNode.priority);
            return newNode;
        }

        int cmp = newNode.key.compareTo(current.key);

        if (cmp < 0) {
            current.left = insertNode(current.left, newNode);

            if (current.left != null && current.left.priority > current.priority) {
                log("RIGHT ROTATION on key=" + current.key);
                current = rotateRight(current);
            }
        } else {
            current.right = insertNode(current.right, newNode);

            if (current.right != null && current.right.priority > current.priority) {
                log("LEFT ROTATION on key=" + current.key);
                current = rotateLeft(current);
            }
        }

        return current;
    }

    public boolean delete(K key) {
        clearOperationLog();

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
            log("Deleting key=" + node.key + ", priority=" + node.priority);

            if (node.left == null && node.right == null) {
                log("Leaf removed: key=" + node.key);
                return null;
            }

            if (node.left == null) {
                log("Node replaced by right child: key=" + node.key);
                return node.right;
            }

            if (node.right == null) {
                log("Node replaced by left child: key=" + node.key);
                return node.left;
            }

            if (node.left.priority > node.right.priority) {
                log("RIGHT ROTATION on key=" + node.key);
                node = rotateRight(node);
                node.right = deleteNode(node.right, key);
            } else {
                log("LEFT ROTATION on key=" + node.key);
                node = rotateLeft(node);
                node.left = deleteNode(node.left, key);
            }
        }

        return node;
    }

    private Node rotateLeft(Node root) {
        Node newRoot = root.right;
        Node movedSubtree = newRoot.left;

        newRoot.left = root;
        root.right = movedSubtree;

        return newRoot;
    }

    private Node rotateRight(Node root) {
        Node newRoot = root.left;
        Node movedSubtree = newRoot.right;

        newRoot.right = root;
        root.left = movedSubtree;

        return newRoot;
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

    public List<Map.Entry<K, V>> getInOrder() {
        List<Map.Entry<K, V>> result = new ArrayList<>();
        inOrder(root, result);
        return result;
    }

    private void inOrder(Node node, List<Map.Entry<K, V>> result) {
        if (node == null) {
            return;
        }

        inOrder(node.left, result);
        result.add(new AbstractMap.SimpleEntry<>(node.key, node.value));
        inOrder(node.right, result);
    }

    public String getOperationLog() {
        return operationLog.toString();
    }

    public void clearOperationLog() {
        operationLog.setLength(0);
    }

    private void log(String message) {
        operationLog.append(message).append("\n");
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
}