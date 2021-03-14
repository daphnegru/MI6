package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Future;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Squad;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
	private Future<Boolean> result;
	private int remaining;

	public Moneypenny(int id) {
		super("Moneypenny" + id);
		s = Squad.getInstance();
		this.id=id;
		currTick=0;
		result = new Future<Boolean>();
	}

	public int getCurrTick(){
		return currTick;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class,tick->{

			currTick=tick.getTick();
			remaining=tick.getTimel();
		});

		subscribeEvent(AgentsAvailableEvent.class, message ->{
			//checks if there are agents
			boolean available = s.getAgents(message.getSerial());
			List<String> names = s.getAgentsNames(message.getSerial());
			Integer currId = id;
			Object[] report = new Object[3];
			report[0] = names;
			report[1]=currId;
			report[2] = result;
			//if there are agents
			if (available){
				complete(message,report);
			}
			else {
				Integer x = -1;
				report[1] = x;
				complete(message,report);
			}
			//checks if there is gadget and the time is good
			Boolean ans = result.get(remaining*100,TimeUnit.MILLISECONDS);
			if (ans!=null && ans) {
					s.sendAgents(message.getSerial(), message.getDuration());
			}
			else {
				if (available) {
						s.releaseAgents(message.getSerial());
					}
				}

		});

		subscribeBroadcast(FinalTickBroadcast.class, message ->{
			result.resolve(null);
			this.terminate();
		});
	}
}