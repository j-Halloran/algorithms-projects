//Author Jake Halloran
//Project 3 CS 1501
//Last Edited: 3/15/16

//Importing scanner for user input;
import java.util.Scanner; 

/**
* Class that serves as the user control over a priority queue based database of user defined cars
* the program supports a dynamically expanding number of cars as well as continued operation until
* the user explicitly decides to exit the program
* @author Jake Halloran
**/
public class CarTracker{
	static CarQueue carQueue; //Priority Queue of user defined cars
	static Scanner input = new Scanner(System.in); //Global user input scanner
	
	/**
	* Infinitely looping main that calls subfunctions listed below
	**/
	public static void main(String[] args){
		boolean doAgain = true; //loop condition flag
		carQueue = new CarQueue(); //Initialize car queue
		input = new Scanner(System.in); //Initialize Scanner
		
		//Loop until user enters 8 for exit
		while(doAgain){
			int userChoice = 0;
			
			//List of user options prints once per subfunction run
			System.out.println("\nSelection Menu\n1) Add A Car\n2) Update A Car");
			System.out.println("3) Remove A Specific Car\n4) Retrieve Lowest Price Car");
			System.out.println("5) Retrieve Lowest Mileage Car\n6) Retrieve Lowest Price Car By Make And Model");
			System.out.println("7) Retrieve Lowest Mileage Car By Make And Model\n8) Exit");
			
			//Inner loop that goes until user picks a valid subroutine
			while(userChoice<1||userChoice>8){
				try{	
					System.out.print("Enter your choice (1-8): ");
					userChoice = input.nextInt();
				}
				//Stops the user from entering anything but an integer
				catch(java.util.InputMismatchException e){
					System.out.println("Enter only integers please.");
					userChoice = 0;
					input = new Scanner(System.in);
				}
			}
			
			//Switch case to easily follow user decision
			switch (userChoice){
				case 1: carAdd(); break;
				case 2: carUpdate(); break;
				case 3: remove(); break;
				case 4: retrievePrice(); break;
				case 5: retrieveMiles(); break;
				case 6: retrieveMakePrice(); break;
				case 7: retrieveMakeMiles(); break;
				case 8: doAgain = false; break;
			}
		}
	}
	
	/**
	* Adds a new car to both queues
	**/
	private static void carAdd(){
		Car newCar = new Car();
		carQueue.carAdd(newCar);
	}
	
	/**
	* Updates and existing car in the queue
	**/
	private static void carUpdate(){
		System.out.print("Enter VIN of car to update: ");
		input.nextLine();
		String VIN = input.nextLine().toUpperCase();
		int result = carQueue.carUpdate(VIN);
		if(result == -1){
			System.out.println("VIN entered does not match an existing vehicle.");
		}
		else{
			System.out.println("Update successful.");
		}
	}
	
	/**
	* Removes a vehicle matching an inputted VIN
	**/
	private static void remove(){
		System.out.print("Enter VIN of car to remove: ");
		input.nextLine();
		String VIN = input.nextLine().toUpperCase();
		int result = carQueue.remove(VIN);
		if(result == -1){
			System.out.println("VIN entered does not match an existing vehicle.");
		}
		else{
			System.out.println("Removal successful.");
		}
	}

	/**
	* Retrieves the vehicle with the lowest price
	**/
	private static void retrievePrice(){
		Car lowPrice = carQueue.retrievePrice();
		if(lowPrice == null){
			System.out.println("Retrieval impossible. No cars are in the queue.");
		}
		else{
			System.out.print("\nData for lowest price vehicle: ");
			System.out.print(lowPrice);
		}
	}
	
	/**
	* Retrieves the vehicle with the lowest mileage
	**/
	private static void retrieveMiles(){
		Car lowMiles = carQueue.retrieveMiles();
		if(lowMiles == null){
			System.out.println("Retrieval impossible. No cars are in the queue.");
		}
		else{
			System.out.print("\nData for lowest mileage vehicle: ");
			System.out.print(lowMiles);
		}
	}
	
	/**
	* Retrieves the vehicle with the lowest price for a given make and model
	**/
	private static void retrieveMakePrice(){
		System.out.print("Enter make of desired car: ");
		input.nextLine();
		String make = input.nextLine().toUpperCase();
		System.out.print("Enter model of desired car: ");
		String model = input.nextLine().toUpperCase();
		
		Car lowPrice = carQueue.retrieveMakePrice(make,model);
		
		if(lowPrice == null){
			System.out.println("Retrieval failed. Either the queue is empty or no matching make and model cars found.");
		}
		else{
			System.out.print("\nData for lowest price vehicle: ");
			System.out.print(lowPrice);
		}
	}
	
	/**
	* Retrieves the vehicle with the lowest mileage for a given make and model
	**/
	private static void retrieveMakeMiles(){
		System.out.print("Enter make of desired car: ");
		input.nextLine();
		String make = input.nextLine().toUpperCase();
		System.out.print("Enter model of desired car: ");
		String model = input.nextLine().toUpperCase();
		
		Car lowMiles = carQueue.retrieveMakeMiles(make,model);
		
		if(lowMiles == null){
			System.out.println("Retrieval failed. Either the queue is empty or no matching make and model cars found.");
		}
		else{
			System.out.print("\nData for lowest mileage vehicle: ");
			System.out.print(lowMiles);
		}
	}

}

