package Test02.backend;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Stack;

import Test02.backend.Compiler.Directive;
import Test02.backend.Compiler.Instruction;
import Test02.backend.Compiler.ProgramGenerator;
import Test02.frontend.CGrammarInitializer;
import Test02.frontend.CTokenType;
import Test02.frontend.Declarator;
import Test02.frontend.LRStateTableParser;
import Test02.frontend.Symbol;

public class UnaryNodeExecutor extends BaseExecutor implements IExecutorReceiver{
	private Symbol structObjSymbol = null;
	private Symbol monitorSymbol = null;
	
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
    		ProgramGenerator.getInstance().emit(Instruction.SIPUSH, value.toString());
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
        	symbol = (Symbol)child.getAttribute(ICodeKey.SYMBOL);
        	        	
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
        		ArrayList<Object> symList = (ArrayList<Object>)argsNode.getAttribute(ICodeKey.SYMBOL);
            	FunctionArgumentList.getFunctionArgumentList().setFuncArgList(argList);	
            	FunctionArgumentList.getFunctionArgumentList().setFuncArgSymbolList(symList);
        	}
        	
        	//找到函数执行树头节点
        	ICodeNode func = CodeTreeBuilder.getCodeTreeBuilder().getFunctionNodeByName(funcName);
        	if (func != null) {
        		Executor executor = ExecutorFactory.getExecutorFactory().getExecutor(func);
        		ProgramGenerator.getInstance().setInstructionBuffered(true);
        		executor.Execute(func);
        		ProgramGenerator.getInstance().emit(Instruction.RETURN);
        		ProgramGenerator.getInstance().emitDirective(Directive.END_METHOD);
        		ProgramGenerator.getInstance().setInstructionBuffered(false);
        		compileFunctionCall(funcName);
        		
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
        	
        case CGrammarInitializer.Unary_StructOP_Name_TO_Unary:
        	child = root.getChildren().get(0);
        	String fieldName = (String)root.getAttribute(ICodeKey.TEXT);
    		symbol = (Symbol)child.getAttribute(ICodeKey.SYMBOL);
    		
    		if (isSymbolStructPointer(symbol)) {
    			copyBetweenStructAndMem(symbol, false);
    		}
    		
    		Symbol args = symbol.getArgList();
    		while (args != null) {
    			if (args.getName().equals(fieldName)) {
    				break;
    			}
    			
    			args = args.getNextSymbol();
    		}
    		
    		if (args == null) {
    			System.err.println("access a filed not in struct object!");
    			System.exit(1);
    		}
    		
    		root.setAttribute(ICodeKey.SYMBOL, args);
    		root.setAttribute(ICodeKey.VALUE, args.getValue());
    		
    		if (isSymbolStructPointer(symbol) == true) {
    			checkValidPointer(symbol);
    			structObjSymbol = symbol;
    			monitorSymbol = args;
    			
    			ExecutorBrocasterImpl.getInstance().registerReceiverForAfterExe(this);
    		} else {
    			structObjSymbol = null;
    		}
    		
        	break;
        	
    	}
    	
    	return root;
    }
	
	private void compileFunctionCall(String funcName) {
		ProgramGenerator generator = ProgramGenerator.getInstance();
		String declaration = generator.getDeclarationByName(funcName);
		String call = generator.getProgramName() + "/" + declaration;
		generator.emit(Instruction.INVOKESTATIC, call);
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
	    	int v = buffer.getInt();
	    	root.setAttribute(ICodeKey.VALUE, v);
	    }
	}
	
	private boolean isSymbolStructPointer(Symbol symbol) {
		if (symbol.getDeclarator(Declarator.POINTER) != null && symbol.getArgList() != null) {
			return true;
		}
		
		return false;
	}
	
	private void checkValidPointer(Symbol symbol) {
		if (symbol.getDeclarator(Declarator.POINTER) != null && symbol.getValue() == null) {
			System.err.println("Aceess Empty Pointer");
			System.exit(1);
		}
	}
	
	@Override
	public void handleExecutorMessage(ICodeNode code) {
		int productNum = (Integer)code.getAttribute(ICodeKey.PRODUCTION);
		if (productNum != CGrammarInitializer.NoCommaExpr_Equal_NoCommaExpr_TO_NoCommaExpr) {
			return ;
		}
		
		Object object = code.getAttribute(ICodeKey.SYMBOL);
		
		if(object == null) {
			return;
		}
		
       Symbol symbol = null; 
	   if (object instanceof IValueSetter) {
			symbol = ((IValueSetter)object).getSymbol();
		} else {
			symbol = (Symbol)object;
		}
        
        
		if (symbol == monitorSymbol) {
			System.out.println("UnaryNodeExecutor receive msg for assign execution");
			copyBetweenStructAndMem(structObjSymbol, true);
		}
	}
	
	private void copyBetweenStructAndMem(Symbol symbol, boolean isFromStructToMem) {
		Integer addr = (Integer)symbol.getValue();
		MemoryHeap memHeap = MemoryHeap.getInstance();
    	Map.Entry<Integer, byte[]> entry = memHeap.getMem(addr);
    	byte[] mems = entry.getValue();
    	Stack<Symbol> stack = reverseStructSymbolList(symbol);
    	int offset = 0;
    	
    	while (stack.empty() != true) {
    		Symbol sym = stack.pop(); 
    		
    		try {
    			if (isFromStructToMem == true) {
    				offset += writeStructVariablesToMem(sym, mems, offset);	
    			} else {
    				offset += writeMemToStructVariables(sym, mems, offset);
    			}
				
			} catch (Exception e) {
		        System.err.println("error when copyin struct variables to memory");
				e.printStackTrace();
			}
    	}
	}
	
	private int writeStructVariablesToMem(Symbol symbol, byte[] mem, int offset) throws Exception{
		if (symbol.getArgList() != null) {
			 return writeStructVariablesToMem(symbol, mem, offset);
		}
		
		int sz = symbol.getByteSize();
		if (symbol.getValue() == null && symbol.getDeclarator(Declarator.ARRAY) == null) {
			return sz;
		}
		
		if (symbol.getDeclarator(Declarator.ARRAY) == null) {
			Integer val =  (Integer)symbol.getValue();
			byte[] bytes = ByteBuffer.allocate(4).putInt(val).array();
			for (int i = 3; i >= 4 - sz; i--) {
				mem[offset + 3 - i] = bytes[i];
			}
			
			return sz;
		} else {
			return copyArrayVariableToMem(symbol, mem, offset);
		}
	}
	
	
	private int writeMemToStructVariables(Symbol symbol, byte[] mem, int offset) throws Exception {
		if (symbol.getArgList() != null) {
			//struct variable, copy mem to struct recursively
			return writeMemToStructVariables(symbol, mem, offset);
		}
		
		int sz = symbol.getByteSize();
		int val = 0;
		if (symbol.getDeclarator(Declarator.ARRAY) == null) {
			val = fromByteArrayToInteger(mem, offset, sz);
			symbol.setValue(val);
		} else {
			return copyMemToArrayVariable(symbol, mem, offset);
		}
		
		return sz;
	}
	
	private int fromByteArrayToInteger(byte[] mem, int offset, int sz) {
		int val = 0;
		switch (sz) {
		case 1:
			val = mem[offset];
			break;
		case 2:
			val = (mem[offset + 1] << 8 | mem[offset]);
			break;
		case 4:
			val = (mem[offset+3] << 24 | mem[offset+2] << 16 | mem[offset + 1] << 8 | 
					mem[offset]);
			break;
		}
		
		return val;
	}
	
	private int copyMemToArrayVariable(Symbol symbol, byte[] mem, int offset) {
		int sz = symbol.getByteSize();
		Declarator declarator = symbol.getDeclarator(Declarator.ARRAY);
		if (declarator == null) {
			return 0;
		}
		
		int size = 0;
		int elemCount = declarator.getElementNum();
		for (int i = 0; i < elemCount; i++) {
			int val = fromByteArrayToInteger(mem, offset + size, sz);
			size += sz;
			try {
				declarator.addElement(i, val);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return size;
	}
	
	private int copyArrayVariableToMem(Symbol symbol, byte[] mem, int offset) {
		Declarator declarator = symbol.getDeclarator(Declarator.ARRAY);
		if (declarator == null) {
			return 0;
		}
		
		int sz = symbol.getByteSize();
		int elemCount = declarator.getElementNum();
		for (int i = 0; i < elemCount; i++) {
			try {
				Integer val = (Integer)declarator.getElement(i);
				byte[] bytes = ByteBuffer.allocate(sz).putInt(val).array();
				for (int j = 0; j < sz; j++) {
					mem[offset + j] = bytes[j];
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return sz* elemCount;
	}
	
	private Stack<Symbol>reverseStructSymbolList(Symbol symbol) {
		Stack<Symbol> stack = new Stack<Symbol>();
		Symbol sym = symbol.getArgList();
		while (sym != null) {
			stack.push(sym);
			sym = sym.getNextSymbol();
		}
		
		return stack;
	}

	
}
