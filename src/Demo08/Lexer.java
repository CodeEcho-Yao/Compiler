package Demo08;

public class Lexer {
    public enum Token {
    	EOS, //正则表达式末尾
    	ANY,     // . 通配符
    	AT_BOL,  //^ 开头匹配符
    	AT_EOL,  //$ 末尾匹配符
    	CCL_END,  //字符集类结尾括号 ]
    	CCL_START,  //字符集类开始括号 [
    	CLOSE_CURLY, // }
    	CLOSE_PAREN,  //)
    	CLOSURE,      //*
    	DASH,       // -
    	END_OF_INPUT,  //输入流结束
    	L,        //字符常量
    	OPEN_CURLY, // {
    	OPEN_PAREN, // (
    	OPTIONAL,  //? 
    	OR,       // |
    	PLUS_CLOSE
    };
    
    private final int ASCII_COUNT = 128;
    private Token[] tokenMap = new Token[ASCII_COUNT];
    private Token currentToken = Token.EOS;
    RegularExpressionHandler exprHandler = null;
    private int exprCount = 0;
    private String curExpr = "";
    private int charIndex = 0;
    private boolean inQuoted = false; //是否在双引号内
    private boolean sawEsc = false;  //是否读取到转移符 /
    private int lexeme ;
    
    public Lexer(RegularExpressionHandler exprHandler) {
    	initTokenMap();
    	this.exprHandler = exprHandler;
    }
    
    private void initTokenMap() {
    	for (int i = 0; i < ASCII_COUNT; i++) {
    		tokenMap[i] = Token.L;
    	}
    	
        tokenMap['.'] = Token.ANY;
        tokenMap['^'] = Token.AT_BOL;
        tokenMap['$'] = Token.AT_EOL;
        tokenMap[']'] = Token.CCL_END;
        tokenMap['['] = Token.CCL_START;
        tokenMap['}'] = Token.CLOSE_CURLY;
        tokenMap[')'] = Token.CLOSE_PAREN;
        tokenMap['*'] = Token.CLOSURE;
        tokenMap['-'] = Token.DASH;
        tokenMap['{'] = Token.OPEN_CURLY;
        tokenMap['('] = Token.OPEN_PAREN;
        tokenMap['?'] = Token.OPTIONAL;
        tokenMap['|'] = Token.OR;
        tokenMap['+'] = Token.PLUS_CLOSE; 
    }
    
    public boolean MatchToken(Token t) {
    	return currentToken == t;
    }
    
    public int getLexeme() {
    	return lexeme;
    }
    
    public String getCurExpr() {
    	return curExpr;
    }
    
    public Token getCurrentToken() {
    	return currentToken;
    }
    public Token advance() {
    	if (currentToken == Token.EOS) {
    		//一个正则表达式解析结束后读入下一个表达式
    		if (exprCount >= exprHandler.getRegularExpressionCount()) {
    			//所有正则表达式都解析完毕
    			currentToken = Token.END_OF_INPUT;
    			return currentToken;
    		}
    		else {
    			curExpr = exprHandler.getRegularExpression(exprCount);
    			exprCount++;
    		}
    	}
    	
    	if (charIndex >= curExpr.length()) {
    		currentToken = Token.EOS;
    		charIndex = 0;
			return currentToken; 
    	}
    	
    	if (curExpr.charAt(charIndex) == '"') {
    		inQuoted = !inQuoted;
    		charIndex++;
    	}
    	
    	sawEsc = (curExpr.charAt(charIndex) == '\\');
    	if (sawEsc && curExpr.charAt(charIndex + 1) != '"' && inQuoted == false) {
    		lexeme = handleEsc();
    	}
    	else {
    		if (sawEsc && curExpr.charAt(charIndex + 1) == '"') {
    			charIndex += 2;
    			lexeme = '"';
    		}
    		else {
    			lexeme = curExpr.charAt(charIndex);
    			charIndex++;
    		}
    	}
    	
    	currentToken = (inQuoted || sawEsc) ? Token.L : tokenMap[lexeme];
    	
    	return currentToken;
    }
    
    private int handleEsc() {
    	/*当转移符 \ 存在时，它必须与跟在它后面的字符或字符串一起解读
    	 *我们处理的转义字符有以下几种形式
    	 * \b backspace
    	 * \f formfeed
    	 * \n newline
    	 * \r carriage return 回车
    	 * \s space 空格
    	 * \t tab
    	 * \e ASCII ESC ('\033')
    	 * \DDD 3位八进制数
    	 * \xDDD 3位十六进制数
    	 * \^C C是任何字符， 例如^A, ^B 在Ascii 表中都有对应的特殊含义
    	 * ASCII 字符表参见：
    	 * http://baike.baidu.com/pic/%E7%BE%8E%E5%9B%BD%E4%BF%A1%E6%81%AF%E4%BA%A4%E6%8D%A2%E6%A0%87%E5%87%86%E4%BB%A3%E7%A0%81/8950990/0/9213b07eca8065387d4c671896dda144ad348213?fr=lemma&ct=single#aid=0&pic=9213b07eca8065387d4c671896dda144ad348213
    	 */
    	
    	int rval = 0;
    	String exprToUpper = curExpr.toUpperCase();
    	charIndex++; //越过转移符 \
    	switch (exprToUpper.charAt(charIndex)) {
    	case '\0' : 
    		  rval = '\\'; 
    		  break;
    	case 'B': 
    		  rval = '\b';
    		  break;
    	case 'F':
    		  rval = '\f';
    		  break;
    	case 'N' :
    		  rval = '\n';
    		  break;
    	case 'R' :
    		  rval = '\r';
    		  break;
    	case 'S':
    		  rval = ' ';
    		  break;
    	case 'T':
    		  rval = '\t';
    		  break;
    	case 'E' :
    		  rval = '\033';
    		  break;
    	case '^':
    		  charIndex++;
    		  /*
    		   * 因此当遇到^后面跟在一个字母时，表示读入的是控制字符
    		   * ^@ 在ASCII 表中的数值为0，^A 为1, 字符@在ASCII 表中数值为80， 字符A在ASCII表中数值为81
    		   * 'A' - '@' 等于1 就对应 ^A 在 ASCII 表中的位置
    		   * 具体可参看注释给出的ASCII 图
    		   * 
    		   */
    		  rval = (char) (curExpr.charAt(charIndex) - '@');
    		  break;
    	case 'X':
    		/*
    		 * \X 表示后面跟着的三个字符表示八进制或十六进制数
    		 */
    		charIndex++; //越过X
    		if (isHexDigit(curExpr.charAt(charIndex))) {
    			rval = hex2Bin(curExpr.charAt(charIndex));
    			charIndex++;
    		}
    		
    		if (isHexDigit(curExpr.charAt(charIndex))) {
    			rval <<= 4;
    			rval |= hex2Bin(curExpr.charAt(charIndex));
    			charIndex++;
    		}
    		
    		if (isHexDigit(curExpr.charAt(charIndex))) {
    			rval <<= 4;
    			rval |= hex2Bin(curExpr.charAt(charIndex));
    			charIndex++;
    		}
    		charIndex--; //由于在函数底部会对charIndex++ 所以这里先 --
    		break;
    		
    		default:
    			if (isOctDigit(curExpr.charAt(charIndex)) == false) {
    				rval = curExpr.charAt(charIndex);
    			}
    			else {
    				charIndex++;
    				rval = oct2Bin(curExpr.charAt(charIndex));
    				charIndex++;
    				if (isOctDigit(curExpr.charAt(charIndex))) {
    					rval <<= 3;
    					rval |= oct2Bin(curExpr.charAt(charIndex));
    					charIndex++;
    				}
    				
    				if (isOctDigit(curExpr.charAt(charIndex))) {
    					rval <<= 3;
    					rval |= oct2Bin(curExpr.charAt(charIndex));
    					charIndex++;
    				}
    				
    				charIndex--;//由于在函数底部会对charIndex++ 所以这里先 --
    			}		
    	}
    	
    	charIndex++;
    	return rval;
    }
    
    private int hex2Bin(char c) {
    	/*
    	 * 将十六进制数对应的字符转换为对应的数值，例如
    	 * A 转换为10， B转换为11
    	 * 字符c 必须满足十六进制字符： 0123456789ABCDEF
    	 */
    	return (Character.isDigit(c) ? (c) - '0' : (Character.toUpperCase(c) - 'A' + 10)) & 0xf;
    }
    
    private int oct2Bin(char c) {
    	/*
    	 * 将字符c 转换为对应的八进制数
    	 * 字符c 必须是合法的八进制字符: 01234567
    	 */
    	return ((c) - '0') & 0x7;
    }
    
    private boolean isHexDigit(char c) {	
    	return (Character.isDigit(c)|| ('a' <= c && c <= 'f') || ('A' <= c && c <= 'F'));
    }
    
    private boolean isOctDigit(char c) {
    	return ('0' <= c && c <= '7');
    }
}
