package Demo02;

/*
	语法解析
 */

public class Compiler {

	public static void main(String[] args) {
		Lexer lexer = new Lexer();
		BasicParser basic_parser = new BasicParser(lexer);
		basic_parser.statements();
	}
}
