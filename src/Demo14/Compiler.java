package Demo14;

public class Compiler {

	public static void main(String[] args) {
		Lexer lexer = new Lexer();
		//lexer.runLexer();
		//ImprovedParser improvedParser = new ImprovedParser(lexer);
		//improvedParser.statements();
		
		/*Parser parser = new Parser(lexer);
		parser.statements();
		*/
		
		PdaParser pdaParser = new PdaParser(lexer);
		pdaParser.parse();
		System.out.println("PdaParser accept input string");
	}
}
