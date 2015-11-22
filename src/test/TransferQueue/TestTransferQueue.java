package test.TransferQueue;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

/**
 * Created by uv2sun on 15/11/21.
 */
public class TestTransferQueue {
    public static void main(String[] args) {
        TransferQueue<String> queue = new LinkedTransferQueue<>();
        Producter p1 = new Producter(queue, 1);
        Consumer c1 = new Consumer(queue, 1);
        new Thread(c1).start();
        new Thread(p1).start();
        new Thread(new Producter(queue, 2)).start();
    }
}
