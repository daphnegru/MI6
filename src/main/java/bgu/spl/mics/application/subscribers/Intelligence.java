package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBroker;
import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.FinalTickBroadcast;
import bgu.spl.mics.application.messages.MissionReceivedEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

import javax.swing.plaf.synth.SynthDesktopIconUI;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A Publisher\Subscriber.
 * Holds a list of Info objects and sends them
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence extends Subscriber {
	List<MissionInfo> missionInfos;
	private MessageBroker messageBroker;
	private int id;

	public Intelligence(int id) {
		super("Intelligence" + id);
		missionInfos = new CopyOnWriteArrayList<>();
		messageBroker = MessageBrokerImpl.getInstance();
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(FinalTickBroadcast.class,(FinalTickBroadcast finalTickBroadcast)->{
			this.terminate();
		});
		subscribeBroadcast(TickBroadcast.class,(TickBroadcast tickBroadcast)->{
			//finds the mission with currtick if exists
			for(MissionInfo mission: missionInfos){
				if(tickBroadcast.getTick()==mission.getTimeIssued()){
					//sends the mission message
					MissionReceivedEvent m = new MissionReceivedEvent(mission);
					Future<Integer> intel = messageBroker.sendEvent(m);
				}
			}
		});
	}

	public void setMissionInfos(List<MissionInfo> list){
		this.missionInfos=list;
	}
}