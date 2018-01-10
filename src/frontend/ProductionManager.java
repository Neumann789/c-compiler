package frontend;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class ProductionManager {
	
	private static ProductionManager self = null;
	
	FirstSetBuilder firstSetBuilder = new FirstSetBuilder();
	
    private HashMap<Integer, ArrayList<Production>> productionMap = new HashMap<Integer, ArrayList<Production>>();
    
    public static ProductionManager getProductionManager() {
    	if (self == null) {
    		self = new ProductionManager();
    	}
    	
    	return self;
    }
    
    public void initProductions() {
    	CGrammarInitializer cGrammarInstance =  CGrammarInitializer.getInstance();
    	
    	productionMap = cGrammarInstance.getProductionMap();
    		
    }
    
    public void runFirstSetAlgorithm() {
    	firstSetBuilder.runFirstSets();
    }
    
    public FirstSetBuilder getFirstSetBuilder() {
    	return firstSetBuilder;
    }
    
    public void printAllProductions() {
    	for (Map.Entry<Integer, ArrayList<Production>> entry : productionMap.entrySet()) {
    		ArrayList<Production> list = entry.getValue();
    		for (int i = 0; i < list.size(); i++) {
    			list.get(i).print();
    			System.out.print("\n");
    		}
    	}
    }
    
    
    public ArrayList<Production> getProduction(int left) {
    	return productionMap.get(left);
    }
    
    public Production getProductionByIndex(int index) {
    	
    	for (Entry<Integer, ArrayList<Production>> item : productionMap.entrySet()) {
    		ArrayList<Production> productionList = item.getValue();
    		for (int i = 0; i < productionList.size(); i++) {
    			if (productionList.get(i).getProductionNum() == index) {
    				return productionList.get(i);
    			}
    		}
    	}
    	
    	return null;
    }
    
    private ProductionManager() {
    	
    }
}
