package Demo24;

import utils.CompilerParser.SymbolDefine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;


public class GrammarState {
	private static int stateNumCount = 0;
	private boolean printInfo = false;
	private boolean transitionDone = false;
    private int stateNum = -1;
    private GrammarStateManager stateManager = GrammarStateManager.getGrammarManager();
    private ArrayList<Production> productions = new ArrayList<Production>();
    private HashMap<Integer, GrammarState> transition = new HashMap<Integer, GrammarState>();
    private ArrayList<Production> closureSet = new ArrayList<Production>();
    private ProductionManager productionManager = ProductionManager.getProductionManager();
    private HashMap<Integer, ArrayList<Production>> partition = new HashMap<Integer, ArrayList<Production>>();
    private ArrayList<Production> uselessProduction = new ArrayList<Production>();
    
  
    public static void  increateStateNum() {
    	stateNumCount++;
    }
    
    public boolean isTransitionMade() {
    	return transitionDone;
    }
    
    public GrammarState(ArrayList<Production> productions) {
    	this.stateNum = stateNumCount;
    	
    	this.productions = productions;

    	this.closureSet.addAll(this.productions);
    }
    
    public void print() {
    	System.out.println("State Number: " + stateNum);
    	for (int i = 0; i < productions.size(); i++) {
    		productions.get(i).print();
    	}
    }
    
    public void printTransition() {
    	for (Map.Entry<Integer, GrammarState> entry: transition.entrySet()) {
    		System.out.println("transfter on " + SymbolDefine.getSymbolStr(entry.getKey()) + " to state ");
    		entry.getValue().print();
    		System.out.print("\n");
    	}
    }
    
    public void createTransition() {
    	if (transitionDone == true) {
    		return;
    	}
    	
    	transitionDone = true;
    	System.out.println("\n====make transition=====\n");
    	print();
    	
    	makeClosure();
    	partition();
    	makeTransition();
    	
    	System.out.print("\n");
    	
    	printInfo = true;
    }
    
    private void makeClosure() {
    	
    	Stack<Production> productionStack = new Stack<Production>();
    	for (int i = 0; i < productions.size(); i++) {
    		productionStack.push(productions.get(i));
    	}
    	
    	System.out.println("---begin make closure----");
    	
    	while (productionStack.empty() == false) {
    		Production production = productionStack.pop();
    		System.out.println("\nproduction on top of stack is : ");
    		production.print();
    		//production.printBeta();
    		if (SymbolDefine.isSymbolTerminals(production.getDotSymbol()) == true) {
    			    System.out.println("symbol after dot is not non-terminal, ignore and prcess next item");
    			    continue;	
    			}
    		
    		int symbol = production.getDotSymbol();
    		ArrayList<Production> closures = productionManager.getProduction(symbol);
    		ArrayList<Integer> lookAhead = production.computeFirstSetOfBetaAndC();
    		
    		Iterator it = closures.iterator();
    		while (it.hasNext()) {
    			Production oldProduct = (Production) it.next();
    			Production newProduct = (oldProduct).cloneSelf();
    			newProduct.addLookAheadSet(lookAhead);
    			
    			System.out.print("create new production: ");
    			newProduct.print();
    			
    			if (closureSet.contains(newProduct) == false) {  
    				System.out.println("push and add new production to stack and closureSet");
    				
    				closureSet.add(newProduct);
    				productionStack.push(newProduct);
    				
    				removeRedundantProduction(newProduct);
    				
    				
    			}
    			else {
    				System.out.println("the production is already exist!");
    			}
    			
    		}
    	
    		
    	}
    	
    	printClosure();
    	System.out.println("----end make closure----");
    	
    }
    
    private void removeRedundantProduction(Production product) {
    	boolean removeHappended = true;
    	
    	while (removeHappended) {
    		removeHappended = false;
    		
    		Iterator it = closureSet.iterator();
    		while (it.hasNext()) {
    			Production item = (Production) it.next();
    			if (product.coverUp(item)) {
    				removeHappended = true;
    				closureSet.remove(item);
    				
    				System.out.print("remove redundant production: ");
    				item.print();
    				break;
    			}
    		}
    	}
    }
    
  
    
    private void printLookAheadSet(ArrayList<Integer> set) {
    	System.out.print("current production's look ahead set : { ");
    	for (int i = 0; i < set.size(); i++) {
    		System.out.print(SymbolDefine.getSymbolStr(set.get(i)) + " ");
    	}
    	System.out.println("}");
    }
    
    private void printClosure() {
    	if (printInfo) {
    		return;
    	}
    	
    	System.out.println("ClosueSet is: ");
    	for (int i = 0; i < closureSet.size(); i++) {
    		closureSet.get(i).print();
    	}
    }
    
    private void partition() {
    	for (int i = 0; i < closureSet.size(); i++) {
    		int symbol = closureSet.get(i).getDotSymbol();
    		if (symbol == SymbolDefine.UNKNOWN_SYMBOL) {
    			continue;
    		}
    		
    		ArrayList<Production> productionList = partition.get(symbol);
    		if (productionList == null) {
    			productionList = new ArrayList<Production>();
    			partition.put(closureSet.get(i).getDotSymbol(), productionList);
    		}
    		
    		if (productionList.contains(closureSet.get(i)) == false) {
    	        productionList.add(closureSet.get(i));	
    		}
    	}
    	
    	
    	
    	printPartition();
    }
    
    private void printPartition() {
    	if (printInfo) {
    		return;
    	}
    	
    	for(Map.Entry<Integer, ArrayList<Production>> entry : partition.entrySet()) {
    		
    		System.out.println("partition for symbol: " + SymbolDefine.getSymbolStr(entry.getKey()));
    		
    		ArrayList<Production> productionList = entry.getValue();
    		for (int i = 0; i < productionList.size(); i++) {
    			productionList.get(i).print();
    		}
    	}
    }
    
    private GrammarState makeNextGrammarState(int left) {
    	ArrayList<Production> productionList = partition.get(left);
    	ArrayList<Production> newStateProductionList = new ArrayList<Production>();
    	
    	for (int i = 0; i < productionList.size(); i++) {
    		Production production = productionList.get(i);
    		newStateProductionList.add(production.dotForward());
    	}
    	
    	return  stateManager.getGrammarState(newStateProductionList);
    }
    
    private void makeTransition() {
    	for (Map.Entry<Integer, ArrayList<Production>> entry : partition.entrySet()) {
    		System.out.println("\n====begin print transition info ===");
    		GrammarState nextState = makeNextGrammarState(entry.getKey());
    		transition.put(entry.getKey(), nextState);
    		System.out.println("from state " + stateNum + " to State " + nextState.stateNum + " on " + 
    		SymbolDefine.getSymbolStr(entry.getKey()));
    		System.out.println("----State " + nextState.stateNum + "------");
    		nextState.print();
    	}
    	
    	extendFollowingTransition();
    }
    
    private void extendFollowingTransition() {
    	for (Map.Entry<Integer, GrammarState> entry : transition.entrySet()) {
    		GrammarState state = entry.getValue();
    		if (state.isTransitionMade() == false) {
    			state.createTransition();
    		}
    	}
    }
    
    @Override
    public boolean equals(Object obj) {
    	GrammarState state = (GrammarState)obj;
    	
    	return state.productions.equals(this.productions);

    }
}
