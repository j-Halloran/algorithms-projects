import java.io.*;
import java.util.Objects;

/**
* This class represents a node of a DLB trie
* contains an array of the entire child level rather than have multiple nodes for each level
* @author Jake Halloran
* @version 1.0
**/
public class DlbNode{
	public String data; //stores current node character
	private String nextData; //stores data to pass further down
	private DlbNode nextNode[]; //array of child nodes to save space over having sideways peer nodes
	private DlbNode parent; //reference to parent node
	private boolean subFlag; //boolean flag indicating whether or not to sub symbols for chars
    public int count; //count of child nodes
	static	PrintWriter my_dictionary; //object to handle writing the try to disk
	
	
	/**
	* Root level constructor, contains output writer and boolean flag saying whether or not to substitute data
	* @param my_dictionary the output file writer for printing the trie
	* @param subFlag boolean argument that determines whether or not to substitue symbols for letters
	* @throws IOException if the printwriter passed as a parameter fails
	**/
	public DlbNode(PrintWriter my_dictionary,boolean subFlag)  throws IOException{
		data = null;
		this.subFlag=subFlag;
		nextNode = new DlbNode[5];
		count =0;
		parent=null;
		this.my_dictionary=my_dictionary;
	}
	
	/**
	* Non-root node constructor containing data to be stored and a parent reference
	* Contains entire level of the trie in one node rather than as a peer linked list
	* @param word the data to be stored or passed down the trie
	* @param parent reference to parent node in the linked list
	* @param subFlag boolean argument that determines whether or not to substitue symbols for letters
	**/
	public DlbNode(String word,DlbNode parent,boolean subFlag){
		this.parent=parent;
		this.subFlag = subFlag;
		nextNode = new DlbNode[5];
		count =0;
		if(word.length()>1){
			data = word.substring(0,1);
			this.add(word);
		}
		else{
			data = word;
		}
	}

	/**
	* Testing method for manual data setting, not used in final version
	* @param data the data to set the current node as containing
	**/
	public void setData (String data){
		this.data=(data);
	}
	
	/**
	* Prints the trie data to a file
	* @param currentWord the current word that is being built and will be written if eligible
	* @throws IOException if the my_dictionary file cannot be written to
	**/
	public void writeTrie (StringBuilder currentWord) throws IOException{
		for(int i=0;i<count;i++){
			if(nextNode[i]==null){
				continue;
			}
			if (nextNode[i].data.equals("^")){
			//	System.out.println("Output reached");
				my_dictionary.println(currentWord+data);
			//	System.out.println(currentWord+data);
			}
			else{
				if(data!=null){
					currentWord.append(data);
				}
				nextNode[i].writeTrie(currentWord);
				if(currentWord.length()>0){
					currentWord.deleteCharAt(currentWord.length()-1);
				}
			}
		}
	}
	
	/**
	* Adds data to the trie, and pushes additional data further down the trie into new child nodes
	* Additionally, calls the function that replaces eligible characters with symbols if necessary
	* @param word the word whose data will be added to the tree recursively
	**/
	public void add (String word){
		boolean flag = false;
		if(word.length()==0){
			return;
		}
		if(parent==null){
			nextData = word;
		}
		else{
			nextData = word.substring(1);
		}
		
		//System.out.println(word);
		if(nextData.isEmpty()){
			return;
		}
		if(count>0){
			for(int i=0;i<count;i++){
				if(nextNode[i]!=null){
					if(nextNode[i].data.equals(nextData.substring(0,1))){
						nextNode[i].add(nextData);
						flag = true;
			//			System.out.println("Found and added.");
						break;
					}
				}
			}
		}
		if(!flag && count<nextNode.length){
			nextNode[count++]= new DlbNode(nextData,this,subFlag);
		//	System.out.println("added w/o resizing");
		}
		else if(!flag){
			DlbNode temp[] = new DlbNode[nextNode.length];
			for(int i =0;i<nextNode.length;i++){
				temp[i]=nextNode[i];
			}	
		
			nextNode = new DlbNode[nextNode.length*2];
			for(int j=0;j<nextNode.length/2;j++){
				nextNode[j]=temp[j];
			}
		
			nextNode[count++]=new DlbNode(nextData, this,subFlag);
		//	System.out.println("added with resizing");
		}
	
		if(subFlag){
			checkSubstitutes();		
		}
	}
	
	/**
	* Adds a new node with the first character substituted with a symbol if necessary
	*/
	private void checkSubstitutes(){
		if (parent==null){
			return;
		}
		if(nextData.length()>0){
			char nextChar = nextData.charAt(0);
			String newWord = nextData.substring(1);
			switch (nextChar){
				case 't':
					this.add(data+"7"+newWord);
					return;
				case 'a':
					this.add(data+"4"+newWord);
					return;
				case 'o':
					this.add(data+"0"+newWord);
					return;
				case 'e':
					this.add(data+"3"+newWord);
				//	System.out.println(newWord);
					return;
				case 'i':
					this.add(data+"1"+newWord);
					return;
				case 'l':
					this.add(data+"1"+newWord);
					return;
				case 's':
					this.add(data+"$"+newWord);
					return;
			}
		}
	}
	
	/**
	*Returns the data contained in a node of the dlb trie in String form
	*Used for testing but not intrinsic to program functionality
	**/
	public String toString(){
		return data;
	}
	
	/**
	*Generates the first character in what will become a five character password
	*Additionally, manages file i/o for each smaller sub file
	* @param pass password or papssword prefix being currently validated
	* @param numChars number of alphabetic /characters in current password
	* @param numNums number of arabic numerals in current password
	* @param numSymbols number of symbols in current password
	* @param goodPass print writer used to print validated passwords to file
	* @throws IOException if any of the subfiles are unable to be written to
	**/
	public void startGeneration(StringBuilder pass, int numChars, int numNums, int numSymbols, PrintWriter goodPass) throws IOException{
		char nextChar;
		StringBuilder fileName = new StringBuilder("0passwords.txt");
		
		for(int i=0;i<42;i++){
			
			boolean charFlag =false,numFlag=false,symbolFlag=false;
			
			//Gets the next character to append to the current state
			nextChar = getNextChar(i);
			
			//Throws out illegal characters
			if(nextChar=='a'||nextChar=='i'||nextChar=='1'||nextChar=='4'){
				continue;
			}
			
			//Opens individual printing file
			if(i!=41){
				fileName.setCharAt(0,nextChar);
			}
			else{
				fileName.setCharAt(0,'+');
			}
			PrintWriter charWords = new PrintWriter(new BufferedWriter (new FileWriter(fileName.toString())));
			
			//add the first character to the password
			pass.append(nextChar);
			
			//Add to counters
			if(i<10){
				numNums=1;
			}
			else if(i<36){
				numChars = 1;
			}
			else{
				numSymbols = 1;
			}
			//Recursively generate password possibilities
			recursiveGenerate(pass,numChars,numNums,numSymbols,goodPass,charWords);
			
			//delete the first character so it can be replaced
			pass.deleteCharAt(0);
			
			//reset counters
			numChars = numSymbols = numNums = 0;
			
			//close this characters output file
			charWords.close();
		}
	}
	
	/**
	* Generates valid passwords one character at a time, pruning as it goes
	* @param pass password or papssword prefix being currently validated
	* @param numChars number of alphabetic /characters in current password
	* @param numNums number of arabic numerals in current password
	* @param numSymbols number of symbols in current password
	* @param goodPass print writer used to print validated passwords to file
	* @param charWords print writer used to print all passwords starting with same character to a single file
	* @throws IOException if the file to write good passwords to cannot be written to
	**/
	public void recursiveGenerate(StringBuilder pass, int numChars, int numNums, int numSymbols,PrintWriter goodPass, PrintWriter charWords) throws IOException{
		char nextChar;
		
		for(int i=0;i<42;i++){
			boolean charFlag =false,numFlag=false,symbolFlag=false;
			
			//Gets the next character to append to the current state
			nextChar = getNextChar(i);
			
			//Throws out illegal characters
			if(nextChar=='a'||nextChar=='i'||nextChar=='1'||nextChar=='4'){
				continue;
			}
			
			//Protection against stuff that should never occur
			if(numChars>3||numNums>2||numSymbols>2){
				return;
			}
			
			//Adds to the char num or symbol variable as needed by the current letter
			else if(i>=10&&i<36){
				numChars++;
				charFlag=true;
				if(numChars>3){
					numChars--;
					continue;
				}
			}
			else if(i<10){
				numNums++;
				numFlag=true;
				if(numNums>2){
					numNums--;
					continue;
				}
			}
			else{
				numSymbols++;
				symbolFlag=true;
				if(numSymbols>2){
					numSymbols--;
					return;
				}
			}
			
			//Appends the new character to the password
			pass.append(nextChar);
			
			//If the password is full, does final checks and then prints it if possible
			if(numChars+numSymbols+numNums==5&&pass.length()==5){
				if(numChars>0&&numSymbols>0&&numNums>0)	{
					boolean flag= true;
					for(int j=0; j<pass.length();j++){
							if(!validate(new StringBuilder(pass.substring(j)))){
								flag=false;
							}
					}
					if(flag){
						goodPass.println(pass);
						charWords.println(pass);
					}
				}
			}
			else{
				//Recurse automatically after first stumble so there is not an error with only one character
				if(pass.length()<2){
					recursiveGenerate(pass,numChars,numNums,numSymbols,goodPass,charWords);
				}
				else{
					//Validates the current word up to the current point
					boolean flag=true;
					for(int j=0; j<pass.length();j++){
							if(!validate(new StringBuilder(pass.substring(j)))){
								flag=false;
							}
					}
					if(flag){
						recursiveGenerate(pass,numChars,numNums,numSymbols,goodPass,charWords);
					}
				}
			}
			
			//Deincrements variables, for the next recursive call
			pass.deleteCharAt(pass.length()-1);
			if(charFlag){numChars--;}
			if(numFlag){numNums--;}
			if(symbolFlag){numSymbols--;}
		}
	}
	
	/**
	*Validates portions of a password and decides wether it is valid or can become valid
	*@param pass the password or portion of a password being validated
	*@return whether the password is a valid password/password prefix or is invalid and will not be valid
	**/
	private boolean validate(StringBuilder pass){
		for(int i=0;i<count;i++){
			if(nextNode[i].data.equals("^")){
				return false;
			}
		}
		if(pass.length()==0){
			return true;
		}
		for(int j=0;j<count;j++){
			if(nextNode[j].data.equals(pass.substring(0,1))){
				return (nextNode[j].validate(new StringBuilder(pass.substring(1))));
			}
			else if((j+1)==count){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	*Uses ASCII character numbering to shortcut getting a new character
	*@param i integer generated by recursive generate matching the next character to return
	*@return the ASCII representation of the parameter integer
	**/
	public char getNextChar(int i){
		if(i<10){
			return (char)(i+48);
		}
		else if(i>=10&&i<36){
			return (char)(i+87);
		}
		else{
			switch (i){
				case 36: return '!';
				case 37: return '@';
				case 38: return '$';
				case 39: return '^';
				case 40: return '_';
				case 41: return '*';
			}
		}
		System.out.println(i);
		return 'Z';
	}
	
	/**
	*Returns the longest prefix of data possible
	* @param password the password attempt a prefix is being found for
	* @param originalLength the original length of the attempted password
	* @return the length of the longest prefix
	**/
	public int findPrefix(String password,int originalLength){
		//returns the original length if the word is a valid 5 char password
		for(int i=0;i<count;i++){
			if(nextNode[i].data.equals("+")){
				return originalLength;
			}
		}
		
		//return the original length if there are no more characters to parse
		if(password.length()==0){
			return originalLength;
		}
		
		//Delve deeper into the prefix if possible
		for(int j=0;j<count;j++){
			if(nextNode[j].data.equals(password.substring(0,1))){
				return nextNode[j].findPrefix(password.substring(1),originalLength);
			}
		}
		
		//default return if the next character is not found in the trie
		return (originalLength-password.length());
	}
	
	/**
	* Finds passwords with longest possible prefix in common with an invalid user password attempt
	* @param password all or a portion of the users password attempt
	* @param toWrite the similar password to be written to screen if qualification checks are passed
	* @param prefixLength the length of the longest possible prefix of the user password attempt
	* @param similarCount the number of similar passwords already written to stdout
	* @return the number of similar passwords written to the console
	**/
	public int findSimilar(String password, StringBuilder toWrite, int prefixLength,int similarCount){
		
		//Auto returns if too many passwords have already been generated
		if(similarCount>=10){
			return 10;
		}
		
		//(unecessarily) throws out user passwords of too long an attempt
		if(toWrite.length()>5){
			return similarCount;
		}
		
		//Delves as deep into the password trie as the common prefix allows
		if(prefixLength>0&&password.length()!=0){
			for(int i=0;i<count;i++){
				if(nextNode[i].data.equals(password.substring(0,1))){
					similarCount=nextNode[i].findSimilar(password.substring(1),toWrite,prefixLength-1,similarCount);
				}
			}
		}
		
		//Only appends non-null data to data soon to be printed
		if(data!=null){
			toWrite.append(data);
		}
		
		//If the new password is too large, throw it out and return
		if(toWrite.length()>5){
			return similarCount;
		}
		
		//If the current toWrite statement is a valid password, prints the toWrite string builder to console
		for(int i=0;i<count;i++){
			if(nextNode[i].data.equals("+")&&checkSimilar(toWrite)){
				System.out.println(toWrite);
				toWrite.deleteCharAt(toWrite.length()-1);
				return ++similarCount;
			}
		}
		
		//Returns if enough passwords have been generated
		if(similarCount>=10){
			return 10;
		}
		
		//Adds more similar passwords to screen if necessary through use of recursion
		for(int j=0;j<count&&similarCount<11;j++){
			similarCount=nextNode[j].findSimilar(password,toWrite,prefixLength,similarCount);
		}
		
		//Resets the toWrite event
		if(toWrite.length()>0){
			toWrite.deleteCharAt(toWrite.length()-1);
		}
		
		//Default return that passes the current number of passwords printed to the console
		return similarCount;
	}
	
	/**
	* Checks to make sure similar passwords are valid
	* @param pass the similar password being tested
	* @return if the password is valid or not
	**/
	private boolean checkSimilar(StringBuilder pass){
		int numChars=0,numNums=0,numSymbols=0; //variables to count characters
		for(int i=0;i<pass.length();i++){
			switch (pass.charAt(i)){
				case '0':
				case '2':
				case '3':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9': numNums++; continue; //if numeral, count it
				case '*':
				case '^':
				case '$':
				case '!':
				case '@':
				case '_': numSymbols++; continue; //if symbol, count it
				default: numChars++; //if character count it
				
			}
		}
		
		//return true iff there are valid numbers of characters symbols and numerals
		if(numChars==0||numNums==0||numSymbols==0){
			return false; 
		}
		if(numChars>3||numNums>2||numSymbols>2){
			return false;
		}
		return true;
	}
}
