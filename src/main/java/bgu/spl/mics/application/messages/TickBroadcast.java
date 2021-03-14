package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {

    private int tick;
    private int timel;
    public TickBroadcast(int tick, int timel){
        this.tick=tick;
        this.timel=timel;
    }

    public int getTick() {
        return tick;
    }
    public int getTimel(){return timel;}

}