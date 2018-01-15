package practice.compile;
/**
 * 
 * ClassName: Parser <br/>
 * Function: 语义解析器. <br/>
 * Date: 2018年1月12日 下午12:56:29 <br/>
 *
 * @author fanghuabao
 * @version 
 * @since JDK 1.7
 */
public class Parser {
	private Lexer lexer;
	
	String[] names = {"t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7"};
	private int nameP = 0;
	
	private String newName() {
	    if (nameP >= names.length) {
	    	System.out.println("Expression too complex: " + lexer.yylineno);
	    	System.exit(1);
	    }
	    
	    String reg = names[nameP];
	    nameP++;
	    
	    return reg;
	}
	
	private void freeNames(String s) {
		if (nameP > 0) {
			names[nameP] = s;
			nameP--;
		}
		else {
			System.out.println("(Internal error) Name stack underflow: " + lexer.yylineno);;
		}
	}
	
    public Parser(Lexer lexer) {
    	this.lexer = lexer;
    }
    
    public void statements() {
    	String tempvar = newName();
    	expression(tempvar); 
    	
    	while (lexer.match(Lexer.EOI) != false) {
    		expression (tempvar);
    		freeNames(tempvar);
    		
    		if (lexer.match(Lexer.SEMI)) {
    			lexer.advance();
    		}
    		else {
    			System.out.println("Inserting missing semicolon: " + lexer.yylineno);
    		}
    	}
    }
    
    private void expression(String tempVar) {
    	String tempVar2;
    	term(tempVar);
    	while (lexer.match(Lexer.PLUS)) {
    		lexer.advance();
    		tempVar2 = newName();
    		term(tempVar2);
    		System.out.println(tempVar + " += " + tempVar2);
    		freeNames(tempVar2);
    	}
    }
    
   
    
    private void term(String tempVar) {
    	String tempVar2;
    	
    	factor (tempVar);
    	while (lexer.match(Lexer.TIMES)) {
    		lexer.advance();
    		tempVar2 = newName();
    		factor(tempVar2);
    		System.out.println(tempVar + " *= " + tempVar2);
    		freeNames(tempVar2);
    	}
    	
    }
    
  
    
    private void factor(String tempVar) {
    	if (lexer.match(Lexer.NUM_OR_ID)) {
    		System.out.println(tempVar + " = " + lexer.yytext);
    		lexer.advance();
    	}
    	else if (lexer.match(Lexer.LP)) {
    		lexer.advance();
    		expression(tempVar);
    		if (lexer.match(Lexer.RP)) {
    			lexer.advance();
    		}
    		else {
    			System.out.println("Missmatched parenthesis: " + lexer.yylineno);
    		}
    	}
    	else {
    		System.out.println("Number or identifier expected: " + lexer.yylineno);
    	}
    }
}
