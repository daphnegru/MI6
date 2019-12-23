package bgu.spl.mics.application;

import java.util.concurrent.atomic.AtomicInteger;
/**
 * This class counts all the services that have been initialized.
 * **/
public class ThreadCounter {

    private AtomicInteger count;

    private ThreadCounter() {
        count = new AtomicInteger(0);
    }

    public static ThreadCounter GetInstance(){
        return ThreadCounterHolder.threadCounter;
    }

    public AtomicInteger getCount() {
        return count;
    }

    public void increase(){
        count.incrementAndGet();
    }

    private static class ThreadCounterHolder{
        private static ThreadCounter threadCounter = new ThreadCounter();
    }
}