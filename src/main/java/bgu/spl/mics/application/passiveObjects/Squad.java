package bgu.spl.mics.application.passiveObjects;
import java.util.*;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Squad {
	private Map<String, Agent> agents;

	/**
	 * Retrieves the single instance of this class.
	 */
	private Squad(){
		agents=new HashMap<>();
	}


	private static class SingletonHolder{
		private static Squad squad= new Squad();
	}

	public static Squad getInstance() {
		return SingletonHolder.squad;
	}

	/**
	 * Initializes the squad. This method adds all the agents to the squad.
	 * <p>
	 * @param agents 	Data structure containing all data necessary for initialization
	 * 						of the squad.
	 */
	public void load (Agent[] agents) {
		for(int i=0;i<agents.length;i++){
			String serial= agents[i].getSerialNumber();
			this.agents.put(serial,agents[i]);
		}
	}

	/**
	 * Releases agents.
	 */
	public void releaseAgents(List<String> serials){
		for(int i=0;i<serials.size();i++){
			agents.get(serials.get(i)).release();
			synchronized (this) {
				notifyAll();
			}
		}
	}

	/**
	 * simulates executing a mission by calling sleep.
	 * @param time   milliseconds to sleep
	 */
	public void sendAgents(List<String> serials, int time){
		long t = time;
		for (int i = 0; i<serials.size(); i++){
			Agent a = agents.get(serials.get(i));
			synchronized (a){
				try{
					Thread.sleep(t);
				}
				catch (InterruptedException e) {}
			}
		}
		releaseAgents(serials);
	}

	/**
	 * acquires an agent, i.e. holds the agent until the caller is done with it
	 * @param serials   the serial numbers of the agents
	 * @return ‘false’ if an agent of serialNumber ‘serial’ is missing, and ‘true’ otherwise
	 */
	public boolean getAgents(List<String> serials){
		for (int i = 0; i<serials.size();i++){
			if (!agents.containsKey(serials.get(i))){
				return false;
			}
		}

		for (int i = 0; i <serials.size();i++){
			Agent a = agents.get(serials.get(i));
			synchronized (a){
				while(!a.isAvailable()){
					try{
						a.wait();
					}
					catch (InterruptedException e) {}
				}
				a.acquire();
			}
		}
		return true;
	}

	/**
	 * gets the agents names
	 * @param serials the serial numbers of the agents
	 * @return a list of the names of the agents with the specified serials.
	 */
	public List<String> getAgentsNames(List<String> serials){
		List<String> agentsNames= new LinkedList<>();
		for(int i=0;i<serials.size();i++){
			if(agents.containsKey(serials.get(i))){
				agentsNames.add(agents.get(i).getName());
			}
		}
		return agentsNames;
	}

}
