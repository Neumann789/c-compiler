package frontend;

import java.util.ArrayList;
import java.util.HashMap;

public class Declarator {
    public static int  POINTER = 0;
    public static int  ARRAY = 1;
    public static int  FUNCTION = 2;
    
    private int  declareType = POINTER;
    private int  numberOfElements = 0;
    
    HashMap<Integer, Object> elements = null;
    
    public Declarator(int type) {
    	if (type < POINTER) {
    		declareType = POINTER;
    	} 
    	else if (type > FUNCTION) {
    		declareType = FUNCTION;
    	} else {
    		declareType = type;
    	}
    }
    
    public void setElementNum(int num) {
    	if (num < 0) {
    		numberOfElements = 0;
    	} else {
    		numberOfElements = num;
    		elements = new  HashMap<Integer, Object>();
    	}
    }
    
    public void addElement(int index, Object obj) throws Exception {
    	if (elements == null) {
    		throw new Exception("This is not an Array!");
    	}
    	
    	if (index >= numberOfElements) {
    	    throw new Exception("Array Out Of Range");	
    	}
    	
    	if (elements != null) {
    		elements.put(index, obj);
    	} 
    }
    
    public Object getElement(int index) throws Exception {
    	if (elements == null || index >= numberOfElements) {
    		throw new Exception("element list is null or index out of range");
    	}
    	
    	return elements.get(index);
    }
    
    public int getType() {
    	return declareType ;
    }
    
    public int getElementNum() {
    	return numberOfElements;
    }
}
