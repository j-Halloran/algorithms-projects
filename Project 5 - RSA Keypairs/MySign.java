import java.security.MessageDigest;
import java.math.BigInteger;
import java.io.*;
import java.util.Scanner;
import java.util.Random;
import java.security.NoSuchAlgorithmException;

/**
* This class allows a user to use an RSA key pair to sign files and verify those signatures
* Last Edited: 4/23/16
* @author Jake Halloran
**/
public class MySign{
	/**
	* @param args Contains the filename of the message to hash and whether to sign or verify
	* @throws IOException if there is an issue reading any of the files
	* @throws IllegalArgumentException if there are improper command line arguments
	**/
	public static void main(String args[]) throws NoSuchAlgorithmException{
		byte[] inputBuffer = new byte[1000];
		int bytesRead = 0;
		
		//Command line argument verification
		if(args.length!=2){
			System.err.println("You must pass either s or v and a filename.");
			System.exit(1);
		}
		if(!args[0].equals("s")&&!args[0].equals("v")){
			System.err.println("The first commandline argument must be either v or s.");
			System.exit(1);
		}
		try{
			if(args[0].equals("s")){
				MessageDigest md  = MessageDigest.getInstance("SHA-256");
				Scanner input = null;
				StringBuilder inputBuilder = new StringBuilder();
				
				//open the command line file
				try{
					input = new Scanner(new File(args[1]));
				}
				catch(FileNotFoundException ex){
					System.err.println("The entered file does not exist.");
					System.exit(1);
				}
				
				//Read the bytes of the file into the message digest
				while(input.hasNextLine()){
					String nextLine = input.nextLine();
					inputBuilder.append(nextLine);
					inputBuilder.append("\n");
				}
				String inputResult = inputBuilder.toString();
				byte[] inputBytes = inputResult.getBytes();
				md.update(inputBytes);
				byte[] digest = md.digest();
				BigInteger result = new BigInteger(1,digest);
				
				//try to open privkey.rsa
				BufferedReader privkeyReader = null;
				try{
					privkeyReader = new BufferedReader(new FileReader(new File("privkey.rsa")));
				}
				catch(FileNotFoundException ex){
					System.err.println("The required file privkey.rsa was not found.");
					System.exit(1);
				}
				
				//Read in d and n for computation
				BigInteger d = new BigInteger(privkeyReader.readLine());
				BigInteger n = new BigInteger(privkeyReader.readLine());
				
				//Calculate the signed message
				result = result.modPow(d,n);
				
				//Close input file
				input.close();
				
				String outputName = args[1]+".signed";
				ObjectOutputStream output =new ObjectOutputStream( new FileOutputStream(outputName));
				output.writeObject(inputBuilder);
				output.writeObject(result);
				output.close();
				
			}
			else if(args[0].equals("v")){
				MessageDigest md = MessageDigest.getInstance("SHA-256");
				ObjectInputStream input = null;
				StringBuilder inputBuilder = new StringBuilder();
				BigInteger previousHash = null;
				String inFileName = args[1];
				try{
					input = new ObjectInputStream(new FileInputStream(inFileName));
				}
				catch(IOException ex){
					System.err.println("The entered file does not exist or has invalid format.");
					System.exit(1);
				}
				
				//Read in both objects from the file
				try{
					inputBuilder = (StringBuilder) input.readObject();
					previousHash = (BigInteger) input.readObject();
				}
				catch(ClassNotFoundException e){
					System.err.println("Error reading signed file.");
					System.exit(1);
				}
				//Calculate the message digest again 
				String inputResult = inputBuilder.toString();
				byte[] inputBytes = inputResult.getBytes();
				md.update(inputBytes);
				byte[] digest = md.digest();
				BigInteger result = new BigInteger(1,digest);
				
				//try to open pubkey.rsa
				BufferedReader pubkeyReader = null;
				try{
					pubkeyReader = new BufferedReader(new FileReader(new File("pubkey.rsa")));
				}
				catch(IOException ex){
					System.err.println("The required file pubkey.rsa was not found.");
					System.exit(1);
				}
				
				//Read in e and  n for processing
				BigInteger e = new BigInteger(pubkeyReader.readLine());
				BigInteger n = new BigInteger(pubkeyReader.readLine());
				
				//"Encrypt" the previous hash
				previousHash = previousHash.modPow(e,n);
				
				//Check signature
				if(previousHash.equals(result)){
					System.out.println("The RSA signature on this file is valid.");
				}
				else{
					System.out.println("The RSA signature on this file is invalid.");
				}
			}
		}
		//Lazy catch anything I missed
		catch(IOException ex){
			System.err.println("Error reading file.");
			System.exit(1);
		}
	}
}