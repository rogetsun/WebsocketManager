package test.TransferQueue;

import java.util.concurrent.TransferQueue;

/**
 * Created by uv2sun on 15/11/21.
 */
public class Producter implements Runnable {
    private TransferQueue<String> queue;
    private int id;
    private int c = 0;

    public Producter(TransferQueue<String> queue, int id) {
        this.queue = queue;
        this.id = id;
    }

    public void product() {
        queue.offer("product[" + id + "]:" + (c++));
    }

    @Override
    public void run() {
        while (true) {
            product();
            if (c % 10 == 0) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
