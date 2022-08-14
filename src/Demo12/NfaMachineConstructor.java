package Demo12;

import java.util.Set;
public class NfaMachineConstructor {
	private Lexer lexer;
	private NfaManager nfaManager = null;
	
    public NfaMachineConstructor(Lexer lexer) throws Exception {
    	this.lexer = lexer;
    	nfaManager = new NfaManager();
    	
    	while (lexer.MatchToken(Lexer.Token.EOS)) {
    		lexer.advance();
    	}
    }
    
    public void expr(NfaPair pairOut) throws Exception {
    	/*
    	 * expr 由一个或多个cat_expr 之间进行 OR 形成
    	 * 如果表达式只有一个cat_expr 那么expr 就等价于cat_expr
    	 * 如果表达式由多个cat_expr做或连接构成那么 expr-> cat_expr | cat_expr | ....
    	 * 由此得到expr的语法描述为:
    	 * expr -> expr OR cat_expr
    	 *         | cat_expr 
    	 *
    	 */
    	 cat_expr(pairOut);
    	 NfaPair localPair = new NfaPair();
    	
    	 while (lexer.MatchToken(Lexer.Token.OR)) {
    		 lexer.advance();
    		 cat_expr(localPair);
    		 
    		 Nfa startNode = nfaManager.newNfa();
    		 startNode.next2 = localPair.startNode;
    		 startNode.next = pairOut.startNode;
    		 pairOut.startNode = startNode;
    		 
    		 Nfa endNode = nfaManager.newNfa();
    		 pairOut.endNode.next = endNode;
    		 localPair.endNode.next = endNode;
    		 pairOut.endNode = endNode;
    	 }
    	 
    }
    
   
    public void cat_expr(NfaPair pairOut) throws Exception
    {
    	/*
    	 * cat_expr -> factor factor .....
    	 * 由于多个factor 前后结合就是一个cat_expr所以
    	 * cat_expr-> factor cat_expr
    	 */

    	if (first_in_cat(lexer.getCurrentToken())) {
    		factor(pairOut);
    	}
    	
    	char c = (char)lexer.getLexeme();
    	
    	while (first_in_cat(lexer.getCurrentToken()) ){
    		NfaPair pairLocal = new NfaPair();
    		factor(pairLocal);
    		
    		pairOut.endNode.next = pairLocal.startNode;
    		
    		pairOut.endNode = pairLocal.endNode;
    	}
    	
    	
    }
    
    private boolean first_in_cat(Lexer.Token tok) throws Exception {
    	switch (tok) {
    	//正确的表达式不会以 ) $ 开头,如果遇到EOS表示正则表达式解析完毕，那么就不应该执行该函数
    	case CLOSE_PAREN:
    	case AT_EOL:
    	case OR:
    	case EOS:
    		return false;
    	case CLOSURE:
    	case PLUS_CLOSE:
    	case OPTIONAL:
    		//*, +, ? 这几个符号应该放在表达式的末尾
    		ErrorHandler.parseErr(ErrorHandler.Error.E_CLOSE);
    		return false;
    	case CCL_END:
    		//表达式不应该以]开头
    		ErrorHandler.parseErr(ErrorHandler.Error.E_BRACKET);
    		return false;
    	case AT_BOL:
    		//^必须在表达式的最开始
    		ErrorHandler.parseErr(ErrorHandler.Error.E_BOL);
    		return false;
    	}
    	
    	return true;
    }
    
    public void factor(NfaPair pairOut) throws Exception {
    	term(pairOut);
    	
    	boolean handled = false;
    	handled = constructStarClosure(pairOut);
    	if (handled == false) {
    		handled = constructPlusClosure(pairOut);
    	}
    	
    	if (handled == false) {
    		handled = constructOptionsClosure(pairOut);
    	}
    	
    }
    
    
    public boolean constructStarClosure(NfaPair pairOut) throws Exception {
    	/*
    	 * term*
    	 */
    	Nfa start, end;
    //	term(pairOut);
    	
    	if (lexer.MatchToken(Lexer.Token.CLOSURE) == false) {
    		return false;
    	}
    	
    	start = nfaManager.newNfa();
    	end = nfaManager.newNfa();
    	
    	start.next = pairOut.startNode;
    	pairOut.endNode.next = pairOut.startNode;
    	
    	start.next2 = end;
    	pairOut.endNode.next2 = end;
    	
    	pairOut.startNode = start;
    	pairOut.endNode = end;
    	
    	lexer.advance();
    	
    	return true;
    }
    
    public boolean constructPlusClosure(NfaPair pairOut) throws Exception {
    	/*
    	 * term+
    	 */
    	Nfa start, end;
    //	term(pairOut);
    	
    	if (lexer.MatchToken(Lexer.Token.PLUS_CLOSE) == false) {
    		return false;
    	}
    	
    	start = nfaManager.newNfa();
    	end = nfaManager.newNfa();
    	
    	start.next = pairOut.startNode;
    	pairOut.endNode.next2 = end;
    	pairOut.endNode.next = pairOut.startNode;
    	
    	
    	pairOut.startNode = start;
    	pairOut.endNode = end;
    	
    	lexer.advance();
    	return true;
    }
    
    public boolean constructOptionsClosure(NfaPair pairOut) throws Exception {
    	/*
    	 * term?
    	 */
    	Nfa start, end;
  //  	term(pairOut);
    	
    	if (lexer.MatchToken(Lexer.Token.OPTIONAL) == false) {
    		return false;
    	}
    	
    	start = nfaManager.newNfa();
    	end = nfaManager.newNfa();
    	
    	start.next = pairOut.startNode;
    	pairOut.endNode.next = end;
    	
    	start.next2 = end;
    	
    	pairOut.startNode = start;
    	pairOut.endNode = end;
    	
    	lexer.advance();
    	
    	return true;
    }
    
    public void term(NfaPair pairOut)throws Exception {
        /*
         * term ->  character | [...] | [^...] | [character-charcter] | . | (expr)
         * 
         */
    	
    	boolean handled = constructExprInParen(pairOut);
    	if (handled == false) {
    		handled = constructNfaForSingleCharacter(pairOut);
    	}
    			
    	if (handled == false) {
    		handled = constructNfaForDot(pairOut);
    	}
    	
    	if (handled == false) {
    		constructNfaForCharacterSet(pairOut);
    	}
    	
    	
    }
    
    private boolean constructExprInParen(NfaPair pairOut) throws Exception {
    	if (lexer.MatchToken(Lexer.Token.OPEN_PAREN)) {
    		lexer.advance();
    		expr(pairOut);
    		if (lexer.MatchToken(Lexer.Token.CLOSE_PAREN)) {
    			lexer.advance();
    		}
    		else {
    			ErrorHandler.parseErr(ErrorHandler.Error.E_PAREN);
    		}
    		
    		return true;
    	}
    	
    	return false;
    }
    
    public boolean constructNfaForSingleCharacter(NfaPair pairOut) throws Exception {
    	if (lexer.MatchToken(Lexer.Token.L) == false) {
    		return false;
    	}
    	
    	Nfa start = null;
    	start = pairOut.startNode = nfaManager.newNfa();
    	pairOut.endNode = pairOut.startNode.next = nfaManager.newNfa();
    	
    	start.setEdge(lexer.getLexeme());
    	
    	lexer.advance();
    	
    	return true;
    }
    
    public boolean constructNfaForDot(NfaPair pairOut) throws Exception {
    	if (lexer.MatchToken(Lexer.Token.ANY) == false) {
    		return false;
    	}
    	
    	Nfa start = null;
    	start = pairOut.startNode = nfaManager.newNfa();
    	pairOut.endNode = pairOut.startNode.next = nfaManager.newNfa();
    	
    	start.setEdge(Nfa.CCL);
    	start.addToSet((byte)'\n');
    	start.addToSet((byte)'\r');
    	start.setComplement();
    	
    	lexer.advance();
    	
    	return true;
    }
    
    public boolean constructNfaForCharacterSetWithoutNegative(NfaPair pairOut) throws Exception {
    	
    	if (lexer.MatchToken(Lexer.Token.CCL_START) == false) {
    		return false;
    	}
    	
    	lexer.advance();
    	
    	Nfa start = null;
    	start = pairOut.startNode = nfaManager.newNfa();
    	pairOut.endNode = pairOut.startNode.next = nfaManager.newNfa();
    	start.setEdge(Nfa.CCL);
    	
    	if (lexer.MatchToken(Lexer.Token.CCL_END) == false) {
    		dodash(start.inputSet);
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.CCL_END) == false) {
    		ErrorHandler.parseErr(ErrorHandler.Error.E_BADEXPR);
    	}
    	lexer.advance();
    	
    	return true;
    }
    
    public boolean constructNfaForCharacterSet(NfaPair pairOut) throws Exception {
    	if (lexer.MatchToken(Lexer.Token.CCL_START) == false) {
    		return false;
    	}
    	
    	lexer.advance();
    	boolean negative = false;
    	if (lexer.MatchToken(Lexer.Token.AT_BOL)) {
    		negative = true;
    	}
    	
    	Nfa start = null;
    	start = pairOut.startNode = nfaManager.newNfa();
    	pairOut.endNode = pairOut.startNode.next = nfaManager.newNfa();
    	start.setEdge(Nfa.CCL);
    	
    	if (lexer.MatchToken(Lexer.Token.CCL_END) == false) {
    		dodash(start.inputSet);
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.CCL_END) == false) {
    		ErrorHandler.parseErr(ErrorHandler.Error.E_BADEXPR);
    	}
    	
    	if (negative) {
    		start.setComplement();
    	}
    	
    	lexer.advance();
    	
    	return true;
    }
    
    private void dodash(Set<Byte> set) {
    	int first = 0;
    	
    	while (lexer.MatchToken(Lexer.Token.EOS) == false && 
    			lexer.MatchToken(Lexer.Token.CCL_END) == false) {
    		
    		if (lexer.MatchToken(Lexer.Token.DASH) == false) {
    			first = lexer.getLexeme();
    			set.add((byte)first);
    		}
    		else {
    			lexer.advance(); //越过 -
    			for (; first <= lexer.getLexeme(); first++) {
    				set.add((byte)first);
    			}
    		}
    		
    		lexer.advance();
    	}
    	
    		
    }
}
