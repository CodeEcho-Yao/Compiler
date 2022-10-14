package Demo24;


import utils.CompilerParser.SymbolDefine;

import java.util.ArrayList;


public class GrammarStateManager {
    private ArrayList<GrammarState> stateList = new ArrayList<GrammarState>();
    private static GrammarStateManager self = null;
    
    public static GrammarStateManager getGrammarManager()  {
    	if (self == null) {
    		self = new GrammarStateManager();
    	}
    	
    	return self;
    }
    
    private GrammarStateManager() {
    	
    }
    
    public void buildTransitionStateMachine() {
    	ProductionManager productionManager = ProductionManager.getProductionManager();
    	GrammarState state = getGrammarState(productionManager.getProduction(SymbolDefine.STMT));
    	
    	state.createTransition();
    }
    
    public GrammarState getGrammarState(ArrayList<Production> productionList) {
    	
    	GrammarState state = new GrammarState(productionList);
    	if (stateList.contains(state) == false) {
    		stateList.add(state);
    		GrammarState.increateStateNum();
    		return state;
    	}
    	
    	for (int i = 0; i < stateList.size(); i++) {
			if (stateList.get(i).equals(state)) {
				state =  stateList.get(i);
			}
		}
    	
    	return state;
    }
    
}
