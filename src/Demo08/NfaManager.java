package Demo08;

import java.util.ArrayList;
import java.util.Stack;


public class NfaManager {
    private final int NFA_MAX = 256; //最多运行分配256个NFA节点
    private Nfa[] nfaStatesArr = null;
    private Stack<Nfa> nfaStack = null;
    private int nextAlloc = 0; //nfa数组下标
    private int nfaStates = 0; //分配的nfa编号
    
    public NfaManager() throws Exception {
    	nfaStatesArr = new Nfa[NFA_MAX];
    	for (int i = 0; i < NFA_MAX; i++) {
    		nfaStatesArr[i] = new Nfa();
    	}
    	
    	nfaStack = new Stack<Nfa>();
    	
    	if (nfaStatesArr == null || nfaStack == null) {
    		ErrorHandler.parseErr(ErrorHandler.Error.E_MEM);
    	}
    }
    
    public Nfa newNfa() throws Exception {
    	
    	if (++nfaStates >= NFA_MAX) {
    		ErrorHandler.parseErr(ErrorHandler.Error.E_LENGTH);
    	}
    	
    	Nfa nfa = null;
    	if (nfaStack.size() > 0) {
    		nfa = nfaStack.pop();
    	}
    	else {
    		nfa = nfaStatesArr[nextAlloc];
    		nextAlloc++;
    	}
    	
    	nfa.clearState();
    	nfa.setStateNum(nfaStates);
    	nfa.setEdge(Nfa.EPSILON);
    	
    	return nfa;
    }
    
    public void discardNfa(Nfa nfaDiscarded) {
    	--nfaStates;
    	nfaDiscarded.clearState();
    	nfaStack.push(nfaDiscarded);
    }
    
   
}
