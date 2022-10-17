package Demo36.backend;

import Demo36.frontend.CGrammarInitializer;

public class StatementExecutor extends BaseExecutor{
	 @Override 
	 public Object Execute(ICodeNode root) {
	    	executeChildren(root);
	    	
	    	return root;
	    }
}
