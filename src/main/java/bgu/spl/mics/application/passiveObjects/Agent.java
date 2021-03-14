package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.Semaphore;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Agent {
	private String name;
	private String sNumber;
	private Boolean available;
	private Semaphore s;

	public Agent(String name,String serialNumber){
		this.name=name;
		this.sNumber=serialNumber;
		this.available=true;
		s = new Semaphore(1,true);
	}

	/**
	 * Sets the serial number of an agent.
	 */
	public void setSerialNumber(String serialNumber) {
		this.sNumber=serialNumber;
	}

	/**
	 * Retrieves the serial number of an agent.
	 * <p>
	 * @return The serial number of an agent.
	 */
	public String getSerialNumber() {
		return this.sNumber;
	}

	/**
	 * Sets the name of the agent.
	 */
	public void setName(String name) {
		this.name=name;
	}

	/**
	 * Retrieves the name of the agent.
	 * <p>
	 * @return the name of the agent.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves if the agent is available.
	 * <p>
	 * @return if the agent is available.
	 */
	public boolean isAvailable() {
		return available;
	}

	/**
	 * Acquires an agent.
	 */
	public void acquire() {
		try {
			s.acquire();
		}
		catch (Exception e){
			System.out.println("agent");
		}
		available=false;
	}

	/**
	 * Releases an agent.
	 */
	public void release(){
		available=true;
		s.release();
	}
}