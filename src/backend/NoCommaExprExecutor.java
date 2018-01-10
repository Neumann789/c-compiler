package backend;



import backend.Compiler.Instruction;
import backend.Compiler.ProgramGenerator;
import frontend.CGrammarInitializer;
import frontend.Symbol;

public class NoCommaExprExecutor extends BaseExecutor{
	ExecutorFactory factory = ExecutorFactory.getExecutorFactory();
	ProgramGenerator generator = ProgramGenerator.getInstance();
	
    @Override 
    public Object Execute(ICodeNode root) {
    	executeChildren(root);
    	
    	int production = (int)root.getAttribute(ICodeKey.PRODUCTION);
    	Object value;
    	ICodeNode child;
    	switch (production) {
    	case CGrammarInitializer.Binary_TO_NoCommaExpr: 
    		child = root.getChildren().get(0);
    		copyChild(root, child);
    		break;
    		
    	case CGrammarInitializer.NoCommaExpr_Equal_NoCommaExpr_TO_NoCommaExpr:
    		child = root.getChildren().get(0);
    		String t = (String)child.getAttribute(ICodeKey.TEXT);
    		IValueSetter setter;
    		setter = (IValueSetter)child.getAttribute(ICodeKey.SYMBOL);
    		child = root.getChildren().get(1);
    		//newly add
    		value = child.getAttribute(ICodeKey.SYMBOL);
    		if (value == null) {
    		    value = child.getAttribute(ICodeKey.VALUE);
    		}
    		
    		try {
				setter.setValue(value);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("Runtime Error: Assign Value Error");
			}
    		
    		child = root.getChildren().get(0);
    		child.setAttribute(ICodeKey.VALUE, value);
    		copyChild(root, root.getChildren().get(0));
			
    		break;
    	}
    	
    	
    	return root;
    }
    
  
}
