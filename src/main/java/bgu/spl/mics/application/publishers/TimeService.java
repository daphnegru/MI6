package bgu.spl.mics.application.publishers;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MessageBroker;
import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.Publisher;
import bgu.spl.mics.application.messages.FinalTickBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * TimeService is the global system timer There is only one instance of this Publisher.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other subscribers about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends Publisher {
	private final int tick = 100;
	private int duration;
	private Timer timer;
	private int tickCount;


	public TimeService(int duration) {
		super("TimeService");
		this.duration=duration;
		timer = new Timer();
		tickCount=0;
	}

	@Override
	protected void initialize() {
		run();
	}

	@Override
	public void run() {
		timer.schedule(new TimerTask(){
			public void run() {
				if(tickCount>duration){
					Broadcast finalTick = new FinalTickBroadcast();
					getSimplePublisher().sendBroadcast(finalTick);
					timer.cancel();
					timer.purge();
				}
				else {
					Broadcast broadcast = new TickBroadcast(tickCount++);
					getSimplePublisher().sendBroadcast(broadcast);
				}
			}
		},tick,tick);
	}

}