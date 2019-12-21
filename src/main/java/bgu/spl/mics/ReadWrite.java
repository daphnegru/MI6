package bgu.spl.mics;

public class ReadWrite {
//    protected int activeReaders;  // threads executing read_
//    protected int waitingReaders;  // always zero or one
//
//    public ReadWrite(){
//        activeReaders=0;
//        waitingReaders=0;
//
//    }
//    protected boolean allowReader() {
//        return waitingReaders == 0 && activeReaders == 0;
//    }
//
//
//    protected void  activeReadersplus(){
//        ++activeReaders;
//    }
//    protected void activeReadersmin(){
//        --activeReaders;
//    }
//
//
//    protected synchronized void beforeread() {
//        ++waitingReaders;
//        while (!allowReader())
//            try { wait(); } catch (InterruptedException ex) {}
//        --waitingReaders;
//        ++activeReaders;
//    }
//
//    protected synchronized void afterRead()  {
//        --activeReaders;
//        notifyAll();  // Will unblock any pending writer
//    }

    protected int activeReaders_ = 0;  // threads executing read_
    protected int activeWriters_ = 0;  // always zero or one
    protected int waitingReaders_ = 0; // threads not yet in read_
    protected int waitingWriters_ = 0; // same for write_
    protected boolean allowReader() {
        return waitingWriters_ == 0 && activeWriters_ == 0;
    }

    protected boolean allowWriter() {
        return activeReaders_ == 0 && activeWriters_ == 0;
    }

    protected synchronized void beforeRead() {
        ++waitingReaders_;
        while (!allowReader())
            try { wait(); } catch (InterruptedException ex) {}
        --waitingReaders_;
        ++activeReaders_;
    }

    protected synchronized void afterRead()  {
        --activeReaders_;
        notifyAll();  // Will unblock any pending writer
    }

    protected synchronized void beforeWrite() {
        ++waitingWriters_;
        while (!allowWriter())
            try { wait(); } catch (InterruptedException ex) {}
        --waitingWriters_;
        ++activeWriters_;
    }

    protected synchronized void afterWrite() {
        --activeWriters_;
        notifyAll();
    }
}