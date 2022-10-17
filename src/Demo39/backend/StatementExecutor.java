package Demo39.backend;

import Demo39.frontend.CGrammarInitializer;

public class StatementExecutor extends BaseExecutor{
	 @Override 
	 public Object Execute(ICodeNode root) {
	    	executeChildren(root);
	    	
	    	return root;
	    }
}
