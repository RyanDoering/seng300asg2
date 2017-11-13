package ca.ucalgary.seng300.a2;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.lsmr.vending.*;
import org.lsmr.vending.hardware.*;

/**
 * @author Vending Solutions Incorporated
 * Developed by: Nguyen Viktor(10131322), Michaela Olšáková(30002591), Roman Sklyar(10131059)
 * 
 *
 */



public class Controller {
	
	//displays "Hi there" if no change is in the machine
	public void sayHello(){
		if(total == 0){
	   client.getDisplay().display("Hi There!");
		}
		
	}
	//changes the display to show "", used in side with sayHello to alternate 
	public void stopSayingHello(){
		if(total == 0){
			client.getDisplay().display(""); //just make this nothing 
			
	       }
	}
	
	public void displayTotal(){
		if(total != 0){
			double workingtotal = (double)total;
			double dollars = 0;
			double cents = 0;
			while(workingtotal >= 100){
				dollars++;
				workingtotal = workingtotal - 100;
			}
			cents = workingtotal/100;
			workingtotal = dollars + cents;
			client.getDisplay().display(String.format("Credit: $%.2f", workingtotal)); //just make this nothing 
			
	       }
	}
	
	//call this after every test to turn off the timers
	public void cleanUpTimers(){
		timer1.cancel();
		timer2.cancel();
	}

		/**
		 * Set-up machine variables
		 */
	public static String messageBeingDisplayed; //this is just for testing, no other way i can think of
	private int total, buttonPressed;
	private boolean validCoin;
	private VendingMachine client;
	private MySlotListener slotListener;
	private MyButtonListener buttonListener;
	private MyPopRackListener popRackListener;
	private MyDeliveryChuteListener chuteListener;
	private MyDisplayListener displayListener; //display listener
	private MyOutOfOrderLightListener outOfOrderLightListener;
	private MyExactChangeOnlyLightListener exactChangeOnlyLightListener;
	private IndicatorLight outOfOrderLight;
	private IndicatorLight exactChangeOnlyLight;
	private PushButton compareButton;
	private boolean coinSlotEnabled;
	private boolean buttonEnabled;
	private boolean outOfOrder;
	Timer timer1 = new Timer(); //timers
	Timer timer2 = new Timer();
	
	//Controller constructor to hook up all parts in the vending machine together
	public Controller(){
		total = 0;
		validCoin = false;
		coinSlotEnabled = true;
		 
		
		int[] CAD = { 5, 10, 25, 100, 200 };

		//According to Clients specifications:
		//Canadian Currency, 6 types of pop, capacity of coinRack=15, 10 pops per rack, 200 coins in receptacle
		client = new VendingMachine(CAD, 6, 15, 10, 200, 10, 10);	//10 is delivery chute capacity and coin return capacity - temp values because its not my problem
		
		exactChange();

		outOfOrderLight = client.getOutOfOrderLight();
		exactChangeOnlyLight = client.getExactChangeLight();
		
		slotListener = new MySlotListener();
		client.getCoinSlot().register(slotListener);

		buttonListener = new MyButtonListener();
		for (int i = 0; i < client.getNumberOfSelectionButtons(); i++) {
			client.getSelectionButton(i).register(buttonListener);
		}

		popRackListener = new MyPopRackListener();
		for (int i = 0; i < client.getNumberOfPopCanRacks(); i++) {
			client.getPopCanRack(i).register(popRackListener);
			
		}

		chuteListener = new MyDeliveryChuteListener();
		client.getDeliveryChute().register(chuteListener);
		
		outOfOrderLightListener = new MyOutOfOrderLightListener();
		outOfOrderLight.register(outOfOrderLightListener);
		
		exactChangeOnlyLightListener = new MyExactChangeOnlyLightListener();
		exactChangeOnlyLight.register(exactChangeOnlyLightListener);

		
		// set up the pop cans and prices and add them to the rack
		List<String> popCanNames = new ArrayList<String>();
		List<Integer> popCanCosts = new ArrayList<Integer>();	
		
		for (int i = 0; i<6; i++) {
			popCanNames.add(Integer.toString(i));
			popCanCosts.add(250);
		}
		
		//Create an array the size of total pop can options
		int[] popCanAmounts = new int[popCanNames.size()];		
		
		//Populate this array with 5 in each index, for now we want 5 of each type of pop loaded into the machine
		//for(int i=0; i<popCanNames.size();i++)
		//	popCanAmounts[i] = 5;			
		
		client.configure(popCanNames, popCanCosts);

		//Load cans into the machine
		client.loadPopCans(popCanAmounts);
		
		//If no cans have been loaded, toggle the outOfOrder light on
		if(this.popCanRacksEmpty()) {
			outOfOrder = true;
			outOfOrderLight.activate();
		}
		
				
		//added code - this is the portion that changes to display while the credit value == 0
		//every 15 second interval will have "Hi there" for 5 seconds then nothing for 10
		//effective 5 sec display of hi followed by 10 seconds of blank

		displayListener = new MyDisplayListener();
		client.getDisplay().register(displayListener);
		//no delay(secretly, there is a 200ms delay such that the thread doesnt display hi before the coin has been able to be inserted
		//, says hi every 15 seconds
        timer1.schedule(new TimerTask() {

            @Override
            public void run() {
                sayHello();
            }
        }, 200, 15000);
        
       //5 sec delay, cleans up the mesasge hi
        timer2.schedule(new TimerTask() {

            @Override
            public void run() {
                stopSayingHello();
            }
        }, 5200, 15000);
		
	}
	
  /**
   * Used for testing purposes, set a specified total credit
   * @param credit
  */
  public void setTotal(int credit) {
    total = credit;
  }
	
	/**
	 * Add to the total when a coin has been inserted
	 * @param coin
	 */
	public int getTotal(){
		return total;
	}
	
	/**
	 * Getter for the VM instance
	 * @param coin
	 */
	public VendingMachine getVending(){
		return client;
	}
	/**
	 * Add to the total when a coin has been inserted
	 * @param coin
	 */
	private void incrementTotal(int coin){
		total += coin;
		displayTotal();
	}
	
	/**
	 * Used primarily for testing purposes of the listener
	 * @return whether or not the coin slot is enabled
	 */
	public boolean coinSlotEnabled(){
		return coinSlotEnabled;
	}
	
	/**
	 * Checks if all pop can racks are empty
	 * @return whether all pop can racks are empty or not
	 */
	public boolean popCanRacksEmpty() {
		
		boolean  rackWithPopExists = false;
		
		for(int i = 0; i < client.getNumberOfPopCanRacks(); i++)
			if (client.getPopCanRack(i).size() > 0)
				rackWithPopExists = true;
		
		if(rackWithPopExists)
			return false;
		else
			return true;
	}
	/**
	 * Used primarily for testing purposes of the listener
	 * @return whether or not the button is enabled
	 */
	public boolean buttonEnabled(){
		return buttonEnabled;
	}
	
	/**
	 * Decrement the total when a pop has been purchased
	 * @param price
	 * @throws SimulationException
     *  If total becomes negative.
	 */
	public void decrementTotal(int price) throws SimulationException{
		if (total >= price) {
		total -= price;
    displayTotal();}
		else throw new SimulationException("Decrement cannot result in total being a negative value");

		
	}
	
	/**
	 * Checks to see if there is exact change available for distribution
	 * @return boolean value, true if there is enough, false if there is not enough
	 */
	public void exactChange() {
    int numOfPops = client.getNumberOfSelectionButtons();
    int cost;
    int loonieCheck, toonieCheck;
    int[] costs = new int[numOfPops];
    for(int i = 0; i < numOfPops; i++) {
      costs[i] = client.getPopKindCost(i);
    }
    // Currently checking all items in the vending machine. Can be made more efficient if duplicate numbers are
    // removed from the array and are the only ones checked. May be done in future
    for(int j = 0; j < numOfPops; j++) {
      cost = costs[j];
      if((cost % 25) != 0) {
        if(!(checkChange(cost % 25))) {
          client.getExactChangeLight().activate();
        }
      }
      loonieCheck = ((cost + 99) / 100) * 100;
      toonieCheck = loonieCheck + 100;
      if(!checkChange(loonieCheck - cost) || !checkChange(toonieCheck - cost)) {
        client.getExactChangeLight().activate();
      } else {
        client.getExactChangeLight().deactivate();
      }
    }
	}
	
	
	/**
	 * Checks to see if change is available against the value inputed
	 * @param value is the value to be checked. Use getTotal to check for change for current credit
	 * @return boolean value, true if there's enough change, false if insufficient.
	 */
	public boolean checkChange(int value) {
	 	int nickelTotal = client.getCoinRackForCoinKind(5).size();
	 	int dimeTotal = client.getCoinRackForCoinKind(10).size();
	 	int quarterTotal = client.getCoinRackForCoinKind(25).size();
	 	int loonieTotal = client.getCoinRackForCoinKind(100).size();
	 	int toonieTotal = client.getCoinRackForCoinKind(200).size();
	 	int changeRequired = value;
	 	while(changeRequired >= 200 && toonieTotal > 0) {
	   		changeRequired -= 200;
	   		toonieTotal--;
	 	}
	 	while(changeRequired >= 100 && loonieTotal > 0) {
	   		changeRequired -= 100;
	   		loonieTotal--;
	 	}
	 	while(changeRequired >= 25 && quarterTotal > 0) {
	   		changeRequired -= 25;
	   		quarterTotal--;
	 	}
	  while(changeRequired >= 10 && dimeTotal > 0) {
	   		changeRequired -= 10;
	   		dimeTotal--;
	  }
	  while(changeRequired >= 5 && nickelTotal > 0) {
	   		changeRequired -= 5;
	   		nickelTotal--;
	  }
	  if(changeRequired == 0) {
	   		return true;
	  }
	  return false;
	  }
	
	public void buttonCheck(PushButton button) {
	  compareButton = button;
	  for(int i = 0; i < client.getNumberOfSelectionButtons(); i++) {
	    if(compareButton == client.getSelectionButton(i)) {
	      buttonPressed = i;
	      System.out.println("Button " + i + " was pressed");
	    }
	  }
	}
	
	public int getButtonPressed() {
	  return buttonPressed;
	}
	
	//RYAN, new method to dispense change, only does exact change right now
	//Methd call to dispense change also added when a pop is dispensed 
	  public void dispenseChange(int credit) throws CapacityExceededException, EmptyException, DisabledException
	  {
		  int change = credit;
		  
		  if (checkChange(change) == true) //if we can give exact change, then go through all the coin types and give back the change 
		  {
			  int toonies = Math.round((int)change/200);
			  change = change % 200;
			  int loonies = Math.round((int)change/100);
			  change = change % 100;
			  int quarters = Math.round((int)change/25);
			  change = change % 25;
			  int dimes = Math.round((int)change/10);
			  change = change % 10;
			  int nickels = Math.round((int)change/5);
			  change = change % 5;
			  
			  if (toonies != 0)
			  {
				  for (int i = 0; i == toonies; i++)
				  {
					  client.getCoinRackForCoinKind(200).releaseCoin();
					  client.getCoinReturn().acceptCoin(new Coin(200));
				  }
			  }
			  if (loonies != 0)
			  {
				  for (int i = 0; i == loonies; i++)
				  {
					  client.getCoinRackForCoinKind(100).releaseCoin();
					  client.getCoinReturn().acceptCoin(new Coin(100));
				  }
			  }
			  if (quarters != 0)
			  {
				  for (int i = 0; i == quarters; i++)
				  {
					  client.getCoinRackForCoinKind(25).releaseCoin();
					  client.getCoinReturn().acceptCoin(new Coin(25));
				  }
			  }
			  if (dimes != 0)
			  {
				  for (int i = 0; i == dimes; i++)
				  {
					  client.getCoinRackForCoinKind(10).releaseCoin();
					  client.getCoinReturn().acceptCoin(new Coin(10));
				  }
			  }
			  if (nickels != 0)
			  {
				  for (int i = 0; i == nickels; i++)
				  {
					  client.getCoinRackForCoinKind(5).releaseCoin();
					  client.getCoinReturn().acceptCoin(new Coin(5));
				  }
			  }
			  
		  }
		  else 
		  {
			int nickelTotal = client.getCoinRackForCoinKind(5).size();
		    	int dimeTotal = client.getCoinRackForCoinKind(10).size();
		    	int quarterTotal = client.getCoinRackForCoinKind(25).size();
		    	int loonieTotal = client.getCoinRackForCoinKind(100).size();
		    	int toonieTotal = client.getCoinRackForCoinKind(200).size();
		    	int changeRequired = credit;
		   
		    	while(changeRequired >= 200 && toonieTotal > 0) {
		      		changeRequired -= 200;
		      		toonieTotal--;
		      		client.getCoinRackForCoinKind(200).releaseCoin();
				client.getCoinReturn().acceptCoin(new Coin(200));
		    	}
		    	while(changeRequired >= 100 && loonieTotal > 0) {
		      		changeRequired -= 100;
		      		loonieTotal--;
		      		client.getCoinRackForCoinKind(100).releaseCoin();
				client.getCoinReturn().acceptCoin(new Coin(100));
		    	}
		    	while(changeRequired >= 25 && quarterTotal > 0) {
		      		changeRequired -= 25;
		      		quarterTotal--;
		      		client.getCoinRackForCoinKind(25).releaseCoin();
				client.getCoinReturn().acceptCoin(new Coin(25));
		    	}
		    	while(changeRequired >= 10 && dimeTotal > 0) {
		      		changeRequired -= 10;
		      		dimeTotal--;
		      		client.getCoinRackForCoinKind(10).releaseCoin();
				client.getCoinReturn().acceptCoin(new Coin(10));
		    	}
		    	while(changeRequired >= 5 && nickelTotal > 0) {
		      		changeRequired -= 5;
		      		nickelTotal--;
		      		client.getCoinRackForCoinKind(5).releaseCoin();
				client.getCoinReturn().acceptCoin(new Coin(5));
		    	}
		  }
	  }
//DONE 
	
	/**
	 * User has entered a coin. Insert it into the hardware and listen to whether its valid. Update total accordingly.
	 * @param coin
	 */
	public void insertCoin(Coin coin){
		CoinSlot slot = client.getCoinSlot();
		try {
			slot.addCoin(coin);
		} catch (DisabledException e) {
			System.out.println("Coin slot is disabled");
		}
		if (validCoin == true){
			incrementTotal(coin.getValue());
			validCoin = false;
		}
	}
	
	/**
	 * The logic of button presses. Ensures there is enough money in the machine before dispensing pop
	 * Updates the total accordingly. Does nothing if the pop can rack is empty.
	 * @param button
	 * @throws SimulationException
	 */
	public void pushButton(Integer button) throws SimulationException{
		
		if(button >=client.getNumberOfSelectionButtons()) {
			throw new SimulationException("Invalid button pressed");
		}
		else {
			
			if (getTotal() >= client.getPopKindCost(button)){
		  		
		  		client.getSelectionButton(button).press();
		  		
		  		try {

		  			try {
		  				
		  				//Store coins from CoinReceptacle into CoinRacks
		  				client.getCoinReceptacle().storeCoins();
		  			
		  			} catch(CapacityExceededException e){		  				
		  				//CoinRacks are full
		  				outOfOrder = true;
		  				outOfOrderLight.activate();
		  				
		  			} catch(DisabledException e) {throw new SimulationException("Coin Receptacle Disabled");}		  			
					
					client.getPopCanRack(button).dispensePopCan();
					
					decrementTotal(client.getPopKindCost(button));
					
					if(popCanRacksEmpty()) {
						outOfOrder = true;
						outOfOrderLight.activate();
					}
					
				} catch (DisabledException e) {
					// TODO Auto-generated catch block
					throw new SimulationException("Pop rack is disabled");
				} catch (EmptyException e) {
					throw new SimulationException("Pop rack empty");					
				
				} catch (CapacityExceededException e) {
					// TODO Auto-generated catch block
					throw new SimulationException("Capacity exceeded");
				}
		  	}
			else{ 
				System.out.println("Not enough CREDIT!");
				//Currently do nothing				

			}
		}
		
		
	}

	/**
	 * A class for the CoinSlotListener to get events from the machine
	 * 
	 */
	private class MySlotListener implements CoinSlotListener {

		@Override
		public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
			System.out.println("slot enabled");
			coinSlotEnabled = true;
		}

		@Override
		public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
			System.out.println("slot disabled");
			coinSlotEnabled = false;
		}

		@Override
		public void validCoinInserted(CoinSlot slot, Coin coin) {
			System.out.println("Valid coin inserted");
			validCoin = true;
		}

		@Override
		public void coinRejected(CoinSlot slot, Coin coin) {
			System.out.println("Coin rejected");
			validCoin = false;
		}

	}

	/**
	 * A class for the ButtonListener to get events from the machine
	 * 
	 */
	private class MyButtonListener implements PushButtonListener {

		@Override
		public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
			System.out.println("button enabled.");
			buttonEnabled = true;
		}

		@Override
		public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
			System.out.println("button disabled");
			buttonEnabled = false;
		}


		@Override
		public void pressed(PushButton button) {
		  buttonCheck(button);
			System.out.println("pressed");
			
		}
	}

	/**
	 * A class for the PopRackListener to get events from the machine
	 * 
	 */
	private class MyPopRackListener implements PopCanRackListener {

		@Override
		public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
			System.out.println("Pop Rack enabled");

		}

		@Override
		public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
			System.out.println("Pop Rack Disabled");

		}

		@Override
		public void popCanAdded(PopCanRack popCanRack, PopCan popCan) {
			System.out.println("Pop Can Added");

		}

		@Override
		public void popCanRemoved(PopCanRack popCanRack, PopCan popCan) {
			System.out.println("Pop can removed");

		}

		@Override
		public void popCansFull(PopCanRack popCanRack) {
			// DO NOTHING
		}

		@Override
		public void popCansEmpty(PopCanRack popCanRack) {
			System.out.println("This rack is empty");

		}

		@Override
		public void popCansLoaded(PopCanRack rack, PopCan... popCans) {
			// DO NOTHING

		}

		@Override
		public void popCansUnloaded(PopCanRack rack, PopCan... popCans) {
			// DO NOTHING

		}

	}

	/**
	 * A class for the DeliveryChuteListener to get events from the machine
	 */
	private class MyDeliveryChuteListener implements DeliveryChuteListener {

		@Override
		public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
			System.out.println("Chute Enabled");

		}

		@Override
		public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
			System.out.println("Chute Disabled");

		}

		@Override
		public void itemDelivered(DeliveryChute chute) {
			System.out.println("Item Delivered");
			

		}

		@Override
		public void doorOpened(DeliveryChute chute) {
			// DO NOTHING

		}

		@Override
		public void doorClosed(DeliveryChute chute) {
			// DO NOTHING

		}

		@Override
		public void chuteFull(DeliveryChute chute) {
			System.out.println("Chute Full");

		}

	}
	private class MyDisplayListener implements DisplayListener {

		@Override
		public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void messageChange(Display display, String oldMessage, String newMessage) {
			System.out.println("new message is ++\n" + newMessage); //for testing remove this when not needed 
			messageBeingDisplayed = newMessage;						//for testing
			
		
		}
	}
	private class MyOutOfOrderLightListener implements IndicatorLightListener{

		@Override
		public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
			System.out.println("Out of Order Light enabled");
			
		}

		@Override
		public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
			exactChange();
		  System.out.println("Out of Order Light Disabled");
			
		}

		@Override
		public void activated(IndicatorLight light) {
			System.out.println("Out of Order Light activated");
			
		}

		@Override
		public void deactivated(IndicatorLight light) {
			System.out.println("Out of Order Light deactivated");
			
		}
	}
	private class MyExactChangeOnlyLightListener implements IndicatorLightListener{

		@Override
		public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
			System.out.println("Exact Change Only Light enabled");
			
		}

		@Override
		public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		  System.out.println("Exact Change Only Light disabled");
			
		}

		@Override
		public void activated(IndicatorLight light) {
			System.out.println("Exact Change Only Light activated");
			
		}

		@Override
		public void deactivated(IndicatorLight light) {
			System.out.println("Exact Change Only Light deactivated");
			
		}
		
	}
	
}