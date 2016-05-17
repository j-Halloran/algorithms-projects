/**
* Author: Jake Halloran (jph74@pitt.edu)
* Last Edited: 4/5/16
* CS 1501 Assignment 4
**/

import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.PrintWriter;
import java.util.PriorityQueue;
import java.util.LinkedList;

/**
* This class creates and operates a console based airline route management system
* Supports adding and removing routes as well as displaying routes based on various conditions
**/
public class Airline{
	private static ArrayList<ArrayList<Edge>> adj; //Array List based adjacency list
	private static int numCities; //count of the number of cities in the database
	private static ArrayList<String> citylist; //list of city names. of size numCities
	private static boolean[] inTree; //used for MST to determine if a vertex was visited
	private static LinkedList<Edge> mst; //linked list holding the edges in the MST
	
	/**
	* Main function that opens the user inputted file and then calls the subfunctions
	* @param args Command line arguments not used.
	**/
	public static void main(String[] args){
		String filename;
		Scanner input = new Scanner(System.in);
		System.out.print("What is the name of the file containing airline data: ");
		filename = input.nextLine();
		Scanner fileReader = null;
		citylist = null;
		
		try{
			fileReader = new Scanner(new File(filename));
		}
		catch(FileNotFoundException e){
			System.err.println("The entered file does not exist. Exiting.");
		}
		startGraph(fileReader);
		choiceMenu();
		fileReader.close();
		writeData(filename);
	}
	
	/**
	* Reads all data in from the user inputted file.
	* @param fileReader the Scanner object used to process the user input file
	* @throws IllegalArgumentException if the file is not properly formatted
	**/
	private static void startGraph(Scanner fileReader){
		numCities = Integer.parseInt(fileReader.nextLine());
		
		//verify file data structuring
		if(numCities<0) throw new IllegalArgumentException("Entered file contains invalid data.");
		
		//intialize list of cities
		citylist = new ArrayList<String>();
		
		//Read in city names
		for(int i=0;i<numCities;i++){
			citylist.add(fileReader.nextLine());
		}
		
		//Initialize adjacency list
		adj = new ArrayList<ArrayList<Edge>>();
		
		//Add space to hold array lists of edges for each city
		for(int j=0;j<numCities;j++){
			adj.add(new ArrayList<Edge>());
		}
		
		int origin, dest;
		double price,mileage;
		boolean routeFlag = false;
		
		//Read in each preexisting edge and add it to the adjacency list
		while(fileReader.hasNextLine()){
			routeFlag = false;
			String edge = fileReader.nextLine();
			String[] edgeData  = new String[4];
			edgeData = edge.split(" ");
			origin = Integer.parseInt(edgeData[0]);
			dest = Integer.parseInt(edgeData[1]);
			mileage = Double.parseDouble(edgeData[2]);
			price = Double.parseDouble(edgeData[3]);
			
			//Throw and exception if the file formatting is wrong
			if(origin<0||origin>numCities||dest<0||dest>numCities||price<0||mileage<0){
				throw new IllegalArgumentException("Entered File Contains Invalid Data");
			}
			
			//Remove duplicate routes and in-place routes
			if(origin==dest) continue;
			for(int k=0; k<adj.get(origin-1).size();k++){
				if(adj.get(origin-1).get(k).dest()==dest){
					routeFlag = true;
					break;
				}
			}
			if(routeFlag) continue;
			
			//add valid edges to graph
			adj.get(origin-1).add(new Edge(origin,dest,price,mileage));
			adj.get(dest-1).add(new Edge(dest,origin,price,mileage));
		}
	}

	/**
	* Wrapper function for an infinitely looping menu
	**/
	private static void choiceMenu(){
		Scanner input = new Scanner(System.in);
		int userChoice = 0;
		while(userChoice!=9){
			userChoice = 0;
			System.out.println("\nSelection Menu:");
			System.out.println("1) Show list of all routes.\n2) Display minimum spanning tree.\n3) Shortest mileage path.");
			System.out.println("4) Lowest price path\n5) Lowest number of stops path\n6) List all paths cheaper than a given amount");
			System.out.println("7) Add a new route\n8) Remove a route\n9) Save data and quit");
			while(userChoice<1||userChoice>9){
				System.out.print("Enter your choice (1-9): ");
				userChoice = Integer.parseInt(input.nextLine());
			}
			switch(userChoice){
				case 1: listRoutes(); break;
				case 2: generateMST(); break;
				case 3: shortestMilesPath(); break;
				case 4: lowestPricePath(); break;
				case 5: shortestHopsPath(); break;
				case 6: underCostFind(); break;
				case 7: addRoute(); break;
				case 8: removeRoute(); break;
				default: break;
			}
		}
	}
		
	/**
	* Outputs a formatted list of all air routes to the console
	**/
	private static void listRoutes(){
		System.out.println("\nAll Air Routes");
		System.out.println("Note routes are duplicated, listed from each city's point of view.");
		System.out.println("--------------------------------------------------------------------");
		System.out.println("Origin:\t\t\t     Destination\tMileage\t Price");
		for(int i = 0;i<numCities;i++){
			for(int j = 0;j<adj.get(i).size();j++){
				System.out.format("%-20s%20s\t",citylist.get(adj.get(i).get(j).origin()-1),citylist.get(adj.get(i).get(j).dest()-1));
				System.out.println(adj.get(i).get(j));
			}
		}
	}
	
	/**
	* Generates a minimum spanning tree for all routes in the database based on mileage
	* if all cities are not connected, generates a MST for each connected component
	**/
	private static void generateMST(){
		//Unused object that exists to access functions 
		Edge temp = new Edge();
		
		//Creates a priority queue that is ordered based on the edge mileage
		PriorityQueue<Edge> queue = new PriorityQueue<Edge>(numCities, temp.mileComparator());
		mst = new LinkedList<Edge>(); //intialize MST
		inTree = new boolean[numCities]; //mark all vertices as unvisited
	
		//Runs the first tree
		if(!inTree[0]) runPrim(0,queue);
		
		//Prints out the first tree
		System.out.println("\nMINIMUM SPANNING TREE");
		System.out.println("Unless multiple connected components are identified, the MST contains all cities.");
		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("Connected Component 1");
		Edge e =mst.poll();
		while(e!=null){
			System.out.format("%s to %s: %.0f\n",citylist.get(e.origin()-1),citylist.get(e.dest()-1),e.mileage());
			e = mst.poll();
		}
		
		//Repeats above process with each connected component if all cities were not included originally
		int componentCount = 1;
		for(int i = 0;i<numCities;i++){
			if(!inTree[i]){
				mst = new LinkedList<Edge>();
				queue = new PriorityQueue<Edge>(numCities, temp.mileComparator());
				runPrim(i,queue);
				System.out.printf("\nConnected Component %d\n",++componentCount);
				e = mst.poll();
				if(e == null){
					System.out.printf("%s has no connecting flight paths.\n",citylist.get(i));
					continue;
				}
				while(e!=null){
					System.out.format("%s to %s: %.0f\n",citylist.get(e.origin()-1),citylist.get(e.dest()-1),e.mileage());
					e = mst.poll();
				}
			}
		}
		
	}
	
	/**
	* Uses a lazy prim algorithm to generate the MST per node
	* @param i the integer representation of the origin vertex
	* @param queue priorityQueue of all relevent edges
	**/
	private static void runPrim(int i,PriorityQueue<Edge> queue){
		scan(i,queue); //mark the current vertex as visited and queue its valid edges
		
		//Loop while there are still edges availible to process
		while(queue.size()!=0){
			Edge e = queue.poll(); //pop the lowest mileage edge in the queue
			int origin = e.origin();
			int dest = e.dest();
			
			//if both ends have already been visited, no need to process the edge
			if(inTree[origin-1]&&inTree[dest-1]) continue;
			
			//Otherwise add the edge to the MST and mark its endpoints and queue their valid edges
			mst.add(e);
			if(!inTree[origin-1]) scan(origin-1,queue);
			if(!inTree[dest-1]) scan(dest-1, queue);
			
		}
	}
	
	/**
	* Adds edges touching vertex i if the other endpoint has not been scanned
	* @param i the integer of the origin vertex
	* @param queue PriorityQueue of all relevent edges
	**/
	private static void scan(int i, PriorityQueue<Edge> queue){
		assert(!inTree[i]); //this vertex should not yet be in the queue under any conditions
		
		//Mark this vertex as being in the MST
		inTree[i] = true;
		
		//Add edges that lead to vertices not in the MST to the queue
		for(int j=0;j<adj.get(i).size();j++){
			Edge e = adj.get(i).get(j);
			if(!inTree[e.other(i+1)-1]) queue.add(e);
		}
	}
	
	/**
	* Uses Dijkstra's algorithm to find the shortest mileage path
	**/
	private static void shortestMilesPath(){
		ShortPathFind path = new ShortPathFind(adj,1,numCities,citylist);
		if(path.findPath()){
			path.printPath();
		}
	}
	
	/**
	* Uses Dijkstra's algorithm to find the lowest price path
	**/
	private static void lowestPricePath(){
		ShortPathFind path = new ShortPathFind(adj,2,numCities,citylist);
		if(path.findPath()){
			path.printPath();
		}
	}
	
	/**
	* Uses Dijkstra's algorithm to find the fewest hops path
	**/
	private static void shortestHopsPath(){
		ShortPathFind path = new ShortPathFind(adj,0,numCities,citylist);
		if(path.findPath()){
			path.printPath();
		}
	}
	
	/**
	* Lists all possible routes under a user inputted price that do not repeat cities
	**/
	private static void underCostFind(){
		//Reads in the max price
		Scanner input = new Scanner(System.in);
		System.out.print("\nEnter the maximum trip price: ");
		double maxPrice = Double.parseDouble(input.nextLine());
		if(maxPrice<=0){
			System.out.println("The max price must be greater than $0.00");
			return;
		}
		
		//Prints out header for section
		System.out.println("\nALL PATHS OF COST "+maxPrice+" OR LESS");
		System.out.println("-----------------------------------------------------------------");
		System.out.println("Note, all paths printed twice, once from each city.");
		
		boolean[] inUse = new boolean[numCities]; //marks which vertices have been visited
		ArrayList<Edge> path = new ArrayList<Edge>(); //array list to hold each path for printing;
		for(int i = 0;i < numCities; i++){
			double curPrice = 0; //reset the price counter
			underCostPath(i,inUse,path,curPrice,maxPrice); //find all paths under the max price
			inUse = new boolean[numCities]; //reset the use list
		}
	}
	
	/**
	* Recursive Algorithm to print all possible flight paths under a given price
	* @param vertex the integer representation of the current vertex
	* @param inUse boolean array marking which cities have been visited
	* @param path the current path from the origin city to the present city
	* @param curPrice the total price of the current path
	* @param maxPrice the maximum path price not to be exceeded
	**/
	private static void underCostPath(int vertex, boolean[] inUse, ArrayList<Edge> path, double curPrice, double maxPrice){
		//if stupid shit happens, return before it effects anything
		if(curPrice>maxPrice){
			return;
		}
		
		//Prints this level if applicable (AKA prints if not first node, or voer max price )
		if(curPrice>0){
			System.out.printf("Cost %.0f Path: ",curPrice);
			for(int i = 0;i < path.size(); i++){
				Edge e = path.get(i);
				if(i==0){
					System.out.printf(" %s %.0f %s ",citylist.get(e.origin()-1),e.price(),citylist.get(e.dest()-1));
					continue;
				}
				System.out.printf("%.0f %s ",e.price(),citylist.get(e.dest()-1));
			}
			System.out.println();
		}
		
		inUse[vertex] = true; //marks the vertex preventing future use
		
		//continues down the path if there are unvisited vertex's whose edges are not too expensive
		for(int i = 0;i<adj.get(vertex).size();i++){
			Edge e = adj.get(vertex).get(i);
			if(!inUse[e.dest()-1]){
				if(e.price()+curPrice<=maxPrice){
					path.add(e);
					underCostPath(e.dest()-1,inUse,path,(curPrice+e.price()),maxPrice);
					path.remove(e);
				}
			}
		}
		inUse[vertex] = false;
	}
	
	
	/**
	* Allows the user to enter a new route between two cities
	* Additionally, gives user option to add new cities if desired
	**/
	private static void addRoute(){
		int originNum, destNum;
		double price, mileage;
		String origin, dest, userInput = "";
		
		//Reads in origin city and gives user option to add to database if it does not exist
		Scanner input = new Scanner(System.in);
		System.out.print("\nEnter the origin city: ");
		origin = input.nextLine();
		if(!citylist.contains(origin)){
			System.out.println("The entered city is not in the database.");
			while(!userInput.equalsIgnoreCase("y")&&!userInput.equalsIgnoreCase("n")){
				System.out.print("Would you like to add the new city? (y/n): ");
				userInput = input.nextLine();
			}
			if(userInput.equalsIgnoreCase("y")){
				numCities++;
				citylist.add(origin);
				adj.add(new ArrayList<Edge>());
				originNum = numCities - 1;
			}
			else{
				System.out.println("Returning without adding route.");
				return;
			}
		}
		else{
			originNum = citylist.indexOf(origin);
		}
		
		//Reads in destination city and gives user option to add to database if it does not exist
		System.out.print("Enter the destination city: ");
		dest = input.nextLine();
		if(!citylist.contains(dest)){
			userInput = "";
			System.out.println("The entered city is not in the database.");
			while(!userInput.equalsIgnoreCase("y")&&!userInput.equalsIgnoreCase("n")){
				System.out.print("Would you like to add the new city? (y/n): ");
				userInput = input.nextLine();
			}
			if(userInput.equalsIgnoreCase("y")){
				numCities++;
				citylist.add(dest);
				adj.add(new ArrayList<Edge>());
				destNum = numCities - 1;
			}
			else{
				System.out.println("Returning without adding route.");
				return;
			}
		}
		else{
			destNum = citylist.indexOf(dest);
		}
		
		//Returns if the 2 cities already have a route between them
		for(int i = 0;i<adj.get(originNum).size();i++){
			if(adj.get(originNum).get(i).dest()==destNum+1){
				System.out.println("Route between these cities already exists.");
				System.out.println("To change the route's data, delete and then readd the route.");
				return;
			}
		}
		
		//Adds a new route to the database
		System.out.print("Enter mileage of new route: ");
		mileage = Double.parseDouble(input.nextLine());
		System.out.print("Enter price of new route: ");
		price = Double.parseDouble(input.nextLine());
		adj.get(originNum).add(new Edge(originNum+1,destNum+1,price,mileage));
		adj.get(destNum).add(new Edge(destNum+1,originNum+1,price,mileage));
	}
	
	/**
	* Removes a sepcified route from the list
	**/
	private static void removeRoute(){
		Scanner input = new Scanner(System.in);
		String userInput = "";
		int origin, dest;
		
		//Reads origin and exits if it is not found
		System.out.print("Enter origin city of route to remove: ");
		userInput = input.nextLine();
		if(!citylist.contains(userInput)){
			System.out.println("Entered city is not in database. Returning to menu.");
			return;
		}
		origin = citylist.indexOf(userInput);
		
		//Reads destination and exits if it is not found
		System.out.print("Enter destination city of route to remove: ");
		userInput = input.nextLine();
		if(!citylist.contains(userInput)){
			System.out.println("Entered city is not in database. Returning to menu.");
			return;
		}
		dest = citylist.indexOf(userInput);
		
		//Deletes a route from the database
		for(int i=0;i<adj.get(origin).size();i++){
			if(adj.get(origin).get(i).dest()==dest+1){
				adj.get(origin).remove(i);
				for(int j=0;j<adj.get(dest).size();j++){
					if((adj.get(dest).get(j).origin()==dest+1)&&(adj.get(dest).get(j).dest()==origin+1)){
						adj.get(dest).remove(j);
						return;
					}
				}
			}
		}
		System.out.println("Route not found in database.");
	}	
	
	/**
	* Writes all original data and new data to file
	* @param filename The name of the orignal user input file being written to
	**/
	private static void writeData(String filename){
		PrintWriter writer = null; //file write object to output route data
		try{
			writer = new PrintWriter(new File(filename));
		}
		catch(FileNotFoundException e){
			System.err.println("Error writing output to file. Exiting.");
			System.exit(1);
		}
		//Prints number and list of cities
		writer.println(numCities);
		for(int i=0;i<numCities;i++){
			writer.println(citylist.get(i));
		}
		
		//Prints all existing routes
		for(int j=0;j<numCities;j++){
			for(int k=0;k<adj.get(j).size();k++){
				//Only prints flights in one direction
				if(adj.get(j).get(k).origin()<adj.get(j).get(k).dest()){
					writer.printf("%d %d %.0f %.2f\n",adj.get(j).get(k).origin(),adj.get(j).get(k).dest(),adj.get(j).get(k).mileage(),adj.get(j).get(k).price());
				}
			}
		}
		writer.close();
	}
	
}