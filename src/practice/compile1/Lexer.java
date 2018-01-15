package practice.compile1;

import java.util.Scanner;


public class Lexer {
    public static final int  EOI = 0;
    public static final int  SEMI = 1;
    public static final int  PLUS = 2;
    public static final int  TIMES = 3;
    public static final int  LP = 4;
    public static final int  RP = 5;
    public static final int  NUM_OR_ID = 6;
    public static final int  UNKNOWN_SYMBOL = 7;
    
    private int lookAhead = -1;
    
    public String yytext = "";
    public int yyleng = 0;
    public int yylineno = 0;
    
    private String input_buffer = "";
    private String current = "";
    
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
    		    	return EOI;
    		    }
    		    
    		    current = input_buffer;
    		    ++yylineno;
    		    current.trim();
    		}//while (current == "")
    		
    		if (current.isEmpty()) {
    			return EOI;
    		}
    		    
    		    for (int i = 0; i < current.length(); i++) {
    		    	
    		    	yyleng = 0;
    		    	yytext = current.substring(0, 1);
    		    	switch (current.charAt(i)) {
    		    	case ';': current = current.substring(1); return SEMI;
    		    	case '+': current = current.substring(1); return PLUS;
    		    	case '*': current = current.substring(1);return TIMES;
    		    	case '(': current = current.substring(1);return LP;
    		    	case ')': current = current.substring(1);return RP;
    		    	
    		    	case '\n':
    		    	case '\t':
    		    	case ' ': current = current.substring(1); break;
    		    	
    		    	default:
    		    		if (isAlnum(current.charAt(i)) == false) {
    		    			return UNKNOWN_SYMBOL;
    		    		}
    		    		else {
    		    			
    		    			while (i < current.length() && isAlnum(current.charAt(i))) {
    		    				i++;
    		    				yyleng++;
    		    			} // while (isAlnum(current.charAt(i)))
    		    			
    		    			yytext = current.substring(0, yyleng);
    		    			current = current.substring(yyleng); 
    		    			return NUM_OR_ID;
    		    		}
    		    		
    		    	} //switch (current.charAt(i))
    		    }//  for (int i = 0; i < current.length(); i++) 
    		
    	}//while (true)	
    }//lex()
    
    public boolean match(int token) {
    	if (lookAhead == -1) {
    		lookAhead = lex();
    	}
    	
    	return token == lookAhead;
    }
    
    public void advance() {
    	lookAhead = lex();
    }
    
    public void runLexer() {
    	while (!match(EOI)) {
    		System.out.println("Token: " + token() + " ,Symbol: " + yytext );
    		advance();
    	}
    }
    
    private String token() {
    	String token = "";
    	switch (lookAhead) {
    	case EOI:
    		token = "EOI";
    		break;
    	case PLUS:
    		token = "PLUS";
    		break;
    	case TIMES:
    		token = "TIMES";
    		break;
    	case NUM_OR_ID:
    		token = "NUM_OR_ID";
    		break;
    	case SEMI:
    		token = "SEMI";
    		break;
    	case LP:
    		token = "LP";
    		break;
    	case RP:
    		token = "RP";
    		break;
    	}
    	
    	return token;
    }
}
