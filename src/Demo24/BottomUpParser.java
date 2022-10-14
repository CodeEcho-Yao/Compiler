package Demo24;

public class BottomUpParser {
    public static void main(String[] args) {
    	ProductionManager productionManager = ProductionManager.getProductionManager();
    	productionManager.initProductions();
    	
    	GrammarStateManager stateManager = GrammarStateManager.getGrammarManager();
    	stateManager.buildTransitionStateMachine();
    }
}
