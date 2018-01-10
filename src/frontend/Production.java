package frontend;
import java.util.ArrayList;


public class Production {
    private int dotPos = 0;
    private boolean printDot = false;
    private int left = 0;
    private ArrayList<Integer> right = null;
    private ArrayList<Integer> lookAhead = new ArrayList<Integer>();
    private int productionNum = -1;
    
    public Production(int productionNum, int left, int dot, ArrayList<Integer> right) {
        this.left = left;
        this.right = right;
        this.productionNum = productionNum;
        
        if (dot >= right.size()) {
        	dot = right.size();
        }
        
        lookAhead.add(CTokenType.SEMI.ordinal());
        
        this.dotPos = dot;
    }
    
  
    public Production dotForward() {
    	Production product = new Production(productionNum, this.left, dotPos+1, this.right); 
    	
    	product.lookAhead = new ArrayList<Integer>();
    	for (int i = 0; i < this.lookAhead.size(); i++) {
    		product.lookAhead.add(this.lookAhead.get(i));
    	}
    	
    	return  product;
    }
    
    public Production cloneSelf() {
    	
        Production product = new Production(productionNum, this.left, dotPos, this.right); 
    	
    	product.lookAhead = new ArrayList<Integer>();
    	for (int i = 0; i < this.lookAhead.size(); i++) {
    		product.lookAhead.add(this.lookAhead.get(i));
    	}
    	
    	return  product;
    }
    
    public ArrayList<Integer> computeFirstSetOfBetaAndC() {
    	
    	
    	ArrayList<Integer> set = new ArrayList<Integer>();
    	for (int i = dotPos + 1; i < right.size(); i++) {
    		set.add(right.get(i));
    	}
    	
    	ProductionManager manager = ProductionManager.getProductionManager();
    	ArrayList<Integer> firstSet = new ArrayList<Integer>();
    	
    	//beta 部分不为空,计算FIRST(B)作为该表达式的lookahad集合
    	if (set.size() > 0) {
    		for (int i = 0; i < set.size(); i++) {
    	    	
        		ArrayList<Integer> lookAhead = manager.getFirstSetBuilder().getFirstSet(set.get(i));
        		
        		for (int j = 0; j < lookAhead.size(); j++) {
        			if (firstSet.contains(lookAhead.get(j)) == false) {
        				firstSet.add(lookAhead.get(j));
        			}
        		}
        		
        		if (manager.getFirstSetBuilder().isSymbolNullable(set.get(i)) == false) {
        			break;
        		}
        		
        		if (i == lookAhead.size() - 1) {
        			//beta is composed by nulleable terms
        			firstSet.addAll(this.lookAhead);
        		}
        	}
    	} else  {
    		//如果beta 部分为空的话，直接将上一个表达式的lookaHead集合作为该表达式的lookAhead集合
    		firstSet.addAll(lookAhead);
    	}
    	
    	
    	return firstSet;
    }
    
    public void printBeta() {
    	System.out.print("Beta part of production is: ");
    	for (int i = dotPos + 1; i < right.size(); i++) {
    		//System.out.print(SymbolDefine.getSymbolStr(right.get(i)) + " ");
    		int val = right.get(i);
    		System.out.print(CTokenType.values()[val].toString());
    	}
    	
    	if (dotPos+1 >= right.size()) {
    		System.out.print("null");
    	}
    	
    	System.out.print("\n");
    }
    
    public void addLookAheadSet(ArrayList<Integer> list) {
    	lookAhead = list;
    }
 
    
    public int getLeft() {
    	return left;
    }
    
    public ArrayList<Integer> getRight() {
    	return right;
    }
    
    public int getDotPosition() {
    	return dotPos;
    }
    
    public int getDotSymbol() {
    	if (dotPos >= right.size()) {
    		return CTokenType.UNKNOWN_TOKEN.ordinal();
    	}
    	
    	return right.get(dotPos);
    }
    
    @Override
    public boolean equals(Object obj) {
    	/*
    	 * 判断两个表达式是否相同有两个条件，一是表达式相同，而是对应的Look ahead 集合也必须一致
    	 */
    	Production product = (Production)obj;
    	
    	if (this.productionEequals(product) && this.lookAheadSetComparing(product) == 0) {
    		return true;
    	}
    	
    	return false;
    }
    
   
    
    public boolean coverUp(Production product) {
    	/*
    	 * 如果表达式相同，但是表达式1的look ahead 集合 覆盖了表达式2的look ahead 集合，
    	 * 那么表达式1 就覆盖 表达式2
    	 */
    	if (this.productionEequals(product) && this.lookAheadSetComparing(product) > 0) {
    		return true;
    	}
    	
    	return false;
    }
    
    public  boolean productionEequals(Production product) {
    	if (this.left != product.getLeft()) {
    		return false;
    	}
    	
    	if (this.right.equals(product.getRight()) == false) {
    		return false;
    	}
    	
    	if (this.dotPos != product.getDotPosition()) {
    		return false;
    	}
    	
    	
    	return true;
    }
    
    
    public int lookAheadSetComparing(Production product) {
    	if (this.lookAhead.size() > product.lookAhead.size()) {
    		//looAhead 集合不但要比对方大，而且对方lookAhead的所有元素都在本方的lookAhead中，这样本方才算是覆盖对方
    		for (int i = 0; i < product.lookAhead.size(); i++) {
    			if (this.lookAhead.contains(product.lookAhead.get(i)) == false) {
    				return -1;
    			}
    		}
    		return 1;
    	}
    	
    	if (this.lookAhead.size() < product.lookAhead.size()) {
    		return -1;
    	}
    	
    	if (this.lookAhead.size() == product.lookAhead.size()) {
    		for (int i = 0; i < this.lookAhead.size(); i++) {
        		if (this.lookAhead.get(i) != product.lookAhead.get(i)) {
        			return -1;
        		}
        	} 
    	}
    	
    	return 0;
    }
    
    public void print() {
    	System.out.print(CTokenType.getSymbolStr(left) + " -> " );
    	for (int i = 0; i < right.size(); i++) {
    		if (i == dotPos) {
    			 printDot = true;
    			 System.out.print(".");
    		}
    		
    		System.out.print(CTokenType.getSymbolStr(right.get(i)) + " ");
    	}
    	
    	if (printDot == false) {
    		System.out.print(".");
    	}
    	
    	System.out.print("look ahead set: { ");
    	for (int i = 0; i < lookAhead.size(); i++) {
    		System.out.print(CTokenType.getSymbolStr(lookAhead.get(i)) + " ");
    	}
    	System.out.println("}");
    }
    
    public boolean canBeReduce() {
    	return dotPos >= right.size();
    }
    
    public int  getProductionNum() {
    	return productionNum;
    }
    
    public ArrayList<Integer>  getLookAheadSet() {
    	return lookAhead;
    }

}
