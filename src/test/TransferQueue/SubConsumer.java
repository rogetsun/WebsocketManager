package test.TransferQueue;

import java.util.concurrent.TransferQueue;

/**
 * Created by uv2sun on 15/11/22.
 */
public class SubConsumer extends Consumer {
    public SubConsumer(TransferQueue<String> queue, int id) {
        super(queue, id);
    }
}
