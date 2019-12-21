package bgu.spl.mics.application;

import java.util.concurrent.atomic.AtomicInteger;
/**
 * This class counts all the services that have been initialized.
 * **/
public class ThreadCounter {

    private AtomicInteger counter;

    private ThreadCounter() {
        counter = new AtomicInteger(0);
    }

    public static ThreadCounter GetInstance(){
        return ThreadCounterHolder.threadCounter;
    }

    public AtomicInteger getCounter() {
        return counter;
    }

    public void increase(){
        counter.incrementAndGet();
    }

    private static class ThreadCounterHolder{
        private static ThreadCounter threadCounter = new ThreadCounter();
    }
}