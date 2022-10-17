package Demo38.backend;

import java.util.Collections;

public abstract class BaseExecutor implements Executor{
    protected void executeChildren(ICodeNode root) {
    	ExecutorFactory factory = ExecutorFactory.getExecutorFactory();
    	Collections.reverse(root.getChildren());
    	int i = 0;
    	while (i < root.getChildren().size()) {
    		ICodeNode child = root.getChildren().get(i);
    		Executor executor = factory.getExecutor(child);
    		executor.Execute(child);
    		i++;
    	}
    }
    
    
    protected void copyChild(ICodeNode root, ICodeNode child) {
    	root.setAttribute(ICodeKey.SYMBOL, child.getAttribute(ICodeKey.SYMBOL));
    	root.setAttribute(ICodeKey.VALUE, child.getAttribute(ICodeKey.VALUE));
    	root.setAttribute(ICodeKey.TEXT, child.getAttribute(ICodeKey.TEXT));
    }
}
