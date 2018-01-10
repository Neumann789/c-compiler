package frontend;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;


public class GrammarState {
	public static int stateNumCount = 0;
	private boolean printInfo = false;
	private boolean transitionDone = false;
    public  int stateNum = -1;
    private GrammarStateManager stateManager = GrammarStateManager.getGrammarManager();
    private ArrayList<Production> productions = new ArrayList<Production>();
    private HashMap<Integer, GrammarState> transition = new HashMap<Integer, GrammarState>();
    private ArrayList<Production> closureSet = new ArrayList<Production>();
    private ProductionManager productionManager = ProductionManager.getProductionManager();
    private HashMap<Integer, ArrayList<Production>> partition = new HashMap<Integer, ArrayList<Production>>();
    private ArrayList<Production> mergedProduction = new ArrayList<Production>();
    
    public static void  increateStateNum() {
    	stateNumCount++;
    }
    
    public boolean isTransitionMade() {
    	return transitionDone;
    }
    
    public GrammarState(ArrayList<Production> productions) {
    	this.stateNum = stateNumCount;
    	
    	this.productions = productions;
    	
    	this.closureSet.addAll(this.productions);
    }
    
    public void stateMerge(GrammarState state) {
    	if (this.productions.contains(state.productions) == false) {
    		for (int i = 0; i < state.productions.size(); i++) {
    			if (this.productions.contains(state.productions.get(i)) == false
    					&& mergedProduction.contains(state.productions.get(i)) == false
    					) {
    				mergedProduction.add(state.productions.get(i));
    			}
    		}
    	}
    	
    }
    
    public void print() {
    	System.out.println("State Number: " + stateNum);
    	for (int i = 0; i < productions.size(); i++) {
    		productions.get(i).print();
    	}
    	
    	for (int i = 0; i < mergedProduction.size(); i++) {
    		mergedProduction.get(i).print();	
    	}
    }
    
    public void printTransition() {
    	for (Map.Entry<Integer, GrammarState> entry: transition.entrySet()) {
    		System.out.println("transfter on " + CTokenType.getSymbolStr(entry.getKey()) + " to state ");
    		entry.getValue().print();
    		System.out.print("\n");
    	}
    }
    
    public void createTransition() {
    	if (transitionDone == true) {
    		return;
    	}
    	
    	transitionDone = true;
  
    	
    	makeClosure();
    	
    	partition();
  
    	makeTransition();
   
    	printInfo = true;
    	
    }
    
    private void makeClosure() {
    	
    	Stack<Production> productionStack = new Stack<Production>();
    	for (int i = 0; i < productions.size(); i++) {
    		productionStack.push(productions.get(i));
    	}
    	
    //	System.out.println("---begin make closure----");
    	
    	while (productionStack.empty() == false) {
    		Production production = productionStack.pop();
    		
    		
    		System.out.println("\nproduction on top of stack is : ");
    		production.print();
    		production.printBeta();
    		
    		
    		if (CTokenType.isTerminal(production.getDotSymbol()) == true) {
    			    System.out.println("symbol after dot is not non-terminal, ignore and prcess next item");
    			    continue;	
    			}
    		
    		int symbol = production.getDotSymbol();
    		ArrayList<Production> closures = productionManager.getProduction(symbol);
    		ArrayList<Integer> lookAhead = production.computeFirstSetOfBetaAndC();
    		
    		Iterator it = closures.iterator();
    		while (it.hasNext()) {
    			Production oldProduct = (Production) it.next();
    			Production newProduct = (oldProduct).cloneSelf();
    			
    			
    			newProduct.addLookAheadSet(lookAhead);
    			
    			
    			if (closureSet.contains(newProduct) == false) {  
    				System.out.println("push and add new production to stack and closureSet");
    				
    				closureSet.add(newProduct);
    				productionStack.push(newProduct);
    				System.out.println("Add new production:");
    				newProduct.print();
    				removeRedundantProduction(newProduct);	
    			}
    			else {
    				System.out.println("the production is already exist!");
    			}
    			
    		}
    	
    		
    	}
    	
    	//printClosure();
    	//System.out.println("----end make closure----");
    	
    }
    
    private void removeRedundantProduction(Production product) {
    	boolean removeHappended = true;
    	
    	while (removeHappended) {
    		removeHappended = false;
    		
    		Iterator it = closureSet.iterator();
    		while (it.hasNext()) {
    			Production item = (Production) it.next();
    			if (product.coverUp(item)) {
    				removeHappended = true;
    				closureSet.remove(item);
    				if (stateNum == 1) {
    				 //   System.out.print("remove redundant production: ");
        				//item.print();
    				}
    		
    				break;
    			}
    		}
    	}
    }
    
  
    private void printClosure() {
    	if (printInfo) {
    		return;
    	}
    	
    	System.out.println("ClosueSet is: ");
    	for (int i = 0; i < closureSet.size(); i++) {
    		closureSet.get(i).print();
    	}
    }
    
    private void partition() {
    	for (int i = 0; i < closureSet.size(); i++) {
    		int symbol = closureSet.get(i).getDotSymbol();
    		if (symbol == CTokenType.UNKNOWN_TOKEN.ordinal()) {
    			continue;
    		}
    		
    		ArrayList<Production> productionList = partition.get(symbol);
    		if (productionList == null) {
    			productionList = new ArrayList<Production>();
    			partition.put(closureSet.get(i).getDotSymbol(), productionList);
    		}
    		
    		if (productionList.contains(closureSet.get(i)) == false) {
    	        productionList.add(closureSet.get(i));	
    		}
    	}
    	
    	
    	
    //	printPartition();
    }
    
    private void printPartition() {
    	if (printInfo) {
    		return;
    	}
    	
    	for(Map.Entry<Integer, ArrayList<Production>> entry : partition.entrySet()) {
    		
    		System.out.println("partition for symbol: " + CTokenType.getSymbolStr(entry.getKey()));
    		
    		ArrayList<Production> productionList = entry.getValue();
    		for (int i = 0; i < productionList.size(); i++) {
    			productionList.get(i).print();
    		}
    	}
    }
    
    private GrammarState makeNextGrammarState(int left) {
    	ArrayList<Production> productionList = partition.get(left);
    	ArrayList<Production> newStateProductionList = new ArrayList<Production>();
    	
    	for (int i = 0; i < productionList.size(); i++) {
    		Production production = productionList.get(i);
    		newStateProductionList.add(production.dotForward());
    	}
    	
    	return  stateManager.getGrammarState(newStateProductionList);
    }
    
    private void makeTransition() {
    	
    	for (Map.Entry<Integer, ArrayList<Production>> entry : partition.entrySet()) {
    //		System.out.println("\n====begin print transition info ===");
    		GrammarState nextState = makeNextGrammarState(entry.getKey());
    		transition.put(entry.getKey(), nextState);
    	//	System.out.println("from state " + stateNum + " to State " + nextState.stateNum + " on " + 
    	//	CTokenType.getSymbolStr(entry.getKey()));
    		//System.out.println("----State " + nextState.stateNum + "------");
    	//	nextState.print();
    		
    		stateManager.addTransition(this, nextState, entry.getKey());
    	}
    	
    	extendFollowingTransition();
    }
    
    private void extendFollowingTransition() {
    	for (Map.Entry<Integer, GrammarState> entry : transition.entrySet()) {
    		GrammarState state = entry.getValue();
    		if (state.isTransitionMade() == false) {
    			state.createTransition();
    		}
    	}
    }
    
    @Override
    public boolean equals(Object obj) {
    	return checkProductionEqual(obj, false);
    }
    
    public boolean checkProductionEqual(Object obj, boolean isPartial) {
    	GrammarState state = (GrammarState)obj;
    	 
    	if (state.productions.size() != this.productions.size()) {
    		return false;
    	}
    	
    	int equalCount = 0;
    	
    	for (int i = 0; i < state.productions.size(); i++) {
             for (int j = 0; j < this.productions.size(); j++) {
            	 if (isPartial == false) {
               	  if (state.productions.get(i).equals(this.productions.get(j)) == true) {
         				equalCount++;
         				break;
         			 }
                 }
                 else {
               	    if (state.productions.get(i).productionEequals(this.productions.get(j)) == true) {
               	    	equalCount++;
               	    	break;
               	    }
                 }
             }
    			
    		}
    	
    		
    	return equalCount == state.productions.size();
    }
    
    public HashMap<Integer, Integer> makeReduce() {
    	HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
    	reduce(map, this.productions);
    	reduce(map, this.mergedProduction);
    	
    	return map;
    }
    
    private void  reduce(HashMap<Integer, Integer> map, ArrayList<Production> productions) {
    	for (int i = 0; i < productions.size(); i++) {
    		if (productions.get(i).canBeReduce()) {
    			ArrayList<Integer> lookAhead = productions.get(i).getLookAheadSet();
    			for (int j = 0; j < lookAhead.size(); j++) {
    				map.put(lookAhead.get(j), (productions.get(i).getProductionNum()));
    			}
    		}
    	}
    }
    
    
}
