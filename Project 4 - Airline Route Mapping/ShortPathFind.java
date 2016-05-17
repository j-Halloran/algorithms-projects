/**
* This class uses dijkstra's algortihm to find the shortest path based
* on a parameterized weight system 0 for none, 1 for mileage, 2 for price
* @author Jake Halloran
* Last Edited: 4/8/16
**/
import java.util.Stack;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Comparator;
import java.util.Collections;

public class ShortPathFind{
	private double[] dist; //distance of paths
	private Edge[] lastEdge; //last edge on shortest path to index
	private ArrayList<DistanceNode> queue; //holds distances vertex pairs
	private int weightType; //selects the weight to use
	private ArrayList<ArrayList<Edge>> adj; //Adjacency list for graph
	private int numCities; //number of cities, used to bound several arrays in size
	private ArrayList<String> citylist; //list of cities of size numCities
	private int destination; //user chosen destination city
	private int originCity; //user chosen origin point
	
	/**
	* Constructor that duplicated variables over from Airline.java and stores them
	* @param adj the adjacency list for the graph
	* @param weightType the weight that should be used, 0 for counting hops, 1 for mileage, 2 for price
	* @param numCities the total number of cities in the database
	* @param citylist the array list containing all city names in the database
	**/
	public ShortPathFind(ArrayList<ArrayList<Edge>> adj, int weightType, int numCities, ArrayList<String> citylist){
		this.weightType = weightType;
		this.adj = adj;
		this.numCities = numCities;
		this.citylist = citylist;
	}
	
	/**
	* Generates shortest path from a user entered origin to all other vertices and stores them
	* @return false if either the origin or destination city does not exist, true otherwise
	**/
	public boolean findPath(){
		//Reads in origin and destination cities
		Scanner input = new Scanner(System.in);
		System.out.print("\nEnter the Origin City: ");
		String userCity = input.nextLine();
		if(!citylist.contains(userCity)){
			System.out.println("Origin city not in database. Returning."); 
			return false;
		}
		int origin = citylist.indexOf(userCity);
		originCity = origin;
		
		System.out.print("Enter destination City: ");
		userCity = input.nextLine();
		if(!citylist.contains(userCity)){
			System.out.println("Destination city not in database. Returning.");
			return false;
		}
		int dest = citylist.indexOf(userCity);
		destination = dest;
		
		//Begins dijkstra's algorithm
		dist = new double[numCities]; //initialize dist
		lastEdge = new Edge[numCities]; //initialize edge container
		queue = new ArrayList<DistanceNode>(); //queue to hold distances to each city
		
		//Initialize distances to each city as inf as sentinel value
		for(int i=0;i<numCities;i++){
			dist[i] = Double.POSITIVE_INFINITY;
		}
		//Set distance to self as 0
		dist[origin] = 0.0;
		//Queue initial node
		queue.add(new DistanceNode(origin, 0.0));
		//While nodes are left to process
		while(queue.size()>0){
			//Pop the vertex with the lowest distance to remaining
			int vertex = queue.remove(0).index();
			
			//Attempt to lower the distance to other nodes through the popped vertex
			for(Edge e: adj.get(vertex)){
				relax(e);
			}
		}
		return true;
	}
	
	/**
	* Attempts to lower the distance to a vertex by connecting it to the current vertex
	* @param e The Edge that is attempting to replace the distance to a vertex
	**/
	private void relax(Edge e){
		int origin = e.origin()-1; //read the origin and convert to 0-indexed
		int dest = e.dest()-1; //read dest and convert to 0-indexed
		boolean flag = false; //flag for adding to array list
		
		//If path through this edge gets to a node in less distance store the new distance
		if(dist[dest]>dist[origin]+e.weight(weightType)){
			dist[dest] = dist[origin] + e.weight(weightType);
			lastEdge[dest] = e;
			
			//if the vertex is already stored, just update its distance
			for(int i =0; i<queue.size(); i++){
				if(queue.get(i).index()==dest){
					queue.get(i).setKey(dist[dest]);
					flag = true;
				}
			}
			
			//Add the destination city if not already present and sort the array list
			if(!flag){
				queue.add(new DistanceNode(dest,dist[dest]));
				Collections.sort(queue,queue.get(0).keyComparator());
			}
		}
	}
	
	/**
	* Prints the path from a user entered origin city to a user entered destination city
	**/
	public void printPath(){
		//if there isn't a path say so
		if(!(dist[destination]<Double.POSITIVE_INFINITY)){
			System.out.println("No path was found between these cities.");
			return;
		}
		
		//Copy the path edges onto a stack for printing
		Stack<Edge> path = new Stack<Edge>();
		for(Edge e = lastEdge[destination]; e!=null; e = lastEdge[e.origin()-1]){
			path.push(e);
		}
		
		//Display the path in reverse order
		System.out.printf("\nMINIMUM PATH FROM %s TO %s\n",citylist.get(originCity),citylist.get(destination));
		System.out.println("Path is in reverse order.");
		System.out.println("--------------------------------------------------");
		
		//Print weight statement based on weight type
		switch(weightType){
			case(0): System.out.printf("Total number of hops: %.0f\n",dist[destination]); break;
			case(1): System.out.printf("Total Mileage: %.0f\n",dist[destination]); break;
			case(2): System.out.printf("Total Price: %.0f\n",dist[destination]);
		}
		
		//Prints the path in reverse order
		if(weightType==0){
			for(Edge e: path){
				if((e.origin()-1) == originCity){
					System.out.format("%s to %s",citylist.get(e.dest()-1),citylist.get(e.origin()-1));
					continue;
				}
				System.out.format("%s to ",citylist.get(e.dest()-1));
			}
			System.out.println();
		}
		else{
			for(Edge e: path){
				if((e.origin()-1) == originCity){
					System.out.format("%s %.0f %s",citylist.get(e.dest()-1),e.weight(weightType),citylist.get(e.origin()-1));
					continue;
				}
				System.out.format("%s %.0f ",citylist.get(e.dest()-1),e.weight(weightType));
			}
			System.out.println();
		}
	}
}


/**
* Class to store data used to represent the shortest distance to a vertex in dijkstra's algortihm
**/
class DistanceNode{
	private double key; //distance to origin
	private int index; //vertex number
	
	/**
	* Constructor that accepts an index and key value and stores them
	* @param index the integer representation of the store vertex
	* @param key the value of this vertex's distance from origin
	**/
	public DistanceNode(int index, double key){
		this.index = index;
		this.key = key;
	}
	
	/**
	* Allows caller to change key value. Useful in implementing dijkstra's
	* @param key the new distance value to be stored
	**/
	public void setKey(double key){
		this.key = key;
	}
	
	/**
	* Accessor for the vertex stored in the object
	* @return integer representation of the stored vertex
	**/
	public int index(){
		return index;
	}
	
	/**
	* Accessor for the distance stored in the object
	* @return double representing the distance from the origin to this vertex
	**/
	public double key(){
		return key;
	}
	
	/**
	* Comparator for keys
	* @return a comparator for the key
	**/
    public static Comparator<DistanceNode> keyComparator() {
        return new Comparator<DistanceNode>() {
            public int compare(DistanceNode a, DistanceNode b){
				if(a.key()==b.key()){
					return 0;
				}
				else if(a.key()>b.key()){
					return 1;
				}
				return -1;
			}
        };
    }
}