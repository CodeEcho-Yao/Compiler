package Demo18;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class ParseTableBuilder {
	
    private HashMap<Integer, Symbols> symbolMap = new HashMap<Integer, Symbols>();
    private ArrayList<Symbols> symbolArray = new ArrayList<Symbols>();
    private boolean runFirstSetPass = true;
    
    public ParseTableBuilder() {
    	initProductions();
    }
    
    private void initProductions() {
    	ArrayList<int[]> productions = new ArrayList<int[]>();
    	productions.add(new int[]{SymbolDefine.EXPR, SymbolDefine.SEMI});
    	Symbols stmt = new Symbols(SymbolDefine.STMT, false, productions);
    	symbolMap.put(SymbolDefine.STMT, stmt);
    	symbolArray.add(stmt);
    	
    	
    	productions = new ArrayList<int[]>();
    	productions.add(new int[] {SymbolDefine.TERM, SymbolDefine.EXPR_PRIME});
    	Symbols expr = new Symbols(SymbolDefine.EXPR, true, productions);
    	symbolMap.put(SymbolDefine.EXPR, expr);
    	symbolArray.add(expr);
    	
    	productions = new ArrayList<int[]>();
    	productions.add(new int[] {SymbolDefine.PLUS, SymbolDefine.TERM, SymbolDefine.EXPR_PRIME});
    	Symbols expr_prime = new Symbols(SymbolDefine.EXPR_PRIME, true, productions);
    	symbolMap.put(SymbolDefine.EXPR_PRIME, expr_prime);
    	symbolArray.add(expr_prime);
    	
    	productions = new ArrayList<int[]>();
    	productions.add(new int[]{SymbolDefine.FACTOR, SymbolDefine.TERM_PRIME});
    	Symbols term = new Symbols(SymbolDefine.TERM, false, productions);
    	symbolMap.put(SymbolDefine.TERM, term);
    	symbolArray.add(term);
    	
    	productions = new ArrayList<int[]>();
    	productions.add(new int[] {SymbolDefine.TIMES, SymbolDefine.FACTOR, SymbolDefine.TERM_PRIME});
    	Symbols term_prime = new Symbols(SymbolDefine.TERM_PRIME, true, productions);
    	symbolMap.put(SymbolDefine.TERM_PRIME, term_prime);
    	symbolArray.add(term_prime);
    	
    	productions = new ArrayList<int[]>();
    	productions.add(new int[] {SymbolDefine.LP, SymbolDefine.EXPR, SymbolDefine.RP});
    	productions.add(new int[] {SymbolDefine.NUM_OR_ID});
    	Symbols factor = new Symbols(SymbolDefine.FACTOR, false, productions);
    	symbolMap.put(SymbolDefine.FACTOR, factor);
    	symbolArray.add(factor);
    	
    	Symbols semi = new Symbols(SymbolDefine.SEMI, false, null);
    	symbolMap.put(SymbolDefine.SEMI, semi);
    	symbolArray.add(semi);
    	
    	Symbols plus = new Symbols(SymbolDefine.PLUS, false, null);
    	symbolMap.put(SymbolDefine.PLUS, plus);
    	symbolArray.add(plus);
    	
    	Symbols times = new Symbols(SymbolDefine.TIMES, false, null);
    	symbolMap.put(SymbolDefine.TIMES, times);
    	symbolArray.add(times);
    	
    	Symbols lp = new Symbols(SymbolDefine.LP, false, null);
    	symbolMap.put(SymbolDefine.LP, lp);
    	symbolArray.add(lp);
    	
    	Symbols rp = new Symbols(SymbolDefine.RP, false, null);
    	symbolMap.put(SymbolDefine.RP, rp);
    	symbolArray.add(rp);
    	
    	Symbols num_or_id = new Symbols(SymbolDefine.NUM_OR_ID, false, null);
    	symbolMap.put(SymbolDefine.NUM_OR_ID, num_or_id);
    	symbolArray.add(num_or_id);
    }
    
    public void runFirstSets() {
    	while (runFirstSetPass) {
    		runFirstSetPass = false;
    		
    		Iterator<Symbols> it = symbolArray.iterator();
    		while (it.hasNext()) {
    			Symbols symbol = it.next();
    			addSymbolFirstSet(symbol);
    		}
    		
    		printAllFirstSet();
    		System.out.println("============");
    	}
    }
    
    private void addSymbolFirstSet(Symbols symbol) {
    	if (isSymbolTerminals(symbol.value) == true) {
    		return;
    	}
    	
    	for (int i = 0;  i < symbol.productions.size(); i++) {
    		int[] rightSize = symbol.productions.get(i);
    		if (isSymbolTerminals(rightSize[0]) && symbol.firstSet.contains(rightSize[0]) == false) {
    			runFirstSetPass = true;
    			symbol.firstSet.add(rightSize[0]);
    		}
    		else if (isSymbolTerminals(rightSize[0]) == false) {
    			//add first set of nullable
    			int pos = 0;
    			Symbols curSymbol = null;
    			do {
    				curSymbol = symbolMap.get(rightSize[pos]);
    				if (symbol.firstSet.containsAll(curSymbol.firstSet) == false) {
    					runFirstSetPass = true;
    					
    					for (int j = 0; j < curSymbol.firstSet.size(); j++) {
    						if (symbol.firstSet.contains(curSymbol.firstSet.get(j)) == false) {
    							symbol.firstSet.add(curSymbol.firstSet.get(j));
    						}
    					}//for (int j = 0; j < curSymbol.firstSet.size(); j++)
    					
    				}//if (symbol.firstSet.containsAll(curSymbol.firstSet) == false)
    				
    				pos++;
    			}while(pos < rightSize.length && curSymbol.isNullable);
    		} // else
    		
    	}//for (int i = 0; i < symbol.productions.size(); i++)
    }
    
    private boolean isSymbolTerminals(int symbol) {
    	return symbol < 256;
    }
    
    public void printAllFirstSet() {
    	Iterator<Symbols> it = symbolArray.iterator();
		while (it.hasNext()) {
		    Symbols sym = it.next();
		    printFirstSet(sym);
		}
    }
    
    private void printFirstSet(Symbols symbol) {
    	if (isSymbolTerminals(symbol.value) == true) {
    		return;
    	}
    	
    	String s = SymbolDefine.getSymbolStr(symbol.value);
    	s += "{ ";
    	for (int i = 0; i < symbol.firstSet.size(); i++) {
    		s += SymbolDefine.getSymbolStr(symbol.firstSet.get(i)) + " ";
    	}
    	s += " }";
    	
    	System.out.println(s);
    }
}
