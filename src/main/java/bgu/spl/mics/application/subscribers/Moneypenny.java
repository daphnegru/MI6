package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Squad;

/**
 * Only this type of Subscriber can access the squad.
 * Three are several Moneypenny-instances - each of them holds a unique serial number that will later be printed on the report.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Moneypenny extends Subscriber {

	Squad s;
	private int id;
	private int currTick;

	public Moneypenny(int id) {
		super("Moneypenny" + id);
		s = Squad.getInstance();
		this.id=id;
		currTick=0;
	}

	public int getCurrTick(){
		return currTick;
	}

	public int getId(){
		return id;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class,tick->{
			currTick=tick.getTick();
		});
		subscribeEvent(AgentsAvailableEvent.class, message -> {
			boolean available = s.getAgents(message.getSerial());
			if (available){
				complete(message,id);
			}
			else {
				complete(message,-1);
			}
		});
		subscribeEvent(SendAndReleaseAgentsEvent.class,message -> {
			s.sendAgents(message.getSerials(),message.getDuration());
			s.releaseAgents(message.getSerials());
			complete(message,s.getAgentsNames(message.getSerials()));
		});
		subscribeEvent(ReleaseAgentsEvent.class, message -> {
			s.releaseAgents(message.getSerials());
			complete(message,id);
		});
		subscribeBroadcast(FinalTickBroadcast.class, message -> this.terminate());
	}

}