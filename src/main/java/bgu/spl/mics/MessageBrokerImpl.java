package bgu.spl.mics;

import bgu.spl.mics.application.messages.FinalTickBroadcast;
import bgu.spl.mics.application.messages.ReleaseAgentsEvent;
import bgu.spl.mics.application.messages.SendAndReleaseAgentsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.subscribers.Moneypenny;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;


/**
 * The {@link MessageBrokerImpl class is the implementation of the MessageBroker interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */


public class MessageBrokerImpl implements MessageBroker {
	private Map<Class<? extends Message>, ConcurrentLinkedQueue<Subscriber>> events;
	private Map<Subscriber, ConcurrentLinkedQueue<Message>> subscribers;
	private Map<Event,Future> futures;
	private Moneypenny moneypenny;



	/**
	 * Retrieves the single instance of this class.
	 */
	public MessageBrokerImpl(){
		events= new ConcurrentHashMap<>();
		subscribers= new ConcurrentHashMap<>();
		futures= new ConcurrentHashMap<>();

	}

	private static class MessageBrokerHolder{
		private static MessageBrokerImpl messageBroker = new MessageBrokerImpl();
	}

	public static MessageBroker getInstance() {
		return MessageBrokerHolder.messageBroker;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, Subscriber m) {
		synchronized (type) {
			if (m == moneypenny) {
				if (type.equals(ReleaseAgentsEvent.class) || type.equals(SendAndReleaseAgentsEvent.class)) {
					if (!events.containsKey(type)) {
						ConcurrentLinkedQueue a = new ConcurrentLinkedQueue<Subscriber>();
						events.put(type, a);
					}
					events.get(type).add(m);
				}
			}
			else if (m != moneypenny) {
				if (!type.equals(ReleaseAgentsEvent.class) && !type.equals(SendAndReleaseAgentsEvent.class)) {
					if (!events.containsKey(type)) {
						ConcurrentLinkedQueue a = new ConcurrentLinkedQueue<Subscriber>();
						events.put(type, a);
					}
					events.get(type).add(m);
//					System.out.println("subscribeEvent: "+type.getName() + m.getName());
				}
			}
		}
	}


	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber m) {
		if (!events.containsKey(type)) {
			ConcurrentLinkedQueue a = new ConcurrentLinkedQueue<Subscriber>();
			events.put(type, a);
		}
		events.get(type).add(m);
//		System.out.println("subscribeBroadcast: "+type.getName() + m.getName());
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		futures.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
//		ConcurrentHashMap.KeySetView<Subscriber, BlockingQueue<Message>> subs = (ConcurrentHashMap.KeySetView<Subscriber, BlockingQueue<Message>>) subscribers.keySet();
//		if (b.getClass() == FinalTickBroadcast.class) {
//			for (Subscriber s : subs) {
//				subscribers.get(s).clear();
//				subscribers.get(s).add(b);
////				System.out.println("sendBroadcast: " + s.getName() + b.getClass().getName());
//			}
//		}
//		else{
//			for (Subscriber s : subs) {
////				System.out.println(subscribers.get(s));
//				subscribers.get(s).add(b);
////				System.out.println("sendBroadcast: " + s.getName() + b.getClass().getName());
//			}
//		}

		if (b.getClass()==FinalTickBroadcast.class){
			for (Subscriber s: subscribers.keySet()){
				subscribers.get(s).clear();
				subscribers.get(s).add(b);
			}
		}
		else {
			for (Subscriber s: subscribers.keySet()){
				subscribers.get(s).add(b);
			}
		}

	}




	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		if(events.containsKey(e.getClass())) {
			Future<T> future = new Future<>();
			futures.put(e, future);
			Subscriber m = events.get(e.getClass()).poll();
			if (m != null) {
				subscribers.get(m).add(e);
				events.get(e.getClass()).add(m);
//				System.out.println("sendEvent" + e.getClass().getName() + m.getName());
			}
			return future;
		}

		return null;
	}

	@Override
	public void register(Subscriber m) {
		if(m instanceof Moneypenny && moneypenny==null){
			moneypenny=(Moneypenny)m;
			subscribers.put(moneypenny,new ConcurrentLinkedQueue<>());
//			System.out.println("moneypenny reg");
		}
		else
			subscribers.putIfAbsent(m, new ConcurrentLinkedQueue<>());
//		System.out.println(m.getName() + " reg");
	}

	@Override
	public void unregister(Subscriber m) {
		Set<Class<? extends Message>> removeEvent= events.keySet();
		for (Class p:removeEvent) {
			events.get(p).remove(m);
		}
		Set<Event> e=futures.keySet();
		for(Event p:e){
			if(subscribers.get(m).contains(futures.get(p))){
				futures.get(p).resolve(null);
			}
		}
		subscribers.remove(m);
	}
//	public void unregister(Subscriber m) {
//		for (int i = 0; i < events.size(); i++) {
//			events.get(i).remove(m);
//		}
//		for(int i=0;i<futures.size();i++){
//			if(subscribers.get(m).contains(futures.get(i))) {
//				futures.get(i).resolve(null);
//			}
//		}
//		subscribers.remove(m);
//	}
	@Override
	public Message awaitMessage(Subscriber m) throws InterruptedException {
		synchronized (subscribers.get(m)) {
			while (subscribers.get(m).isEmpty())
				subscribers.get(m).wait();
			return subscribers.get(m).poll();
		}
//		return subscribers.get(m).poll();
	}
}