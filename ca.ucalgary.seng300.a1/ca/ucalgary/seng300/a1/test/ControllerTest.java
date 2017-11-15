package ca.ucalgary.seng300.a2.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.vending.Coin;
import org.lsmr.vending.hardware.*;
import ca.ucalgary.seng300.a2.Controller;

/**
 * @author Vending Solutions Incorporated
 * Developed by: Nguyen Viktor, Michaela Olšáková, Roman Sklyar
 *
 */

public class ControllerTest {
  private Controller myVending;
  private Coin coin;
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    Controller vending = new Controller();
    myVending = vending;

  }

  
  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    myVending.cleanUpTimers();
  }

  //** MATTHEW, Teardown requires to clean up the timers, the 2 test methods make sure the display is working properly
  //comment them out when doing your own testing because they take 12 seconds to finish
  /**
   * Test method for {@link ca.ucalgary.seng300.a1.Controller#Controller()}.
   * @throws DisabledException 
   * @throws InterruptedException 
   */
//  @Test
//  public void passiveDisply() throws InterruptedException{
//    Thread.sleep(3*1000);//3 seconds into the simulation, no coins entered
//    assertEquals(myVending.messageBeingDisplayed, "Hi There!");
//    Thread.sleep(3*1000); //total of 6 seconds in, no longer displaying hi
//    assertEquals(myVending.messageBeingDisplayed, "");
//  }
  //important - when the display is made to display the coin amount when some coin is inserted
  //this test case will have to change to reflect that
//  @Test
//  public void passiveDisplyCoinInsterted() throws InterruptedException, DisabledException{
//    Coin coin = new Coin(100);
//    myVending.insertCoin(coin);
//    Thread.sleep(3*1000);//credit is non 0 so null should still be displayed
//    assertEquals(myVending.messageBeingDisplayed, null);
//    Thread.sleep(3*1000); 
//    assertEquals(myVending.messageBeingDisplayed, null);
//  }
//  
//  @Test
//  public void creditDisply() throws DisabledException{
//    Coin coin2 = new Coin(3);
//    Coin coin = new Coin(100);
//    Coin coin3 = new Coin(5);
//    myVending.insertCoin(coin);
//    myVending.insertCoin(coin2); //doesnt work because the return thingy is "broken"
//    assertEquals(myVending.messageBeingDisplayed,"Credit: $1.00");
//    myVending.insertCoin(coin3);
//    assertEquals(myVending.messageBeingDisplayed,"Credit: $1.05");
//  }
  //**END, MATTHEW
  
  @Test
  public void testController() throws DisabledException, EmptyException {
    System.out.println(myVending.getTotal());

    Coin coin = new Coin(100);

    myVending.insertCoin(coin);
    myVending.insertCoin(coin);
    myVending.insertCoin(coin);

    int[] popCans = new int[6];
    for (int i = 0; i < 6; i++) {
      popCans[i] = 10;
    }

    myVending.getVending().loadPopCans(popCans);
    myVending.pushButton(1);
    System.out.println(myVending.getTotal());

  }

  /**
   * Test method for {@link ca.ucalgary.seng300.a1.Controller#getTotal()}.
   * Determine if the total is returned as expected
   * @throws DisabledException 
   */
  @Test
  public void testGetTotal() throws DisabledException {
    Coin coin = new Coin(100);

    myVending.insertCoin(coin);
    myVending.insertCoin(coin);
    myVending.insertCoin(coin);
    myVending.insertCoin(coin);
    myVending.insertCoin(coin);

    assertEquals(myVending.getTotal(), 500);
  }

  /**
   * Test method to determine if the total is incremented properly when coins are inserted
   * {@link ca.ucalgary.seng300.a1.Controller#incrementTotal(int)}.
   * @throws DisabledException 
   */
  @Test
  public void testIncrementTotal() throws DisabledException {
    Coin coin = new Coin(100);
    
    myVending.insertCoin(coin);
    assertEquals(100,myVending.getTotal());
    myVending.insertCoin(coin);
    assertEquals(200,myVending.getTotal());
  }

  /**
   * Test method to determine if a valid coin insert will actually update the total
   * {@link ca.ucalgary.seng300.a1.Controller#insertCoin(org.lsmr.vending.Coin)}.
   * @throws DisabledException 
   */
  @Test
  public void testValidInsertCoin() throws DisabledException {
    coin = new Coin(5);
    myVending.insertCoin(coin);
    assertEquals(5, myVending.getTotal());
  }

  /**
   * Test method to determine if invalid button presses do anything
   */
  @Test
  public void testInvalidButton() {

    try {
      myVending.pushButton(17);
    } catch (Exception e) {
      // System.out.println(e);
      assertEquals(e.toString(), "Nested exception: Invalid button pressed");
    }
  }

  /**
   * Test method to determine if the program will allow negative values in the machine
   */
  @Test
  public void testNegativeTotal() {

    try {
      myVending.decrementTotal(5);
      // myVending.getTotal();
    } catch (Exception e) {
      // System.out.println(e);
      assertEquals(e.toString(), "Nested exception: Decrement cannot result in total being a negative value");
    }
  }

  /**
   * Test method to determine whether an empty pop rack will still dispense pop cans
   * @throws DisabledException 
   */
  @Test
  public void testEmptyPopRack() throws DisabledException {

    Coin coin = new Coin(100);

    myVending.insertCoin(coin);
    myVending.insertCoin(coin);
    myVending.insertCoin(coin);
    try {
      myVending.pushButton(1);
    } catch(Exception e) {
      assertEquals(true, myVending.getVending().getOutOfOrderLight().isActive());
    }
  }

  /**
   * Test method to determine whether a disabled pop rack will still dispense pop
   * @throws DisabledException 
   */
  @Test
  public void testDisabledPopRack() throws DisabledException {

    Coin coin = new Coin(100);

    myVending.insertCoin(coin);
    myVending.insertCoin(coin);
    myVending.insertCoin(coin);

    try {
      myVending.getVending().getPopCanRack(1).disable();
      myVending.pushButton(1);
      // myVending.getTotal();
    } catch (Exception e) {
      // System.out.println(e.getLocalizedMessage());
      assertEquals(e.toString(), "Nested exception: Pop rack is disabled");
    }
  }

  /**
   * Test method to determine if a pop rack can be filled over capacity
   * @throws DisabledException 
   */
  @Test
  public void testCapacityPopRack() throws DisabledException {

    Coin coin = new Coin(200);

    myVending.insertCoin(coin);
    myVending.insertCoin(coin);
    myVending.insertCoin(coin);

    int[] popCans = new int[6];
    for (int i = 0; i < 6; i++) {
      popCans[i] = 10;
    }

    System.out.println(myVending.getVending().getPopCanRack(1).getCapacity());
    DeliveryChute zeroCapacity = new DeliveryChute(1);
    PopCanChannel myChannel = new PopCanChannel(zeroCapacity);

    myVending.getVending().getPopCanRack(2).connect(myChannel);
    myVending.getVending().loadPopCans(popCans);

    try {

      myVending.pushButton(2);
      myVending.pushButton(2);
      // myVending.getTotal();
    } catch (Exception e) {
      // System.out.println(e.toString());
      assertEquals(e.toString(), "Nested exception: Capacity exceeded");
    }
  }

  /**
   * Test method to see if an invalid coin will be accepted
   * CURRENTLY DOES NOT WORK SINCE COINRETURN IS NOT WORKING
   * {@link ca.ucalgary.seng300.a1.Controller#insertCoin(org.lsmr.vending.Coin)}.
   * @throws DisabledException 
   */
  @Test
  public void testInvalidInsertCoin() throws DisabledException {
    coin = new Coin(3);
    myVending.insertCoin(coin);
    assertEquals(0, myVending.getTotal());
  }

  /**
   * Tests for an exception when the coin slot is disabled.
   * @throws DisabledException 
   */
  @Test(expected = DisabledException.class)
  public void coinSlotDisabled() throws DisabledException {
    Coin coin = new Coin(10);
    myVending.getVending().enableSafety();
    myVending.insertCoin(coin);
  }

  /**
   * Tests if the enabled method of the listener is called
   */
  @Test
  public void coinSlotDisabledListener() {
    myVending.getVending().disableSafety();
    assertEquals(true, myVending.coinSlotEnabled());
  }

  /**
   * Tests if the disabled method of the listener is called
   */
  @Test
  public void coinSlotEnabledListener() {
    myVending.getVending().enableSafety();
    assertEquals(false, myVending.coinSlotEnabled());
  }

  /**
   * Tests if the disabled method of the listener is called
   */
  @Test
  public void buttonEnabledListener() {
    myVending.getVending().getSelectionButton(0).disable();
    assertEquals(false, myVending.buttonEnabled());
  }

  /**
   * Tests if the disabled method of the listener is called
   */
  @Test
  public void buttonDisabledListener() {
    myVending.getVending().getSelectionButton(0).enable();
    assertEquals(true, myVending.buttonEnabled());
  }
  
  //**PRESTON, the 8 test methods check to see if checkChange works properly. Had 5 cases to check if each
  //  individual coin check is working properly
  /**
   * Tests a standard case where every value is used to check for sufficient change
   */  
  @Test
  public void testEnoughChange() {
    int[] coins = {15,15,15,15,15};
    myVending.getVending().loadCoins(coins);
    myVending.setTotal(750);
      assertEquals(true,myVending.checkChange(myVending.getTotal()));
  }

  /**
   * Tests to see if nickel check is working properly
   */
  @Test
  public void testNickelOnly() {
      int[] coins = {15,0,0,0,0};
      myVending.getVending().loadCoins(coins);
      myVending.setTotal(35);
      assertEquals(true,myVending.checkChange(myVending.getTotal()));
  }

  /**
   * Tests to see if dime check is working properly
   */
  @Test
  public void testDimeOnly() {
      int[] coins = {0,15,0,0,0};
      myVending.getVending().loadCoins(coins);
      myVending.setTotal(50);
      assertEquals(true,myVending.checkChange(myVending.getTotal()));
  }

  /**
   * Tests to see if quarter check is working properly
   */
  @Test
  public void testQuarterOnly() {
  int[] coins = {0,0,15,0,0};
      myVending.getVending().loadCoins(coins);
      myVending.setTotal(250);
      assertEquals(true,myVending.checkChange(myVending.getTotal()));
  }

  /**
   * Tests to see if loonie check is working properly
   */
  @Test
  public void testLoonieOnly() {
    int[] coins = {0,0,0,15,0};
    myVending.getVending().loadCoins(coins);
    myVending.setTotal(700);
    assertEquals(true,myVending.checkChange(myVending.getTotal()));
  }

  /**
   * Tests to see if toonie check is working properly
   */
  @Test
  public void testToonieOnly() {
    int[] coins = {0,0,0,0,15};
    myVending.getVending().loadCoins(coins);
    myVending.setTotal(800);
    assertEquals(true,myVending.checkChange(myVending.getTotal()));
  }

  /**
   * Tests for insufficient change
   */
  @Test
  public void testInsufficientChange() {
    int[] coins = {0,2,1,1,0};
    myVending.getVending().loadCoins(coins);
    myVending.setTotal(150);
    assertEquals(false,myVending.checkChange(myVending.getTotal()));
  }

  /**
   * Tests when there is just enough change in the machine
   */
  @Test
  public void testJustEnoughChange() {
    int[] coins = {1,2,1,1,0};
    myVending.getVending().loadCoins(coins);
    myVending.setTotal(150);
    assertEquals(true,myVending.checkChange(myVending.getTotal()));
  }
  
  /**
   * Tests when first initialized with no change, if the light is active
   */
  @Test
  public void testNoChangeLightOn() {
    assertEquals(true,myVending.getVending().getExactChangeLight().isActive());
  }
  
  /**
   * Tests when machine lacks enough change, if the light is turned on
   */
  @Test
  public void testNoChangeLightOn2() {
    myVending.getVending().getExactChangeLight().deactivate();
    int[] coins = {1,0,0,0,0};
    myVending.getVending().loadCoins(coins);
    myVending.exactChange();
    assertEquals(true,myVending.getVending().getExactChangeLight().isActive());
  }
  
  /**
   * Tests when machine lacks enough change, if the light is turned on
   * This test changes the pop values in the vending machine to account for a
   * wider range of costs
   * Total change in machine is $1.70, but only one nickel and one dime which won't pass for
   * $1.20
   */
  @Test
  public void testNoChangeLightOn3() {
    List<String> popCanNames = new ArrayList<String>();
    List<Integer> popCanCosts = new ArrayList<Integer>();

    myVending.getVending().getExactChangeLight().deactivate();
    for (int i = 0; i < 6; i++) {
      popCanNames.add(Integer.toString(i));
      if(i < 1) {
        popCanCosts.add(250);
      } else if (i < 3) {
        popCanCosts.add(265);
      } else {
        popCanCosts.add(280);
      }
    }
    
    myVending.getVending().configure(popCanNames, popCanCosts);
    int[] coins = {1,1,2,1,0};
    myVending.getVending().loadCoins(coins);
    myVending.exactChange();
    assertEquals(true,myVending.getVending().getExactChangeLight().isActive());
  }
  
  /**
   * Tests when racks are filled with sufficient change, if the change light is turned off
   */
  @Test
  public void testNoChangeLightOff() {
    int[] coins = {1,2,1,1,0};
    myVending.getVending().loadCoins(coins);
    myVending.getVending().getExactChangeLight().activate();
    myVending.exactChange();
    assertEquals(false,myVending.getVending().getExactChangeLight().isActive());
  }
  
  /**
   * Tests when racks are filled with bare minimum, if change lights are turned off
   */
  @Test
  public void testNoChangeLightOff2() {
    int[] coins = {0,0,2,1,0};
    myVending.getVending().loadCoins(coins);
    myVending.getVending().getExactChangeLight().activate();
    myVending.exactChange();
    assertEquals(false,myVending.getVending().getExactChangeLight().isActive());
  }
  
  @Test
  public void testNoChangeLightOff3() {
    List<String> popCanNames = new ArrayList<String>();
    List<Integer> popCanCosts = new ArrayList<Integer>();

    for (int i = 0; i < 6; i++) {
      popCanNames.add(Integer.toString(i));
      if(i < 1) {
        popCanCosts.add(250);
      } else if (i < 3) {
        popCanCosts.add(265);
      } else {
        popCanCosts.add(280);
      }
    }
    
    myVending.getVending().configure(popCanNames, popCanCosts);
    int[] coins = {2,1,2,1,0};
    myVending.getVending().loadCoins(coins);
    myVending.getVending().getExactChangeLight().activate();
    myVending.exactChange();
    assertEquals(false,myVending.getVending().getExactChangeLight().isActive());
  }
  
  /**
   * Tests to see which selection button was pressed
   */
  @Test
  public void testPushButtonCheck() {
    int[] coins = {15,15,15,15,15};
    myVending.getVending().loadCoins(coins);
    myVending.getVending().getSelectionButton(2).press();
    assertEquals(2,myVending.getButtonPressed());
  }
  
  /**
   * Tests to see if exact change is dispensed
   * @throws DisabledException 
   * @throws EmptyException 
   * @throws CapacityExceededException 
   */
  @Test
  public void testExactChange() throws Exception {
    int[] coins = {15,15,15,15,15};
    myVending.getVending().loadCoins(coins);
    myVending.setTotal(350);
    myVending.dispenseChange(myVending.getTotal());
    assertEquals(0, myVending.getTotal());
  }
  
  /**
   * Tests to see if exact change is dispensed when just enough change is in the machine
   * @throws DisabledException 
   * @throws EmptyException 
   * @throws CapacityExceededException 
   */
  @Test
  public void testExactChange2() throws Exception {
    int[] coins = {1,1,0,0,0};
    myVending.getVending().loadCoins(coins);
    myVending.setTotal(15);
    myVending.dispenseChange(myVending.getTotal());
    assertEquals(0, myVending.getTotal());
  }
  
  /**
   * Tests to see if near exact change is dispensed
   * @throws DisabledException 
   * @throws EmptyException 
   * @throws CapacityExceededException 
   */
  @Test
  public void testNearExactChange() throws Exception {
    int[] coins = {0,0,2,1,1};
    myVending.getVending().loadCoins(coins);
    myVending.setTotal(355);
    myVending.dispenseChange(myVending.getTotal());
    assertEquals(5, myVending.getTotal());
  }
 
  /**
   * Tests to see if near exact change is dispensed but not more
   * @throws DisabledException 
   * @throws EmptyException 
   * @throws CapacityExceededException 
   */
  @Test
  public void testNearExactChange2() throws Exception {
    int[] coins = {3,3,1,0,0};
    myVending.getVending().loadCoins(coins);
    myVending.setTotal(75);
    myVending.dispenseChange(myVending.getTotal());
    assertEquals(5, myVending.getTotal());
  }
  
  @Test
  public void testOutOfOrderLight() throws Exception {
    myVending.getVending().getOutOfOrderLight().deactivate();
    myVending.setTotal(400);
    myVending.pushButton(1);
    assertEquals(true, myVending.getVending().getOutOfOrderLight().isActive());
  }
  
 /*
  @Test
  public void testCoinRackFull() throws Exception{
    Coin test = new Coin(25);
    int[] coins = {15,15,15,15,15};
    int[] popCans = new int[6];
    for (int i = 0; i < 6; i++) {
      popCans[i] = 10;
    }

    myVending.getVending().loadPopCans(popCans);
    myVending.getVending().loadCoins(coins);
    myVending.getVending().getOutOfOrderLight().deactivate();
    myVending.getVending().getCoinSlot().addCoin(test);
    myVending.setTotal(400);
    myVending.pushButton(2); // Test fails in CoinReceptacle, object does not read as Null so passes the if(object != null) case and causes a null pointer issue
    assertEquals(true, myVending.getVending().getOutOfOrderLight().isActive());
  }
 */
  // END

}