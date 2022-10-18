package Demo46.backend;


public class IfStatementExecutor extends BaseExecutor {

	 @Override 
	 public Object Execute(ICodeNode root) {
		
    	 ICodeNode res = executeChild(root, 0); 
    	 Integer val = (Integer)res.getAttribute(ICodeKey.VALUE);
    	 copyChild(root, res);  
    	 
    	 if (val != null && val != 0) {
    		 executeChild(root, 1);
    	 }
	    	
	    	return root;
	    }

}
