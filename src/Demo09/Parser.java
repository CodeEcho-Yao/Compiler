package Demo09;


public class Parser {
    private NfaManager nfaManager = null;
    private Lexer lexer = null;
    public Parser(Lexer lexer) throws Exception {
    	nfaManager = new NfaManager();
    	this.lexer = lexer;
    }
    
    public Nfa Machine() throws Exception {
    	Nfa start, p;
    	Debug.Enter("Machine");
    	
    	p = start = nfaManager.newNfa();
    	p.next = Rule();
    	
    	while (lexer.MatchToken(Lexer.Token.END_OF_INPUT) == false) {
    		p.next2 = nfaManager.newNfa();
    		p = p.next2;
    		p.next = Rule();
    	}
    	
    	Debug.Leave("Machine");
    	
    	return start;
    }
    
    private Nfa Rule() throws Exception {
    	/*
    	 *     rule  -->  expr EOS
    	 *                ^expr EOS
    	 *                expr$ EOS
    	 */
    	
    	Nfa p, start = null, end = null;
    	NfaPair pair = new NfaPair();
    	
    	Nfa.ANCHOR anchor = Nfa.ANCHOR.NONE;
    	boolean anchorStart = false, anchorEnd = false;
    	
    	Debug.Enter("Rule");
    	if (lexer.MatchToken(Lexer.Token.AT_BOL)) {
    		/*
    		 * 开头匹配，例如要匹配表达式^a
    		 * 那么输入的字符串必须要以字符a开头，并且a的前头只能跟着换行符
    		 */
    		start = nfaManager.newNfa();
    		start.setEdge('\n');
    		anchor = Nfa.ANCHOR.START;
    		lexer.advance();
    		expr(pair);
    		start.next = pair.startNode;
    		end = pair.endNode;
    		
    		anchorStart = true;
    	}
    	else {
    		expr(pair);
    		start = pair.startNode;
    		end = pair.endNode;
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.AT_EOL)) {
    		lexer.advance();
    		end.next = nfaManager.newNfa();
    		
    		/*末尾匹配，例如要匹配正则表达式a$
    		 * 那么输入的字符串必须以字符a结尾，而且字符a后面必须跟着回车换行
    		 * 我们才能认为字符串是以字符a结尾的
    		 */
    		end.setEdge(Nfa.CCL);
    		end.addToSet((byte) '\n');
    		end.addToSet((byte)'\r');
    		end = end.next;
    		anchor = Nfa.ANCHOR.END;
    		anchorEnd = true;
    	}
    	
    	if (anchorStart && anchorEnd) {
    		anchor = Nfa.ANCHOR.BOTH;
    	}
    	end.setAnchor(anchor);
    	lexer.advance();
    	Debug.Leave("Rule");
    	
    	return start;
    	
    }
    
    private void expr(NfaPair pairOut) throws Exception {
    	/*
    	 * expr -> expr OR cat_expr
    	 *         | cat_expr
    	 *  改进为:
    	 *  
    	 *  expr-> cat_expr expr'
    	 *  expr' -> OR cat_expr expr' epsilon
    	 *  
    	 *  上面更改后的语法可以用循环实现
    	 *  while (lexer.matchToken(Lexer.Token.OR)) {
    	 *      cat_expr();
    	 *      do the or
    	 *  }
    	 */
    	cat_expr(pairOut);
    	
    	Nfa e2_start = null, e2_end = null;
    	NfaPair pairLocal = new NfaPair();
    	Nfa p;
    	Debug.Enter("expr");
    	while (lexer.MatchToken(Lexer.Token.OR)) {
    		lexer.advance();
    		cat_expr(pairLocal);
    		e2_start = pairLocal.endNode;
    		e2_end = pairLocal.endNode;
    		
    		p = nfaManager.newNfa();
    		p.next2 = e2_start;
    		p.next = pairOut.startNode;
    		pairOut.startNode = p;
    		
    		p = nfaManager.newNfa();
    		pairOut.endNode.next = p;
    		e2_end.next = p;
    		pairOut.endNode = p;
    	}
    	
    	Debug.Leave("expr");
    }
    
    private void cat_expr(NfaPair pairOut) throws Exception {
    	/*
    	 * cat_expr -> cat_expr | factor
    	 *             factor
    	 *  改进为:
    	 *  
    	 *  cat_expr -> factor cat_expr'
    	 *  cat_expr' -> | factor cat_expr'
    	 *               epsilon
    	 * 
    	 */
    	
    	Nfa e2_start, e2_end;
    	NfaPair pairLocal = new NfaPair();
    	
    	Debug.Enter("cat_expr");
    	if (first_in_cat(lexer.getCurrentToken())) {
    		factor(pairOut);
    	}
    	
    	while (first_in_cat(lexer.getCurrentToken())) {
    	    factor(pairLocal);
    	    e2_start = pairLocal.startNode;
    	    e2_end = pairLocal.endNode;
    	    
    	    pairOut.endNode.cloneNfa(e2_start);
    	    nfaManager.discardNfa(e2_start);
    	    pairOut.endNode = e2_end;
    	}
    	Debug.Leave("cat_expr");
    }
    
    private boolean first_in_cat(Lexer.Token tok) throws Exception {
    	switch (tok) {
    	case CLOSE_PAREN:
    	case AT_EOL:
    	case OR:
    	case EOS:
    		return false;
    		
    	case CLOSURE:
    	case PLUS_CLOSE:
    	case OPTIONAL:
    		ErrorHandler.parseErr(ErrorHandler.Error.E_CLOSE);
    		return false;
    		
    	case CCL_END:
    		ErrorHandler.parseErr(ErrorHandler.Error.E_BRACKET);
    		return false;
    	case AT_BOL:
    		ErrorHandler.parseErr(ErrorHandler.Error.E_BOL);
    		return false;
    	}
    	
    	return true;
    }
    
    private void factor(NfaPair pairOut) throws Exception {
    	/*
    	 * factor --> term* | term+ | term?
    	 */
    	Debug.Enter("factor");
    	term(pairOut);
    	Nfa start, end;
    	if (lexer.MatchToken(Lexer.Token.CLOSURE) || lexer.MatchToken(Lexer.Token.PLUS_CLOSE)
    			|| lexer.MatchToken(Lexer.Token.OPTIONAL)) {
    		start = nfaManager.newNfa();
    		end = nfaManager.newNfa();
    		start.next = pairOut.startNode;
    		pairOut.endNode.next = end;
    		
    		if (lexer.MatchToken(Lexer.Token.CLOSURE) || lexer.MatchToken(Lexer.Token.OPTIONAL)) {
    			start.next2 = end;
    		}
    		
    		if (lexer.MatchToken(Lexer.Token.CLOSURE) || lexer.MatchToken(Lexer.Token.PLUS_CLOSE)) {
    		    pairOut.endNode.next2 = pairOut.startNode;	
    		}
    		
    		pairOut.startNode = start;
    		pairOut.endNode = end;
    		lexer.advance();
    	}
    	Debug.Leave("factor");
    	
    }
    
    private void term(NfaPair pairOut) throws Exception {
    	/*
    	 * term -> [..] | [^...] | [] | [^] | . |(expr) | <character>
    	 * 
    	 * [] 匹配 空格，tab , 换行 但不匹配回车
    	 */
    	Nfa start;
    	int c;
    	Debug.Enter("term");
    	if (lexer.MatchToken(Lexer.Token.OPEN_PAREN)) {
    		lexer.advance();
    		expr(pairOut);
    		if (lexer.MatchToken(Lexer.Token.CLOSE_PAREN)) {
    			lexer.advance();
    		}
    		else {
    			ErrorHandler.parseErr(ErrorHandler.Error.E_PAREN);
    		}
    	}
    	else {
    		pairOut.startNode = start = nfaManager.newNfa();
    		pairOut.endNode = start.next = nfaManager.newNfa();
    		
    		if (!(lexer.MatchToken(Lexer.Token.ANY) || lexer.MatchToken(Lexer.Token.CCL_START))) {
    			start.setEdge(lexer.getLexeme());
    			lexer.advance();
    		}
    		else {
    			start.setEdge(Nfa.CCL);
    			if (lexer.MatchToken(Lexer.Token.ANY)) {
    				start.addToSet((byte)'\n');
    				start.addToSet((byte)'\r');
    				start.setComplement();
    			}
    			else {
    				lexer.advance();
    				if (lexer.MatchToken(Lexer.Token.AT_BOL)) {
    					lexer.advance();
    					start.addToSet((byte)'\n');
    					start.addToSet((byte)'\r');
    					start.setComplement();
    				}
    				if (lexer.MatchToken(Lexer.Token.CCL_END) == false) {
    					dodash(start);
    				}
    				else {
    					for (c = 0; c <= ' '; ++c) {
    						start.addToSet((byte)c);
    					}
    				}
    			}
    			
    			lexer.advance();
    		}
    	}
    	
    	Debug.Leave("term");
    }
    
    private void dodash(Nfa nfa) {
    	int first = 0;
    	while (lexer.MatchToken(Lexer.Token.EOS) == false && 
    		   lexer.MatchToken(Lexer.Token.CCL_END) == false) {
    		if (lexer.MatchToken(Lexer.Token.DASH) == false) {
    			first = lexer.getLexeme();
    			nfa.addToSet((byte)lexer.getLexeme());
    		}
    		else {
    			lexer.advance(); //越过 -
    			for (; first <= lexer.getLexeme(); first++) {
    				nfa.addToSet((byte)first);
    			}
    		}
    	}
    }
}
