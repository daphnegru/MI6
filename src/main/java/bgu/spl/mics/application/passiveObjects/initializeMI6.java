package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.ThreadCounter;
import bgu.spl.mics.application.publishers.TimeService;
import bgu.spl.mics.application.subscribers.Intelligence;
import bgu.spl.mics.application.subscribers.M;
import bgu.spl.mics.application.subscribers.Moneypenny;
import bgu.spl.mics.application.subscribers.Q;
import com.google.gson.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**initializes all the subscribers/publishers
 *writes to the output files
 *
 */

public class initializeMI6 {

    public void run(String filePath) {
        //starts reading the json input
        BufferedReader buffer;
        Gson g;
        JsonObject mi6 = new JsonObject();
        try {
            buffer = new BufferedReader(new FileReader(filePath));
            g = new Gson();
            mi6 = g.fromJson(buffer, JsonObject.class);
        } catch (Exception e) {
            System.out.println("file not found");
        }
        //builds the matrix of all the subscribers
        Subscriber[][] subs = new Subscriber[4][];
        //reads from the json
        JsonArray inventory = mi6.getAsJsonArray("inventory");
        JsonObject services = mi6.getAsJsonObject("services");
        JsonArray squad = mi6.getAsJsonArray("squad");
        //creates the inventory
        Inventory.getInstance().load(addToInventory(inventory));
        //creates the squad
        Squad.getInstance().load(addToSquad(squad));
        int numOfM = services.get("M").getAsInt();
        int numOfMoneyPenny = services.get("Moneypenny").getAsInt();
        JsonArray intelligence = services.getAsJsonArray("intelligence");
        int time = services.get("time").getAsInt();
        //creates the ms
        M[] msubs = setMs(numOfM);
        //creates the moneypennys
        Moneypenny[] moneypennnySubs = setMoneypennys(numOfMoneyPenny);
        //creates the intelligences
        Intelligence[] intelligenceSubs = setIntelligence(intelligence);
        //creates the q
        Q[] q = {new Q()};
        subs[0] = intelligenceSubs;
        subs[2] = q;
        subs[1] = msubs;
        subs[3] = moneypennnySubs;
        //calculates the number of subscribers
        int numOfSubsAndPubs = numOfM + numOfMoneyPenny + intelligenceSubs.length + q.length;
        //creates the threads and starts them
        Thread[] threads = new Thread[numOfSubsAndPubs+1];
        int index = 0;
        for (int i = 0; i <subs.length;i++){
            for (int j = 0; j<subs[i].length;j++){
                Thread thread = new Thread(subs[i][j]);
                threads[index] = thread;
                thread.setName(subs[i][j].getName());
                thread.start();
                index++;
            }
        }
        //makes sure to not start the time thread before all threads are initiliaze
        ThreadCounter threadCounter = ThreadCounter.GetInstance();

        while (numOfSubsAndPubs != threadCounter.getCount().get()){

        }
        //starts time thread
        TimeService timeService = new TimeService(time);
        Thread timeThread = new Thread(timeService);
        threads[index] = timeThread;
        timeThread.setName(timeService.getName());
        timeThread.start();

        //kills the threads
        for (int i =0; i<threads.length;i++){
            try {
                threads[i].join();
            }
            catch (Exception e){
            }
        }
    }

    public String[] addToInventory(JsonArray inventory) {
        String[] gadgets = new String[inventory.size()];
        for (int i = 0; i < inventory.size(); i++) {
            gadgets[i] = inventory.get(i).getAsString();
        }
        return gadgets;
    }

    public Agent[] addToSquad(JsonArray squad) {
        Agent[] agents = new Agent[squad.size()];
        for (int i = 0; i < squad.size(); i++) {
            JsonObject agent = squad.get(i).getAsJsonObject();
            for (int j = 0; j < agent.size(); j++) {
                String name = agent.get("name").getAsString();
                String serialNumber = agent.get("serialNumber").getAsString();
                Agent a = new Agent(name, serialNumber);
                agents[i] = a;
            }
        }
        return agents;
    }

    public M[] setMs(int num) {
        M[] mSubs = new M[num];
        for (int i = 0; i < num; i++) {
            M newM = new M(i);
            mSubs[i] = newM;
        }
        return mSubs;
    }

    public Moneypenny[] setMoneypennys(int num) {
        Moneypenny[] moneypennySubs = new Moneypenny[num];
        for (int i = 0; i < num; i++) {
            Moneypenny newMoneypenny = new Moneypenny(i);
            moneypennySubs[i] = newMoneypenny;
        }
        return moneypennySubs;
    }

    public Intelligence[] setIntelligence(JsonArray intelligence) {
        Intelligence[] intelligences = new Intelligence[intelligence.size()];
        for (int i = 0; i < intelligence.size(); i++) {
            List<MissionInfo> missionInfo = new LinkedList<MissionInfo>();
            JsonObject missions = intelligence.get(i).getAsJsonObject();
            JsonArray m = missions.getAsJsonArray("missions");
            for (int j = 0; j < m.size(); j++) {
                JsonObject info = m.get(j).getAsJsonObject();
                JsonArray serialAgentsNumbers = info.getAsJsonArray("serialAgentsNumbers");
                List<String> serialNums = new LinkedList<String>();
                for (int k = 0; k < serialAgentsNumbers.size(); k++) {
                    String serialNumber = serialAgentsNumbers.get(k).getAsString();
                    serialNums.add(serialNumber);
                }
                serialNums.sort(Comparator.naturalOrder());
                int duration = info.get("duration").getAsInt();
                String gadget = info.get("gadget").getAsString();
                String missionName = info.get("name").getAsString();
                int timeExpired = info.get("timeExpired").getAsInt();
                int timeIssued = info.get("timeIssued").getAsInt();
                MissionInfo missionInfo1 = new MissionInfo(missionName, serialNums, gadget, timeExpired, timeIssued, duration);
                missionInfo.add(missionInfo1);
            }
            Intelligence intel = new Intelligence(i);
            intel.setMissionInfos(missionInfo);
            intelligences[i] = intel;
        }
        return intelligences;
    }

    //writes the outputs to the given file names
    public void outputFiles (String inventoryPath, String diaryPath){
        Inventory.getInstance().printToFile(inventoryPath);
        Diary.getInstance().printToFile(diaryPath);
    }
}