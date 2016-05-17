//Last Edited: 4/7/16
import java.util.Comparator;

/**
* This represents an edge in the adjacency list of the airline route graph.
* @author Jake Halloran
**/
public class Edge{
	private int origin;
	private int dest;
	private double price;
	private double mileage;
	
	/**
	* Edge constructor that takes two vertices, a price weight, and a mileage weight
	* @param origin the integer represented origin vertex
	* @param dest the integer represented destination vertex
	* @param price the price based edge weight
	* @param mileage the flight distance weight in miles
	**/
	public Edge(int origin, int dest, double price, double mileage){
		if(origin<0||dest<0) throw new IllegalArgumentException("Invalid city selection.");
		this.origin = origin;
		this.dest = dest;
		this.price = price;
		this.mileage = mileage;
	}
	
	/**
	* Constructor that exists solely to allow access to comparator methods easily
	**/
	public Edge(){
		return;
	}
	
	/**
	* Return's the edge's price weight
	* @return the price weight of the current flight edge
	**/
	public double price(){
		return price;
	}
	
	/**
	* Returns the edge's mileage weight
	* @return the double value representing the weight of the edge in miles
	**/
	public double mileage(){
		return mileage;
	}
	
	/**
	* Returns the edge's origin vertex
	* @return integer representation of the destination vertex
	**/
	public int origin(){
		return origin;
	}
	
	/**
	* Returns the edge's destination vertex
	* @return the integer representation of the edge
	*/
	public int dest(){
		return dest;
	}
	
	/**
	* Returns the other vertex of the edge or -1 if not applicable
	* @param vertex the vertex that is not the one the program returns
	* @return Integer representation of the other vertex
	*/
	public int other(int vertex){
		if(vertex==origin) return dest;
		else if(vertex==dest) return origin;
		throw new IllegalArgumentException("Vertex: "+vertex+" not valid.");
	}
	
	/**
	* Comparator for mileage edges
	* @return a comparator for the edge class based on mileage
	**/
    public static Comparator<Edge> mileComparator() {
        return new Comparator<Edge>() {
            public int compare(Edge a, Edge b){
				if(a.mileage()==b.mileage()){
					return 0;
				}
				else if(a.mileage()>b.mileage()){
					return 1;
				}
				return -1;
			}
        };
    }
	
	/**
	* Comparator for price edges
	* @return a comparator for the edge class based on price
	**/
    public static Comparator<Edge> priceComparator() {
        return new Comparator<Edge>() {
            public int compare(Edge a, Edge b){
				if(a.price()==b.price()){
					return 0;
				}
				else if(a.price()>b.price()){
					return 1;
				}
				return -1;
			}
        };
    }
	
	/**
	* toString override that prints the price and mileage
	* @return the string representation of the price and mileage
	**/
	public String toString(){
		return String.format("%.0f\t %.2f",mileage,price);
	}

	/**
	* Returns the weight matching the input parameter
	* Equal weight for 0, mileage weight for 1, price weight for 2
	* @param weightSelect integer that chooses which weight to return
	* @return the weight as a double
	* @throws IllegalArgumentException if the weight is not 0,1, or 2
	**/
	public double weight(int weightSelect){
		if(weightSelect==0){
			return 1;
		}
		if(weightSelect==1){
			return mileage;
		}
		if(weightSelect==2){
			return price;
		}
		else{
			throw new IllegalArgumentException("Illegal Weight Type.");
		}
	}
}