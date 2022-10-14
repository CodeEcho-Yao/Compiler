package Demo20;

import java.util.Stack;

public class AttributedParser {
     
    private String[] names = null;
	private int nameIdx = 0;
	    
    private String getname() {
    	return names[nameIdx++];
    }
	    
    private void freename(String name ) {
    	nameIdx--;
    	if (nameIdx >= 0) {
    		names[nameIdx] = name;	
    	}
    }
	    
  
	    
    private Lexer lexer;
    private boolean isLegalStatement = true;
    
    public AttributedParser(Lexer lexer) {
    	this.lexer = lexer;
    	names = new String[]{"t0", "t1", "t2", "t3", "t4", "t5", "t6"};
    }
    
    public void statements() {
    	/*
    	 * statements -> expression ; | expression ; statements
    	 */
    	String t = getname();
    	expression(t);
    	
    	if (lexer.match(Lexer.SEMI)) {
    		/*
    		 * look ahead 读取下一个字符，如果下一个字符不是 EOI
    		 * 那就采用右边解析规则
    		 */
    		lexer.advance(); 
    	}
    	else {
    		/*
    		 *  如果算术表达式不以分号结束，那就是语法错误
    		 */
    		isLegalStatement = false;
    		System.out.println("line: " + lexer.yylineno + " Missing semicolon");
    		return;
    	}
    	
    	if (lexer.match(Lexer.EOI) != true) {
    		/*
    		 * 分号后还有字符，继续解析
    		 */
    		statements();
    	}
    	
    	if (isLegalStatement) {
    		System.out.println("The statement is legal");
    	}
    }
    
    private void expression(String t) {
    	/*
    	 * expression -> term expression'
    	 */
    	term(t);
    	expr_prime(t); //expression'
    }
    
    private void expr_prime(String t) {
    	/*
    	 * expression' -> PLUS term expression' | '空'
    	 */
    	if (lexer.match(Lexer.PLUS)) {
    		lexer.advance();
    		String t2 = getname();
    		term(t2);
    		
    		System.out.println(t + " += " + t2);
    		freename(t2);
    		
    		expr_prime(t);
    	}
    	else if (lexer.match(Lexer.UNKNOWN_SYMBOL)) {
    		isLegalStatement = false;
    		System.out.println("unknow symbol: " + lexer.yytext);
    		return;
    	}
    	else {
    		/*
    		 * "空" 就是不再解析，直接返回
    		 */
    		return;
    	}
    }
    
    private void term(String t) {
    	/*
    	 * term -> factor term'
    	 */
    	factor(t);
    	term_prime(t); //term'
    }
    
    private void term_prime(String t) {
    	/*
    	 * term' -> * factor term' | '空'
    	 */
    	if (lexer.match(Lexer.TIMES)) {
    		lexer.advance();
    		String t2 = getname();
    		
    		factor(t2);
    		System.out.println(t + " *= " + t2);
    		freename(t2);
    		
    		term_prime(t);
    	}
    	else {
    		/*
    		 * 如果不是以 * 开头， 那么执行 '空' 
    		 * 也就是不再做进一步解析，直接返回
    		 */
    		return;
    	}
    }
    
    private void factor(String t) {
    	/*
    	 * factor -> NUM_OR_ID | LP expression RP
    	 */
    	
    	if (lexer.match(Lexer.NUM_OR_ID)) {
    		System.out.println(t + " = " + lexer.yytext);
    		lexer.advance();
    	}
    	else if (lexer.match(Lexer.LP)){
    		lexer.advance();
    		expression(t);
    		if (lexer.match(Lexer.RP)) {
    			lexer.advance();
    		}
    		else {
    			/*
    			 * 有左括号但没有右括号，错误
    			 */
    			isLegalStatement = false;
    			System.out.println("line: " + lexer.yylineno + " Missing )");
    			return;
    		}
    	}
    	else {
    		/*
    		 * 这里不是数字，解析出错
    		 */
    		isLegalStatement = false;
    		System.out.println("illegal statements");
    		return;
    	}
    }
}

