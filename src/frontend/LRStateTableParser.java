package frontend;
import java.util.HashMap;
import java.util.Stack;

import backend.CodeTreeBuilder;
import backend.ICodeFactory;
import backend.ICodeNode;


public class LRStateTableParser {
	private Lexer lexer;
	int    lexerInput = 0;
	int    nestingLevel = 0;
	int    enumVal = 0;
	String text = "";
	public static final String GLOBAL_SCOPE = "global";
	public String symbolScope = GLOBAL_SCOPE;
	
	private Object attributeForParentNode = null;
	private TypeSystem typeSystem = TypeSystem.getTypeSystem();
	private Stack<Integer> statusStack = new Stack<Integer>();
	HashMap<Integer, HashMap<Integer, Integer>> lrStateTable = null;
	CodeTreeBuilder codeTreeBuilder = CodeTreeBuilder.getCodeTreeBuilder();
	
    public LRStateTableParser(Lexer lexer) {
    	this.lexer = lexer;
    	statusStack.push(0);
    	valueStack.push(null);
    	lexer.advance();
    	lexerInput = CTokenType.EXT_DEF_LIST.ordinal();
    	lrStateTable = GrammarStateManager.getGrammarManager().getLRStateTable();
    	codeTreeBuilder.setParser(this);
    }
    
    private Stack<Object> valueStack = new Stack<Object>();
    private Stack<Integer> parseStack = new Stack<Integer>();
    
    public TypeSystem getTypeSystem() {
    	return typeSystem;
    }
    
    public Stack<Object> getValueStack() {
    	return valueStack;
    }
    
    public int getCurrentLevel() {
    	return nestingLevel;
    }
    
    private String relOperatorText;
    public String getRelOperatorText() {
    	return relOperatorText;
    }
    
    private void showCurrentStateInfo(int stateNum) {
    	System.out.println("current input is :" + CTokenType.getSymbolStr(lexerInput));
    	
    	System.out.println("current state is: ");
		GrammarState state = GrammarStateManager.getGrammarManager().getGrammarState(stateNum);
		state.print();
    }
    
    public void parse() {
    
        while (true) {
        	
        	Integer action = getAction(statusStack.peek(), lexerInput);
        	
        	if (action == null) {
        		//解析出错
        		System.out.println("Shift for input: " + CTokenType.values()[lexerInput].toString());
        		System.err.println("The input is denied");
    			return;
        	}
        	
        	if (action > 0) {
        		//showCurrentStateInfo(action);
        		
        		//shift 操作
                statusStack.push(action);
    			text = lexer.yytext;
    			if (lexerInput == CTokenType.RELOP.ordinal()) {
    				relOperatorText = text;
    			}
    			parseStack.push(lexerInput);
    			
    			if (CTokenType.isTerminal(lexerInput)) {
    				System.out.println("Shift for input: " + CTokenType.values()[lexerInput].toString());
    				Object obj = takeActionForShift(lexerInput);
    			
    				lexer.advance();
        			lexerInput = lexer.lookAhead;
        			valueStack.push(obj);
    			} else {
    				lexerInput = lexer.lookAhead;
    			}
    			
        	} else {
        		if (action == 0) {
        			System.out.println("The input can be accepted");
        			return;
        		}
        		
        		int reduceProduction = - action;
        		Production product = ProductionManager.getProductionManager().getProductionByIndex(reduceProduction);
        		System.out.println("reduce by product: ");
        		product.print();
        		
        		takeActionForReduce(reduceProduction);
        	
        		
        		int rightSize = product.getRight().size();
        		while (rightSize > 0) {
        			parseStack.pop();
        			valueStack.pop();
        			statusStack.pop();
        			rightSize--;
        		}
        		
        		lexerInput = product.getLeft();
    			parseStack.push(lexerInput);
    			valueStack.push(attributeForParentNode);
        	}
        }
    }
    
    private Object takeActionForShift(int token) {
    	if (token == CTokenType.LP.ordinal() || token == CTokenType.LC.ordinal()) {
    		nestingLevel++;
    	}
    	if (token == CTokenType.RP.ordinal() || token == CTokenType.RC.ordinal()) {
    		nestingLevel--;
    	}
    	
    	
    	return null;
    	
    }
    
    private void takeActionForReduce(int productNum) {
    
    	switch(productNum) {
    	case CGrammarInitializer.TYPE_TO_TYPE_SPECIFIER:
    		attributeForParentNode = typeSystem.newType(text);
    		break;
    	case CGrammarInitializer.EnumSpecifier_TO_TypeSpecifier:
    		attributeForParentNode = typeSystem.newType("int");
    		break;
    	case CGrammarInitializer.StructSpecifier_TO_TypeSpecifier:
    		attributeForParentNode = typeSystem.newType(text);
    		TypeLink link = (TypeLink)attributeForParentNode;
    		Specifier sp = (Specifier)link.getTypeObject();
    		sp.setType(Specifier.STRUCTURE);
    		StructDefine struct = (StructDefine)valueStack.get(valueStack.size() - 1);
    		sp.setStructObj(struct);
    		break;
    	
    	case CGrammarInitializer.SPECIFIERS_TypeOrClass_TO_SPECIFIERS:
    		attributeForParentNode = valueStack.peek();
    		Specifier last = (Specifier)((TypeLink)valueStack.get(valueStack.size() - 2)).getTypeObject();
    		Specifier dst = (Specifier)((TypeLink)attributeForParentNode).getTypeObject();
    		typeSystem.specifierCpy(dst, last);
    		break;
    	case CGrammarInitializer.NAME_TO_NewName:
    	case CGrammarInitializer.Name_TO_NameNT:
    		attributeForParentNode = typeSystem.newSymbol(text, nestingLevel);
    		break;
    	case CGrammarInitializer.START_VarDecl_TO_VarDecl:
    	case CGrammarInitializer.Start_VarDecl_TO_VarDecl:
    		typeSystem.addDeclarator((Symbol)attributeForParentNode, Declarator.POINTER);
    		break;
    		
    	case CGrammarInitializer.VarDecl_LB_ConstExpr_RB_TO_VarDecl:
    		//数组定义
    		Declarator declarator = typeSystem.addDeclarator((Symbol)valueStack.get(valueStack.size() - 4), Declarator.ARRAY);
    		int arrayNum = (Integer)attributeForParentNode;
    		declarator.setElementNum(arrayNum);
    		attributeForParentNode = valueStack.get(valueStack.size() - 4);
    		break;
    		
    	case CGrammarInitializer.Name_TO_Unary:
    		attributeForParentNode = typeSystem.getSymbolByText(text, nestingLevel, symbolScope);
    		break;
    		
    	case CGrammarInitializer.ExtDeclList_COMMA_ExtDecl_TO_ExtDeclList:
    	case CGrammarInitializer.VarList_COMMA_ParamDeclaration_TO_VarList:
    	case CGrammarInitializer.DeclList_Comma_Decl_TO_DeclList:
    	case CGrammarInitializer.DefList_Def_TO_DefList:
    	{
    		
    		Symbol currentSym = (Symbol)attributeForParentNode;
    		Symbol lastSym = null;
    		if (productNum == CGrammarInitializer.DefList_Def_TO_DefList) {
    			//这个表达式中没有逗号,所以减2，其他的有逗号，所以减3
    			 lastSym = (Symbol)valueStack.get(valueStack.size() - 2);
    		} else {
    			lastSym = (Symbol)valueStack.get(valueStack.size() - 3);	
    		}
    		
    		currentSym.setNextSymbol(lastSym);
    	}
    		break;
    	case CGrammarInitializer.OptSpecifier_ExtDeclList_Semi_TO_ExtDef:
    	case CGrammarInitializer.TypeNT_VarDecl_TO_ParamDeclaration:
    	case CGrammarInitializer.Specifiers_DeclList_Semi_TO_Def:
    		Symbol symbol = (Symbol)attributeForParentNode;
    		
    		TypeLink specifier = null;
    		if (productNum == CGrammarInitializer.TypeNT_VarDecl_TO_ParamDeclaration ) {
    			specifier = (TypeLink)(valueStack.get(valueStack.size() - 2)); 
    		} else {
    			specifier = (TypeLink)(valueStack.get(valueStack.size() - 3));
    		}
    		
    		typeSystem.addSpecifierToDeclaration(specifier, symbol);
    		typeSystem.addSymbolsToTable(symbol, symbolScope);
    		
    		handleStructVariable(symbol);
    		break;
    		
    	case CGrammarInitializer.VarDecl_Equal_Initializer_TO_Decl:
    		//如果这里不把attributeForParentNode设置成对应的symbol对象
    		//那么上面执行 Symbol symbol = (Symbol)attributeForParentNode; 会出错
    		attributeForParentNode = (Symbol)valueStack.get(valueStack.size() - 2);
    		break;
    		
    	case CGrammarInitializer.NewName_LP_VarList_RP_TO_FunctDecl:
    		setFunctionSymbol(true);
    		Symbol argList = (Symbol)valueStack.get(valueStack.size() - 2);
    		((Symbol)attributeForParentNode).args = argList;
    		typeSystem.addSymbolsToTable((Symbol)attributeForParentNode, symbolScope);
    		//遇到函数定义，变量的scope名称要改为函数名,并把函数参数的scope改为函数名
    		symbolScope = ((Symbol)attributeForParentNode).getName();
    		Symbol sym = argList;
    		while (sym != null) {
    			sym.addScope(symbolScope);
    			sym = sym.getNextSymbol();
    		}
    		break;
    		
    	case CGrammarInitializer.NewName_LP_RP_TO_FunctDecl:
    		setFunctionSymbol(false);
    		typeSystem.addSymbolsToTable((Symbol)attributeForParentNode, symbolScope);
    		//遇到函数定义，变量的scope名称要改为函数名
    		symbolScope = ((Symbol)attributeForParentNode).getName();
    		
    		break;
    		
    	case CGrammarInitializer.OptSpecifiers_FunctDecl_CompoundStmt_TO_ExtDef:
    		symbol = (Symbol)valueStack.get(valueStack.size() - 2);
    		specifier = (TypeLink)(valueStack.get(valueStack.size() - 3));
    		typeSystem.addSpecifierToDeclaration(specifier, symbol);
    		
    		//函数定义结束后，接下来的变量作用范围应该改为global
    		symbolScope = GLOBAL_SCOPE;
    		break;
    		
    	case CGrammarInitializer.Name_To_Tag:
    		symbolScope = text;
    		attributeForParentNode = typeSystem.getStructObjFromTable(text);
    		if (attributeForParentNode == null) {
    			attributeForParentNode = new StructDefine(text, nestingLevel, null);
        		typeSystem.addStructToTable((StructDefine)attributeForParentNode);	
    		}
    		
    		break;
    		
    	case CGrammarInitializer.Struct_OptTag_LC_DefList_RC_TO_StructSpecifier:
    		Symbol defList = (Symbol)valueStack.get(valueStack.size() - 2);
    		StructDefine structObj = (StructDefine)valueStack.get(valueStack.size() - 4);
    		structObj.setFields(defList);
    		attributeForParentNode = structObj;
    		//结构体定义结束后，接下来的变量作用范围应该改为global
    		symbolScope = GLOBAL_SCOPE;
    		break;
    	
    	case CGrammarInitializer.Enum_TO_EnumNT:
    		enumVal = 0;
    		break;
    		
    	case CGrammarInitializer.NameNT_TO_Emurator:
    		doEnum();
    		break;
    	case CGrammarInitializer.Name_Eequal_ConstExpr_TO_Enuerator:
    		enumVal = (Integer)(valueStack.get(valueStack.size() - 1));
    		attributeForParentNode = (Symbol)(valueStack.get(valueStack.size() - 3));
    		doEnum();
    		break;
    	case CGrammarInitializer.Number_TO_ConstExpr:
    	case CGrammarInitializer.Number_TO_Unary:
    		attributeForParentNode = Integer.valueOf(text);
    		break;
    	}
    	
    	codeTreeBuilder.buildCodeTree(productNum, text);
    }
    
   private void handleStructVariable(Symbol symbol) {
	   if (symbol == null) {
		   return;
	   }
	   
	   //先看看变量是否属于struct类型
	   boolean isStruct = false;
	   TypeLink typeLink = symbol.typeLinkBegin;
	   Specifier specifier = null;
	   while (typeLink != null) {
		   if (typeLink.isDeclarator == false) { 
			   specifier = (Specifier)typeLink.getTypeObject();
			   if (specifier.getType() == Specifier.STRUCTURE) {
				   isStruct = true;
				   break;
			   }
		   }
		   
		   typeLink = typeLink.toNext();
	   }
	   
	   if (isStruct == true) {
		   //把结构体定义中的每个变量拷贝一份，存储到当前的symbol中
		   StructDefine structDefine = specifier.getStructObj();
		   Symbol copy = null, headCopy = null, original = structDefine.getFields();
		   while (original != null) {
			   if (copy != null) {
				  Symbol sym = original.copy();
				  copy.setNextSymbol(sym);
				  copy = sym;
			   } else {
				   copy = original.copy();
				   headCopy = copy;
			   }
			   
			   original = original.getNextSymbol();
		   }
		   
		   symbol.setArgList(headCopy);
	   }
   }
    
    private void doEnum() {
    	Symbol symbol = (Symbol)attributeForParentNode;
    	if (convSymToIntConst(symbol, enumVal)) {
    		typeSystem.addSymbolsToTable(symbol, symbolScope);
    		enumVal++;
    	}
    	else {
    		System.err.println("enum symbol redefinition: " + symbol.name);
    	}
    }
    
    private boolean convSymToIntConst(Symbol symbol, int val) {
    	if (symbol.getTypeHead() != null) {
    		return false;
    	}
    	
    	TypeLink typeLink = typeSystem.newType("int");
    	Specifier specifier = (Specifier)typeLink.getTypeObject();
    	specifier.setConstantVal(val);
    	specifier.setType(Specifier.CONSTANT);
    	symbol.addSpecifier(typeLink);
    	
    	return true;
    }
    
    private void setFunctionSymbol(boolean hasArgs) {
    	Symbol funcSymbol = null;
    	if (hasArgs) {
    		funcSymbol = (Symbol)valueStack.get(valueStack.size() - 4);
    	} else {
    		funcSymbol = (Symbol)valueStack.get(valueStack.size() - 3);
    	}
    	 
    	
		typeSystem.addDeclarator(funcSymbol, Declarator.FUNCTION);
		attributeForParentNode = funcSymbol;
    }
    
    private Integer getAction(Integer currentState, Integer currentInput) {
    	HashMap<Integer, Integer> jump = lrStateTable.get(currentState);
    	if (jump != null) {
    		Integer next = jump.get(currentInput);
    		if (next != null) {
    			return next;
    		}
    	}
    	
    	return null;
    }
    
}
