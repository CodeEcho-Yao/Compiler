package utils.CompilerParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class ParseTableBuilder {
	
    private HashMap<Integer, Symbols> symbolMap = new HashMap<Integer, Symbols>();
    private ArrayList<Symbols> symbolArray = new ArrayList<Symbols>();
    private boolean runFirstSetPass = true;
    private boolean runFollowSetPass = true;
    private int[][] parseTable;
	int productionCount = 0;
	
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
    	productions.add(new int[0]);
    	Symbols expr = new Symbols(SymbolDefine.EXPR, true, productions);
    	symbolMap.put(SymbolDefine.EXPR, expr);
    	symbolArray.add(expr);
    	
    	productions = new ArrayList<int[]>();
    	productions.add(new int[] {SymbolDefine.PLUS, SymbolDefine.TERM, SymbolDefine.EXPR_PRIME});
    	productions.add(new int[0]);
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
    	productions.add(new int[0]);
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
    		
    	}
    	
    	printAllFirstSet();
		System.out.println("============");
    }
    
    private void addSymbolFirstSet(Symbols symbol) {
    	if (isSymbolTerminals(symbol.value) == true) {
    		return;
    	}
    	
    	for (int i = 0;  i < symbol.productions.size(); i++) {
    		int[] rightSize = symbol.productions.get(i);
    		if (rightSize.length == 0) {
    			continue;
    		}
    		
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
    	System.out.println("============");
    }
    
    public void runFollowSets() {
    	runFirstSets();
    	
    	while (runFollowSetPass) {
    		runFollowSetPass = false;
    		
    		Iterator<Symbols> it = symbolArray.iterator();
    		while (it.hasNext()) {
    			Symbols symbol = it.next();
    			addSymbolFollowSet(symbol);
    		}	
    		
    		
    	}
    	
    	printAllFollowSet();
		System.out.println("***********************");
    }
    
    private void printAllFollowSet() {
    	Iterator<Symbols> it = symbolArray.iterator();
		while (it.hasNext()) {
		    Symbols sym = it.next();
		    printFollowSet(sym);
		}
    }
    
    private void printFollowSet(Symbols symbol) {
    	if (isSymbolTerminals(symbol.value) == true) {
    		return;
    	}
    	
    	String s = SymbolDefine.getSymbolStr(symbol.value);
    	s += "{ ";
    	for (int i = 0; i < symbol.followSet.size(); i++) {
    		s += SymbolDefine.getSymbolStr(symbol.followSet.get(i)) + " ";
    	}
    	s += " }";
    	
    	System.out.println(s);
    }
    private void addSymbolFollowSet(Symbols symbol) {
    	if (isSymbolTerminals(symbol.value) == true) {
    		return;
    	}
    	
    	for (int i = 0;  i < symbol.productions.size(); i++) {
    	    int[] rightSize = symbol.productions.get(i);
    	    
    	    /*
    	     * s -> a' a1 a2 a3....an b
    	     * a1 a2 a3...an are nullable, b is no nullable
    	     * follow(a1) is union of first(a2) first(a3)... first(an) first(b)
    	     * flllow(a2) is union of first(a3) first(a4) ... first(an) first(b)
    	     * ...
    	     */
    	    for (int j = 0; j < rightSize.length; j++) {
    	    	Symbols current = symbolMap.get(rightSize[j]);
    	    	if(isSymbolTerminals(current.value) == true) {
    	    		continue;
    	    	}
    	    	
    	    	for (int k = j + 1; k < rightSize.length; k++) {
    	    		Symbols next = symbolMap.get(rightSize[k]);
    	    		addSetToFollowSet(current, next.firstSet);
    	    		if (next.isNullable == false) {
    	    			break;
    	    		}
    	    	}
    	    }
    	    
    	    /*
    	     * s -> ... a1 a2 a3 ... an
    	     * a1 a2 a3 ... an are nullable no terminals
    	     * everything in follow(s) is in follow(a1) follow(a2)...follow(an)
    	     */
    	    int pos = rightSize.length - 1;
    	    while (pos >= 0) {
    	    	Symbols current = symbolMap.get(rightSize[pos]);
    	    	if (isSymbolTerminals(current.value) == false) {
    	    		addSetToFollowSet(current, symbol.followSet);
    	    	}
    	    	if (isSymbolTerminals(current.value) == true && current.isNullable == false) {
    	    		break;
    	    	}
    	    	pos--;
    	    }
    	    
    	}
    	
    }
    
    private void addSetToFollowSet(Symbols symbolBeAdded, ArrayList<Integer> set) {
    	boolean add = false;
    	if (symbolBeAdded.followSet.contains(set) == false) {
    		for (int i = 0; i < set.size(); i++) {
    			if (symbolBeAdded.followSet.contains(set.get(i)) == false) {
    				symbolBeAdded.followSet.add(set.get(i));
    				runFollowSetPass = true;
    				add = true;
    			}
    		}
    	}
    	
    	if (add) {
    		System.out.print("add symbol to followset:");
    		printFollowSet(symbolBeAdded);
    	}
    }
    
    public void runSelectionSet() {
    	runFirstSets();
    	runFollowSets();
    	Iterator<Symbols> it = symbolArray.iterator();
		while (it.hasNext()) {
			Symbols symbol = it.next();
			addSymbolSelectionSet(symbol);
		}
    	
    }
    
    private void addSymbolSelectionSet(Symbols symbol) {
    	if (isSymbolTerminals(symbol.value) == true) {
    		return;
    	}
    	
    	boolean isNullableProduction = true;
    	for (int i = 0;  i < symbol.productions.size(); i++) {
    	    int[] rightSize = symbol.productions.get(i);
    	    ArrayList<Integer> selection = new ArrayList<Integer>();
    	    
    	    for (int j = 0; j < rightSize.length; j++) {
    	    	Symbols next = symbolMap.get(rightSize[j]);
    	    	if (next.isNullable == false) {
    	    		isNullableProduction = false;
    	    		addSetToSelectionSet(selection, next.firstSet);
    	    		break;
    	    	}  
    	    	
    	    	addSetToSelectionSet(selection, next.firstSet);
    	    }
    	    
    	    if (isNullableProduction) {
        		addSetToSelectionSet(selection, symbol.followSet);
        	}
        	
    	    symbol.selectionSet.add(selection);
    	    isNullableProduction = true;
    	}
    	
    }
    
    private void addSetToSelectionSet(ArrayList<Integer> selectionSet, ArrayList<Integer> set) {
    	for (int i = 0; i < set.size(); i++) {
    		if (selectionSet.contains(set.get(i)) == false) {
    			selectionSet.add(set.get(i));
    		}
    	}
    	
    }
    
    public void buildParseTable() {
	
		initializeParseTable();
		setParsetTable();
		printParseTable();
    }
    
    private void initializeParseTable() {
    	parseTable = new int[SymbolDefine.NO_TERMINAL_MAXRANGE][SymbolDefine.TERMINAL_MAX_RANGE];
		for (int i = 0; i < SymbolDefine.NO_TERMINAL_MAXRANGE; i++) {
			for (int j = 0; j < SymbolDefine.TERMINAL_MAX_RANGE; j++) {
				parseTable[i][j] = -1;
			}
		}
    }
    
    private void setParsetTable() {
		Iterator it = symbolArray.iterator();
		while (it.hasNext()) {
			Symbols symbol = (Symbols) it.next();
			if (isSymbolTerminals(symbol.value) == true) {
	    		continue;
	    	}
			
			for (int i = 0;  i < symbol.selectionSet.size(); i++) {
				ArrayList<Integer> selection = symbol.selectionSet.get(i);
				for (int j = 0; j < selection.size(); j++) {
					int column = selection.get(j);
					if (column > SymbolDefine.TERMINAL_MAX_RANGE) {
						column = column;
					}
					parseTable[symbol.value - SymbolDefine.NO_TERMINAL_VALUE_BASE][selection.get(j)] = productionCount;
				}
				productionCount++;
			}
		}
    }
    
    private void printParseTable() {
    	for (int i = 0; i < SymbolDefine.NO_TERMINAL_MAXRANGE; i++) {
    		for (int j = 0; j < SymbolDefine.TERMINAL_MAX_RANGE; j++) {
    			System.out.print(parseTable[i][j] + " ");
    		}
    		System.out.print("\n");
    	}
    }
}
