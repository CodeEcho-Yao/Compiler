package Test01.backend;

import Test01.frontend.CGrammarInitializer;

public class StatementListExecutor extends BaseExecutor{
	 @Override 
	 public Object Execute(ICodeNode root) {
	    	executeChildren(root);
	    	Object child = root.getChildren().get(0);
	        copyChild(root, root.getChildren().get(0));
	    	return root;
	    }
}
