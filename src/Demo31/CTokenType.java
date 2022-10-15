package Demo31;

public enum CTokenType {
	
	//non-termals
	    
		PROGRAM, EXT_DEF_LIST, EXT_DEF, OPT_SPECIFIERS, EXT_DECL_LIST,
		EXT_DECL, VAR_DECL, SPECIFIERS, 
	
		TYPE_OR_CLASS, TYPE_NT,
		
		/*struct def*/
		STRUCT_SPECIFIER,OPT_TAG,DEF_LIST,TAG,DEF,DECL_LIST,DECL,
		
		FUNCT_DECL, VAR_LIST, PARAM_DECLARATION,
		
		TYPE_SPECIFIER, NEW_NAME, NAME_NT,
		
		
	
    //terminals
	NAME, TYPE, STRUCT,CLASS, LP, RP, LB, RB,PLUS,LC,RC,
	
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
