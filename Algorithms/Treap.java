package Algorithms;

import java.util.ArrayList;
import java.util.List;
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
            return false;
        }

        root = insert(root, new Node(key, value));
        return true;
    }

    private Node insert(Node current, Node newNode) {
        if (current == null) {
            return newNode;
        }

        int cmp = newNode.key.compareTo(current.key);

        if (cmp < 0) {
            current.left = insert(current.left, newNode);
            if (current.left.priority > current.priority) {
                current = rotateRight(current);
            }
        } else {
            current.right = insert(current.right, newNode);
            if (current.right.priority > current.priority) {
                current = rotateLeft(current);
            }
        }

        return current;
    }

    public boolean delete(K key) {
        if (!contains(key)) {
            return false;
        }

        root = delete(root, key);
        return true;
    }

    private Node delete(Node node, K key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            node.left = delete(node.left, key);
        } else if (cmp > 0) {
            node.right = delete(node.right, key);
        } else {
            if (node.left == null && node.right == null) {
                return null;
            } else if (node.left == null) {
                node = rotateLeft(node);
                node.left = delete(node.left, key);
            } else if (node.right == null) {
                node = rotateRight(node);
                node.right = delete(node.right, key);
            } else {
                if (node.left.priority > node.right.priority) {
                    node = rotateRight(node);
                    node.right = delete(node.right, key);
                } else {
                    node = rotateLeft(node);
                    node.left = delete(node.left, key);
                }
            }
        }

        return node;
    }

    private Node rotateLeft(Node x) {
        System.out.println("Left rotation on node: " + x.key);

        Node y = x.right;
        x.right = y.left;
        y.left = x;

        return y;
    }

    private Node rotateRight(Node y) {
        System.out.println("Right rotation on node: " + y.key);

        Node x = y.left;
        y.left = x.right;
        x.right = y;

        return x;
    }

    public List<String> inorder() {
        List<String> result = new ArrayList<>();
        inorder(root, result);
        return result;
    }

    private void inorder(Node node, List<String> result) {
        if (node == null) {
            return;
        }

        inorder(node.left, result);
        result.add("Spot " + node.key + " -> " + node.value);
        inorder(node.right, result);
    }

    public K predecessor(K key) {
        Node current = root;
        Node predecessor = null;

        while (current != null) {
            int cmp = key.compareTo(current.key);

            if (cmp <= 0) {
                current = current.left;
            } else {
                predecessor = current;
                current = current.right;
            }
        }

        return predecessor == null ? null : predecessor.key;
    }

    public K successor(K key) {
        Node current = root;
        Node successor = null;

        while (current != null) {
            int cmp = key.compareTo(current.key);

            if (cmp < 0) {
                successor = current;
                current = current.left;
            } else {
                current = current.right;
            }
        }

        return successor == null ? null : successor.key;
    }

    public boolean validateTreap() {
        return validateBST(root, null, null) && validateHeap(root);
    }

    private boolean validateBST(Node node, K min, K max) {
        if (node == null) {
            return true;
        }

        if (min != null && node.key.compareTo(min) <= 0) {
            return false;
        }

        if (max != null && node.key.compareTo(max) >= 0) {
            return false;
        }

        return validateBST(node.left, min, node.key)
                && validateBST(node.right, node.key, max);
    }

    private boolean validateHeap(Node node) {
        if (node == null) {
            return true;
        }

        if (node.left != null && node.left.priority > node.priority) {
            return false;
        }

        if (node.right != null && node.right.priority > node.priority) {
            return false;
        }

        return validateHeap(node.left) && validateHeap(node.right);
    }
}