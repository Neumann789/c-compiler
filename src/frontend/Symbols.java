package frontend;
import java.util.ArrayList;


public class Symbols {
    public  int value;
    public  ArrayList<int[]> productions;
    public  ArrayList<Integer> firstSet = new ArrayList<Integer>();
    public  ArrayList<Integer> followSet = new ArrayList<Integer>();
    public  ArrayList<ArrayList<Integer>> selectionSet = new ArrayList<ArrayList<Integer>>();
    
    public  boolean isNullable;
    
    public Symbols(int symVal, boolean nullable, ArrayList<int[]> productions) {
    	value = symVal;
    	this.productions =  productions; 
    	isNullable = nullable;
    	
    	if (CTokenType.isTerminal(symVal)) {
    		//terminal's first set is itself
    		firstSet.add(symVal);
    	}
    }
    
    public void addProduction(int[] production) {
    	if (productions.contains(production) == false) {
    		productions.add(production);
    	}
    }
    
    
}
