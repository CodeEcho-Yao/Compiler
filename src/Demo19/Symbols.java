package Demo19;

import java.util.ArrayList;


public class Symbols {
    public  int value;
    public  ArrayList<int[]> productions;
    public  ArrayList<Integer> firstSet = new ArrayList<Integer>();
    public  ArrayList<Integer> followSet = new ArrayList<Integer>();
    
    public  boolean isNullable;
    
    public Symbols(int symVal, boolean nullable, ArrayList productions) {
    	value = symVal;
    	this.productions =  productions; 
    	isNullable = nullable;
    	
    	if (symVal < 256) {
    		//terminal's first set is itself
    		firstSet.add(symVal);
    	}
    }
    
    
}
