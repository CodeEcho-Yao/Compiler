package Demo17;

import java.util.Stack;


public class TopdownPaserWithParseTable {
    private Lexer lexer;
    ParseTable parseTable = new ParseTable();
    
    private Stack<Integer> pdaStack = new Stack<Integer>();
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
  
   
    public  TopdownPaserWithParseTable(Lexer lexer) {
    	this.lexer = lexer;
    	names = new String[]{"t0", "t1", "t2", "t3", "t4", "t5", "t6"};
    	parentAttribute = Attribute.getAttribute(null);
    	pushGrammarSymbol(SymbolDefine.STMT);
    	lexer.advance();
    }
    
    
    public void parse() {
    	while (pdaStack.empty() == false) {
    		Integer symbol = pdaStack.peek();
    		
    		switch (symbol) {
    		
    		case SymbolDefine.ACTION_0:
    			pdaStack.pop();
    			String t = getname();
    			int curPos = valueStack.size() - 1;
    			
    			valueStack.get(curPos - 1).right = new String();
    			valueStack.get(curPos - 1).right = t;
    			
    			
    			valueStack.get(curPos - 2).right = new String();
    			valueStack.get(curPos - 2).right = t;
    			valueStack.pop();
    			break;
    		case SymbolDefine.ACTION_1:
    			pdaStack.pop();
    			String attribute = (String)valueStack.pop().right;
    			freename(attribute);
    			break;
    		case SymbolDefine.ACTION_2:
    			pdaStack.pop();
    			Attribute curAttribute = valueStack.pop();
    			String  parentAttribute = (String)curAttribute.left;
    			String  childAttribute = (String)curAttribute.right;
    			System.out.println(parentAttribute + " += " + childAttribute);
    			freename(childAttribute);
    			break;
    		case SymbolDefine.ACTION_3:
    			pdaStack.pop();
    			curAttribute = valueStack.pop();
    			System.out.println((String)curAttribute.left + " *= " + curAttribute.right);
    			break;
    		case SymbolDefine.ACTION_4:
    			pdaStack.pop();
    			curAttribute = valueStack.pop();
    			System.out.println((String)curAttribute.left + " = " + lexer.yytext);
    			lexer.advance();
    			break;
    			
    		case SymbolDefine.EOI:
    			if (lexer.match(Lexer.EOI)) {
    				return;
    			} else {
    				parseError();
    			}
    			break;
    			
    		case SymbolDefine.NUM_OR_ID:
    			popStacks();
    			if (lexer.match(Lexer.NUM_OR_ID) == false) {
    				parseError();
    			}		
    			
    			break;
    		case SymbolDefine.PLUS:
    			popStacks();
    			if (lexer.match(Lexer.PLUS) == false) {
    				parseError();
    			}
    			lexer.advance();
    			break;
    		case SymbolDefine.TIMES:
    			popStacks();
    			if (lexer.match(Lexer.TIMES) == false) {
    				parseError();
    			}
    			lexer.advance();
    			break;
    		case SymbolDefine.LP:
    			popStacks();
    			if (lexer.match(Lexer.LP) == false) {
    				parseError();
    			}
    			lexer.advance();
    			break;
    		case SymbolDefine.RP:
    			//pdaStack.pop();
    			popStacks();
    			if (lexer.match(Lexer.RP) == false) {
    				parseError();
    			}
    			lexer.advance();
    			break;
    		case SymbolDefine.SEMI:
    			popStacks();
    			if (lexer.match(Lexer.SEMI) == false) {
    				parseError();
    			}
    			lexer.advance();
    			break;
    			
            default:
    			int whatToDo = parseTable.getWhatToDo(symbol, lexer.lookAhead);
    			if (whatToDo == -1) {
    				parseError();
    			} else {
    				popStacks();
    				int[] replaceSymbols = parseTable.getPushTabItems(whatToDo);
    				if (replaceSymbols != null) {
    					for (int i = 0; i < replaceSymbols.length; i++) {
        					pushGrammarSymbol(replaceSymbols[i]);
        				}
    				}
    				
    			}
    			break;
    		 }
    		
    		
    	}
    		
    		
    }
    	
      
    
    private void popStacks() {
    	pdaStack.pop();
    	parentAttribute = valueStack.pop();
    }
    
    private void pushGrammarSymbol(int grammar) {
    	pdaStack.push(grammar);
    	Attribute attr = Attribute.getAttribute(parentAttribute.right);
       
    	valueStack.push(attr);
    }
    
    private void parseError() {
		System.err.println("PDA parse error");
		System.exit(1);
    }
}
