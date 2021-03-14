package bgu.spl.mics.application.passiveObjects;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
		agents=new HashMap<String, Agent>();
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
		//loads the agents
		for(int i=0;i<agents.length;i++){
			String serial= agents[i].getSerialNumber();
			this.agents.put(serial,agents[i]);
		}
	}

	/**
	 * Releases agents.
	 */
	public synchronized void releaseAgents(List<String> serials){
		//releases the agents
		for(int i=0;i<serials.size();i++){
			if(agents.containsKey(serials.get(i))){
				agents.get(serials.get(i)).release();
			}
		}
	}

	/**
	 * simulates executing a mission by calling sleep.
	 * @param time   milliseconds to sleep
	 */
	public void sendAgents(List<String> serials, int time){
		//sends the agents
		try{
			//simulates the time that the mission takes place
			Thread.sleep(time*100);
		}
		catch (Exception e){
			Thread.currentThread().interrupt();
		}
		//sends the agents to be released
		releaseAgents(serials);
	}

	/**
	 * acquires an agent, i.e. holds the agent until the caller is done with it
	 * @param serials   the serial numbers of the agents
	 * @return ‘false’ if an agent of serialNumber ‘serial’ is missing, and ‘true’ otherwise
	 */
	public boolean getAgents(List<String> serials){
		//checks if the agents are in squad
		for (int i = 0; i<serials.size();i++){
			if (!agents.containsKey(serials.get(i))){
				return false;
			}
		}
		//after all the agents are in squad he acquires then
		for (int i = 0; i < serials.size(); i++) {
			Agent a = agents.get(serials.get(i));
			a.acquire();
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
				Agent a = agents.get(serials.get(i));
				String name = a.getName();
				agentsNames.add(name);
			}
		}
		return agentsNames;
	}
}