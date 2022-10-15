package Demo27;

import utils.CompilerParser.Lexer;

public class BottomUpParser {
    public static void main(String[] args) {
    	ProductionManager productionManager = ProductionManager.getProductionManager();
    	productionManager.initProductions();
    	
    	GrammarStateManager stateManager = GrammarStateManager.getGrammarManager();
    	stateManager.buildTransitionStateMachine();
    	
    	System.out.println("Input string for parsing:");
    	LRStateTableParser parser = new LRStateTableParser(new Lexer());
    	parser.parse();
    }
}
