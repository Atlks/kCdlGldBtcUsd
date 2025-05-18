package uti;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

public class ObjectPool<T> {
    private final BlockingQueue<T> pool;

    public ObjectPool(int maxSize, Supplier<T> creator) {
        this.pool = new LinkedBlockingQueue<>(maxSize);
        for (int i = 0; i < maxSize; i++) {
            pool.offer(creator.get());
        }
    }

    public T acquire() throws InterruptedException {
        return pool.take(); // 等待直到有空闲对象
    }

    public void release(T obj) {
        if (obj != null) {
            pool.offer(obj);
        }
    }

    public int availableCount() {
        return pool.size();
    }
}
