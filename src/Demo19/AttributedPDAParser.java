package Demo19;

import java.util.Stack;



public class AttributedPDAParser {
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
    	RIGHT_PARENT,
    	ACTION_0,   //{$1=$2=getname();}
    	ACTION_1,  //{freename($0);}
    	ACTION_2, //{System.out.println($$ + " += " + $0); freename($0);}
    	ACTION_3, //{System.out.println($$ + " *= " + $0); freename($0);}
    	ACTION_4  //{System.out.println($$ + " = " + lexer.yytext);}
    };
    
    private Lexer lexer;
    private Stack<Grammar> pdaStack = new Stack<Grammar>();
    private Stack<Attribute> valueStack = new Stack<Attribute>();
    private Attribute parentAttribute = null;
    
    private String[] names = null;
   	private int nameIdx = 0;
   	    
    private String getname() {
       	return names[nameIdx++];
    }
   	    
    private void freename(String name ) {
       	nameIdx--;
       	if (nameIdx >= 0) {
       		names[nameIdx] = name;	
       	}
    }
  
    
    public  AttributedPDAParser(Lexer lexer) {
    	this.lexer = lexer;
    	names = new String[]{"t0", "t1", "t2", "t3", "t4", "t5", "t6"};
    	parentAttribute = Attribute.getAttribute(null);
    	pushGrammarSymbol(Grammar.STMT);
    }
    
    
    public void parse() {
    	while (pdaStack.empty() == false) {
    		Grammar action = pdaStack.peek();
    		
    		switch (action) {
    		case STMT:
    			if (lexer.match(Lexer.EOI)) {
    				popStacks();
    			}
    			else {
    				popStacks();
    				pushGrammarSymbol(Grammar.STMT);
    				pushGrammarSymbol(Grammar.SEMI);
    				pushGrammarSymbol(Grammar.ACTION_1);
    				pushGrammarSymbol(Grammar.EXPR);
    				pushGrammarSymbol(Grammar.ACTION_0);
    			}
    			
    			break;
    		case EXPR:
    			popStacks();
    			pushGrammarSymbol(Grammar.EXPR_PRIME);
    			pushGrammarSymbol(Grammar.TERM);
    			break;
    		case TERM:
    			popStacks();
    			pushGrammarSymbol(Grammar.TERM_PRIME);
    			pushGrammarSymbol(Grammar.FACTOR);
    			break;
    		case TERM_PRIME:
    			popStacks();
    			if (lexer.match(Lexer.TIMES)) {
    				pushGrammarSymbol(Grammar.TERM_PRIME);
    				pushGrammarSymbol(Grammar.ACTION_3);
    				pushGrammarSymbol(Grammar.FACTOR);
    				pushGrammarSymbol(Grammar.ACTION_0);
    				pushGrammarSymbol(Grammar.MULTIPLE);
    			}
    			break;
    		case FACTOR:
    			popStacks();
    			if (lexer.match(Lexer.NUM_OR_ID)) {
    				pushGrammarSymbol(Grammar.ACTION_4);
    				pushGrammarSymbol(Grammar.NUM_OR_ID);
    			}
    			else if (lexer.match(Lexer.LP)) {
    				pushGrammarSymbol(Grammar.RIGHT_PARENT);
    				pushGrammarSymbol(Grammar.EXPR);
    				pushGrammarSymbol(Grammar.LEFT_PARENT);
    			}
    			else {
    				parseError();
    			}
    			break;
    		case EXPR_PRIME:
    			popStacks();
    			if (lexer.match(Lexer.PLUS)) {
    				pushGrammarSymbol(Grammar.EXPR_PRIME);
    				pushGrammarSymbol(Grammar.ACTION_2);
    				pushGrammarSymbol(Grammar.TERM);
    				pushGrammarSymbol(Grammar.ACTION_0);
    				pushGrammarSymbol(Grammar.PLUS);
    			}
    			break;
    		case NUM_OR_ID:
    			popStacks();
    			if (lexer.match(Lexer.NUM_OR_ID) == false) {
    				parseError();
    			}		
    			
    			break;
    		case PLUS:
    			popStacks();
    			if (lexer.match(Lexer.PLUS) == false) {
    				parseError();
    			}
    			lexer.advance();
    			break;
    		case MULTIPLE:
    			popStacks();
    			if (lexer.match(Lexer.TIMES) == false) {
    				parseError();
    			}
    			lexer.advance();
    			break;
    		case LEFT_PARENT:
    			popStacks();
    			if (lexer.match(Lexer.LP) == false) {
    				parseError();
    			}
    			lexer.advance();
    			break;
    		case RIGHT_PARENT:
    			//pdaStack.pop(); 1+(2*3)
    			popStacks();
    			if (lexer.match(Lexer.RP) == false) {
    				parseError();
    			}
    			lexer.advance();
    			break;
    		case SEMI:
    			popStacks();
    			if (lexer.match(Lexer.SEMI) == false) {
    				parseError();
    			}
    			lexer.advance();
    			break;
    			
    		case ACTION_0:
    			pdaStack.pop();
    			String t = getname();
    			int curPos = valueStack.size() - 1;
    			System.out.println("value stack grammar: " + valueStack.get(curPos - 1).getGrammar());
    			valueStack.get(curPos - 1).right = new String();
    			valueStack.get(curPos - 1).right = t;
    			
    			System.out.println("value stack grammar: " + valueStack.get(curPos - 2).getGrammar());
    			valueStack.get(curPos - 2).right = new String();
    			valueStack.get(curPos - 2).right = t;
    			valueStack.pop();
    			break;
    		case ACTION_1:
    			pdaStack.pop();
    			String attribute = (String)valueStack.pop().right;
    			freename(attribute);
    			break;
    		case ACTION_2:
    			pdaStack.pop();
    			Attribute curAttribute = valueStack.pop();
    			String  parentAttribute = (String)curAttribute.left;
    			String  childAttribute = (String)curAttribute.right;
    			System.out.println(parentAttribute + " += " + childAttribute);
    			freename(childAttribute);
    			break;
    		case ACTION_3:
    			pdaStack.pop();
    			curAttribute = valueStack.pop();
    			System.out.println((String)curAttribute.left + " *= " + curAttribute.right);
    			break;
    		case ACTION_4:
    			pdaStack.pop();
    			curAttribute = valueStack.pop();
    			System.out.println((String)curAttribute.left + " = " + lexer.yytext);
    			lexer.advance();
    			break;
    		}
    		
    	}
    	
      }
    
    private void popStacks() {
    	pdaStack.pop();
    	parentAttribute = valueStack.pop();
    }
    
    private void pushGrammarSymbol(Grammar grammar) {
    	pdaStack.push(grammar);
    	Attribute attr = Attribute.getAttribute(parentAttribute.right);
        attr.setGrammar(grammar.toString());
    	valueStack.push(attr);
    }
    
    private void parseError() {
		System.err.println("PDA parse error");
		System.exit(1);
    }
}
