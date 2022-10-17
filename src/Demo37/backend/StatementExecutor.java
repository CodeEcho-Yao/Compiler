package Demo37.backend;

import Demo37.frontend.CGrammarInitializer;

public class StatementExecutor extends BaseExecutor{
	 @Override 
	 public Object Execute(ICodeNode root) {
	    	executeChildren(root);
	    	
	    	return root;
	    }
}
