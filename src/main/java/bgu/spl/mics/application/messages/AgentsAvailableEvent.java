package bgu.spl.mics.application.messages;


import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Agent;

import java.util.List;
import java.util.Map;

public class AgentsAvailableEvent implements Event<Object[]> {

    private List<String> serial;
    private int duration;

    public AgentsAvailableEvent (List<String> serial, int duration){
        this.duration=duration;
        this.serial=serial;
    }

    public List<String> getSerial(){
        return serial;
    }

    public int getDuration(){
        return duration;
    }
}