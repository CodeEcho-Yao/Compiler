package Demo41.backend;

import Demo41.frontend.CGrammarInitializer;

public class StatementListExecutor extends BaseExecutor{
	 @Override 
	 public Object Execute(ICodeNode root) {
	    	executeChildren(root);
	    
	    	return root;
	    }
}
