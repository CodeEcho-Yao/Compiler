package Demo29;

import java.util.HashMap;
import java.util.Stack;


public class LRStateTableParser {
	private Lexer lexer;
	int    lexerInput = 0;
	int    nestingLevel = 0;
	String text = "";

	private Object attributeForParentNode = null;
	private TypeSystem typeSystem = new TypeSystem();
	private Stack<Integer> statusStack = new Stack<Integer>();
	HashMap<Integer, HashMap<Integer, Integer>> lrStateTable = null;
    public LRStateTableParser(Lexer lexer) {
    	this.lexer = lexer;
    	statusStack.push(0);
    	valueStack.push(null);
    	lexer.advance();
    	lexerInput = CTokenType.EXT_DEF_LIST.ordinal();
    	lrStateTable = GrammarStateManager.getGrammarManager().getLRStateTable();
    }
    
    private Stack<Object> valueStack = new Stack<Object>();
    private Stack<Integer> parseStack = new Stack<Integer>();
    
    private void showCurrentStateInfo(int stateNum) {
    	System.out.println("current input is :" + CTokenType.getSymbolStr(lexerInput));
    	
    	System.out.println("current state is: ");
		GrammarState state = GrammarStateManager.getGrammarManager().getGrammarState(stateNum);
		state.print();
    }
    
    public void parse() {
    
        while (true) {
        	
        	Integer action = getAction(statusStack.peek(), lexerInput);
        	
        	if (action == null) {
        		//解析出错
        		System.err.println("The input is denied");
    			return;
        	}
        	
        	if (action > 0) {
        		//showCurrentStateInfo(action);
        		
        		//shift 操作
                statusStack.push(action);
    			text = lexer.yytext;
    			
    			parseStack.push(lexerInput);
    			
    			if (CTokenType.isTerminal(lexerInput)) {
    				System.out.println("Shift for input: " + CTokenType.values()[lexerInput].toString());
    				
    				lexer.advance();
        			lexerInput = lexer.lookAhead;
        			valueStack.push(null);
    			} else {
    				lexerInput = lexer.lookAhead;
    			}
    			
        	} else {
        		if (action == 0) {
        			System.out.println("The input can be accepted");
        			return;
        		}
        		
        		int reduceProduction = - action;
        		Production product = ProductionManager.getProductionManager().getProductionByIndex(reduceProduction);
        		System.out.println("reduce by product: ");
        		product.print();
        		
        		takeActionForReduce(reduceProduction);
        	
        		
        		int rightSize = product.getRight().size();
        		while (rightSize > 0) {
        			parseStack.pop();
        			valueStack.pop();
        			statusStack.pop();
        			rightSize--;
        		}
        		
        		lexerInput = product.getLeft();
    			parseStack.push(lexerInput);
    			valueStack.push(attributeForParentNode);
        	}
        }
    }
    
    private void takeActionForReduce(int productNum) {
    	switch(productNum) {
    	case CGrammarInitializer.TYPE_TO_TYPE_SPECIFIER:
    		attributeForParentNode = typeSystem.newType(text);
    		break;
    	case CGrammarInitializer.CLASS_TO_TypeOrClass:
    		attributeForParentNode = typeSystem.newClass(text);
    		break;
    	case CGrammarInitializer.SPECIFIERS_TypeOrClass_TO_SPECIFIERS:
    		attributeForParentNode = valueStack.peek();
    		Specifier last = (Specifier)((TypeLink)valueStack.get(valueStack.size() - 2)).getTypeObject();
    		Specifier dst = (Specifier)((TypeLink)attributeForParentNode).getTypeObject();
    		typeSystem.specifierCpy(dst, last);
    		break;
    	case CGrammarInitializer.NAME_TO_NewName:
    		attributeForParentNode = typeSystem.newSymbol(text, nestingLevel);
    		break;
    	case CGrammarInitializer.START_VarDecl_TO_VarDecl:
    		typeSystem.addDeclarator((Symbol)attributeForParentNode, Declarator.POINTER);
    		break;
    	case CGrammarInitializer.ExtDeclList_COMMA_ExtDecl_TO_ExtDeclList:
    		Symbol currentSym = (Symbol)attributeForParentNode;
    		Symbol lastSym = (Symbol)valueStack.get(valueStack.size() - 3);
    		currentSym.setNextSymbol(lastSym);
    		break;
    	case CGrammarInitializer.OptSpecifier_ExtDeclList_Semi_TO_ExtDef:
    		Symbol symbol = (Symbol)attributeForParentNode;
    		TypeLink specifier = (TypeLink)(valueStack.get(valueStack.size() - 3));
    		typeSystem.addSpecifierToDeclaration(specifier, symbol);
    		typeSystem.addSymbolsToTable(symbol);
    		break;
    		
    	}
    }
    
    private Integer getAction(Integer currentState, Integer currentInput) {
    	HashMap<Integer, Integer> jump = lrStateTable.get(currentState);
    	if (jump != null) {
    		Integer next = jump.get(currentInput);
    		if (next != null) {
    			return next;
    		}
    	}
    	
    	return null;
    }
    
}
