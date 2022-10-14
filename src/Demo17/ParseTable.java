package Demo17;

import java.util.ArrayList;


public class ParseTable {
    private ArrayList<int[]> yy_pushtab = new ArrayList<int[]>();
    private int[][]  yy_d = null;
    private int rowBase = SymbolDefine.STMT;
    public ParseTable() {
    	initYyPushTable();
    	initYydTable();
    }
    
    private void initYyPushTable() {
    	int[] yyp0 = null;
    	yy_pushtab.add(yyp0);
    	
    	int[] yyp1 = new int[]{SymbolDefine.STMT, SymbolDefine.SEMI, SymbolDefine.ACTION_1, SymbolDefine.EXPR, SymbolDefine.ACTION_0};
    	yy_pushtab.add(yyp1);
    	
    	int[] yyp2 = new int[]{SymbolDefine.EXPR_PRIME, SymbolDefine.TERM};
    	yy_pushtab.add(yyp2);
    	
    	int[] yyp3 = new int[] {SymbolDefine.EXPR_PRIME, SymbolDefine.ACTION_2, SymbolDefine.TERM, SymbolDefine.ACTION_0,
    			SymbolDefine.PLUS};
    	yy_pushtab.add(yyp3);
    	
    	int[] yyp4 = null;
    	yy_pushtab.add(yyp4);
    	
    	int[] yyp5 = new int[] {SymbolDefine.TERM_PRIME, SymbolDefine.FACTOR};
    	yy_pushtab.add(yyp5);
    	
    	int[] yyp6 = new int[] {SymbolDefine.TERM_PRIME, SymbolDefine.ACTION_3, SymbolDefine.FACTOR, SymbolDefine.ACTION_0,
    			SymbolDefine.TIMES};
    	yy_pushtab.add(yyp6);
    	
    	
    	int[] yyp7 = null;
    	yy_pushtab.add(yyp7);
    	
    	int[] yyp8 = new int[]{SymbolDefine.ACTION_4, SymbolDefine.NUM_OR_ID};
    	yy_pushtab.add(yyp8);
    	
    	int[] yyp9 = new int[] {SymbolDefine.RP, SymbolDefine.EXPR, SymbolDefine.LP};
    	yy_pushtab.add(yyp9);
    	
    	}
    
    private void initYydTable() {
    	yy_d = new int[6][];
    	
    	yy_d[SymbolDefine.STMT - rowBase] = new int[]{ 0, -1, -1, -1, 1,  1, -1};
    	yy_d[SymbolDefine.EXPR - rowBase] = new int[]{-1, -1, -1, -1, 2, 2, -1};
    	yy_d[SymbolDefine.TERM - rowBase] = new int[]{-1, -1, -1, -1, 5, 5, -1};
    	yy_d[SymbolDefine.EXPR_PRIME - rowBase] = new int[] {-1, 4, 3, -1, -1, -1, 4};
    	yy_d[SymbolDefine.FACTOR - rowBase] = new int[]{-1, -1, -1, -1, 8, 9, -1};
    	yy_d[SymbolDefine.TERM_PRIME - rowBase] = new int[]{-1, 7, 7, 6, -1, -1, 7};
    }
    
    public int getWhatToDo(int topOfStackSymbol, int lookAheadSymbol) {
    	return yy_d[topOfStackSymbol - rowBase][lookAheadSymbol];
    }
    
    
    public int[] getPushTabItems(int whatToDo) {
    	return yy_pushtab.get(whatToDo);
    }
}
