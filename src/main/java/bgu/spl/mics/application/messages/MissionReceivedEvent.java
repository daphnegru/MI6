package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

import java.util.List;

public class MissionReceivedEvent implements Event<Integer> {

    private String missionName;
    private List<String> serialAgentsNumbers;
    private String gadget;
    private int timeIssued;
    private int timeExpired;
    private int duration;


    public MissionReceivedEvent (String missionName, List<String> serialAgentsNumbers, String gadget, int timeExpired, int timeIssued, int duration){
        this.missionName=missionName;
        this.serialAgentsNumbers = serialAgentsNumbers;
        this.gadget=gadget;
        this.timeIssued = timeIssued;
        this.timeExpired = timeExpired;
        this.duration = duration;

    }

    public String getMissionName(){
        return missionName;
    }

    public List<String> getSerialAgentsNumbers(){
        return serialAgentsNumbers;
    }

    public String getGadget(){
        return gadget;
    }

    public int getTimeIssued(){
        return timeIssued;
    }

    public int getTimeExpired(){
        return timeExpired;
    }

    public int getDuration(){
        return duration;
    }
}