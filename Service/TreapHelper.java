package Service;

import Algorithms.Treap;

import java.lang.reflect.Field;

public final class TreapHelper {

    private TreapHelper() {
    }

    public record SnapshotNode<K, V>(
            K key,
            V value,
            int priority,
            SnapshotNode<K, V> left,
            SnapshotNode<K, V> right
    ) {
    }

    public static <K extends Comparable<K>, V> SnapshotNode<K, V> buildSnapshot(Treap<K, V> treap) {
        Object root = getRoot(treap);
        return buildSnapshotRecursive(root);
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