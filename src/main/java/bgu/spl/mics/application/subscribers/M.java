package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBroker;
import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Report;
import java.util.List;

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
		subscribeBroadcast(TickBroadcast.class, message ->{
			time = message.getTick();
		});
		subscribeEvent(MissionReceivedEvent.class,message->{
			List<String> serials = message.getSerialAgentsNumbers();
			AgentsAvailableEvent availableEvent = new AgentsAvailableEvent(serials);
			Future<Integer> availableFuture = messageBroker.sendEvent(availableEvent);
			if (availableFuture != null) {
				Integer moneypenny = availableFuture.get();
				if (moneypenny != null) {
					if (moneypenny != -1) {
						GadgetAvailableEvent gadgetAvailableEvent = new GadgetAvailableEvent(message.getGadget());
						Future<Integer> availableGadget = messageBroker.sendEvent(gadgetAvailableEvent);
						if (availableGadget != null) {
							Integer qTime = availableGadget.get();
							if (qTime != null) {
								if (qTime != -1) {
									if (message.getTimeExpired() < time) {
										SendAndReleaseAgentsEvent sendAgentsEvent = new SendAndReleaseAgentsEvent(serials, message.getDuration());
										Future<List<String>> sendAgents = messageBroker.sendEvent(sendAgentsEvent);
										Report report = new Report(message.getMissionName(), id, serials, sendAgents.get(), moneypenny, gadgetAvailableEvent.getName(), message.getTimeIssued(), qTime, time);
										d.addReport(report);
										complete(message, id);
									}
									else {
										ReleaseAgentsEvent releaseAgentsEvent = new ReleaseAgentsEvent(serials);
										Future<Integer> releaseAgents = messageBroker.sendEvent(releaseAgentsEvent);
										complete(message, null);
									}
								}
								else {
									ReleaseAgentsEvent releaseAgentsEvent = new ReleaseAgentsEvent(serials);
									Future<Integer> releaseAgents = messageBroker.sendEvent(releaseAgentsEvent);
									complete(message, null);
								}
							}
						}
					}
					else {
						complete(message,null);
					}
				}
			}
			else {
				complete(message,null);
			}
		});
		subscribeBroadcast(FinalTickBroadcast.class, message -> this.terminate());
		d.incrementTotal();
	}

}