package backend;

import backend.Compiler.Instruction;
import backend.Compiler.ProgramGenerator;
import frontend.CGrammarInitializer;
import frontend.Declarator;
import frontend.Specifier;
import frontend.Symbol;

public class DefExecutor extends BaseExecutor {

	@Override
	public Object Execute(ICodeNode root) {
		int production = (int)root.getAttribute(ICodeKey.PRODUCTION);
		ProgramGenerator generator = ProgramGenerator.getInstance();
		Symbol symbol = (Symbol)root.getAttribute(ICodeKey.SYMBOL);
		switch (production) {
		case CGrammarInitializer.Specifiers_DeclList_Semi_TO_Def:
			 Declarator declarator = symbol.getDeclarator(Declarator.ARRAY);
			 if (declarator != null) {
				 if (symbol.getSpecifierByType(Specifier.STRUCTURE) == null) {
						//如果是结构体数组，这里不做处理
					    generator.createArray(symbol);
				 }
			 } else {
				 //change here 遇到变量定义时，自动将其初始化为0
				 int i = generator.getLocalVariableIndex(symbol);
				 generator.emit(Instruction.SIPUSH, ""+0);
				 generator.emit(Instruction.ISTORE, ""+i);
			 }
			 
			 break; 
		}
		
		return root;
	}

}
