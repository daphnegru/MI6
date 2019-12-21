package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import javafx.util.Pair;

public class GadgetAvailableEvent implements Event<Integer> {
    String name;


    public GadgetAvailableEvent(String name){
        this.name=name;

    }

    public String getName(){
        return name;
    }
}