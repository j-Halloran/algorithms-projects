//Author: Jake Halloran
//Last Edited: 3/15/16

/**
* Class that uses java's built in indirection array based priority queue
* in order to store a sorted list of vehicle references
* Stores data in two separate queues for runtime speed however, obviously causes add
* constant multiple of 2 increase in storage requirements
* @author Jake Halloran
**/
import java.util.PriorityQueue;
import java.util.Iterator;
public class CarQueue{
	PriorityQueue<Car> mileQueue; //Indirection based prioirty queue for lowest mileage
	PriorityQueue<Car> priceQueue; //Indirection based priority queue for lowest price
	
	/**
	* Default constructor that initializes priority queues
	**/
	public CarQueue(){
		Car temp = null; //creates a null car in order to access static methods
		
		//Uses the static comparator generator methods to initialize both priority queues
		//This works because there are two static comparator classes within the Car class
		mileQueue = new PriorityQueue<Car>(11,temp.mileComparator());
		priceQueue = new PriorityQueue<Car>(11, temp.priceComparator());
	}
	
	/**
	* Adds a new vehicle to the queue using price as the first priority
	* functions by using the .offer() method that is prefered by the java collections framework
	* @param newCar A reference to the vehicle being added
	**/
	public void carAdd(Car newCar){
			priceQueue.offer(newCar);
			mileQueue.offer(newCar);
	}
	
	/**
	* Allows the user to update a car by entering its VIN
	* @param VIN the VIN of the car being updated
	* @return -1 if the vehicle was not found, 1 otherwise
	**/
	public int carUpdate(String VIN){
		Iterator<Car> carIterator = mileQueue.iterator(); //Iterator to iterate over PQ
		
		//Goes until all elements have been accessed
		while(carIterator.hasNext()){
			Car temp = (Car)carIterator.next(); //Stores each elements
			
			//Activates if iff the VIN of the current car matches user input
			if(temp.getVIN().equals(VIN)){
				//Pulls the car to be edited out of the queue
				priceQueue.remove(temp);
				mileQueue.remove(temp);
				
				//Initialize variables to get user input on what to change
				java.util.Scanner input = new java.util.Scanner(System.in);
				int userChoice = 0;
				
				//Loops until the user chooses something valid to change
				System.out.println("\n1) Change Price\n2) Change Mileage\n3) Change Color");
				while(userChoice<1||userChoice>3){
					System.out.print("Enter you choice (1-3): ");
					userChoice = Integer.parseInt(input.nextLine()); //avoids wierd bufffer issue
				}
				
				//Updates the car based on new user input
				if(userChoice ==1){
					System.out.print("Enter new price: ");
					temp.setPrice(Integer.parseInt(input.nextLine()));
				}
				else if(userChoice ==2){
					System.out.print("Enter new mileage: ");
					temp.setMileage(Integer.parseInt(input.nextLine()));
				}
				else if(userChoice ==3){
					System.out.print("Enter new color: ");
					temp.setColor(input.nextLine());
				}
				
				//Readd the updated vehicle to the queues
				priceQueue.add(temp);
				mileQueue.add(temp);
				return 1;
			}
		}
		//Return -1 if the VIN does not match a car in the array, or there are no cars to iterate through
		return -1;
	}
	
	/**
	* Removes a specific vehicle from the queue
	* @param VIN the VIN of the vehicle being removed
	* @return -1 if the remove failed and 1 otherwise
	**/
	public int remove(String VIN){
		Iterator<Car> carIterator = priceQueue.iterator();
		Car temp = null;
		
		//Loops over the iterator until the vehicle is found or there are no more vehicles left
		while(carIterator.hasNext()){
			temp = carIterator.next();
			if(temp.getVIN().equals(VIN)){
				//Remove from both queues
				priceQueue.remove(temp);
				mileQueue.remove(temp);
				//Return 1 if the removal was successful
				return 1; 
			}
		}
		//Return -1 if the vehicle is not found
		return -1;
	}
	
	/**
	* Retrieves and returns the car with the lowest price overall
	* @return reference to the lowest priced car or null if no cars have been added
	**/
	public Car retrievePrice(){
		return priceQueue.peek(); //simply returns the first element if one exists otherwise, returns null
	}	
	
	/**
	* Retrieves and returns the car with the lowest mileage overall
	* @return reference to the lowest miles car or null if there are no cars in the queue
	**/
	public Car retrieveMiles(){
		return mileQueue.peek(); //simply returns the first element or null if it does not exist
	}
	
	/**
	* Retrieves and returns the car with the lowest price for a given make and model
	* @param make The string make of the chosen car
	* @param model The string representation of the current vehicle's model
	* @return reference to the lowest priced car that matches or null if not found
	**/
	public Car retrieveMakePrice(String make, String model){
		if(priceQueue.size()==0){
			return null;
		}
		else{
			//Iterates until a car with matching make and model is found and then returns the lowest one
			//or returns null if no match is found
			Iterator<Car> carIterator = priceQueue.iterator();
			Car min = null;
			
			while(carIterator.hasNext()){
				Car temp = carIterator.next();
				if(temp.getModel().equals(model)&&temp.getMake().equals(make)){
					if(min == null){
						min = temp;
					}
					if(min.getPrice()>temp.getPrice()){
						min = temp;
					}
				}
			}
			return min;
		}
	}
	
	/**
	* Retrieves and returns the car with the lowest mileage for a given make and model
	* @param make The string make of the chosen car
	* @param model The string representation of the current vehicle's model
	* @return reference to the lowest miles car that matches or null if not found
	**/
	public Car retrieveMakeMiles(String make, String model){
		if(mileQueue.size()==0){
			return null;
		}
		else{
			Iterator<Car> carIterator = mileQueue.iterator();
			Car min = null;
			
			while(carIterator.hasNext()){
				Car temp = carIterator.next();
				if(temp.getModel().equals(model)&&temp.getMake().equals(make)){
					if(min==null){
						min = temp;
					}
					if(min.getMileage()>temp.getMileage()){
						min = temp;
					}
				}
			}
			return min;
		}
	}
	
}