package com.mars.ydd.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class ResetableCountDownLatch extends AbstractQueuedSynchronizer {
    private static final long serialVersionUID = 3047511279800385595L;

    private final int initCount;

    public ResetableCountDownLatch(int count) {
        this.initCount = count;
        setState(count);
    }

    public int getCount() {
        return getState();
    }

    public void resetCount() {
        setState(initCount);
    }

    public void clearCount() {
        setState(0);
    }

    public int tryAcquireShared(int acquires) {
        return getState() == 0 ? 1 : -1;
    }

    public boolean tryReleaseShared(int releases) {
        for (;;) {
            int c = getState();
            if (c == 0)
                return false;
            int nextc = c - 1;
            if (compareAndSetState(c, nextc))
                return nextc == 0;
        }
    }

    public void await() throws InterruptedException {
        acquireSharedInterruptibly(1);
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }

    public void countDown() {
        releaseShared(1);
    }

    public String toString() {
        return super.toString() + "[Count = " + getCount() + "]";
    }
}