package test.TransferQueue;

import java.util.concurrent.TransferQueue;

/**
 * Created by uv2sun on 15/11/21.
 */
public class Consumer implements Runnable {
    private TransferQueue<String> queue;
    private int id;

    public Consumer(TransferQueue<String> queue, int id) {
        this.queue = queue;
        this.id = id;
    }

    public void consume() throws InterruptedException {
        while (true) {
            String product = queue.take();
            System.out.println("consumer[" + id + "]:" + product);
        }
    }

    @Override
    public void run() {
        try {
            consume();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
