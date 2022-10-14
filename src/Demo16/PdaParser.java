package Demo16;

import java.util.Stack;


public class PdaParser {
    enum Grammar {
    	STMT,
    	EXPR,
    	EXPR_PRIME,
    	TERM,
    	TERM_PRIME,
    	FACTOR,
    	NUM_OR_ID,
    	PLUS,
    	SEMI,
    	MULTIPLE,
    	LEFT_PARENT,
    	RIGHT_PARENT
    };
    
    private Lexer lexer;
    private Stack<Grammar> pdaStack = new Stack<Grammar>();
  
    
    public  PdaParser(Lexer lexer) {
    	this.lexer = lexer;
    	pdaStack.push(Grammar.STMT);
    }
    
    public void parse() {
    	while (pdaStack.empty() == false) {
    		Grammar action = pdaStack.peek();
    		
    		switch (action) {
    		case STMT:
    			if (lexer.match(Lexer.EOI)) {
    				pdaStack.pop();
    			}
    			else {
    				pdaStack.pop();
    				pdaStack.push(Grammar.STMT);
    				pdaStack.push(Grammar.SEMI);
    				pdaStack.push(Grammar.EXPR);
    			}
    			
    			break;
    		case EXPR:
    			pdaStack.pop();
    			pdaStack.push(Grammar.EXPR_PRIME);
    			pdaStack.push(Grammar.TERM);
    			break;
    		case TERM:
    			pdaStack.pop();
    			pdaStack.push(Grammar.TERM_PRIME);
    			pdaStack.push(Grammar.FACTOR);
    			break;
    		case TERM_PRIME:
    			pdaStack.pop();
    			if (lexer.match(Lexer.TIMES)) {
    				pdaStack.push(Grammar.TERM_PRIME);
    				pdaStack.push(Grammar.FACTOR);
    				pdaStack.push(Grammar.MULTIPLE);
    			}
    			break;
    		case FACTOR:
    			pdaStack.pop();
    			if (lexer.match(Lexer.NUM_OR_ID)) {
    				pdaStack.push(Grammar.NUM_OR_ID);
    			}
    			else if (lexer.match(Lexer.LP)) {
    				pdaStack.push(Grammar.RIGHT_PARENT);
    				pdaStack.push(Grammar.EXPR);
    				pdaStack.push(Grammar.LEFT_PARENT);
    			}
    			else {
    				parseError();
    			}
    			break;
    		case EXPR_PRIME:
    			pdaStack.pop();
    			if (lexer.match(Lexer.PLUS)) {
    				pdaStack.push(Grammar.EXPR_PRIME);
    				pdaStack.push(Grammar.TERM);
    				pdaStack.push(Grammar.PLUS);
    			}
    			break;
    		case NUM_OR_ID:
    			pdaStack.pop();
    			if (lexer.match(Lexer.NUM_OR_ID) == false) {
    				parseError();
    			}		
    			lexer.advance();
    			break;
    		case PLUS:
    			pdaStack.pop();
    			if (lexer.match(Lexer.PLUS) == false) {
    				parseError();
    			}
    			lexer.advance();
    			break;
    		case MULTIPLE:
    			pdaStack.pop();
    			if (lexer.match(Lexer.TIMES) == false) {
    				parseError();
    			}
    			lexer.advance();
    			break;
    		case LEFT_PARENT:
    			pdaStack.pop();
    			if (lexer.match(Lexer.LP) == false) {
    				parseError();
    			}
    			lexer.advance();
    			break;
    		case RIGHT_PARENT:
    			pdaStack.pop();
    			if (lexer.match(Lexer.RP) == false) {
    				parseError();
    			}
    			lexer.advance();
    			break;
    		case SEMI:
    			pdaStack.pop();
    			if (lexer.match(Lexer.SEMI) == false) {
    				parseError();
    			}
    			lexer.advance();
    			break;
    		}
    		
    	}
    	
      }
    
    private void parseError() {
		System.err.println("PDA parse error");
		System.exit(1);
    }
}
