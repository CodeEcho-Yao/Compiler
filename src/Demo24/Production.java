package Demo24;


import utils.CompilerParser.SymbolDefine;

import java.util.ArrayList;


public class Production {
    private int dotPos = 0;
    private boolean printDot = false;
    private int left = 0;
    private ArrayList<Integer> right = null;
    private ArrayList<Integer> lookAhead = new ArrayList<Integer>();
    
    public Production(int left, int dot, ArrayList<Integer> right) {
        this.left = left;
        this.right = right;
        
        if (dot >= right.size()) {
        	dot = right.size();
        }
        
        lookAhead.add(SymbolDefine.EOI);
        
        this.dotPos = dot;
    }
    
    public Production dotForward() {
    	Production product = new Production(this.left, dotPos+1, this.right); 
    	
    	product.lookAhead = new ArrayList<Integer>();
    	for (int i = 0; i < this.lookAhead.size(); i++) {
    		product.lookAhead.add(this.lookAhead.get(i));
    	}
    	
    	return  product;
    }
    
    public Production cloneSelf() {
    	
        Production product = new Production(this.left, dotPos, this.right); 
    	
    	product.lookAhead = new ArrayList<Integer>();
    	for (int i = 0; i < this.lookAhead.size(); i++) {
    		product.lookAhead.add(this.lookAhead.get(i));
    	}
    	
    	return  product;
    }
    
    public ArrayList<Integer> computeFirstSetOfBetaAndC() {
    	
    	ArrayList<Integer> set = new ArrayList<Integer>();
    	set.addAll(lookAhead);
    	
    	ProductionManager manager = ProductionManager.getProductionManager();
    	
    	for (int i = dotPos + 1; i < right.size(); i++) {
    		ArrayList<Integer> firstSet = manager.getFirstSetBuilder().getFirstSet(right.get(i));
    		
    		for (int j = 0; j < firstSet.size(); j++) {
    			if (set.contains(firstSet.get(j)) == false) {
    				set.add(firstSet.get(j));
    			}
    		}
    		
    		if (manager.getFirstSetBuilder().isSymbolNullable(right.get(i)) == false) {
    			break;
    		}
    	}
    	
    	return set;
    }
    
    public void printBeta() {
    	System.out.print("Beta part of production is: ");
    	for (int i = dotPos + 1; i < right.size(); i++) {
    		System.out.print(SymbolDefine.getSymbolStr(right.get(i)) + " ");
    	}
    	
    	if (dotPos+1 >= right.size()) {
    		System.out.print("null");
    	}
    	
    	System.out.print("\n");
    }
    
    public void addLookAheadSet(ArrayList<Integer> list) {
    	for (int i = 0; i < list.size(); i++) {
    		if (lookAhead.contains(list.get(i)) == false) {
    			lookAhead.add(list.get(i));
    		}
    	}
    }
    
    public int getLeft() {
    	return left;
    }
    
    public ArrayList<Integer> getRight() {
    	return right;
    }
    
    public int getDotPosition() {
    	return dotPos;
    }
    
    public int getDotSymbol() {
    	if (dotPos >= right.size()) {
    		return SymbolDefine.UNKNOWN_SYMBOL;
    	}
    	return right.get(dotPos);
    }
    
    @Override
    public boolean equals(Object obj) {
    	Production product = (Production)obj;
    	
    	if (this.productionEequals(product) && this.lookAheadSetComparing(product) == 0) {
    		return true;
    	}
    	
    	
    	return false;
    	
    }
    
    public boolean coverUp(Production product) {
    	
    	if (this.productionEequals(product) && this.lookAheadSetComparing(product) > 0) {
    		return true;
    	}
    	
    	return false;
    }
    
    private boolean productionEequals(Production product) {
    	if (this.left != product.getLeft()) {
    		return false;
    	}
    	
    	if (this.right.equals(product.getRight()) == false) {
    		return false;
    	}
    	
    	if (this.dotPos != product.getDotPosition()) {
    		return false;
    	}
    	
    	return true;
    }
    
    private int lookAheadSetComparing(Production product) {
    	if (this.lookAhead.size() > product.lookAhead.size()) {
    		return 1;
    	}
    	
    	if (this.lookAhead.size() < product.lookAhead.size()) {
    		return -1;
    	}
    	
    	if (this.lookAhead.size() == product.lookAhead.size()) {
    		for (int i = 0; i < this.lookAhead.size(); i++) {
        		if (this.lookAhead.get(i) != product.lookAhead.get(i)) {
        			return -1;
        		}
        	} 
    	}
    	
    	return 0;
    }
    
    public void print() {
    	System.out.print(SymbolDefine.getSymbolStr(left) + " -> " );
    	for (int i = 0; i < right.size(); i++) {
    		if (i == dotPos) {
    			 printDot = true;
    			 System.out.print(".");
    		}
    		
    		System.out.print(SymbolDefine.getSymbolStr(right.get(i)) + " ");
    	}
    	
    	if (printDot == false) {
    		System.out.print(".");
    	}
    	
    	System.out.print("look ahead set: { ");
    	for (int i = 0; i < lookAhead.size(); i++) {
    		System.out.print(SymbolDefine.getSymbolStr(lookAhead.get(i)) + " ");
    	}
    	System.out.println("}");
    }


  

}
