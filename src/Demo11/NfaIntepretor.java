package Demo11;

import utils.InputSystem.Input;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;


public class NfaIntepretor {
	private Nfa start;
	private Input input;
	
	public boolean debug = true;
	
    public NfaIntepretor(Nfa start, Input input) {
        this.start = start;
        this.input = input;
    }
    
    
    public Set<Nfa> e_closure(Set<Nfa> input) {
    	/*
    	 * 计算input集合中nfa节点所对应的ε闭包，
    	 * 并将闭包的节点加入到input中
    	 */
    	if (debug)
    	    System.out.print("ε-Closure( " + strFromNfaSet(input) + " ) = ");
    	
    	
    	Stack<Nfa> nfaStack = new Stack<Nfa>();
    	if (input == null || input.isEmpty()) {
    		return null;
    	}
    	
    	Iterator<Nfa> it = input.iterator();
    	while (it.hasNext()) {
    		nfaStack.add(it.next());
    	}
    	
    	while (nfaStack.empty() == false) {
    		Nfa p = nfaStack.pop();
    		
    		if (p.next != null && p.getEdge() == Nfa.EPSILON) {
    			if (input.contains(p.next) == false)  {
    				Nfa next = p.next;
    				
    				nfaStack.push(p.next);
    				input.add(p.next);
    			}
    		}
    		
    		if (p.next2 != null && p.getEdge() == Nfa.EPSILON) {
    			if (input.contains(p.next2) == false) {
    				Nfa next = p.next2;
    				
    				nfaStack.push(p.next2);
    				input.add(p.next2);
    			}
    		}
    	}
    	
    	if (input != null && debug) {
    		System.out.println("{ " + strFromNfaSet(input) + " }");
    	}
    	
    	
    	return input;
    }
    
    private String strFromNfaSet(Set<Nfa> input) {
    	String s = "";
    	Iterator it = input.iterator();
    	while (it.hasNext()) {
    		s += ((Nfa)it.next()).getStateNum();
    		if (it.hasNext()) {
    			s += ",";
    		}
    	}
    	
    	return s;
    }
    
    public Set<Nfa> move(Set<Nfa> input, char c) {
    	Set<Nfa> outSet = new HashSet<Nfa>();
    	Iterator it = input.iterator();
    	
    	while (it.hasNext()) {
    		Nfa p = (Nfa)it.next();
    		
    		int stateNum = p.getStateNum();
    		Set<Byte> s = p.inputSet;
    		Byte cb = (byte)c;
    		boolean b = s.contains(cb);
    		boolean ccl = p.getEdge() == Nfa.CCL;
    		
    		
    		if (p.getEdge() == c || (p.getEdge() == Nfa.CCL && p.inputSet.contains(cb))) {
    			Nfa next = p.next;
    			
    			outSet.add(p.next);
    		}
    	}
    	
    	if (outSet != null && debug) {
    		System.out.print("move({ " + strFromNfaSet(input) + " }, '" + c + "')= ");
        	System.out.println("{ " + strFromNfaSet(outSet) + " }");
    	}
    	
    	
    	return outSet;	
    }
    
    public void intepretNfa() {
    	//从控制台读入要解读的字符串
    	System.out.println("Input string: ");
    	input.ii_newFile(null);
    	input.ii_advance();
    	input.ii_pushback(1);
    	
    	Set<Nfa> next = new HashSet<Nfa>();
    	next.add(start);
    	next = e_closure(next);
    	
    	Set<Nfa> current = null; 
    	char c;
    	String inputStr = "";
    	boolean lastAccepted = false;
    	
    	while ((c = (char) input.ii_advance()) != input.EOF) {
    		current = move(next, c);
    		next = e_closure(current);
    		
    		if (next != null) {
    			if (hasAcceptState(next)) {
    				lastAccepted = true;
    			}
    		}
    		else {
    			break;
    		}
    		
    		inputStr += c;
    	}
    	
    	if (lastAccepted) {
    		System.out.println("The Nfa Machine can recognize string: " + inputStr);
    	}
    	
    }
    
    private boolean hasAcceptState(Set<Nfa> input) {
    	boolean isAccepted = false;
    	if (input == null || input.isEmpty()) {
    		return false;
    	}
    	
    	String acceptedStatement = "Accept State: ";
    	Iterator it = input.iterator();
    	while (it.hasNext()) {
    		Nfa p = (Nfa)it.next();
    		if (p.next == null && p.next2 == null) {
    			isAccepted = true;
    			acceptedStatement += p.getStateNum() + " ";
    		}
    	}
    	
    	if (isAccepted) {
    		System.out.println(acceptedStatement);
    	}
    	
    	return isAccepted;
    }
}
