package Service;

import Algorithms.Treap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class TreapHelper {

    private TreapHelper() {
    }

    public record Entry<K, V>(K key, V value) {
    }

    public record SnapshotNode<K, V>(
            K key,
            V value,
            int priority,
            SnapshotNode<K, V> left,
            SnapshotNode<K, V> right
    ) {
    }

    public static <K extends Comparable<K>, V> List<Entry<K, V>> inorder(Treap<K, V> treap) {
        List<Entry<K, V>> result = new ArrayList<>();
        Object root = getRoot(treap);
        inorderRecursive(root, result);
        return result;
    }

    public static <K extends Comparable<K>, V> SnapshotNode<K, V> buildSnapshot(Treap<K, V> treap) {
        Object root = getRoot(treap);
        return buildSnapshotRecursive(root);
    }

    @SuppressWarnings("unchecked")
    private static <K, V> void inorderRecursive(Object node, List<Entry<K, V>> result) {
        if (node == null) {
            return;
        }

        Object left = getField(node, "left");
        Object right = getField(node, "right");

        inorderRecursive(left, result);

        K key = (K) getField(node, "key");
        V value = (V) getField(node, "value");

        result.add(new Entry<>(key, value));

        inorderRecursive(right, result);
    }

    @SuppressWarnings("unchecked")
    private static <K, V> SnapshotNode<K, V> buildSnapshotRecursive(Object node) {
        if (node == null) {
            return null;
        }

        K key = (K) getField(node, "key");
        V value = (V) getField(node, "value");
        int priority = (int) getField(node, "priority");

        return new SnapshotNode<>(
                key,
                value,
                priority,
                buildSnapshotRecursive(getField(node, "left")),
                buildSnapshotRecursive(getField(node, "right"))
        );
    }

    private static Object getRoot(Object treap) {
        return getField(treap, "root");
    }

    private static Object getField(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            throw new RuntimeException("Cannot access field: " + fieldName, e);
        }
    }
}