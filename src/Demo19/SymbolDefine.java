package Demo19;

import java.util.HashMap;


public class SymbolDefine {
	public static final int EXPR = 257;
	public static final int EXPR_PRIME = 259;
	public static final int FACTOR = 260;
	public static final int STMT = 256;
	public static final int TERM = 258;
	public static final int TERM_PRIME = 261;
	
	public static final int ACTION_0 = 512;
	public static final int ACTION_1 = 513;
	public static final int ACTION_2 = 514;
	public static final int ACTION_3 = 515;
	public static final int ACTION_4 = 516;
	
	
	public static final int LP = 5;
	public static final int NUM_OR_ID = 4;
	public static final int PLUS = 2;
	public static final int RP = 6;
	public static final int SEMI = 1;
	public static final int TIMES = 3;
	public static final int EOI = 0;
	public static final int  WHITE_SPACE = 7;
	public static final int  UNKNOWN_SYMBOL = 8;
	
	
	public static String getSymbolStr(int symbol) {
		switch (symbol) {
		case EXPR:
			return "EXPR";
		case EXPR_PRIME:
			return "EXPR_PRIME";
		case FACTOR:
			return "FACTOR";
		case STMT:
			return "STMT";
		case TERM:
			return "TERM";
		case TERM_PRIME:
			return "TERM_PRIME";
		case LP:
			return "LEFT_PARENT";
		case NUM_OR_ID:
			return "NUM_OR_ID";
		case PLUS:
			return "PLUS";
		case RP:
			return "RIGHT_PARENT";
		case SEMI:
			return "SEMI";
		case TIMES:
			return "TIMES";
			default:
				return "UNKNOWN SYMBOL";
		}
	}
}
