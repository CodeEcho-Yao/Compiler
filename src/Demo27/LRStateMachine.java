package Demo27;

import utils.CompilerParser.SymbolDefine;

import java.util.HashMap;


public class LRStateMachine {
    enum STATE_MACHINE_ACTION {
    	error,
    	r1,
    	r2,
    	r3,
    	s1,
    	s2,
    	s3,
    	s4,
    	state_2,
    	state_3,
    	state_5,
    	accept
    }
    
    private HashMap<Integer, HashMap<Integer,STATE_MACHINE_ACTION >> stateMachine = 
    		new HashMap<Integer, HashMap<Integer,STATE_MACHINE_ACTION >>();
    
    public LRStateMachine() {
    	HashMap<Integer, STATE_MACHINE_ACTION> row0 = new HashMap<Integer, STATE_MACHINE_ACTION>();
    	row0.put(SymbolDefine.NUM_OR_ID, STATE_MACHINE_ACTION.s1);
    	row0.put(SymbolDefine.EXPR, STATE_MACHINE_ACTION.state_2);
    	row0.put(SymbolDefine.TERM, STATE_MACHINE_ACTION.state_3);
    	stateMachine.put(0, row0);
    	
    	HashMap<Integer, STATE_MACHINE_ACTION> row1 = new HashMap<Integer, STATE_MACHINE_ACTION>();
    	row1.put(SymbolDefine.EOI, STATE_MACHINE_ACTION.r3);
    	row1.put(SymbolDefine.PLUS, STATE_MACHINE_ACTION.r3);
    	stateMachine.put(1, row1);
    	
    	HashMap<Integer, STATE_MACHINE_ACTION> row2 = new HashMap<Integer, STATE_MACHINE_ACTION>();
    	row2.put(SymbolDefine.EOI, STATE_MACHINE_ACTION.accept);
    	row2.put(SymbolDefine.PLUS, STATE_MACHINE_ACTION.s4);
    	stateMachine.put(2, row2);
    	
    	HashMap<Integer, STATE_MACHINE_ACTION> row3 = new HashMap<Integer, STATE_MACHINE_ACTION>();
    	row3.put(SymbolDefine.EOI, STATE_MACHINE_ACTION.r2);
    	row3.put(SymbolDefine.PLUS, STATE_MACHINE_ACTION.r2);
    	stateMachine.put(3, row3);
    	
    	HashMap<Integer, STATE_MACHINE_ACTION> row4 = new HashMap<Integer, STATE_MACHINE_ACTION>();
    	row4.put(SymbolDefine.NUM_OR_ID, STATE_MACHINE_ACTION.s1);
    	row4.put(SymbolDefine.TERM, STATE_MACHINE_ACTION.state_5);
    	stateMachine.put(4, row4);
    	
    	HashMap<Integer, STATE_MACHINE_ACTION> row5 = new HashMap<Integer, STATE_MACHINE_ACTION>();
    	row5.put(SymbolDefine.EOI, STATE_MACHINE_ACTION.r1);
    	row5.put(SymbolDefine.PLUS, STATE_MACHINE_ACTION.r1);
    	stateMachine.put(5, row5);
    }
    
    public STATE_MACHINE_ACTION  getAction(int tos, int symbol) {
    	if (stateMachine.get(tos).get(symbol) == null) {
    		return STATE_MACHINE_ACTION.error;
    	}
    	
    	return stateMachine.get(tos).get(symbol);
    }
    
}
