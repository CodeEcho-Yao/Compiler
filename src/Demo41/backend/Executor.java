package Demo41.backend;

import Demo41.frontend.LRStateTableParser;

public interface Executor {
    public Object Execute(ICodeNode root);
}
