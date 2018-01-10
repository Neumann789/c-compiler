package backend;

import frontend.CGrammarInitializer;

public class DefListExecutor extends BaseExecutor {

	@Override
	public Object Execute(ICodeNode root) {
		int production = (int)root.getAttribute(ICodeKey.PRODUCTION);
		
		switch (production) {
		case CGrammarInitializer.Def_To_DefList:
			executeChild(root,0);
			break;
		case CGrammarInitializer.DefList_Def_TO_DefList:
			executeChild(root, 0);
			executeChild(root, 1);
			
			break;
		}
		
		return root;
	}

}
