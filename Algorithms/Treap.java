package Algorithms;

import java.util.Random;

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
        if (contains(key)) {
            return false;
        }

        root = insertNode(root, new Node(key, value));
        return true;
    }

    private Node insertNode(Node current, Node newNode) {
        if (current == null) {
            return newNode;
        }

        int cmp = newNode.key.compareTo(current.key);

        if (cmp < 0) {
            current.left = insertNode(current.left, newNode);

            if (current.left != null && current.left.priority > current.priority) {
                current = rotateRight(current);
            }
        } else {
            current.right = insertNode(current.right, newNode);

            if (current.right != null && current.right.priority > current.priority) {
                current = rotateLeft(current);
            }
        }

        return current;
    }

    public boolean delete(K key) {
        if (!contains(key)) {
            return false;
        }

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
            if (node.left == null && node.right == null) {
                return null;
            }

            if (node.left == null) {
                return node.right;
            }

            if (node.right == null) {
                return node.left;
            }

            if (node.left.priority > node.right.priority) {
                node = rotateRight(node);
                node.right = deleteNode(node.right, key);
            } else {
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