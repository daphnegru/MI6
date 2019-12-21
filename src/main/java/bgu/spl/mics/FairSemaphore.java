package bgu.spl.mics;

import java.util.LinkedList;

class FairSemaphore {
    private final int permits;
    private int free;
    private final LinkedList<Thread> threads;

    public FairSemaphore(int permits) {
        this.permits = permits;
        free = permits;
        threads = new LinkedList<Thread>();
    }

    public synchronized void acquire() throws InterruptedException {
        Thread current = Thread.currentThread();
        threads.add(current);
        while (threads.peek() != current || free <= 0) {
            wait();
        }
        free--;
        threads.removeFirst();
        notifyAll();
    }

    public synchronized void release() throws InterruptedException {
        if (free < permits) {
            free++;
            notifyAll();
        }
    }

    public synchronized boolean tryAcquire(){
        if (free>0){
            free--;
            return true;
        }
        else {
            return false;
        }
    }
}
