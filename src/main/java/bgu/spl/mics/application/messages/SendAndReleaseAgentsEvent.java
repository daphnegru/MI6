package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

import java.util.List;

public class SendAndReleaseAgentsEvent implements Event<List<String>> {
    private List<String> serials;
    private int duration;
    int currTick;


    public SendAndReleaseAgentsEvent (List<String> serials, int duration){
        this.serials=serials;
        this.duration=duration;
    }

    public List<String> getSerials(){
        return serials;
    }

    public int getDuration(){
        return duration;
    }
}
