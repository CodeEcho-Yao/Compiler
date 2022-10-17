package Demo38.backend;

import Demo38.frontend.LRStateTableParser;

public interface Executor {
    public Object Execute(ICodeNode root);
}
