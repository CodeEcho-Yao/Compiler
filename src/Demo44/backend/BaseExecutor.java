package Demo44.backend;

import java.util.Collections;

public abstract class BaseExecutor implements Executor{
    protected void executeChildren(ICodeNode root) {
    	ExecutorFactory factory = ExecutorFactory.getExecutorFactory();
    	root.reverseChildren();
    	
    	int i = 0;
    	while (i < root.getChildren().size()) {
    		ICodeNode child = root.getChildren().get(i);
    		Executor executor = factory.getExecutor(child);
    		if (executor != null) {
    			executor.Execute(child);	
    		}
    		else {
    			System.err.println("Not suitable Executor found, node is: " + child.toString());
    		}
    		
    		i++;
    	}
    }
    
    
    protected void copyChild(ICodeNode root, ICodeNode child) {
    	root.setAttribute(ICodeKey.SYMBOL, child.getAttribute(ICodeKey.SYMBOL));
    	root.setAttribute(ICodeKey.VALUE, child.getAttribute(ICodeKey.VALUE));
    	root.setAttribute(ICodeKey.TEXT, child.getAttribute(ICodeKey.TEXT));
    }
    
    protected ICodeNode executeChild(ICodeNode root, int childIdx) {
    	//把孩子链表的倒转放入到节点本身，减少逻辑耦合性
    	root.reverseChildren();
    	ICodeNode child;
    	ExecutorFactory factory = ExecutorFactory.getExecutorFactory();
		child = (ICodeNode)root.getChildren().get(childIdx);  
		Executor executor = factory.getExecutor(child);
    	ICodeNode res = (ICodeNode)executor.Execute(child);
    	
    	return res;
    }
}
