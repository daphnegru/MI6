package bgu.spl.mics.application.messages;


import bgu.spl.mics.Event;
import java.util.List;

public class AgentsAvailableEvent implements Event<Integer> {

    private List<String> serial;


    public AgentsAvailableEvent (List<String> serial){
        this.serial=serial;
    }

    public List<String> getSerial(){
        return serial;
    }
}