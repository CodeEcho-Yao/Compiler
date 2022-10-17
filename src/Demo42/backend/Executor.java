package Demo42.backend;

import Demo42.frontend.LRStateTableParser;

public interface Executor {
    public Object Execute(ICodeNode root);
}
