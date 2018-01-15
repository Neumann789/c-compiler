package practice.compile;
public class Compiler {

	public static void main(String[] args) {
/*		Lexer lexer = new Lexer();
		BasicParser basic_parser = new BasicParser(lexer);
		basic_parser.statements();
		lexer.runLexer();*/
		
		//testLexer();
		testParser();
		
	}
	
	public static void testLexer(){
		Lexer lexer = new Lexer();
		lexer.runLexer();
	}
	
	public static void testParser(){
		Lexer lexer = new Lexer();
		BasicParser basic_parser = new BasicParser(lexer);
		basic_parser.statements();
	}
}
