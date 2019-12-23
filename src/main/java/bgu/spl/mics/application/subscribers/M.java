package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBroker;
import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Report;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

	public M(int id) {
		super("M" + id);
		d = Diary.getInstance();
		this.id=id;
		messageBroker = MessageBrokerImpl.getInstance();
		time = 0;
	}

	@Override
	protected void initialize() {
		d.incrementTotal();
		subscribeBroadcast(TickBroadcast.class, message ->{
			time = message.getTick();
		});

		subscribeEvent(MissionReceivedEvent.class,message->{
			List<String> serials = message.getMission().getSerialAgentsNumbers();
			AgentsAvailableEvent availableAgents = new AgentsAvailableEvent(serials,message.getMission().getDuration());
			Future<Object[]> agents = messageBroker.sendEvent(availableAgents);
			if (agents!=null){
				Object[] future = agents.get();
				Integer x = -1;
				if (future[1] != x){
					GadgetAvailableEvent gadgetAvailable = new GadgetAvailableEvent(message.getMission().getGadget());
					Future<Integer> gadget = messageBroker.sendEvent(gadgetAvailable);
					if (gadget!=null && gadget.get() != x){
						if (time < message.getMission().getTimeExpired()){
							((Future<Boolean>)(future[2])).resolve( true);
							String name = message.getMission().getMissionName();
							int m = id;
							int moneypenny = (Integer)future[1];
							List<String> names = (List<String>)future[0];
							//System.out.println(name);
							String gadgetName = message.getMission().getGadget();
							int issued = message.getMission().getTimeIssued();
							Integer q = (Integer)gadget.get();
							System.out.println("mtime "+time);
							Report report = new Report(name,m,message.getMission().getSerialAgentsNumbers(),names,moneypenny,gadgetName,issued,q,time);
							d.addReport(report);
							complete(message,true);
						}
					}
					else {
						((Future<Boolean>)(future[2])).resolve( false);
						complete(message,false);
					}
				}
				else {
					((Future<Boolean>)(future[2])).resolve( false);
					complete(message,false);
				}
			}
			else {
				complete(message,false);
			}

		});

		subscribeBroadcast(FinalTickBroadcast.class, message -> this.terminate());
	}

}