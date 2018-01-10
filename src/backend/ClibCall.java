package backend;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import backend.Compiler.Instruction;
import backend.Compiler.ProgramGenerator;
import frontend.Declarator;
import frontend.Symbol;
import frontend.TypeSystem;

public class ClibCall {
	private Set<String> apiSet;
	
    private ClibCall() {
    	apiSet = new HashSet<String>();
    	apiSet.add("printf");
    	apiSet.add("malloc");
    	apiSet.add("sizeof");
    }
    private static ClibCall instance = null;
    
    public static ClibCall getInstance() {
    	
        if (instance == null) {
        	instance = new ClibCall();
        }
        
        return instance;
    }
    
    public boolean isAPICall(String funcName) {
    	return apiSet.contains(funcName);
    }
    
    public Object invokeAPI(String funcName) {
    	switch (funcName) {
    	
    	case "printf":
    		return handlePrintfCall();
    	
    	case "malloc":
    		return handleMallocCall();
    		
    	case "sizeof":
    		return handleSizeOfCall();
    	default:
    		return null;
    	}
    }
    
    private Object handleSizeOfCall() {
    	ArrayList<Object> symList = FunctionArgumentList.getFunctionArgumentList().getFuncArgSymsList(false);
    	Integer totalSize = calculateVarSize((Symbol)symList.get(0));
    	
    	return totalSize;
    }
    
    private Integer calculateVarSize(Symbol symbol) {
    	int size = 0;
    	if (symbol.getArgList() == null) {
    		size = symbol.getByteSize();
    	} else {
    		Symbol head = symbol.getArgList();
    		while (head != null) {
    			size += calculateVarSize(head);
    			head = head.getNextSymbol();
    		}
    	}
    	
    	Declarator declarator = symbol.getDeclarator(Declarator.ARRAY); 
    	if ( declarator != null) {
    		size = size * declarator.getElementNum();
    	}
    	
    	return size;
    }
    
    private Object handleMallocCall() {
    	ArrayList<Object> argsList = FunctionArgumentList.getFunctionArgumentList().getFuncArgList(false);
    	int size = (Integer)argsList.get(0);
    	int addr = 0;
    	
    	if (size > 0) {
    		MemoryHeap memHeap = MemoryHeap.getInstance();
        	addr = memHeap.allocMem(size);	
    	} 
    	
    	return addr;
    	
    }
    
    private Object handlePrintfCall() {
    	ArrayList<Object> argsList = FunctionArgumentList.getFunctionArgumentList().getFuncArgList(false);
    	String argStr = (String)argsList.get(0);
    	String formatStr = "";
    	
    	int i = 0;
    	int argCount = 1;
    	String str = "";
    	while (i < argStr.length()) {
    		if (argStr.charAt(i) == '%' && i+1 < argStr.length() && 
    				argStr.charAt(i+1) == 'd') {
    			i += 2;
    			//generateJavaAssemblyForPrintf(str);
    			str = "";
    			formatStr += argsList.get(argCount);
    			argCount++;
    			//printInteger();
    		} else {
    			str += argStr.charAt(i);
    			formatStr += argStr.charAt(i);
    			i++;
    		}
    	}
    	
    	System.out.println(formatStr);
    	
    	generateJavaAssemblyForPrintf(argStr, argCount - 1);
    	return null;
    }
    
    private void generateJavaAssemblyForPrintf(String argStr, int argCount) {
    	ProgramGenerator generator = ProgramGenerator.getInstance();
    	String funcName = generator.getCurrentFuncName();
    	TypeSystem typeSystem = TypeSystem.getTypeSystem();
    	ArrayList<Symbol> list = typeSystem.getSymbolsByScope(funcName);
    	ArrayList<Object> argList = FunctionArgumentList.getFunctionArgumentList().getFuncArgSymsList(true);
    	int localVariableNum = list.size();
    	int count = 0;
    	int argSymCount = 0;
    	//change here
    	while (count < argCount) {
    		Object obj = argList.get(argSymCount);
    		if (obj instanceof ArrayValueSetter) {
    			ArrayValueSetter setter = (ArrayValueSetter)obj;
			    Object idxObj = setter.getIndex();
			    Symbol arraySym = setter.getSymbol();
			    if (idxObj instanceof Integer) {
				    int i = (int)idxObj;
				    generator.readArrayElement(arraySym, i);
			    } else {
				    generator.readArrayElement(arraySym, idxObj);
			    }
    		} else {
    		    Symbol argSym = (Symbol)obj;
			    int d = generator.getLocalVariableIndex(argSym);
			    generator.emit(Instruction.ILOAD, "" + d);
    		}
    		
    		argSymCount++;
    		generator.emit(Instruction.ISTORE, "" + (localVariableNum + count));
    		count++;
    	}
    	
    	int i = 0;
    	String str = "";
    	count = argCount - 1;
    	
    	while (i < argStr.length()) {
    		if (argStr.charAt(i) == '%' && i+1 < argStr.length() && 
    				argStr.charAt(i+1) == 'd') {
    			i += 2;
    			printString(str);
    			str = "";
    			
    			printInteger(localVariableNum + count);
    			count--;
    		} else {
    			str += argStr.charAt(i);
    			i++;
    		}
    	}
    	
    	if (str != "") {
    		printString(str);
    	}
    	
    	printString("\n");
    }
    
    private void printString(String s) {
    	if (s.length() == 0) {
    		return;
    	}
    	
    	ProgramGenerator generator = ProgramGenerator.getInstance();
    	generator.emit(Instruction.GETSTATIC, "java/lang/System/out Ljava/io/PrintStream;");
    	generator.emit(Instruction.LDC, "\"" + s + "\"");
    	String printMethod = "java/io/PrintStream/print(Ljava/lang/String;)V";
    	generator.emit(Instruction.INVOKEVIRTUAL, printMethod);
    }
    
    private void printInteger(int posInList) {
    	ProgramGenerator generator = ProgramGenerator.getInstance();
    	generator.emit(Instruction.GETSTATIC, "java/lang/System/out Ljava/io/PrintStream;");
    	generator.emit(Instruction.ILOAD, "" + posInList);
    	String printMethod = "java/io/PrintStream/print(I)V";
    	generator.emit(Instruction.INVOKEVIRTUAL, printMethod);
    }
}
