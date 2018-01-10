package backend;

import frontend.CGrammarInitializer;

public class LocalDefExecutor extends BaseExecutor {

	@Override
	public Object Execute(ICodeNode root) {
		executeChild(root, 0);
		
		return root;
	}

}
