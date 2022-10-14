package Demo23;

import Demo20.SymbolDefine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ProductionManager {
	
	private static ProductionManager self = null;
	
    private HashMap<Integer, ArrayList<Production>> productionMap = new HashMap<Integer, ArrayList<Production>>();
    
    public static ProductionManager getProductionManager() {
    	if (self == null) {
    		self = new ProductionManager();
    	}
    	
    	return self;
    }
    
    public void initProductions() {
    	//s -> e
    	ArrayList<Integer> right = null;
    	right = getProductionRight(new int[]{SymbolDefine.EXPR});
    	Production production = new Production(SymbolDefine.STMT, 0, right);
    	addProduction(production);
    	
    	//e -> e + t
    	right = getProductionRight(new int[]{SymbolDefine.EXPR, SymbolDefine.PLUS, SymbolDefine.TERM});
    	production = new Production(SymbolDefine.EXPR, 0, right);
    	addProduction(production);
    	
    	//e -> t
    	right = getProductionRight(new int[]{SymbolDefine.TERM});
    	production = new Production(SymbolDefine.EXPR, 0, right);
    	addProduction(production);
    	
    	//t -> t * f
    	right = getProductionRight(new int[]{SymbolDefine.TERM, SymbolDefine.TIMES, SymbolDefine.FACTOR});
    	production = new Production(SymbolDefine.TERM, 0, right);
    	addProduction(production);
    	
    	//t -> f
    	right = getProductionRight(new int[]{SymbolDefine.FACTOR});
    	production = new Production(SymbolDefine.TERM, 0, right);
    	addProduction(production);
    	
    	//f -> ( e )
    	right = getProductionRight(new int[]{SymbolDefine.LP, SymbolDefine.EXPR, SymbolDefine.RP});
    	production = new Production(SymbolDefine.FACTOR, 0, right);
    	addProduction(production);
    	
    	//f->NUM
    	right = getProductionRight(new int[]{SymbolDefine.NUM_OR_ID});
    	production = new Production(SymbolDefine.FACTOR, 0, right);
    	addProduction(production);
    }
    
    public void printAllProductions() {
    	for (Map.Entry<Integer, ArrayList<Production>> entry : productionMap.entrySet()) {
    		ArrayList<Production> list = entry.getValue();
    		for (int i = 0; i < list.size(); i++) {
    			list.get(i).print();
    			System.out.print("\n");
    		}
    	}
    }
    
    private ArrayList<Integer> getProductionRight(int[] arr) {
    	ArrayList<Integer> right = new ArrayList<Integer>();
    	for (int i = 0; i < arr.length; i++) {
    		right.add(arr[i]);
    	}
    	
    	return right;
    }
    
    private void addProduction(Production production) {
    	
    	ArrayList<Production> productionList = productionMap.get(production.getLeft());
		
		if (productionList == null) {
			productionList = new ArrayList<Production>();
			productionMap.put(production.getLeft(), productionList);
		}
		
		if (productionList.contains(production) == false) {
			productionList.add(production);
		}
    }
    
    public ArrayList<Production> getProduction(int left) {
    	return productionMap.get(left);
    }
    
    private ProductionManager() {
    	
    }
}
