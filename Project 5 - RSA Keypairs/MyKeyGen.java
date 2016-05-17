import java.math.BigInteger;
import java.util.Random;
import java.io.*;
/**
* This class uses java's big integer to create a public/private key pair for an implementation of 
* 1024 bit RSA. DONT use for actually important things.
* @author Jake Halloran (jph74@pitt.edu)

**/
public class MyKeyGen{
	
	/**
	* Generates a RSA public, private key pair of 1024 bit length
	* @param args Command line arguments are not used in this program
	* @throws IOException If there is an issue creating the files to store the keys in
	**/
	public static void main(String args[]){
		String one = "1";
		BigInteger p = new BigInteger(one);
		BigInteger q = new BigInteger(one);
		BigInteger n = new BigInteger(one);
		BigInteger phiN = new BigInteger(one);
		BigInteger temp = new BigInteger(one);
		BigInteger e = new BigInteger(one);
		BigInteger d = new BigInteger(one);
		
		//Generates probably prime p and q values
		p = p.probablePrime(512,new Random());
		q = q.probablePrime(512,new Random());
				
		//Calculates n as p*q
		n = p.multiply(q);
		
		//Calculates phi(n) = (q-1)*(p-1)
		temp = p.subtract(p.ONE);
		phiN = temp.multiply((q.subtract(q.ONE)));
		
		//Iterates until it finds a value for that GCD(e,phi(n)) == 1
		BigInteger i= new BigInteger(one);
		e = e.ONE;
		temp = temp.ONE;
		while(i.compareTo(phiN)==-1){
			i = i.add(temp);
			e = e.nextProbablePrime();
			if(e.gcd(phiN).equals(e.ONE)){
				break;
			}
		}
		
		//Calculates d as E^(-1) mod (phi(n))
		d = e.modInverse(phiN);
		
		//Writes key pair to file
		try{
			PrintWriter privateWriter = new PrintWriter(new File("privkey.rsa"));
			PrintWriter publicWriter = new PrintWriter(new File("pubkey.rsa"));
			
			privateWriter.println(d);
			privateWriter.print(n);
			publicWriter.println(e);
			publicWriter.print(n);
			
			privateWriter.close();
			publicWriter.close();
		}
		catch(IOException ex){
			System.err.println("Error writing key pair to file.");
		}
	}
}