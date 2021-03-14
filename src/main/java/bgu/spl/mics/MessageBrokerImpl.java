package bgu.spl.mics;

import bgu.spl.mics.application.messages.FinalTickBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

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
	private Map<Subscriber, BlockingDeque<Message>> subscribers;
	private Map<Event,Future> futures;
	private static MessageBrokerImpl instance = new MessageBrokerImpl();



	/**
	 * Retrieves the single instance of this class.
	 */
	public MessageBrokerImpl(){
		events= new ConcurrentHashMap<>();
		subscribers= new ConcurrentHashMap<>();
		futures= new ConcurrentHashMap<>();
	}

	public static MessageBrokerImpl getInstance() {
		return instance;
	}


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, Subscriber m) {
		//subscribes the subscriber to the message type
		synchronized (type) {
			if (!events.containsKey(type)) {
				ConcurrentLinkedQueue a = new ConcurrentLinkedQueue<Subscriber>();
				events.put(type, a);
			}
			events.get(type).add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber m) {
		//subscribes the subscriber to the message type
		synchronized (type) {
			if (!events.containsKey(type)) {
				ConcurrentLinkedQueue a = new ConcurrentLinkedQueue<Subscriber>();
				events.put(type, a);
			}
			events.get(type).add(m);
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		futures.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		//sends the broadcast to all the subscribers
		synchronized (b.getClass()) {
			Set<Subscriber> sub = subscribers.keySet();
			//if b is final tick
			if (b.getClass() == FinalTickBroadcast.class) {
				for (Subscriber s : sub) {
					synchronized (s) {
						//adds b first
						subscribers.get(s).addFirst(b);
					}
				}
			} else {
				for (Subscriber s : sub) {
					synchronized (s) {
						subscribers.get(s).add(b);
					}

				}
			}
		}
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		//sends event to the right subscriber
		if (events.containsKey(e.getClass())) {
			Future<T> future = new Future<>();
			futures.put(e, future);
			if (events.get(e.getClass()) != null) {
				synchronized (e.getClass()) {
					Subscriber m = events.get(e.getClass()).poll();
					if (m != null) {
						subscribers.get(m).add(e);
						events.get(e.getClass()).add(m);
					}
				}
				return future;
			}
		}
		return null;

	}

	@Override
	public void register(Subscriber m) {
		subscribers.putIfAbsent(m, new LinkedBlockingDeque<>());
	}

	@Override
	public void unregister(Subscriber m) {
		//unregisters the subscriber
		synchronized (m) {
			Set<Class<? extends Message>> removeEvent = events.keySet();
			for (Class p : removeEvent) {
				events.get(p).remove(m);
			}
			Set<Event> e = futures.keySet();
			for (Event p : e) {
				if (subscribers.get(m).contains(futures.get(p))) {
					futures.get(p).resolve(null);
				}
			}
			subscribers.remove(m);
		}

	}
	@Override
	public Message awaitMessage(Subscriber m) throws InterruptedException {
		return subscribers.get(m).take();
	}

}