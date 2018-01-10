package backend;
import frontend.CGrammarInitializer;


public class ExprExecutor extends BaseExecutor{
    @Override 
    public Object Execute(ICodeNode root) {
    	executeChildren(root);
    	int production = (int)root.getAttribute(ICodeKey.PRODUCTION);
    	
    	switch (production) {
    	case CGrammarInitializer.NoCommaExpr_TO_Expr:
    		copyChild(root, root.getChildren().get(0));
    		break;
    	}
    	
    	return root;
    }
}
