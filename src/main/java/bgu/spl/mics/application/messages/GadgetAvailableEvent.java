package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class GadgetAvailableEvent implements Event<Integer> {
    String name;

    public GadgetAvailableEvent(String name){
        this.name=name;

    }

    public String getName(){
        return name;
    }
}