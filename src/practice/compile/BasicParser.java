package practice.compile;


public class BasicParser {
    private Lexer lexer;
    private boolean isLegalStatement = true;
    
    public BasicParser(Lexer lexer) {
    	this.lexer = lexer;
    }
    
    public void statements() {
    	/*
    	 * statements -> expression ; | expression ; statements
    	 */
    	
    	expression();
    	
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
    
    private void expression() {
    	/*
    	 * expression -> term expression'
    	 */
    	term();
    	expr_prime(); //expression'
    }
    
    private void expr_prime() {
    	/*
    	 * expression' -> PLUS term expression' | '空'
    	 */
    	if (lexer.match(Lexer.PLUS)) {
    		lexer.advance();
    		term();
    		expr_prime();
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
    
    private void term() {
    	/*
    	 * term -> factor term'
    	 */
    	factor();
    	term_prime(); //term'
    }
    
    private void term_prime() {
    	/*
    	 * term' -> * factor term' | '空'
    	 */
    	if (lexer.match(Lexer.TIMES)) {
    		lexer.advance();
    		factor();
    		term_prime();
    	}
    	else {
    		/*
    		 * 如果不是以 * 开头， 那么执行 '空' 
    		 * 也就是不再做进一步解析，直接返回
    		 */
    		return;
    	}
    }
    
    private void factor() {
    	/*
    	 * factor -> NUM_OR_ID | LP expression RP
    	 */
    	
    	if (lexer.match(Lexer.NUM_OR_ID)) {
    		lexer.advance();
    	}
    	else if (lexer.match(Lexer.LP)){
    		lexer.advance();
    		expression();
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
