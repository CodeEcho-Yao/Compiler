package Demo26;

import utils.CompilerParser.SymbolDefine;

import java.util.ArrayList;


public class Production {
    private int dotPos = 0;
    private boolean printDot = false;
    private int left = 0;
    private ArrayList<Integer> right = null;
    
    public Production(int left, int dot, ArrayList<Integer> right) {
        this.left = left;
        this.right = right;
        
        if (dot >= right.size()) {
        	dot = right.size();
        }
        
        this.dotPos = dot;
    }
    
    public Production dotForward() {
    	return new Production(this.left, dotPos+1, this.right);
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
    	
    	System.out.print("\n");
    }


  

}
