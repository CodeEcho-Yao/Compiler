package Demo27;

import utils.CompilerParser.Lexer;
import utils.CompilerParser.SymbolDefine;

import java.util.Stack;

public class LRParser {
	private Lexer lexer;
	LRStateMachine stateMachine = new LRStateMachine();
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
    public LRParser(Lexer lexer) {
    	this.lexer = lexer;
    	statusStack.push(0);
    	lexer.advance();
    	lexerInput = lexer.lookAhead;
    	
    }
    
    private Stack<Object> valueStack = new Stack<Object>();
    private Stack<Integer> parseStack = new Stack<Integer>();
    
    public void parse() {
    	LRStateMachine.STATE_MACHINE_ACTION action = stateMachine.getAction(statusStack.peek(), lexerInput);
    	while (action != LRStateMachine.STATE_MACHINE_ACTION.accept) {
    		
    		action = stateMachine.getAction(statusStack.peek(), lexerInput);
    		
    		if (action == LRStateMachine.STATE_MACHINE_ACTION.error) {
    			System.err.println("The input is denied");
    			return;
    		}
    		
    		if (action == LRStateMachine.STATE_MACHINE_ACTION.accept) {
    			System.out.println("The input can be accepted");
    			return;
    		}
    		
    		if (action == LRStateMachine.STATE_MACHINE_ACTION.s1) {
    			statusStack.push(1);
    			
    			text = lexer.yytext;
    			parseStack.push(lexerInput);
    			valueStack.push(null);
    			
    			lexer.advance();
    			lexerInput = lexer.lookAhead;
    		}
    		if (action == LRStateMachine.STATE_MACHINE_ACTION.s2) {
    			statusStack.push(2);
    			text = lexer.yytext;
    			
    			parseStack.push(lexerInput);
    			valueStack.push(null);
    			
    			lexer.advance();
    			lexerInput = lexer.lookAhead;
    		}
    		
    		if (action == LRStateMachine.STATE_MACHINE_ACTION.s3) {
    			statusStack.push(3);
    			text = lexer.yytext;
    			
    			parseStack.push(lexerInput);
    			valueStack.push(null);
    			
    			lexer.advance();;
    			lexerInput = lexer.lookAhead;
    			
    		}
    		
    		if (action == LRStateMachine.STATE_MACHINE_ACTION.s4) {
    			statusStack.push(4);
    			
    			parseStack.push(lexerInput);
    			valueStack.push(null);
    			
    			text = lexer.yytext;
    			lexer.advance();
    			lexerInput = lexer.lookAhead;
    			
    			
    		}
    		
    		if (action == LRStateMachine.STATE_MACHINE_ACTION.r1) {
    			//expr -> expr + term
    			
    			String topAttribute = (String)valueStack.get(valueStack.size() - 1);
    			String secondAttribute = (String)valueStack.get(valueStack.size() - 3);
    			System.out.println(secondAttribute + " += " + topAttribute);
    			free_name(topAttribute);
    			
    			statusStack.pop();
    			statusStack.pop();
    			statusStack.pop();
    			
    			parseStack.pop();
    			valueStack.pop();
    			parseStack.pop();
    			valueStack.pop();
    			parseStack.pop();
    			valueStack.pop();
    			
    			lexerInput = SymbolDefine.EXPR;
    			attributeForParentNode = secondAttribute;
    			parseStack.push(lexerInput);
    			valueStack.push(attributeForParentNode);
    		}
    		
    		if (action == LRStateMachine.STATE_MACHINE_ACTION.r2) {
    			//expr -> term
    			
    			statusStack.pop();	
    			
    			parseStack.pop();
    			attributeForParentNode = valueStack.pop();
    			
    			lexerInput = SymbolDefine.EXPR;
    			
    			parseStack.push(lexerInput);
    			valueStack.push(attributeForParentNode);
    		}
    		
    		if (action == LRStateMachine.STATE_MACHINE_ACTION.r3) {
    			//term -> num
    			
    			statusStack.pop();
    			
    			lexerInput = SymbolDefine.TERM;
    			String name = new_name();
    			System.out.println(name + " = " + text);
    			attributeForParentNode = name;
    			
    			parseStack.pop();
    			valueStack.pop();
    			parseStack.push(lexerInput);
    			valueStack.push(attributeForParentNode);
    			
    		}
    		
    		if (action == LRStateMachine.STATE_MACHINE_ACTION.state_2) {
    			statusStack.push(2);
    			lexerInput = lexer.lookAhead;
    		}
    		
    		if (action == LRStateMachine.STATE_MACHINE_ACTION.state_3) {
    			statusStack.push(3);
    			lexerInput = lexer.lookAhead;
    		}
    		
    		if (action == LRStateMachine.STATE_MACHINE_ACTION.state_5) {
    			statusStack.push(5);
    			lexerInput = lexer.lookAhead;
    		}
    	}
    }
}
