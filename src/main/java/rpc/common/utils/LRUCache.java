package rpc.common.utils;

import java.util.HashMap;
import java.util.Map;

public class LRUCache<T, R> {
    private static class Node<T, R> {
        T key;
        R val;
        Node<T, R> next;
        Node<T, R> prev;

        public Node(T key, R val) {
            this.key = key;
            this.val = val;
            this.next = null;
            this.prev = null;
        }
    }

    Map<T, Node<T, R>> map = new HashMap<>();
    Node<T, R> head = null;
    Node<T, R> tail = null;
    int size;

    public LRUCache(int size) {
        this.size = size;
    }

    public void put(T key, R value) {
        if ( map.containsKey(key) ) {
            map.get(key).val = value;
            this.get(key);
        } else {
            if ( map.size() < size ) {
                this.putHead(key, value);
            } else {
                remove(tail.key);
                putHead(key, value);
            }
        }
    }

    public R get(T key) {
        return putHead(key, remove(key));
    }

    private R putHead(T key, R val) {
        Node<T, R> node = new Node<>(key, val);
        if ( head == null ) {
            head = node;
            tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
        map.put(key, node);

        return val;
    }

    public R remove(T key) {
        Node<T, R> node = map.remove(key);
        if ( node == null ) {
            throw new NullPointerException("Key " + key + " not found");
        }

        if ( node == head && node == tail ){
            head = null;
            tail = null;
        } else if ( node == head ) {
            head.next.prev = null;
            head = head.next;
        } else if ( node == tail ) {
            tail.prev.next = null;
            tail = tail.prev;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        node.next = null;
        node.prev = null;

        return node.val;
    }

    public void out() {
        Node<T, R> p = head;
        if ( head == null ) {
            return;
        }
        System.out.print("[");
        do {
            System.out.print("(" + p.key + ", " + p.val + "), ");
            p = p.next;
        } while (p != null);
        System.out.println("], head = (" + head.key + ", " + head.val + "), " + "tail = (" + tail.key + ", " + tail.val + ")");

    }


    public static void main(String[] args) {
        LRUCache<Integer, Integer> test = new LRUCache<>(2);
        test.out();
        test.put(1, 1);
        test.out();
        test.put(2, 2);
        test.out();
        test.get(1);
        test.out();
        test.put(3, 3);
        test.out();

    }


}
