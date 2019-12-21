package bgu.spl.mics.application.passiveObjects;

import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *  That's where Q holds his gadget (e.g. an explosive pen was used in GoldenEye, a geiger counter in Dr. No, etc).
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {
	private List<String> gadgets;
	/**
	 * Retrieves the single instance of this class.
	 */
	private Inventory(){
		gadgets=new CopyOnWriteArrayList<String>();
	}

	private static class SingletonHolder {
		private static Inventory instance = new Inventory();
	}

	public static Inventory getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * Initializes the inventory. This method adds all the items given to the gadget
	 * inventory.
	 * <p>
	 * @param inventory 	Data structure containing all data necessary for initialization
	 * 						of the inventory.
	 */
	public void load (String[] inventory) {
		for(int i=0;i<inventory.length;i++){
			gadgets.add(inventory[i]);
		}
	}

	/**
	 * acquires a gadget and returns 'true' if it exists.
	 * <p>
	 * @param gadget 		Name of the gadget to check if available
	 * @return 	‘false’ if the gadget is missing, and ‘true’ otherwise
	 */
	public boolean getItem(String gadget) {
		synchronized (gadget) {
			if (gadgets.contains(gadget)) {
				this.gadgets.remove(gadget);
				return true;
			}
			notifyAll();
			return false;
		}
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object List<String> which is a
	 * list of all the of the gadgeds.
	 * This method is called by the main method in order to generate the output.
	 */


	public void printToFile(String filename){
		Gson g = new Gson();
		String inventory = g.toJson(gadgets);
		try (Writer write = new FileWriter(filename)){
			write.write(inventory);
		}
		catch (Exception e){
			System.out.println("filename incorrect");
		}
	}

}