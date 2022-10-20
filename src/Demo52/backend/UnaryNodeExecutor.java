package Demo52.backend;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import Demo52.frontend.CGrammarInitializer;
import Demo52.frontend.CTokenType;
import Demo52.frontend.Declarator;
import Demo52.frontend.LRStateTableParser;
import Demo52.frontend.Symbol;

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
    		if (symbol != null) {
    			root.setAttribute(ICodeKey.VALUE, symbol.getValue());
        		root.setAttribute(ICodeKey.TEXT, symbol.getName());
    		}
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
			
			try {
				Declarator declarator = symbol.getDeclarator(Declarator.ARRAY);
				if (declarator != null) {
					Object val = declarator.getElement(index);
					root.setAttribute(ICodeKey.VALUE, val);
					ArrayValueSetter setter = new ArrayValueSetter(symbol, index);
					root.setAttribute(ICodeKey.SYMBOL, setter);
					root.setAttribute(ICodeKey.TEXT, symbol.getName());	
				}
				Declarator pointer = symbol.getDeclarator(Declarator.POINTER);
				if (pointer != null) {
					setPointerValue(root, symbol, index);
					//create a PointerSetter
					PointerValueSetter pv = new PointerValueSetter(symbol, index);
					root.setAttribute(ICodeKey.SYMBOL, pv);
					root.setAttribute(ICodeKey.TEXT, symbol.getName());	
				}
				
			}catch (Exception e) {
				System.err.println(e.getMessage());
				System.exit(1);
			}
    		break;
    		
        case CGrammarInitializer.Unary_Incop_TO_Unary:
        case CGrammarInitializer.Unary_DecOp_TO_Unary:
        	symbol = (Symbol)root.getChildren().get(0).getAttribute(ICodeKey.SYMBOL);
        	Integer val = (Integer)symbol.getValue();
        	IValueSetter setter;
    		setter = (IValueSetter)symbol;
    		try {
    			if (production == CGrammarInitializer.Unary_Incop_TO_Unary)
				    setter.setValue(val + 1);
    			else {
    				setter.setValue(val - 1);
    			}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("Runtime Error: Assign Value Error");
			}
    		break;
    		
        case CGrammarInitializer.LP_Expr_RP_TO_Unary:
        	child = root.getChildren().get(0);
        	copyChild(root, child);
        	break;
        
        case CGrammarInitializer.Start_Unary_TO_Unary:
        	child = root.getChildren().get(0); 
        	int addr = (Integer)child.getAttribute(ICodeKey.VALUE); //get mem addr
        	MemoryHeap memHeap = MemoryHeap.getInstance();
        	Map.Entry<Integer, byte[]> entry = memHeap.getMem(addr);
        	int offset = addr - entry.getKey();
        	if (entry != null) {
        	    byte[] memByte = entry.getValue();
        	    root.setAttribute(ICodeKey.VALUE, memByte[offset]);
        	}
        	
        	DirectMemValueSetter directMemSetter = new DirectMemValueSetter(addr);
        	root.setAttribute(ICodeKey.SYMBOL, directMemSetter);
        	break;
    		
        case CGrammarInitializer.Unary_LP_RP_TO_Unary:
        case CGrammarInitializer.Unary_LP_ARGS_RP_TO_Unary:
        	//先获得函数名
        	String funcName = (String)root.getChildren().get(0).getAttribute(ICodeKey.TEXT);
        	if (production == CGrammarInitializer.Unary_LP_ARGS_RP_TO_Unary) {
        		ICodeNode argsNode = root.getChildren().get(1);
        		ArrayList<Object> argList = (ArrayList<Object>)argsNode.getAttribute(ICodeKey.VALUE);
            	FunctionArgumentList.getFunctionArgumentList().setFuncArgList(argList);	
        	}
        	
        	//找到函数执行树头节点
        	ICodeNode func = CodeTreeBuilder.getCodeTreeBuilder().getFunctionNodeByName(funcName);
        	if (func != null) {
        		Executor executor = ExecutorFactory.getExecutorFactory().getExecutor(func);
        		executor.Execute(func);
        		Object returnVal = func.getAttribute(ICodeKey.VALUE);
            	if (returnVal != null) {
            		System.out.println("function call with name " + funcName + " has return value that is " + returnVal.toString());
            		root.setAttribute(ICodeKey.VALUE, returnVal);
            	}
        	} else {
        		ClibCall libCall = ClibCall.getInstance();
    			if (libCall.isAPICall(funcName)) {
    				Object obj = libCall.invokeAPI(funcName);
    				root.setAttribute(ICodeKey.VALUE, obj);
    			} 
        	}
			
        	break;
        	
    	}
    	
    	return root;
    }

	private void setPointerValue(ICodeNode root, Symbol symbol, int index) {
		MemoryHeap memHeap = MemoryHeap.getInstance();
		int addr = (Integer)symbol.getValue();
		Map.Entry<Integer, byte[]> entry = memHeap.getMem(addr);
		byte[] content = entry.getValue();
	    if (symbol.getByteSize() == 1) {
	    	root.setAttribute(ICodeKey.VALUE, content[index]);
	    } else {
	    	ByteBuffer buffer = ByteBuffer.allocate(4);
	    	buffer.put(content, index, 4);
	    	buffer.flip();
	    	root.setAttribute(ICodeKey.VALUE, buffer.getLong());
	    }
	}
}
