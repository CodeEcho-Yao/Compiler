package Demo28;

import java.util.HashMap;
import java.util.Stack;


public class LRStateTableParser {
	private Lexer lexer;
	int    lexerInput = 0;
	String text = "";

	private Object attributeForParentNode = null;
	
	private Stack<Integer> statusStack = new Stack<Integer>();
	HashMap<Integer, HashMap<Integer, Integer>> lrStateTable = null;
    public LRStateTableParser(Lexer lexer) {
    	this.lexer = lexer;
    	statusStack.push(0);
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
        	//		valueStack.push(null);
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
        		
        	
        		
        		int rightSize = product.getRight().size();
        		while (rightSize > 0) {
        			parseStack.pop();
        		//	valueStack.pop();
        			statusStack.pop();
        			rightSize--;
        		}
        		
        		lexerInput = product.getLeft();
    			parseStack.push(lexerInput);
    		//	valueStack.push(attributeForParentNode);
        	}
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
