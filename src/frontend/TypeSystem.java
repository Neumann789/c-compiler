package frontend;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import backend.ClibCall;


public class TypeSystem {
	private static TypeSystem typeSystem = null;
	
	public static TypeSystem getTypeSystem() {
		if (typeSystem == null) {
			typeSystem = new TypeSystem();
		}
		
		return typeSystem;
	}
	private TypeSystem() {
		
	}
	private HashMap<String, ArrayList<Symbol>> symbolTable = new HashMap<String, ArrayList<Symbol>>();
	private HashMap<String, StructDefine> structTable = new HashMap<String, StructDefine>();
	
	public ArrayList<Symbol> getSymbolsByScope(String scope) {
		ArrayList<Symbol> list = new ArrayList<Symbol>();
		for (Map.Entry<String, ArrayList<Symbol>> entry : symbolTable.entrySet()) {
			ArrayList<Symbol> args = entry.getValue();
			for (int i = 0; i < args.size(); i++) {
				Symbol sym = args.get(i);
				if (sym.getScope().equals(scope)) {
					list.add(sym);
				}
			}
		}
		
		return list;
	}
	
	public void addStructToTable(StructDefine struct) {
		if (structTable.containsKey(struct.getTag())) {
			System.err.println("Struct with name: " + struct.getTag() + " is already defined");
			return;
		}
		
		structTable.put(struct.getTag(), struct);
	}
	
	public StructDefine getStructObjFromTable(String tag) {
		return structTable.get(tag);
	}
	
	public void addSymbolsToTable(Symbol headSymbol, String scope) {
		while (headSymbol != null) {
			headSymbol.addScope(scope);
			
			ArrayList<Symbol> symList = symbolTable.get(headSymbol.name);
			if (symList == null) {
				symList = new ArrayList<Symbol>();
				symList.add(headSymbol);
				symbolTable.put(headSymbol.name, symList);
			}
			else {
				handleDublicateSymbol(headSymbol, symList);
			}
			
			headSymbol = headSymbol.getNextSymbol();
		}
	}
	
	public void removeSymbolFromTable(Symbol symbol) {
		ArrayList<Symbol> symList = symbolTable.get(symbol.name);
		int pos = 0;
		while (pos < symList.size()) {
			Symbol sym = symList.get(pos);
			if (sym.getLevel() == symbol.getLevel()) {
				symList.remove(pos);
				return;
			}
			
			pos++;
		}
	}
	
	public ArrayList<Symbol> getSymbol(String text) {
		return symbolTable.get(text);
	}
	
	private void handleDublicateSymbol(Symbol symbol, ArrayList<Symbol>symList) {
		boolean harmless = true;
		Iterator it = symList.iterator();
		
		while (it.hasNext()) {
			Symbol sym = (Symbol)it.next();
			if (sym.equals(symbol) == true) {
				//TODO, handle duplication here
				System.err.println("Symbol definition replicate: " + sym.name);
				System.exit(1);
			}
		}
		
		if (harmless == true) {
			symList.add(symbol);
		}
	}
	
	
	
    public TypeLink newType(String typeText) {
    	Specifier sp = null;
    	int type = Specifier.CHAR;
    	boolean isLong = false, isSigned = true;
    	switch (typeText.charAt(0)) {
    	case 'c': 
    		if (typeText.charAt(1) == 'h') {
    			type = Specifier.CHAR;
    		}
    		break; 
    	case 'd':
    	case 'f':
    		System.err.println("No floating point support");
    		System.exit(1);
    		break;
    	case 'i':
    		type = Specifier.INT;
    		break;
    	case 'l':
    		isLong = true;
    		break;
    	case 'u':
    		isSigned = false;
    		break;
    	case 'v':
    		if (typeText.charAt(2) == 'i') {
    			type = Specifier.VOID;
    		}
    		break;
    	case 's':
    		//ignore short signed
    		break;
    	}
    	
    	sp = new Specifier();
    	sp.setType(type);
    	sp.setLong(isLong);
    	sp.setSign(isSigned);
    	
    	TypeLink link = new TypeLink(false, false, sp);
    	return link;
    }
    
    public void specifierCpy(Specifier dst, Specifier org) {
    	dst.setConstantVal(org.getConstantVal());
    	dst.setExternal(org.isExternal());
    	dst.setLong(org.getLong());
    	dst.setOutputClass(org.getOutputClass());
    	dst.setSign(org.isSign());
    	dst.setStatic(org.isStatic());
    	dst.setStorageClass(org.getStorageClass());
    }
    
    public TypeLink newClass(String classText) {
    	Specifier sp = new Specifier();
    	sp.setType(Specifier.NONE);
    	setClassType(sp, classText.charAt(0));
    	
    	TypeLink link = new TypeLink(false, false, sp);
    	return link;
    }
    
    private void setClassType(Specifier sp, char c) {
    	switch(c) {
    	case 0:
    		sp.setStorageClass(Specifier.FIXED);
    		sp.setStatic(false);
    		sp.setExternal(false);
    		break;
    	case 't':
    		sp.setStorageClass(Specifier.TYPEDEF);
    		break;
    	case 'r':
    		sp.setStorageClass(Specifier.REGISTER);
    		break;
    	case 's':
    		sp.setStatic(true);
    		break;
    	case 'e':
    		sp.setExternal(true);
    		break;
    		
    	default:
    			System.err.println("Internal error, Invalid Class type");
    			System.exit(1);
    			break;
     	}
    }
    
    public Symbol newSymbol(String name, int level) {
    	return new Symbol(name, level);
    }
    
    public Declarator addDeclarator(Symbol symbol, int declaratorType) {
    	Declarator declarator = new Declarator(declaratorType);
    	TypeLink link = new TypeLink(true, false, declarator);
    	symbol.addDeclarator(link);
    	
    	return declarator;
    }
    
    public void addSpecifierToDeclaration(TypeLink specifier, Symbol symbol) {
    	while (symbol != null) {
    		symbol.addSpecifier(specifier);
    		symbol = symbol.getNextSymbol();
    	}
    }
    
    public Symbol getSymbolByText(String text, int level, String scope) {
    	ClibCall libCall = ClibCall.getInstance();
    	if (libCall.isAPICall(text)) {
    	    return null;	
    	}
    	
    	if (scope.equals(text)) {
    		scope = LRStateTableParser.GLOBAL_SCOPE;
    	}
    	
    	ArrayList<Symbol> symbolList = typeSystem.getSymbol(text);
    	int i = 0;
    	Symbol symbol = null;
    	
    	while (i < symbolList.size()) {
    		Symbol sym = symbolList.get(i);
    		
    		if (symbol == null && symbolList.get(i).getScope().equals(LRStateTableParser.GLOBAL_SCOPE)) {
    			 symbol = symbolList.get(i);
    		} 
    		
    		if (symbolList.get(i).getScope().equals(scope)) {
    			symbol = symbolList.get(i);
    		}
    		
    		if (symbol != null && symbolList.get(i).getLevel() >= level) {
    			symbol = symbolList.get(i);
    		}
    		
    		i++;
    	}
    	
    	return symbol;
    }
    
}
