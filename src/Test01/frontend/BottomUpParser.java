package Test01.frontend;

import Test01.backend.CodeTreeBuilder;
import Test01.backend.Intepretor;
import Test01.backend.Compiler.ProgramGenerator;

public class BottomUpParser {
    public static void main(String[] args) {
    	/*
    	 * 把ProductionManager , FirstSetBuilder 两个类的初始化移到CGrammarInitializer
    	 * 将SymbolDefine 修改成CTokenType, 确定表达式的first set集合运算正确
    	 */
    	ProductionManager productionManager = ProductionManager.getProductionManager();
    	productionManager.initProductions();
    	productionManager.printAllProductions();
    	productionManager.runFirstSetAlgorithm();
    	

    	
    	GrammarStateManager stateManager = GrammarStateManager.getGrammarManager();
    	stateManager.buildTransitionStateMachine();
    	
    	System.out.println("Input string for parsing:");
    	LRStateTableParser parser = new LRStateTableParser(new Lexer());
    	parser.parse();
    	
    	ProgramGenerator generator = ProgramGenerator.getInstance();
    	generator.generate();
    	
    	//java assembly code will created when intepretor iterate every code node
    	CodeTreeBuilder treeBuilder = CodeTreeBuilder.getCodeTreeBuilder();
    	Intepretor intepretor = Intepretor.getIntepretor();
    	if (intepretor != null) {
    		intepretor.Execute(treeBuilder.getCodeTreeRoot());	
    	}
    	
    	generator.finish();
    }
}
