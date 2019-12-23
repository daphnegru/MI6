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
							String gadgetName = message.getMission().getGadget();
							int issued = message.getMission().getTimeIssued();
							Integer q = (Integer)gadget.get();
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
//			List<String> serials = message.getMission().getSerialAgentsNumbers();
//			AgentsAvailableEvent availableEvent = new AgentsAvailableEvent(serials);
//			Future<Integer> availableFuture = messageBroker.sendEvent(availableEvent);
//			if(availableFuture!=null){
//				Integer moneypenny = availableFuture.get();
//				if (moneypenny!=null){
//					if(moneypenny==-1)
//						complete(message,null);
//					else{
//						GadgetAvailableEvent gadgetAvailableEvent = new GadgetAvailableEvent(message.getMission().getGadget());
//						Future<Integer> availableGadget = messageBroker.sendEvent(gadgetAvailableEvent);
//						if(availableGadget!=null){
//							int qTime = availableGadget.get();
//							if (availableGadget.get()!=-1){
//								if (message.getMission().getTimeExpired()<time){
//									SendAndReleaseAgentsEvent sendAgentsEvent = new SendAndReleaseAgentsEvent(serials,message.getMission().getDuration());
//									Future<List<String>> sendAgents = messageBroker.sendEvent(sendAgentsEvent);
//									Report report = new Report(message.getMission().getMissionName(),id,serials,sendAgents.get(),moneypenny,gadgetAvailableEvent.getName(),message.getMission().getTimeIssued(),qTime,time);
//						d.addReport(report);
//						complete(message,id);
//					}
//					else {
//						ReleaseAgentsEvent releaseAgentsEvent = new ReleaseAgentsEvent(serials);
//						Future<Integer> releaseAgents = messageBroker.sendEvent(releaseAgentsEvent);
//						complete(message,-1);
//					}
//				}
//				else {
//					ReleaseAgentsEvent releaseAgentsEvent = new ReleaseAgentsEvent(serials);
//					Future<Integer> releaseAgents = messageBroker.sendEvent(releaseAgentsEvent);
//					complete(message,-1);
//				}
//			}
//			else {
//				complete(message,-1);
//			}
		});
		subscribeBroadcast(FinalTickBroadcast.class, message -> this.terminate());
	}

}