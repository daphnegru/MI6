package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBroker;
import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Report;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * M handles ReadyEvent - fills a report and sends agents to mission.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class M extends Subscriber {
	private int id;
	private Diary d;
	private MessageBroker messageBroker;
	private int time;
	private int remaining;
	private List<Object[]> futures;

	public M(int id) {
		super("M" + id);
		d = Diary.getInstance();
		this.id=id;
		messageBroker = MessageBrokerImpl.getInstance();
		time = 0;
		futures = new LinkedList<>();
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(FinalTickBroadcast.class, message->{
			this.terminate();
		});
		subscribeBroadcast(TickBroadcast.class, message ->{
			time = message.getTick();
			remaining = message.getTimel();
		});
		subscribeEvent(MissionReceivedEvent.class,message->{
			//increments the total
			d.incrementTotal();
			List<String> serials = message.getMission().getSerialAgentsNumbers();
			AgentsAvailableEvent availableAgents = new AgentsAvailableEvent(serials,message.getMission().getDuration());
			Future<Object[]> agents = messageBroker.sendEvent(availableAgents);
			Object[] future = agents.get(remaining*100,TimeUnit.MILLISECONDS);
			//checks if there are agents and that they are available
			if (future!=null){
				Integer x = -1;
				if (future[1] != x){
					GadgetAvailableEvent gadgetAvailable = new GadgetAvailableEvent(message.getMission().getGadget());
					Future<Integer> gadget = messageBroker.sendEvent(gadgetAvailable);
					Integer qTime = gadget.get(remaining*100, TimeUnit.MILLISECONDS);
					//checks if gadget exist
					if (gadget!=null && qTime != x&&qTime!=null){
						//checks if there is still time
						if (time < message.getMission().getTimeExpired()){
							//tells the moneypenny to send the agents
							((Future<Boolean>)(future[2])).resolve( true);
							//sets the report
							String name = message.getMission().getMissionName();
							int m = id;
							int moneypenny = (Integer)future[1];
							List<String> names = (List<String>)future[0];
							String gadgetName = message.getMission().getGadget();
							int issued = message.getMission().getTimeIssued();
							Integer q = (Integer)gadget.get();
							Report report = new Report(name,m,message.getMission().getSerialAgentsNumbers(),names,moneypenny,gadgetName,issued,q,time);
							d.addReport(report);
							complete(message,true);
						}
						else {
							//if there is no time
							((Future<Boolean>)(future[2])).resolve( false);
							complete(message,false);
						}
					}
					else {
						//if there is no gadget
						((Future<Boolean>)(future[2])).resolve( false);
						complete(message,false);
					}
				}
				else {
					//if there are no agents
					((Future<Boolean>)(future[2])).resolve( false);
					complete(message,false);
				}
			}
			else {
				//mission failed
				complete(message,false);
			}

		});



	}

}