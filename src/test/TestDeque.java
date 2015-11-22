package test;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by uv2sun on 15/11/21.
 */
public class TestDeque {
    public static void main(String[] args) {
        Deque<String> deque = new ConcurrentLinkedDeque<>();

        deque.offerLast("1");
        deque.offerLast("2");
        deque.offerLast("3");
        deque.offer("4");
        System.out.println(deque.pollFirst());
        System.out.println(deque);
        deque.offer("x");
        System.out.println(deque.poll());
        System.out.println(deque);
    }
}
