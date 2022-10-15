package Demo29;

import java.util.HashMap;
import java.util.Scanner;


public class Lexer {
    
    public int lookAhead = CTokenType.UNKNOWN_TOKEN.ordinal();
    
    public String yytext = "";
    public int yyleng = 0;
    public int yylineno = 0;
    
    private String input_buffer = "";
    private String current = "";
    private HashMap<String, Integer> keywordMap = new HashMap<String, Integer>();
    
    public Lexer() {
    	initKeyWordMap();
    }
    
    private void initKeyWordMap() {
    	keywordMap.put("auto", CTokenType.CLASS.ordinal());
    	keywordMap.put("static", CTokenType.CLASS.ordinal());
    	keywordMap.put("register", CTokenType.CLASS.ordinal());
    	keywordMap.put("char", CTokenType.TYPE.ordinal());
    	keywordMap.put("float", CTokenType.TYPE.ordinal());
    	keywordMap.put("double", CTokenType.TYPE.ordinal());
    	keywordMap.put("int", CTokenType.TYPE.ordinal());
    	keywordMap.put("long", CTokenType.TYPE.ordinal());
    	keywordMap.put("void", CTokenType.TYPE.ordinal());
    }
    
    private boolean isAlnum(char c) {
    	if (Character.isAlphabetic(c) == true ||
    		    Character.isDigit(c) == true) {
    		return true;
    	}
    	
    	return false;
    }
    
    private int lex() {
    
    	while (true) {
    		
    		while (current == "") {
    		    Scanner s = new Scanner(System.in);
    		    while (true) {
    		    	String line = s.nextLine();
    		    	if (line.equals("end")) {
    		    		break;
    		    	}
    		    	input_buffer += line;
    		    }
    		    s.close();
    		    
    		    if (input_buffer.length() == 0) {
    		    	current = "";
    		    	return CTokenType.SEMI.ordinal();
    		    }
    		    
    		    current = input_buffer;
    		    ++yylineno;
    		    current.trim();
    		}//while (current == "")
    		
    		if (current.isEmpty()) {
    			return CTokenType.SEMI.ordinal();
    		}

    		    for (int i = 0; i < current.length(); i++) {
    		    	
    		    	yyleng = 0;
    		    	yytext = current.substring(i, i + 1);
    		    	switch (current.charAt(i)) {
    		    	case ';': current = current.substring(1); return CTokenType.SEMI.ordinal();
    		    	case '+': current = current.substring(1); return CTokenType.PLUS.ordinal();
    		    	case '*': current = current.substring(1);return CTokenType.STAR.ordinal();
    		    	case '(': current = current.substring(1);return CTokenType.LP.ordinal();
    		    	case ')': current = current.substring(1);return CTokenType.RP.ordinal();
    		    	case ',': current = current.substring(1); return CTokenType.COMMA.ordinal();
    		    	
    		    	case '\n':
    		    	case '\t':
    		    	case ' ': 
    		            current = current.substring(1);
    		            return CTokenType.WHITE_SPACE.ordinal();

    		    	
    		    	default:
    		    		if (isAlnum(current.charAt(i)) == false) {
    		    			return CTokenType.UNKNOWN_TOKEN.ordinal();
    		    		}
    		    		else {
    		    			
    		    			while (i < current.length() && isAlnum(current.charAt(i))) {
    		    				i++;
    		    				yyleng++;
    		    			} // while (isAlnum(current.charAt(i)))
    		    			
    		    			yytext = current.substring(0, yyleng);
    		    			current = current.substring(yyleng); 
    		    			return id_keyword_or_number();
    		    		}
    		    		
    		    	} //switch (current.charAt(i))
    		    }//  for (int i = 0; i < current.length(); i++) 
    		
    	}//while (true)	
    }//lex()
    
    private int id_keyword_or_number() {
    	int type = CTokenType.UNKNOWN_TOKEN.ordinal();
    	if (Character.isAlphabetic(yytext.charAt(0))) {
    		type = isKeyWord(yytext);
    	}
    	
    	return type;
    }
    
    private int isKeyWord(String str) {
    	
    	if (keywordMap.containsKey(str)) {
    		return keywordMap.get(str);
    	}
    	
    	return CTokenType.NAME.ordinal();
    }
    
    public boolean match(int token) {
    	if (lookAhead == -1) {
    		lookAhead = lex();
    	}
    	
    	return token == lookAhead;
    }
    
    public void advance() {
    	lookAhead = lex();
    	while (lookAhead == CTokenType.WHITE_SPACE.ordinal()) {
    		lookAhead = lex();
    	}
    }
    
   
}
