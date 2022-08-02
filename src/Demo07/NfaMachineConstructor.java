package Demo07;

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
    
    public void factor(NfaPair pairOut) throws Exception {
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
    	term(pairOut);
    	
    	if (lexer.MatchToken(Lexer.Token.CLOSURE) == false) {
    		return false;
    	}
    	
    	start = nfaManager.newNfa();
    	end = nfaManager.newNfa();
    	
    	start.next = pairOut.startNode;
    	pairOut.endNode.next = end;
    	
    	start.next2 = end;
    	pairOut.endNode.next2 = start;
    	
    	pairOut.startNode = start;
    	pairOut.endNode = end;
    	
    	return true;
    }
    
    public boolean constructPlusClosure(NfaPair pairOut) throws Exception {
    	/*
    	 * term+
    	 */
    	Nfa start, end;
    	term(pairOut);
    	
    	if (lexer.MatchToken(Lexer.Token.PLUS_CLOSE) == false) {
    		return false;
    	}
    	
    	start = nfaManager.newNfa();
    	end = nfaManager.newNfa();
    	
    	start.next = pairOut.startNode;
    	pairOut.endNode.next = end;
    	
    	
    	pairOut.endNode.next2 = start;
    	
    	pairOut.startNode = start;
    	pairOut.endNode = end;
    	
    	return true;
    }
    
    public boolean constructOptionsClosure(NfaPair pairOut) throws Exception {
    	/*
    	 * term?
    	 */
    	Nfa start, end;
    	term(pairOut);
    	
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
    	
    	return true;
    }
    
    public void term(NfaPair pairOut)throws Exception {
        /*
         * term ->  character | [...] | [^...]
         * 
         */
    	
    	boolean handled = constructNfaForSingleCharacter(pairOut);
    	if (handled == false) {
    		handled = constructNfaForDot(pairOut);
    	}
    	
    	if (handled == false) {
    		constructNfaForCharacterSet(pairOut);
    	}
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
