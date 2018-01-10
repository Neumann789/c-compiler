package backend;

import frontend.LRStateTableParser;

public interface Executor {
    public Object Execute(ICodeNode root);
}
