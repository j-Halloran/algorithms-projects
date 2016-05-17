//Jake Halloran (jph74@pitt.edu)
//Last Edited 2/11/16
//5 Character password generator and checker

//Too lazy to import things independently
import java.io.*;
import java.util.Scanner;

/**
*Password check class that creates and fills a DLB trie with correct passwords and validates user passwords.
*@author Jake Halloran
**/
public class pw_check{
	//generic console input scanner
	static Scanner input = new Scanner(System.in);
	
	/**
	*Calls other pw_check functions after verifying valid command line arguments
	* @param args command line arguments to decide to generate or validate passwords
	**/
	public static void main(String args[]){
		if(args.length==1&&args[0].equals("-g")){
			long startTime = System.nanoTime();
			generatePasswords();
			System.out.println("The list of passwords was generated in: "+(System.nanoTime()-startTime)+" ns.");
		}
		else if(args.length>=1){
			System.out.println("Invalid command line arguments.");
		}
		else{
			validatePasswords();
		}
	}
	
	/**
	*Generates the list of valid passwords by creating a DLB trie and filling it with invalid dictionary words.
	**/
	private static void generatePasswords(){
		
		int currentCharacters =0; //stores num characters in word currently tested
		int currentSymbols =0; //stores num symbols in current word being tested
		int currentNumbers =0; //stores num of numbers in current word being tested
		int numWords =0; //word count flag to make root creation nice
		DlbNode root = null; //reference to root of DLB trie
		
		//Try to catch mandatory file io exceptions
		try{
			//Write invalid dictionary to file variable
			PrintWriter my_dictionary = new PrintWriter(new BufferedWriter(new FileWriter("my_dictionary.txt")));
		
			//Stops user from regenerating good passwords if they already exist unless they want to repeat
			File runPrev = new File("good_passwords.txt");
			if(runPrev.exists()){
				String userChoice = " ";
				while(!userChoice.equals("n")&& !userChoice.equals("y")){
					System.out.print("Good passwrods have already been generated. Regenerate? (y/n): ");
					userChoice = input.nextLine();
				}
				if(!userChoice.equals("y")){
					System.out.println("\nWill not recreate file.");
					return;
				}
				else{
					System.out.println("\nRegenerating Passwords.");
				}
			}
			
			//add dictionary words <= 5 chars in length to DLB tree
			try{
				String currentWord; //stores words as read from dictionary or generated to test
				BufferedReader dictionary = new BufferedReader(new FileReader("dictionary.txt"));
				
				//Reads in each line one at a time
				while ((currentWord = dictionary.readLine())!=null){
					
					//Throws away dictionary words of length greater than passwords can be
					if(currentWord.length()>5){
						continue;
					}
					
					//append end of word flag
					currentWord = currentWord.toLowerCase() + "^";
					
					//create root node if necessary
					if (numWords==0){
						root = new DlbNode(my_dictionary,true);
						root.add(currentWord);
						numWords++;
					}
				
					//otherwise add word to trie
					else{
						root.add(currentWord);
					}
					
					//Adds substitute character prefixed words to trie
					switch (currentWord.charAt(0)){
						case 'a': root.add("4"+currentWord.substring(1)); break;
						case 'e': root.add("3"+currentWord.substring(1)); break;
						case 'i': root.add("1"+currentWord.substring(1)); break;
						case 'l': root.add("1"+currentWord.substring(1)); break;
						case 'o': root.add("0"+currentWord.substring(1)); break;
						case 's': root.add("$"+currentWord.substring(1)); break;
						case 't': root.add("7"+currentWord.substring(1)); break;
					}
				}
			}
			
			//More file io exception handling
			catch(IOException e){
				System.out.println("Error reading dictionary file.");
				e.printStackTrace();
			}
			
			//Create my_dictionary.txt
			try{
				StringBuilder currentWord = new StringBuilder("");
				root.writeTrie(currentWord); //simply calls writing function attached to the root node
				my_dictionary.close(); //closes the dictionary so it will actually write
			}
			
			//Woo, even more io exception handling
			catch(IOException e){
				System.out.println("\nError writing my_dictionary.");
				e.printStackTrace();
			}
			
			//Generates valid passwords from pw_check function
			generate(root);
			System.out.println("Valid passwords successfully generated.");
		}
		
		//And you thought we were done with io exceptions, hah.
		catch(IOException e){
				e.printStackTrace();
		}
	}
	
	/**
	*Uses functions attached to the DlbNode class to generate the list of valid passwords
	*@param root root node of the trie containing the invalid word list
	**/
	private static void generate(DlbNode root){
		StringBuilder pass = new StringBuilder("");
		try{
			//opens the file to write to and calls the generation function attached to the root node
			PrintWriter goodPass = new PrintWriter(new BufferedWriter(new FileWriter("good_passwords.txt")));
			root.startGeneration(pass,0,0,0,goodPass);
			goodPass.close();
		}
		
		//More io exception handling, so exciting
		catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	/**
	*Reads valid passwords into the DLB trie and checks if user inputted passwords are valid
	**/
	private static void validatePasswords(){
		DlbNode root = null; //root node of valid password trie
		StringBuilder filename = new StringBuilder("0passwords.txt");

		//Stops user from running if good passwords not exist
		File runPrev = new File("good_passwords.txt");
		if(!runPrev.exists()){
			System.out.println("The program must first be run with the command flag -g.");
			System.exit(0);
		}
			
		String userInput = " "; //holds user password attempt
		
		//lets user test passwords until they enter 'q'
		while(!userInput.equals("q")){
			StringBuilder similarPrinter; //String builder for similar password generation
			int prefixLength; //Stores longest prefix of userinput that is valid
			
			//Gets user password
			System.out.print("\nEnter a 5 character password or a single 'q' to quit: ");
			userInput = input.nextLine();
			userInput = userInput.toLowerCase();
			
			//Exits if the user enters q
			if(userInput.equals("q")){
				break;
			}
			//Loops if the user enters nothing
			if(userInput==null||userInput.equals("")|userInput.equals(" ")){
				continue;
			}
			if(userInput.substring(0,1).equals(" ")){
				System.out.println("The leading space on your password was removed.");
				userInput = userInput.substring(1);
			}
			
			//Loads the passwords matching the users input
			root = null;
			char firstChar = userInput.charAt(0);
			if(firstChar=='a'||firstChar=='1'||firstChar=='4'||firstChar=='i'){
				filename.setCharAt(0,'0');
				root = loadPasswords(filename,root);
			}
			else if(firstChar=='*'){
				filename.setCharAt(0,'+');
				root = loadPasswords(filename,root);
			}
			else{
				filename.setCharAt(0,firstChar);
				root = loadPasswords(filename,root);
			}
			
			//if the user password is the right length, checks it
			if(userInput.length()==5){
				//finds longest prefix
				prefixLength = root.findPrefix(userInput,5);
				
				//if the prefix is a valid password, tells the user they are awesome
				if(prefixLength==5){
					System.out.println("Congratulations, "+userInput+ " is a valid password!");
				}
				
				//if the user's password is wrong, it tells them and gives alt passwords
				else{
					System.out.println("Sorry, "+userInput+" is not a valid password.\nSome valid password options are: ");
				
					//Sets the output to the longest valid prefix
					if(prefixLength>1){
						similarPrinter = new StringBuilder(userInput.substring(0,prefixLength-1));
					}
					
					//longest prefix of 1 and 0 corner case handling
					else if(prefixLength==1){						
						similarPrinter = new StringBuilder(userInput.charAt(0));
						System.out.println(similarPrinter);
					}
					else{
						similarPrinter = new StringBuilder(0);
					}
					
					int similar=root.findSimilar(userInput,similarPrinter,prefixLength,0); //counts the similar passwords for checking
					
					//Tells user if other passwords cannot be generated
					if(similar==0){
						System.out.println("Error Generating Similar Passwords.");
					}
					//Manually catches some strange error if the first 5 characters are a valid password
					int i=0;
					while(similar+i<10){
						similarPrinter.setCharAt(3,(char)(i+48));
						similarPrinter.setCharAt(4,root.getNextChar(i+37));
						i++;
						System.out.println(similarPrinter.substring(0,5));
					}
				}
			}
			
			//If user password is too long, truncate and generate alternates
			else if(userInput.length()>5){
				System.out.println("Passwords must only be a maximum of 5 characters. \nHowever, your password will be truncated to 5 characters and checked.");
				userInput=userInput.substring(0,5);
				prefixLength = root.findPrefix(userInput,5);
				int similar =0;

				//Sets the output to the longest valid prefix
				if(prefixLength==5){
					similarPrinter = new StringBuilder(userInput.substring(0,3));
					userInput = userInput.substring(0,4);
				}
				else if(prefixLength>1){
					similarPrinter = new StringBuilder(userInput.substring(0,prefixLength-1));
				}
				
				//longest prefix of 1 and 0 corner case handling
				else if(prefixLength==1){
					similarPrinter = new StringBuilder(userInput.charAt(0));
				}
				else{
					similarPrinter = new StringBuilder(0);
				}
				
				similar=root.findSimilar(userInput,similarPrinter,prefixLength,similar); //counts the similar passwords for checking
				//Tells user if other passwords cannot be generated
				if(similar==0){
					System.out.println("Error Generating Similar Passwords.");
				}
				
				//Manually catches some strange error if the first 5 characters are a valid password
				int i=0;
				while(similar+i<10){
					similarPrinter.setCharAt(3,(char)(i+48));
					similarPrinter.setCharAt(4,root.getNextChar(i+37));
					i++;
					System.out.println(similarPrinter.substring(0,5));
				}
			}
			
			else if(userInput.length()<5){
				System.out.println("Passwords must be 5 characters.\nSome valid similar passwords are: ");
				prefixLength = root.findPrefix(userInput,userInput.length());
				
				//Sets the output to the longest valid prefix
				if(prefixLength>1){
					similarPrinter = new StringBuilder(userInput.substring(0,prefixLength-1));
				}
				
				//longest prefix of 1 and 0 corner case handling
				else if(prefixLength==1){
					similarPrinter = new StringBuilder(userInput.charAt(0));
				}
				else{
					similarPrinter = new StringBuilder(0);
				}
				
				int similar=root.findSimilar(userInput,similarPrinter,prefixLength,0); //counts the similar passwords for checking
					
				//Tells user if other passwords cannot be generated
				if(similar==0){
					System.out.println("Error Generating Similar Passwords.");
				}
				//Manually catches some strange error if the first 5 characters are a valid password
				int i=0;
				while(similar+i<10){
					similarPrinter.setCharAt(3,(char)(i+48));
					similarPrinter.setCharAt(4,root.getNextChar(i+37));
					i++;
					System.out.println(similarPrinter.substring(0,5));
				}
			}
		}
	}
	
	/**
	* Loads the proper passwords into the DLB trie for analysis
	* @param filename the name of the file that will be loaded into the DLB
	* @param root The root node of the DLB trie
	* @return reference to the root node of the trie
	**/
	private static DlbNode loadPasswords(StringBuilder filename, DlbNode root){
		try{
			BufferedReader goodPasswords = new BufferedReader(new FileReader(filename.toString())); //password file reader
			int numWords =0; //Variable to stop tree initialization in while loop
			String currentWord; //current trie word to be added
			while ((currentWord = goodPasswords.readLine())!=null){
				//append end of word flag
					currentWord = currentWord.toLowerCase() + "+";
					//create root node if necessary
					if (numWords==0){
						//creates a new root with valid flags
						root = new DlbNode(null,false);
						root.add(currentWord);
						numWords++;
					}
				
					//otherwise add word to trie
					else{
						root.add(currentWord);
					}
			}
			
		}
		
		//If the user's first symbol is not valid, loads default passwords to print similar passwords
		catch (IOException e){
			filename.setCharAt(0,'0');
			root=null;
			root = loadPasswords(filename,root);
		}
		
		//Returns the root node reference
		return root;
	}
}