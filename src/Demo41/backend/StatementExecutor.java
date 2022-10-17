package Demo41.backend;

import java.util.Collections;

import Demo41.frontend.CGrammarInitializer;

public class StatementExecutor extends BaseExecutor{
	private enum LoopType {
		FOR,
		WHILE,
		DO_WHILE
	};
	
	 @Override 
	 public Object Execute(ICodeNode root) {
		 int production = (int)root.getAttribute(ICodeKey.PRODUCTION);
		 
		 switch (production) {
		 case CGrammarInitializer.FOR_OptExpr_Test_EndOptExpr_Statement_TO_Statement:
			 //execute OptExpr
			 executeChild(root, 0);

			 while( isLoopContinute(root, LoopType.FOR) ) {
				 //execute statment in for body
				 executeChild(root, 3);
				 
				 //execute EndOptExpr
				 executeChild(root, 2); 
			 }
			 
			 break;
		 default:
			 executeChildren(root);
			
			 break;
		 }
		 
	     return root;
	 }
	 
	 private boolean isLoopContinute(ICodeNode root, LoopType type) {
		 ICodeNode res = null;
		 if (type == LoopType.FOR) {
			 res = executeChild(root, 1);
			 int result = (Integer)res.getAttribute(ICodeKey.VALUE);
			 return result != 0;
		 }
		 
		 return false;
	 }
}
