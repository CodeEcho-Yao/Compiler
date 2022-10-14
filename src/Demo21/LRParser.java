package Demo21;

import Demo20.Lexer;
import Demo20.SymbolDefine;

import java.util.Stack;

public class LRParser {
	private Lexer lexer;
	LRStateMachine stateMachine = new LRStateMachine();
	int    lexerInput = 0;
	
	private Stack<Integer> parseStack = new Stack<Integer>();
    public LRParser(Lexer lexer) {
    	this.lexer = lexer;
    	parseStack.push(0);
    	lexer.advance();
    	lexerInput = lexer.lookAhead;
    }
    
    public void parse() {
    	LRStateMachine.STATE_MACHINE_ACTION action = stateMachine.getAction(parseStack.peek(), lexerInput);
    	while (action != LRStateMachine.STATE_MACHINE_ACTION.accept) {
    		
    		action = stateMachine.getAction(parseStack.peek(), lexerInput);
    		
    		if (action == LRStateMachine.STATE_MACHINE_ACTION.error) {
    			System.err.println("The input is denied");
    			return;
    		}
    		
    		if (action == LRStateMachine.STATE_MACHINE_ACTION.accept) {
    			System.out.println("The input can be accepted");
    			return;
    		}
    		
    		if (action == LRStateMachine.STATE_MACHINE_ACTION.s1) {
    			parseStack.push(1);
    			lexer.advance();
    			lexerInput = lexer.lookAhead;
    		}
    		if (action == LRStateMachine.STATE_MACHINE_ACTION.s2) {
    			parseStack.push(2);
    			lexer.advance();;
    			lexerInput = lexer.lookAhead;
    		}
    		
    		if (action == LRStateMachine.STATE_MACHINE_ACTION.s3) {
    			parseStack.push(3);
    			lexer.advance();;
    			lexerInput = lexer.lookAhead;
    		}
    		
    		if (action == LRStateMachine.STATE_MACHINE_ACTION.s4) {
    			parseStack.push(4);
    			lexer.advance();
    			lexerInput = lexer.lookAhead;
    		}
    		
    		if (action == LRStateMachine.STATE_MACHINE_ACTION.r1) {
    			parseStack.pop();
    			parseStack.pop();
    			parseStack.pop();
    			lexerInput = SymbolDefine.EXPR;
    		}
    		
    		if (action == LRStateMachine.STATE_MACHINE_ACTION.r2) {
    			parseStack.pop();
    			lexerInput = SymbolDefine.EXPR;
    		}
    		
    		if (action == LRStateMachine.STATE_MACHINE_ACTION.r3) {
    			parseStack.pop();
    			lexerInput = SymbolDefine.TERM;
    		}
    		
    		if (action == LRStateMachine.STATE_MACHINE_ACTION.state_2) {
    			parseStack.push(2);
    			lexerInput = lexer.lookAhead;
    		}
    		
    		if (action == LRStateMachine.STATE_MACHINE_ACTION.state_3) {
    			parseStack.push(3);
    			lexerInput = lexer.lookAhead;
    		}
    		
    		if (action == LRStateMachine.STATE_MACHINE_ACTION.state_5) {
    			parseStack.push(5);
    			lexerInput = lexer.lookAhead;
    		}
    	}
    }
}
