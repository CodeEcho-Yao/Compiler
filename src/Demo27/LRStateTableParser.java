package Demo27;

import utils.CompilerParser.Lexer;
import utils.CompilerParser.SymbolDefine;

import java.util.HashMap;
import java.util.Stack;


public class LRStateTableParser {
	private Lexer lexer;
	int    lexerInput = 0;
	String text = "";
	String[] names = new String[]{"t0", "t1", "t2", "t3", "t4", "t5", "t6",
			"t7"};
	private Object attributeForParentNode = null;
	private int curName = 0;
	private String new_name() {
		if (curName >= names.length) {
			System.out.println("expression too complicated");
			System.exit(1);
		}
		
		return names[curName++];
	}
	
	private void free_name(String s) {
		names[--curName] = s;
	}
	
	private Stack<Integer> statusStack = new Stack<Integer>();
	HashMap<Integer, HashMap<Integer, Integer>> lrStateTable = null;
    public LRStateTableParser(Lexer lexer) {
    	this.lexer = lexer;
    	statusStack.push(0);
    	lexer.advance();
    	lexerInput = lexer.lookAhead;
    	lrStateTable = GrammarStateManager.getGrammarManager().getLRStateTable();
    	
    	showCurrentStateInfo(0);
    }
    
    private Stack<Object> valueStack = new Stack<Object>();
    private Stack<Integer> parseStack = new Stack<Integer>();
    
    private void showCurrentStateInfo(int stateNum) {
    	System.out.println("current input is :" + SymbolDefine.getSymbolStr(lexerInput));
    	
    	System.out.println("current state is: ");
		GrammarState state = GrammarStateManager.getGrammarManager().getGrammarState(stateNum);
		state.print();
    }
    
    public void parse() {
    	/*
    	 * 0:  s -> e
    	 * 1:  e -> e + t
    	 * 2:  e -> t
    	 * 3:  t -> t * f
    	 * 4:  t -> f
    	 * 5:  f -> ( e )
    	 * 6:  f -> NUM
    	 */
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
    			
    			if (SymbolDefine.isSymbolTerminals(lexerInput)) {
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
        	//	System.out.println("reduce by product: ");
        		//product.print();
        		
        		switch(product.getProductionNum()) {
        		case 1:
        		case 3:
        			String topAttribute = (String)valueStack.get(valueStack.size()-1);
        			String secondAttribute = (String)valueStack.get(valueStack.size() - 3);
        			if (product.getProductionNum() == 1) {
        				System.out.println(secondAttribute + " += " + topAttribute);
        			}
        			else {
        				System.out.println(secondAttribute + " *= " + topAttribute);
        			}
        			
        			free_name(topAttribute);
        			attributeForParentNode = secondAttribute;
        			break;
        		case 2:
        		case 4:
        			attributeForParentNode = valueStack.peek();
        			break;
        		case 5:
        			attributeForParentNode = valueStack.get(valueStack.size() - 2);
        			break;
        		case 6:
        			String name = new_name();
        			System.out.println(name + " = " + text);
        			attributeForParentNode = name;
        			break;
               default:
            	   break;
        		}
        		
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
