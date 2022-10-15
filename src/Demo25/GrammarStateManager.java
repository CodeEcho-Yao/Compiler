package Demo25;

import utils.CompilerParser.SymbolDefine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


public class GrammarStateManager {
    private ArrayList<GrammarState> stateList = new ArrayList<GrammarState>();
    private static GrammarStateManager self = null;
    private HashMap<GrammarState, HashMap<Integer, GrammarState>> transitionMap = new HashMap<GrammarState, HashMap<Integer, GrammarState>>();
    
    //是否对状态机网络进行压缩
    private boolean isTransitionTableCompressed = true;
    
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
    	
    	/*
    	 * 初始化节点0后，开始构建整个状态机网络，网络的构建类似一种链式反应，节点0生成节点1到5，每个子节点继续生成相应节点
    	 */
    	state.createTransition();
    	
    	//打印最后形成的状态机网络
    	printStateMap();
    	//printReduceInfo();
    }
    
    public GrammarState getGrammarState(ArrayList<Production> productionList) {
    	/*
    	 * 要生成新的状态节点时，需要查找给定表达式所对应的节点是否已经存在，如果存在，就不必要构造
    	 * 新的节点
    	 */
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
    
    
    public void addTransition(GrammarState from, GrammarState to, int on) {
    	if (isTransitionTableCompressed) {
    		/*
    		 * 压缩时，把相似的节点找到，然后将相似节点合并
    		 */
    		from = getAndMergeSimilarStates(from);
        	to   = getAndMergeSimilarStates(to);	
    	}
    	/*
    	System.out.println("add transition from: ");
    	from.print();
    	System.out.println("on: " + SymbolDefine.getSymbolStr(on) + " to : ");
    	to.print();
    	*/
    	HashMap<Integer, GrammarState> map = transitionMap.get(from);
    	if (map == null) {
    		map = new HashMap<Integer, GrammarState>();
    	}
    	
    	
    	
    	map.put(on, to);
    	transitionMap.put(from, map);
    }
    
    private GrammarState getAndMergeSimilarStates(GrammarState state) {
    	Iterator it = stateList.iterator();
    	GrammarState currentState = null, returnState = state;
    	
    	while(it.hasNext()) {
    	    currentState = (GrammarState) it.next();    	   
    	    
    		if (currentState.equals(state) == false && currentState.checkProductionEqual(state, true) == true) {
    			/*
    			System.out.println("\nFind similar stats: ");
    			currentState.print();
    			System.out.println("=========");
    			state.print();
    			*/
    			if (currentState.stateNum < state.stateNum) {
    				currentState.stateMerge(state);
    				returnState = currentState;
    			}
    			else {
    				state.stateMerge(currentState);
    				returnState = state;
    			}
    			
    			//System.out.println("combined state is:");
    			//returnState.print();
    			break;
    		}
    	}
    	
    	
    	return returnState;
    }
    
    public void printStateMap() {
    	System.out.println("Map size is: " + transitionMap.size());
    	
    	for (Entry<GrammarState, HashMap<Integer, GrammarState>> entry : transitionMap.entrySet()) {
    		GrammarState from = entry.getKey();
    		System.out.println("********begin to print a map row********");
    		System.out.println("from state: ");
    		from.print();
    		
    		HashMap<Integer, GrammarState> map = entry.getValue();
    		for (Entry<Integer, GrammarState> item : map.entrySet()) {
    			int symbol = item.getKey();
    			System.out.println("on symbol: " + SymbolDefine.getSymbolStr(symbol));
    			System.out.println("to state: ");
    			GrammarState to = item.getValue();
    			to.print();
    		}
    		
    		System.out.println("********end a map row********");
    	}
    }
    
    public void printReduceInfo() {
    
    	for(Entry<GrammarState, HashMap<Integer, GrammarState>> item : transitionMap.entrySet()) {
    		GrammarState state = item.getKey();
    		state.print();
    		HashMap<Integer, Integer> map = state.makeReduce();
    		for (Entry<Integer, Integer> entry : map.entrySet()) {
    			System.out.println("Reduce on symbol: " + SymbolDefine.getSymbolStr(entry.getKey()) +
    					" to Production " + entry.getValue());
    		}
    	}
    	
    	
    }
    
   
    
}
