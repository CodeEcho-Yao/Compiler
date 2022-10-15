package Demo27;

import utils.CompilerParser.SymbolDefine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class ProductionManager {
	
	private static ProductionManager self = null;
	
	FirstSetBuilder firstSetBuilder = new FirstSetBuilder();
	
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
    	Production production = new Production(0,SymbolDefine.STMT, 0, right);
    	addProduction(production);
    	
    	//e -> e + t
    	right = getProductionRight(new int[]{SymbolDefine.EXPR, SymbolDefine.PLUS, SymbolDefine.TERM});
    	production = new Production(1, SymbolDefine.EXPR, 0, right);
    	addProduction(production);
    	
    	//e -> t
    	right = getProductionRight(new int[]{SymbolDefine.TERM});
    	production = new Production(2, SymbolDefine.EXPR, 0, right);
    	addProduction(production);
    	
    	//t -> t * f
    	right = getProductionRight(new int[]{SymbolDefine.TERM, SymbolDefine.TIMES, SymbolDefine.FACTOR});
    	production = new Production(3, SymbolDefine.TERM, 0, right);
    	addProduction(production);
    	
    	//t -> f
    	right = getProductionRight(new int[]{SymbolDefine.FACTOR});
    	production = new Production(4, SymbolDefine.TERM, 0, right);
    	addProduction(production);
    	
    	//f -> ( e )
    	right = getProductionRight(new int[]{SymbolDefine.LP, SymbolDefine.EXPR, SymbolDefine.RP});
    	production = new Production(5, SymbolDefine.FACTOR, 0, right);
    	addProduction(production);
    	
    	//f->NUM
    	right = getProductionRight(new int[]{SymbolDefine.NUM_OR_ID});
    	production = new Production(6,SymbolDefine.FACTOR, 0, right);
    	addProduction(production);
    }
    
    public FirstSetBuilder getFirstSetBuilder() {
    	return firstSetBuilder;
    }
    
    public void printAllProductions() {
    	for (Entry<Integer, ArrayList<Production>> entry : productionMap.entrySet()) {
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
    
    public Production getProductionByIndex(int index) {
    	
    	for (Entry<Integer, ArrayList<Production>> item : productionMap.entrySet()) {
    		ArrayList<Production> productionList = item.getValue();
    		for (int i = 0; i < productionList.size(); i++) {
    			if (productionList.get(i).getProductionNum() == index) {
    				return productionList.get(i);
    			}
    		}
    	}
    	
    	return null;
    }
    
    private ProductionManager() {
    	firstSetBuilder.runFirstSets();	
    }
}
