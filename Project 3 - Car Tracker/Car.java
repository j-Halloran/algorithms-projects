//Author Jake Halloran
//Last Edited 3/15/16

import java.util.Scanner;
import java.util.Comparator;

/**
* Class representing a single carr maintained within the priority queue database
* includes accessor and settors for all fields that are able to be changed externally
* Additionally, includes comparators for both price and mileage based data structures
* @author Jake Hallorn
**/
public class Car{
	//List of private fields representing necessary vehicle data
	private String VIN;
	private String make;
	private String model;
	private int price;
	private int mileage;
	private String color;
	
	/**
	* Default constructor that creates a new Car object by requesting input for each field from the user
	**/
	public Car(){	
		Scanner input = new Scanner(System.in);
		System.out.print("\nEnter Vehicle Identification Number: ");
		String userInput = input.nextLine();
		setVIN(userInput);
		
		System.out.print("Enter vehicle make: ");
		userInput = input.nextLine();
		make = userInput.toUpperCase();
		
		System.out.print("Enter vehicle model: ");
		userInput = input.nextLine();
		model = userInput.toUpperCase();
		
		System.out.print("Enter vehicle price in dollars: ");
		userInput = input.nextLine();
		price = Integer.parseInt(userInput);
		
		System.out.print("Enter vehicle mileage: ");
		userInput = input.nextLine();
		mileage = Integer.parseInt(userInput);
		
		System.out.print("Enter vehicle color: ");
		userInput = input.nextLine();
		color = userInput.toUpperCase();
	}
	
	/**
	* Method to validate the user entered VIN and store it
	* @param VIN the VIN the user is trying to set
	**/
	public void setVIN(String VIN){
		boolean flag = false;
		VIN = VIN.toUpperCase();
		while(!flag){
			flag = true;
			if(VIN.length()!=17){
				flag = false;
				System.out.println("The VIN length was incorrect.");
			}
			if(VIN.indexOf("O")!=-1||VIN.indexOf("Q")!=-1||VIN.indexOf("I")!=-1){
				flag = false;
				System.out.println("Invalid characters included in VIN.");
			}
			if(!flag){
				System.out.print("Please enter a new VIN: ");
				Scanner input = new Scanner(System.in);
				VIN = input.nextLine();
				VIN = VIN.toUpperCase();
			}
		}
		this.VIN = VIN;
	}
	
	/**
	* Returns the current vehicle's VIN
	* @return the current VIN in string form
	**/
	public String getVIN(){
		return VIN;
	}
	
	/**
	* Returns the make of the current vehicle
	* @return the string representation of the current vehicle's make
	**/
	public String getMake(){
		return make;
	}
	
	/**
	* Returns the model of the current vehicle
	* @return the string representation of this vehicle's model
	**/
	public String getModel(){
		return model;
	}
	
	/**
	* Allows caller to set the price of this vehicle
	* @param price The new price for this vehicle in whole dollars
	**/
	public void setPrice(int price){
		this.price = price;
	}
	
	/**
	* Returns the current price of this vehicle
	* @return the price in whole dollars of this vehicle
	**/
	public int getPrice(){
		return price;
	}
	
	/**
	* Allows caller to set the vehicle's mileage
	* @param mileage the vehicle's new mileage
	**/
	public void setMileage(int mileage){
		this.mileage = mileage;
	}
	
	/**
	* Returns to the user the mileage of this vehicle
	* @return integer representation of the current mileage
	**/
	public int getMileage(){
		return mileage;
	}
	
	/**
	* Allows caller to set the color of this vehicle
	* @param color the string representation of the new color
	**/
	public void setColor(String color){
		this.color = color.toUpperCase();
	}
	
	/**
	* Accessor for color
	* @return The string name of the vehicle's color
	**/
	public String getColor(){
		return color;
	}
	
	/** 
	* Override of default toString actions
	* @return The null string after printing necessary data
	**/
	public String toString(){
		System.out.println("\nVIN: "+VIN);
		System.out.println("Make: "+make);
		System.out.println("Model: "+model);
		System.out.println("Price: "+price);
		System.out.println("Mileage: "+mileage);
		System.out.println("Color: "+color);
		return "";
	}
	
	/**
	* Comparator for price based priority queue
	* @return a comparator for the Car class based on price
	**/
	public static Comparator<Car> priceComparator() {
        return new Comparator<Car>() {
            public int compare(Car a, Car b){
				if(a.getMileage()==b.getMileage()){
					return 0;
				}
				else if(a.getMileage()>b.getMileage()){
					return -1;
				}
				return 1;
			}
        };
    }
	
	/**
	* Comparator for mileage based priority queue
	* @return a comparator for the car class based on mileage
	**/
    public static Comparator<Car> mileComparator() {
        return new Comparator<Car>() {
            public int compare(Car a, Car b){
				if(a.getPrice()==b.getPrice()){
					return 0;
				}
				else if(a.getPrice()>b.getPrice()){
					return -1;
				}
				return 1;
			}
        };
    }
}

