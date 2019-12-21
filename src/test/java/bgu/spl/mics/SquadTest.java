package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.Squad;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class SquadTest {
    Squad s;
    @BeforeEach
    public void setUp(){
        s = Squad.getInstance();
    }

    @AfterEach
    public void tearDown(){
        s=null;
    }

    @Test
    public void load(){
        Agent a = new Agent("bond","007");
        Agent b = new Agent("James","009");
        Agent[] agents = {a,b};
        s.load(agents);
        List<String> operatives = new ArrayList<>();
        operatives.add(a.getSerialNumber());
        operatives.add(b.getSerialNumber());
        List<String> name = s.getAgentsNames(operatives);
        for (int i = 0; i<name.size();i++){
            assertTrue(name.contains(agents[i]), "agent does not exist");
        }
    }

    @Test
    public void getAgents(){
        Agent a = new Agent("Bond","007");
        Agent b = new Agent("James","009");
        List<String> operatives = new ArrayList<>();
        operatives.add(a.getSerialNumber());
        operatives.add(b.getSerialNumber());
        assertTrue(s.getAgents(operatives),"getAgent failed");
    }

    @Test
    public void getAgentsNames(){
        Agent a = new Agent("Bond","007");
        Agent b = new Agent("James","009");
        List<String> operatives = new ArrayList<>();
        operatives.add(a.getSerialNumber());
        operatives.add(b.getSerialNumber());
        List<String> name = s.getAgentsNames(operatives);
        assertTrue(name.contains(a.getName()), "getAgentNames failed");
        assertTrue(name.contains(b.getName()), "getAgentNames failed");
    }

    @Test
    public void getInstance(){
        Squad test = Squad.getInstance();
        assertEquals(s,test, "getInstance failed");
    }
}