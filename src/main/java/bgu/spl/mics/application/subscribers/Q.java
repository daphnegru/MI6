package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.FinalTickBroadcast;
import bgu.spl.mics.application.messages.GadgetAvailableEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;


/**
 * Q is the only Subscriber\Publisher that has access to the {@link bgu.spl.mics.application.passiveObjects.Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Q extends Subscriber {
	private Inventory inventory;
	private int currTick;

	public Q() {
		super("Q");
		inventory=Inventory.getInstance();
		currTick=0;
	}

	public int getCurrTick(){
		return currTick;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, tick->{
			currTick=tick.getTick();
		});

		subscribeEvent(GadgetAvailableEvent.class,message ->{
			//checks if the gadget is in inventory
			boolean found = inventory.getItem(message.getName());
			//if so sends the currtick back to m for report
			if (found){
				complete(message,currTick);
			}
			//sends -1
			else{
				complete(message,-1);
			}
		});

		subscribeBroadcast(FinalTickBroadcast.class, message-> this.terminate());
	}

}