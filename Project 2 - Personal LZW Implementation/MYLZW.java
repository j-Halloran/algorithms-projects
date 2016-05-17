/**
* @author Jake Halloran
* last edited: 2/26/16
* LZW compression with codebook replacement, no codebook replacement and dynamic codebook replacement modes
**/

import java.io.*;
public class MYLZW {
    private static final int R = 256;        // number of input chars
    private static int L;      			     // number of codewords = 2^W
    private static int W;      			     // codeword width
	
    public static void compress(){ //do nothing mode compress
        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF

        while (input.length() > 0) {
			//Expand codebook when necessary and eligible
			if(W<16 && code == L){
				W++;
				L *=2;
			}
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            BinaryStdOut.write(st.get(s), W);  // Print s's encoding.
            int t = s.length();
            if (t < input.length() && code < L)    // Add s to symbol table.
                st.put(input.substring(0, t + 1), code++);
            input = input.substring(t);            // Scan past s in input.
        }
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    } 

    public static void expand() { //do nothing mode expand
        String[] st = new String[65536];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];

        while (true) {
            BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L) st[i++] = val + s.charAt(0);
            val = s;
			
			//Expand codebook one early when elligible
			if(W<16 && i==(Math.pow(2,W))-1){
				W++;
				L *= 2;
			}
        }
        BinaryStdOut.close();
    }

    public static void main(String[] args){
		L = 512; //set default L and W
		W = 9;
		
		//Properly follow command line arguments as required
        if      (args[0].equals("-")) selectCompress(args);
        else if (args[0].equals("+")) selectExpand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
	
	//Selects proper expansion based on first character contained in read file
	private static void selectExpand(){
		char compressType = BinaryStdIn.readChar();
		switch (compressType){
			case 'd': expand(); break;
			case 'r': resetExpand(); break;
			case 'm': monitorExpand(); break;
			default: throw new IllegalArgumentException("Invalid decompression type.");
		}
	}
	
	//Selects proper compression type based on first character passed after the minus
	private static void selectCompress(String[] args){
		char compressType = args[1].charAt(0);
		switch (compressType){
			case 'd': BinaryStdOut.write('d');compress(); break;
			case 'r': BinaryStdOut.write('r');resetCompress(); break;
			case 'm': BinaryStdOut.write('m');monitorCompress(); break;
			default: throw new IllegalArgumentException("Invalid compression type.");
		}
	}
	
	private static void resetCompress(){ //reset mode compress
		String input = BinaryStdIn.readString(); //reads input file to string
		
		//Starts initial codebook
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF

        while (input.length() > 0) {
			//Expands codebook when necessary
			if(W<16 && code == L){
				W++;
				L *=2;
			}
			
			//Restarts codebook if both l and w are maxxed
			else if(W==16 && code == L){
				W = 9;
				L = 512;
				st = new TST<Integer>();
				for (int i = 0; i < R; i++)
					st.put("" + (char) i, i);
				code = R+1;  // R is codeword for EOF
			}
			
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            BinaryStdOut.write(st.get(s), W);  // Print s's encoding.
            int t = s.length();
            if (t < input.length() && code < L)    // Add s to symbol table.
                st.put(input.substring(0, t + 1), code++);
            input = input.substring(t);            // Scan past s in input.
        }
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
	}
	
	private static void monitorCompress() { //monitor mode compress
		boolean monitorFlag = false; //flag to check if ratio of ratios is bad enough to reset
		int readDataCount = 0; 
		int outputDataCount = 0;
		double monitorRatio = 1.0;
		double currentRatio = 1.0;
		int count = 0;
		int waitCount = 0;
		
		String input = BinaryStdIn.readString(); //string containing file data
		
		//Initializes codebook
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF
		
        while (input.length() > 0) {
			//Expand code book
			if(W<16 && code == L){
				W++;
				L *=2;
			}
			//Start monitoring when code book is full
			else if(W == 16 && code == L && !monitorFlag){
				monitorFlag = true;
				monitorRatio = currentRatio;
			}
			
			//If monitoring and ratio of ratios has degraded, reset the book
			else if(W==16 && code == L && monitorFlag && monitorRatio/currentRatio>=1.1){
				W = 9;
				L = 512;
				st = new TST<Integer>();
				for (int i = 0; i < R; i++)
					st.put("" + (char) i, i);
				code = R+1;  // R is codeword for EOF
				
				//Reset monitor mode special flags and data
				monitorRatio = 1.0;
				monitorFlag = false;
			}
			
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
			readDataCount += s.length() * 8; //add 8bits * input length to read data
            BinaryStdOut.write(st.get(s), W);  // Print s's encoding.
			outputDataCount+= W; //add codeword size to written data
			count++; //counting flag for testing purposes
            int t = s.length();
            if (t < input.length() && code < L)    // Add s to symbol table.
                st.put(input.substring(0, t + 1), code++);
            input = input.substring(t);            // Scan past s in input.
			
			currentRatio = (double)readDataCount/outputDataCount; //set current ratio of total length read *8 / total size of all codewords written
        }
		
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
	}
	
	private static void resetExpand() { //reset mode expand
		String[] st = new String[65536];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];

        while (true) {
			//Read and write data
            BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L) st[i++] = val + s.charAt(0);
            val = s;
			
			//Expand codebook one early when needed
			if(W<16 && i==(Math.pow(2,W))-1){
				W++;
				L *= 2;
			}
			
			//Throw away codebook if full
			if(W==16 && i==(Math.pow(2,W))-1){
				W = 9;
				L = 512;
				st = new String[65536];
				for (i = 0; i < R; i++)
					st[i] = "" + (char) i;
				st[i++] = "";  
				BinaryStdOut.write(val);
				codeword = BinaryStdIn.readInt(W);
				if (codeword == R) return;           // expanded message is empty string
				val = st[codeword];
			}
        }
        BinaryStdOut.close();
	}
	
	private static void monitorExpand() { //monitor mode expand
		String[] st = new String[65536];
		boolean monitorFlag = false; //flag to track if monitor mode is engaged
		int readDataCount = 0;
		int outputDataCount = 0;
		double monitorRatio = 0.0; //base ratio
		double currentRatio = 0.0; //current output/input ratio
		int count =0;
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W);
		readDataCount += W;
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];
		

        while (true) {
			//Read and write data as well as track input and output sizes in bits
			try{BinaryStdOut.write(val);}
			catch(NullPointerException e){System.err.println("Error writing to file. Exiting."); break;}
			outputDataCount += val.length() * 8;
			codeword = BinaryStdIn.readInt(W);
			readDataCount += W;		
			count++;
			
			//Code to increment to next word properly
            if (codeword == R) { break;}
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
			if (i < L) {st[i++] = val + s.charAt(0); }
            val = s;
			
			//Expand codebook one early when able
			if(W<16 && i==(Math.pow(2,W))-1){
				W++;
				L *= 2;
			}
			
			//Start monitoring when the codebook is full
			else if(W==16 && i==(Math.pow(2,W)) && !monitorFlag){
				monitorFlag = true;
				monitorRatio = currentRatio;
			}
			
			//if monitoring and ratio has degraded, reset the codebook
			if(monitorFlag && monitorRatio/currentRatio >= 1.1){
				//Reset and refill length variables and initial array
				W = 9;
				L = 512;
				i = 0;
				st = new String[65536];
				for (i = 0; i < R; i++)
					st[i] = "" + (char) i;
				st[i++] = "";  
				
				//write old word
				BinaryStdOut.write(val);
				outputDataCount += val.length()*8;
				
				//Read next word
				codeword = BinaryStdIn.readInt(W);
				readDataCount += W;
				if (codeword == R) return;           // expanded message is empty string
				val = st[codeword];
				count++;
				monitorFlag = false;
			}
	
			currentRatio = (double)outputDataCount/(readDataCount);
			
        }
        BinaryStdOut.close();
	}
}