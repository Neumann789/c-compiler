package backend;

import java.util.Collections;

import backend.Compiler.Instruction;
import backend.Compiler.ProgramGenerator;
import frontend.CGrammarInitializer;
import frontend.Declarator;
import frontend.Specifier;
import frontend.Symbol;

public class StatementExecutor extends BaseExecutor{
	private ProgramGenerator generator = ProgramGenerator.getInstance();
	
	private enum LoopType {
		FOR,
		WHILE,
		DO_WHILE
	};
	
		
	 @Override 
	 public Object Execute(ICodeNode root) {
		 int production = (int)root.getAttribute(ICodeKey.PRODUCTION);
		 ICodeNode node;
		 
		 switch (production) {
		 case CGrammarInitializer.LocalDefs_TO_Statement:
			 executeChild(root, 0);
			 break;
			 
		 case CGrammarInitializer.FOR_OptExpr_Test_EndOptExpr_Statement_TO_Statement:
			 //execute OptExpr
			 executeChild(root, 0);
			 
			 if (BaseExecutor.isCompileMode) {
				 generator.emitLoopBranch();
				 String branch = generator.getCurrentBranch();
				 isLoopContinute(root, LoopType.FOR);
				 /*   change here increase branch and loop in order to ensure that 
				  *   if/else or loop contained in the loop body have correct branch count
				  */
				 generator.emitComparingCommand();
				 String loop = generator.getLoopBranch();
				 generator.increaseLoopCount();
				 generator.increaseBranch();
				 
				 
				 
				 executeChild(root, 3);
				 executeChild(root, 2); 
				 
				 generator.emitString(Instruction.GOTO + " " + loop);
				 
				 generator.emitString("\n" + branch + ":\n");
				 
			 }

			 while(BaseExecutor.isCompileMode == false && isLoopContinute(root, LoopType.FOR)) {
				 //execute statment in for body
				 executeChild(root, 3);
				 
				 //execute EndOptExpr
				 executeChild(root, 2); 
			 }
			 
			 break;
			 
		 case CGrammarInitializer.While_LP_Test_Rp_TO_Statement:
			 //change here
			 if (BaseExecutor.isCompileMode) {
				 generator.emitLoopBranch();
				 String branch = generator.getCurrentBranch();
				 /*
				  * 先判断循环条件
				  */
				 executeChild(root, 0);
				 generator.emitComparingCommand();
				 /*   change here increase branch and loop in order to ensure that 
				  *   if/else or loop contained in the loop body have correct branch count
				  */
				 
				 String loop = generator.getLoopBranch();
				 generator.increaseLoopCount();
				 generator.increaseBranch();
				 
				 executeChild(root, 1); 
				 
				 
				 generator.emitString(Instruction.GOTO + " " + loop);
				 
				 generator.emitString("\n" + branch + ":\n");
				 
			 }

			 while (BaseExecutor.isCompileMode == false && isLoopContinute(root, LoopType.WHILE)) {
				 executeChild(root, 1);
			 }
			 break;
			 
		 case CGrammarInitializer.Do_Statement_While_Test_To_Statement:
			 do {
				 executeChild(root, 0);
			 } while(isLoopContinute(root, LoopType.DO_WHILE));
			 
			 break;
			 
		 case  CGrammarInitializer.Return_Semi_TO_Statement:
			 isContinueExecution(false);
			 
			 break;
			 
		 case  CGrammarInitializer.Return_Expr_Semi_TO_Statement:
			 node = executeChild(root, 0);
			 Object obj = node.getAttribute(ICodeKey.VALUE);
			 setReturnObj(obj);
			 isContinueExecution(false);
			 
			 break;
			 
		 default:
			 executeChildren(root);
			
			 break;
		 }
		 
	     return root;
	 }
	 
	 private boolean isLoopContinute(ICodeNode root, LoopType type) {
		 ICodeNode res = null;
		 if (type == LoopType.FOR || type == LoopType.DO_WHILE) {
			 res = executeChild(root, 1);
		 }
		 else if (type == LoopType.WHILE) {
			 res = executeChild(root, 0);
		 }
		 
		 int result = (Integer)res.getAttribute(ICodeKey.VALUE);
		 return res != null && result != 0;
		 
	 }
}
