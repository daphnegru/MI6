package bgu.spl.mics;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class FutureTest {
    Future<String> future;
    String ans;

    @BeforeEach
    public void setUp(){
        future = new Future<>();
        ans = "done";
    }

    @AfterEach
    public void tearDown(){
        future=null;
        ans=null;
    }

    @Test
    public void get(){
        future.resolve(ans);
        assertEquals(ans,future.get());
    }

    @Test
    public void resolve(){
        future.resolve(ans);
        assertEquals(ans,future.get());
    }

    @Test
    public void isDone(){
        future.resolve(ans);
        assertEquals(true,future.isDone());
    }

    @Test
    public void getTime(){
        future.resolve(ans);
        long curr = System.currentTimeMillis();
        String t = future.get(1000, TimeUnit.MILLISECONDS);
        long after = System.currentTimeMillis();
        long def = after-curr;
        assert (1000<=def);
    }
}