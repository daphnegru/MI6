package bgu.spl.mics.application.passiveObjects;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the diary where all reports are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Diary {
	private List<Report> reports;
	private static AtomicInteger total;

	/**
	 * Retrieves the single instance of this class.
	 */
	private Diary(){
		reports=new LinkedList<Report>();
		total = new AtomicInteger(0);
	}
	private static class SingletonHolder{
		private static Diary diary= new Diary();
	}

	public static Diary getInstance() {
		return SingletonHolder.diary;
	}

	public List<Report> getReports() {
		return reports;
	}

	/**
	 * adds a report to the diary
	 * @param reportToAdd - the report to add
	 */
	public void addReport(Report reportToAdd){
		reports.add(reportToAdd);
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object List<Report> which is a
	 * List of all the reports in the diary.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printToFile(String filename){
		//make sure it works
		Gson g = new Gson();
		String diary = g.toJson(reports);
		try (Writer write = new FileWriter(filename)){
			write.write(diary);
			write.write(total.get());
		}
		catch (Exception e){
			System.out.println("filename incorrect");
		}
	}

	/**
	 * Gets the total number of received missions (executed / aborted) be all the M-instances.
	 * @return the total number of received missions (executed / aborted) be all the M-instances.
	 */
	public int getTotal(){
		return total.get();
	}

	public void incrementTotal(){
		total.incrementAndGet();
	}
}