package Demo28;

public enum CTokenType {
	
	//non-termals
	    
		PROGRAM, EXT_DEF_LIST, EXT_DEF, OPT_SPECIFIERS, EXT_DECL_LIST,
		EXT_DECL, VAR_DECL, SPECIFIERS, 
		
	//	stmt, expr, term, factor,
		
		
		TYPE_OR_CLASS, TYPE_NT,
		TYPE_SPECIFIER, NEW_NAME, NAME_NT,
		
		
	
    //terminals
	NAME, TYPE, CLASS, LP, RP, LB, RB,PLUS,
	
//	NUM, TIMES,
	
	COMMA, SEMI, WHITE_SPACE, EQUAL,TTYPE, STAR, UNKNOWN_TOKEN;
	
	
	
	public static final int FIRST_TERMINAL_INDEX = NAME.ordinal();
	public static  final int LAST_TERMINAL_INDEX = UNKNOWN_TOKEN.ordinal();
	
	public static final int FIRST_NON_TERMINAL_INDEX = PROGRAM.ordinal();
	public static final int LAST_NON_TERMINAL_INDEX = NAME_NT.ordinal();
	
	public static String getSymbolStr(int val) {
		return CTokenType.values()[val].toString();
	}
	
	public static boolean isTerminal(int val) {
		if (FIRST_TERMINAL_INDEX <= val && val <= LAST_TERMINAL_INDEX) {
			return true;
		}
		
		return false;
	}
	
}
