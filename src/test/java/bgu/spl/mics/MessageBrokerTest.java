package bgu.spl.mics;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class MessageBrokerTest {
    MessageBroker message;

    @BeforeEach
    public void setUp(){
        message= MessageBrokerImpl.getInstance();
    }

    @AfterEach
    public void tearDown(){
        message=null;
    }

    @Test
    public void subscribeEvent(){

    }

    @Test
    public void subscribeBroadcast(){

    }

    @Test
    public void complete(){

    }

    @Test
    public void sendBroadcast(){

    }

    @Test
    public void sendEvent(){

    }

    @Test
    public void register(){

    }

    @Test
    public void unregister(){

    }

    @Test
    public void awaitMessage(){

    }
}