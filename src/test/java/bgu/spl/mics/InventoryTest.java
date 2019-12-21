package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.Squad;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryTest {
    Inventory inventory;
    @BeforeEach
    public void setUp(){
        inventory = Inventory.getInstance();
    }

    @AfterEach
    public void tearDown(){
        inventory=null;
    }

    @Test
    public void load(){
        String[] gadgets = {"m16", "spy-pen", "austin martin"};
        inventory.load(gadgets);
        for (int i = 0; i<gadgets.length;i++){
            assertTrue(inventory.getItem(gadgets[i]), "gadget missing");
        }
    }

    @Test
    public void getItem(){
        String[] gadgets = {"m16", "spy-pen", "austin martin"};
        inventory.load(gadgets);
        for (int i = 0; i<gadgets.length; i++){
            assertTrue(inventory.getItem(gadgets[i]), "getItem failed");
        }
    }

    @Test
    public void getInstance(){
        Inventory test = Inventory.getInstance();
        assertEquals(inventory,test, "getInstance failed");
    }
}