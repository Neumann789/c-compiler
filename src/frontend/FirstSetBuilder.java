package frontend;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class FirstSetBuilder {
	
    private HashMap<Integer, Symbols> symbolMap = new HashMap<Integer, Symbols>();
    private ArrayList<Symbols> symbolArray = new ArrayList<Symbols>();
    private boolean runFirstSetPass = true;
  
	int productionCount = 0;
	
    public FirstSetBuilder() {
    	initProductions();
    }
    
    public boolean isSymbolNullable(int sym) {
    	Symbols symbol= symbolMap.get(sym);
    	if (symbol == null) {
    		return false;
    	}
    	
    	return symbol.isNullable ? true : false;
    }
    
    private void initProductions() {
    	symbolMap = CGrammarInitializer.getInstance().getSymbolMap();
    	symbolArray = CGrammarInitializer.getInstance().getSymbolArray();
    }
    
    public void runFirstSets() {
    	while (runFirstSetPass) {
    		runFirstSetPass = false;
    		
    		Iterator<Symbols> it = symbolArray.iterator();
    		while (it.hasNext()) {
    			Symbols symbol = it.next();
    			addSymbolFirstSet(symbol);
    		}
    		
    	}
    	
    	printAllFirstSet();
		System.out.println("============");
    }
    
    private void addSymbolFirstSet(Symbols symbol) {
    	if (isSymbolTerminals(symbol.value) == true) {
    		if (symbol.firstSet.contains(symbol.value) == false) {
    			symbol.firstSet.add(symbol.value);
    		}
    		return;
    	}
    	
    	for (int i = 0;  i < symbol.productions.size(); i++) {
    		int[] rightSize = symbol.productions.get(i);
    		if (rightSize.length == 0) {
    			continue;
    		}
    		
    		if (isSymbolTerminals(rightSize[0]) && symbol.firstSet.contains(rightSize[0]) == false) {
    			runFirstSetPass = true;
    			symbol.firstSet.add(rightSize[0]);
    		}
    		else if (isSymbolTerminals(rightSize[0]) == false) {
    			//add first set of nullable
    			int pos = 0;
    			Symbols curSymbol = null;
    			do {
    				curSymbol = symbolMap.get(rightSize[pos]);
    				if (symbol.firstSet.containsAll(curSymbol.firstSet) == false) {
    					runFirstSetPass = true;
    					
    					for (int j = 0; j < curSymbol.firstSet.size(); j++) {
    						if (symbol.firstSet.contains(curSymbol.firstSet.get(j)) == false) {
    							symbol.firstSet.add(curSymbol.firstSet.get(j));
    						}
    					}//for (int j = 0; j < curSymbol.firstSet.size(); j++)
    					
    				}//if (symbol.firstSet.containsAll(curSymbol.firstSet) == false)
    				
    				pos++;
    			}while(pos < rightSize.length && curSymbol.isNullable);
    		} // else
    		
    	}//for (int i = 0; i < symbol.productions.size(); i++)
    }
    
    private boolean isSymbolTerminals(int symbol) {
    	return CTokenType.isTerminal(symbol);
    }
    
    public void printAllFirstSet() {
    	Iterator<Symbols> it = symbolArray.iterator();
		while (it.hasNext()) {
		    Symbols sym = it.next();
		    printFirstSet(sym);
		}
    }
    
    private void printFirstSet(Symbols symbol) {
    	
    	String s = CTokenType.getSymbolStr(symbol.value);
    	s += "{ ";
    	for (int i = 0; i < symbol.firstSet.size(); i++) {
    		s += CTokenType.getSymbolStr(symbol.firstSet.get(i)) + " ";
    	}
    	s += " }";
    	
    	System.out.println(s);
    	System.out.println("============");
    }
    
    public ArrayList<Integer> getFirstSet(int symbol) {
    	Iterator<Symbols> it = symbolArray.iterator();
		while (it.hasNext()) {
		    Symbols sym = it.next();
		    if (sym.value == symbol) {
		    	return sym.firstSet;
		    }
		}
		
		return null;
	
    }
    
 
}
