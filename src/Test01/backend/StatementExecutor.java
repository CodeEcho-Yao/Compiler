package Test01.backend;

import java.util.Collections;

import Test01.frontend.CGrammarInitializer;

public class StatementExecutor extends BaseExecutor{
	private enum LoopType {
		FOR,
		WHILE,
		DO_WHILE
	};
	
	 @Override 
	 public Object Execute(ICodeNode root) {
		 int production = (int)root.getAttribute(ICodeKey.PRODUCTION);
		 ICodeNode node;
		 
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
			 
		 case CGrammarInitializer.While_LP_Test_Rp_TO_Statement:
			 while (isLoopContinute(root, LoopType.WHILE)) {
				 executeChild(root, 1);
			 }
			 break;
			 
		 case CGrammarInitializer.Do_Statement_While_Test_To_Statement:
			 do {
				 executeChild(root, 0);
			 } while(isLoopContinute(root, LoopType.DO_WHILE));
			 
			 break;
			 
		 case  CGrammarInitializer.Return_Semi_TO_Statement:
			 isContinueExecution(false);
			 
			 break;
			 
		 case  CGrammarInitializer.Return_Expr_Semi_TO_Statement:
			 node = executeChild(root, 0);
			 Object obj = node.getAttribute(ICodeKey.VALUE);
			 setReturnObj(obj);
			 isContinueExecution(false);
			 
			 break;
			 
		 default:
			 executeChildren(root);
			
			 break;
		 }
		 
	     return root;
	 }
	 
	 private boolean isLoopContinute(ICodeNode root, LoopType type) {
		 ICodeNode res = null;
		 if (type == LoopType.FOR || type == LoopType.DO_WHILE) {
			 res = executeChild(root, 1);
		 }
		 else if (type == LoopType.WHILE) {
			 res = executeChild(root, 0);
		 }
		 
		 int result = (Integer)res.getAttribute(ICodeKey.VALUE);
		 return res != null && result != 0;
		 
	 }
}
