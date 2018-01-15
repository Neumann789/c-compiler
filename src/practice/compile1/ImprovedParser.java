package practice.compile1;
public class ImprovedParser {

    private Lexer lexer;
    private boolean isLegalStatement = true;
    
    public ImprovedParser(Lexer lexer) {
    	this.lexer = lexer;
    }
    
    public void statements() {
    	/*
    	 * statements -> expression ; | expression ; statements
    	 */
   
    	while (lexer.match(Lexer.EOI) != true) {
    		expression();
    		
    		if (lexer.match(Lexer.SEMI)) {
    			lexer.advance();
    		}
    		else {
    			isLegalStatement = false;
    			System.out.println("line: " + lexer.yylineno + " Missing semicolon");
    		}
    	}
    
    	
    	if (isLegalStatement) {
    		System.out.println("The statement is legal");
    	}
    }
    
    private void expression() {
    	/*
    	 * expression -> term expression'
    	 * expression -> PLUS term expression' | 空
    	 */
    	term();
    	while (lexer.match(Lexer.PLUS)) {
    		lexer.advance();
    		term();
    	}
    	
    	if (lexer.match(Lexer.UNKNOWN_SYMBOL)) {
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
    
    
    /*
     * expr_prime 的递归调用可以整合到expression 里
     * 由于当 lexer.match(Lexer.PLUS) 判断成立时，expr_prime() 有递归调用，因此其等价于
     * while (lexer.match(Lexer.PLUS)) {
     *   lexer.advance();
     *   term();
     * }
     * 
    private void expr_prime() {
    	
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
    		
    		return;
    	}
    }
    */
    
    private void term() {
    	factor ();
    	
    	/*
    	 * 将term_prime 的递归调用改成循环方式
    	 * 
    	 */
    	
    	while (lexer.match(Lexer.TIMES)) {
    		lexer.advance();
    		factor();
    	}
    }
    
    /*
     * term_prime 递归调用改循环的道理与expression 的改进同理
     * 
    private void term_prime() {
    	
    	if (lexer.match(Lexer.TIMES)) {
    		lexer.advance();
    		factor();
    		term_prime();
    	}
    	else {
    	
    		return;
    	}
    }
    */
    
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
