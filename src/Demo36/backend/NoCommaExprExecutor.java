package Demo36.backend;

import java.util.Collections;

import Demo36.frontend.CGrammarInitializer;
import Demo36.frontend.Symbol;

public class NoCommaExprExecutor extends BaseExecutor{
	ExecutorFactory factory = ExecutorFactory.getExecutorFactory();
	
    @Override 
    public Object Execute(ICodeNode root) {
    	executeChildren(root);
    	
    	int production = (int)root.getAttribute(ICodeKey.PRODUCTION);
    	Symbol symbol;
    	Object value;
    	ICodeNode child;
    	switch (production) {
    	case CGrammarInitializer.Binary_TO_NoCommaExpr: 
    		child = root.getChildren().get(0);
    		copyChild(root, child);
    		break;
    		
    	case CGrammarInitializer.NoCommaExpr_Equal_NoCommaExpr_TO_NoCommaExpr:
    		child = root.getChildren().get(0);
    		String text = (String)child.getAttribute(ICodeKey.TEXT);
    		symbol = (Symbol)child.getAttribute(ICodeKey.SYMBOL);
    		child = root.getChildren().get(1);
    		value = child.getAttribute(ICodeKey.VALUE);
    		symbol.setValue(value);
    		child = root.getChildren().get(0);
    		child.setAttribute(ICodeKey.VALUE, value);
    		copyChild(root, root.getChildren().get(0));
			System.out.println("Variable " + (String)root.getAttribute(ICodeKey.TEXT) + " is assigned to value of " + value.toString());
			
    		break;
    	}
    	
    	return root;
    }
}
