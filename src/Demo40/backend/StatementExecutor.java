package Demo40.backend;

public class StatementExecutor extends BaseExecutor{
	 @Override 
	 public Object Execute(ICodeNode root) {
		 executeChildren(root);	
	     return root;
	 }
}
