package Demo22;

import Demo20.Lexer;

public class BottomUpParser {
    public static void main(String[] args) {
    	Lexer lexer = new Lexer();
    	LRParser parser = new LRParser(lexer);
    	parser.parse();
    }
}
