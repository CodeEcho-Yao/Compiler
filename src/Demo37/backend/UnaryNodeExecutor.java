package Demo37.backend;

import Demo37.frontend.CGrammarInitializer;
import Demo37.frontend.CTokenType;
import Demo37.frontend.Declarator;
import Demo37.frontend.LRStateTableParser;
import Demo37.frontend.Symbol;

public class UnaryNodeExecutor extends BaseExecutor{
	
	@Override
    public Object Execute(ICodeNode root) {
		executeChildren(root);
		
    	int production = (Integer)root.getAttribute(ICodeKey.PRODUCTION); 
    	String text ;
    	Symbol symbol;
    	Object value;
    	ICodeNode child;
    	
    	switch (production) {
    	case CGrammarInitializer.Number_TO_Unary:
    		text = (String)root.getAttribute(ICodeKey.TEXT);
    		boolean isFloat = text.indexOf('.') != -1;
    		if (isFloat) {
    			value = Float.valueOf(text);
    			root.setAttribute(ICodeKey.VALUE, Float.valueOf(text));	
    		} else {
    			value = Integer.valueOf(text);
    			root.setAttribute(ICodeKey.VALUE, Integer.valueOf(text));
    		}
    		
    		break;
    		
    	
    		
    	case CGrammarInitializer.Name_TO_Unary:
    		symbol = (Symbol)root.getAttribute(ICodeKey.SYMBOL);
    		root.setAttribute(ICodeKey.VALUE, symbol.getValue());
    		root.setAttribute(ICodeKey.TEXT, symbol.getName());
    		break;
    		
    	case CGrammarInitializer.String_TO_Unary:
    		text = (String)root.getAttribute(ICodeKey.TEXT);
    		//symbol = (Symbol)root.getAttribute(ICodeKey.SYMBOL);
    		root.setAttribute(ICodeKey.VALUE, text);
    		//root.setAttribute(ICodeKey.TEXT, symbol.getName());
    		break;
    		
    	case CGrammarInitializer.Unary_LB_Expr_RB_TO_Unary:
    		child = root.getChildren().get(0);
    		symbol = (Symbol)child.getAttribute(ICodeKey.SYMBOL);
    		child = root.getChildren().get(1);
			int index = (Integer)child.getAttribute(ICodeKey.VALUE);
			
			Declarator declarator = symbol.getDeclarator(Declarator.ARRAY);
			try {
				Object val = declarator.getElement(index);
				root.setAttribute(ICodeKey.VALUE, val);
				ArrayValueSetter setter = new ArrayValueSetter(symbol, index);
				root.setAttribute(ICodeKey.SYMBOL, setter);
				root.setAttribute(ICodeKey.TEXT, symbol.getName());
			}catch (Exception e) {
				System.err.println(e.getMessage());
				System.exit(1);
			}
    		break;
    	}
    	
    	return root;
    }

	
}
