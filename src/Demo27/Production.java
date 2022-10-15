package Demo27;

import utils.CompilerParser.SymbolDefine;

import java.util.ArrayList;


public class Production {
    private int dotPos = 0;
    private boolean printDot = false;
    private int left = 0;
    private ArrayList<Integer> right = null;
    private ArrayList<Integer> lookAhead = new ArrayList<Integer>();
    private int productionNum = -1;
    
    public Production(int productionNum, int left, int dot, ArrayList<Integer> right) {
        this.left = left;
        this.right = right;
        this.productionNum = productionNum;
        
        if (dot >= right.size()) {
        	dot = right.size();
        }
        
        lookAhead.add(SymbolDefine.EOI);
        
        this.dotPos = dot;
    }
    
  
    public Production dotForward() {
    	Production product = new Production(productionNum, this.left, dotPos+1, this.right); 
    	
    	product.lookAhead = new ArrayList<Integer>();
    	for (int i = 0; i < this.lookAhead.size(); i++) {
    		product.lookAhead.add(this.lookAhead.get(i));
    	}
    	
    	return  product;
    }
    
    public Production cloneSelf() {
    	
        Production product = new Production(productionNum, this.left, dotPos, this.right); 
    	
    	product.lookAhead = new ArrayList<Integer>();
    	for (int i = 0; i < this.lookAhead.size(); i++) {
    		product.lookAhead.add(this.lookAhead.get(i));
    	}
    	
    	return  product;
    }
    
    public ArrayList<Integer> computeFirstSetOfBetaAndC() {
    	/*
    	 * 计算 First(β C)
    	 * 将β 和 C ,前后相连再计算他们的First集合，如果β 里面的每一项都是nullable的，那么
    	 * First(β C) 就是 First(β) 并上First(C), 由于C 必定是终结符的组合，所以First(C)等于C的第一个非终结符
    	 * 例如C = {+, * , EOI} , First(C) = {+}
    	 */
    	
    	ArrayList<Integer> set = new ArrayList<Integer>();
    	for (int i = dotPos + 1; i < right.size(); i++) {
    		set.add(right.get(i));
    	}
    	set.addAll(lookAhead);
    	
    	ProductionManager manager = ProductionManager.getProductionManager();
    	ArrayList<Integer> firstSet = new ArrayList<Integer>();
    	
    	for (int i = 0; i < set.size(); i++) {
    		ArrayList<Integer> lookAhead = manager.getFirstSetBuilder().getFirstSet(set.get(i));
    		
    		for (int j = 0; j < lookAhead.size(); j++) {
    			if (firstSet.contains(lookAhead.get(j)) == false) {
    				firstSet.add(lookAhead.get(j));
    			}
    		}
    		
    		if (manager.getFirstSetBuilder().isSymbolNullable(set.get(i)) == false) {
    			break;
    		}
    	}
    	
    	return firstSet;
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
    	lookAhead = list;
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
    	/*
    	 * 判断两个表达式是否相同有两个条件，一是表达式相同，而是对应的Look ahead 集合也必须一致
    	 */
    	Production product = (Production)obj;
    	
    	if (this.productionEequals(product) && this.lookAheadSetComparing(product) == 0) {
    		return true;
    	}
    	
    	return false;
    }
    
   
    
    public boolean coverUp(Production product) {
    	/*
    	 * 如果表达式相同，但是表达式1的look ahead 集合 覆盖了表达式2的look ahead 集合，
    	 * 那么表达式1 就覆盖 表达式2
    	 */
    	if (this.productionEequals(product) && this.lookAheadSetComparing(product) > 0) {
    		return true;
    	}
    	
    	return false;
    }
    
    public  boolean productionEequals(Production product) {
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
    
    
    public int lookAheadSetComparing(Production product) {
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
    
    public boolean canBeReduce() {
    	return dotPos >= right.size();
    }
    
    public int  getProductionNum() {
    	return productionNum;
    }
    
    public ArrayList<Integer>  getLookAheadSet() {
    	return lookAhead;
    }

}
