package backend;

import java.util.Collections;

import backend.Compiler.Instruction;
import backend.Compiler.ProgramGenerator;
import frontend.CGrammarInitializer;
import frontend.Symbol;

public class BinaryExecutor extends BaseExecutor{
    @Override
    public Object Execute(ICodeNode root) {
    	executeChildren(root);
    	ProgramGenerator generator = ProgramGenerator.getInstance();
    	ICodeNode child;
    	int production = (int)root.getAttribute(ICodeKey.PRODUCTION);
    	int val1 = 0, val2 = 0;
    	switch (production) {
    	case CGrammarInitializer.Uanry_TO_Binary:
    		
    		child = root.getChildren().get(0);
    		copyChild(root, child);
    		break;
    		
    	case CGrammarInitializer.Binary_Plus_Binary_TO_Binary:
    	case CGrammarInitializer.Binary_DivOp_Binary_TO_Binary:
    	case CGrammarInitializer.Binary_Minus_Binary_TO_Binary:
    	case CGrammarInitializer.Binary_Start_Binary_TO_Binary:
    		
    		BaseExecutor.resultOnStack = true;
    		if (root.getChildren().get(0).getAttribute(ICodeKey.VALUE) != null) {
    		    val1 = (Integer)root.getChildren().get(0).getAttribute(ICodeKey.VALUE);
    		}
   		    Symbol sym1 = (Symbol)root.getChildren().get(0).getAttribute(ICodeKey.SYMBOL);
   		    if (sym1 != null) {
   			   int d = generator.getLocalVariableIndex(sym1);
   			   generator.emit(Instruction.ILOAD, "" + d);
   		    } else {
   			   generator.emit(Instruction.SIPUSH, "" + val1);
   		    }
   		    
   		    if (root.getChildren().get(1).getAttribute(ICodeKey.VALUE) != null) { 
   		        val2 = (Integer)root.getChildren().get(1).getAttribute(ICodeKey.VALUE);
   		    }
   		    Symbol sym2 = (Symbol)root.getChildren().get(1).getAttribute(ICodeKey.SYMBOL);
   		    if (sym2 != null) {
   			   int d = generator.getLocalVariableIndex(sym2);
   			   generator.emit(Instruction.ILOAD, "" + d);
   		    } else {
   			   generator.emit(Instruction.SIPUSH, "" + val2);
   		    }

    		
    		if (production == CGrammarInitializer.Binary_Plus_Binary_TO_Binary) {
    			String text = root.getChildren().get(0).getAttribute(ICodeKey.TEXT) + " plus " + root.getChildren().get(1).getAttribute(ICodeKey.TEXT);
    			root.setAttribute(ICodeKey.VALUE, val1 + val2);	
    			root.setAttribute(ICodeKey.TEXT,  text);
        		System.out.println(text + " is " + (val1+val2) );	
        		ProgramGenerator.getInstance().emit(Instruction.IADD);
    		} else if (production ==  CGrammarInitializer.Binary_Minus_Binary_TO_Binary) {
    			String text = root.getChildren().get(0).getAttribute(ICodeKey.TEXT) + " minus " + root.getChildren().get(1).getAttribute(ICodeKey.TEXT);
    			root.setAttribute(ICodeKey.VALUE, val1 - val2);	
    			root.setAttribute(ICodeKey.TEXT,  text);
        		System.out.println(text + " is " + (val1-val2) );	
        		ProgramGenerator.getInstance().emit(Instruction.ISUB);
    		} else if (production ==  CGrammarInitializer.Binary_Start_Binary_TO_Binary) {
    			String text = root.getChildren().get(0).getAttribute(ICodeKey.TEXT) + " * " + root.getChildren().get(1).getAttribute(ICodeKey.TEXT);
    			root.setAttribute(ICodeKey.VALUE, val1 * val2);	
    			root.setAttribute(ICodeKey.TEXT,  text);
        		System.out.println(text + " is " + (val1 * val2) );
        		
        		ProgramGenerator.getInstance().emit(Instruction.IMUL);
    		}
    		else {
    			root.setAttribute(ICodeKey.VALUE, val1 / val2);	
        		System.out.println( root.getChildren().get(0).getAttribute(ICodeKey.TEXT) + " is divided by "
        				+ root.getChildren().get(1).getAttribute(ICodeKey.TEXT) + " and result is " + (val1/val2) );
        		ProgramGenerator.getInstance().emit(Instruction.IDIV);
    		}
    		
    		root.setAttribute(ICodeKey.TEXT, "onstack");
    		
    		break;
    		
    	case CGrammarInitializer.Binary_RelOP_Binary_TO_Binray:
    		 //change here check null before convert to integer
    		 Object valObj = root.getChildren().get(0).getAttribute(ICodeKey.VALUE);
    		 Object symObj = root.getChildren().get(0).getAttribute(ICodeKey.SYMBOL);
    		 
    		 if (symObj == null) {
    		     val1 = (Integer)valObj;
    		     generator.emit(Instruction.SIPUSH, "" + val1);
    		 } else {
    			 if (symObj instanceof ArrayValueSetter) {
    				 sym1 = (Symbol)((ArrayValueSetter)symObj).getSymbol();
    				 Object index = ((ArrayValueSetter)symObj).getIndex();
    				 generator.readArrayElement(sym1, index);
    			 } else {
    		         sym1 = (Symbol)symObj;
    		         int d = generator.getLocalVariableIndex(sym1);
			         generator.emit(Instruction.ILOAD, "" + d);
    			 }
    		 }
    		 String operator = (String)root.getChildren().get(1).getAttribute(ICodeKey.TEXT);
    		 
    		 valObj = root.getChildren().get(2).getAttribute(ICodeKey.VALUE);
    		 symObj = root.getChildren().get(2).getAttribute(ICodeKey.SYMBOL);
    		 if (symObj == null) {
    		     val2 = (Integer)valObj;
    		     generator.emit(Instruction.SIPUSH, "" + val2);
    		 } else {
    			 if (symObj instanceof ArrayValueSetter) {
    				 sym2 = (Symbol)((ArrayValueSetter)symObj).getSymbol();
    				 Object index = ((ArrayValueSetter)symObj).getIndex();
    				 generator.readArrayElement(sym2, index);
    			 } else {
    		         sym2 = (Symbol)symObj;
    		         int d = generator.getLocalVariableIndex(sym2);
			         generator.emit(Instruction.ILOAD, "" + d);
    			 }

    		 }
    		 String branch = ProgramGenerator.getInstance().getCurrentBranch() + "\n";
    		 
    		 switch (operator) {
    		 case "==":
    			 root.setAttribute(ICodeKey.VALUE, val1 == val2 ? 1 : 0);
    			 break;
    		 case "<":
    			 //generator.emitString(Instruction.IF_ICMPGE.toString() + " " + branch);
    			 generator.setComparingCommand(Instruction.IF_ICMPGE.toString() + " " + branch);
    			 root.setAttribute(ICodeKey.VALUE, val1 < val2? 1 : 0);
    			 break;
    		 case "<=":
    			 generator.setComparingCommand(Instruction.IF_ICMPGT.toString() + " " + branch);
    			 root.setAttribute(ICodeKey.VALUE, val1 <= val2? 1 : 0);
    			 break;
    		 case ">":
    			 //generator.emitString(Instruction.IF_ICMPLE.toString() + " " + branch);
    			 generator.setComparingCommand(Instruction.IF_ICMPLE.toString() + " " + branch);
    			 root.setAttribute(ICodeKey.VALUE, val1 > val2? 1 : 0);
    			 break;
    		 case ">=":
    			 generator.setComparingCommand(Instruction.IF_ICMPLT.toString() + " " + branch);
    			 root.setAttribute(ICodeKey.VALUE, val1 >= val2? 1 : 0);
    			 break;
    			 
    		 case "!=":
    			 root.setAttribute(ICodeKey.VALUE, val1 != val2? 1 : 0);
    			 break;
    		 }
    		 
    		
    		 break;
    		 
    	
    	}
    	
    	return root;
    }
}
