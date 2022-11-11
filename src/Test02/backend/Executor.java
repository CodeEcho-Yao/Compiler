package Test02.backend;

import Test02.frontend.LRStateTableParser;

public interface Executor {
    public Object Execute(ICodeNode root);
}
