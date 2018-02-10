package practice.thompson;

public class ThompsonConstruction {
    private Input input = new Input();
    private MacroHandler macroHandler = null;
    
    public void runMacroExample() throws Exception {
    	System.out.println("Please enter macro definition");
    	
    	renewInputBuffer();
    	macroHandler = new MacroHandler(input);
    	macroHandler.printMacs();

    }
    
    public void runMacroExpandExample() throws Exception {
    	System.out.println("Enter regular expression");
    	renewInputBuffer();
    	
    	RegularExpressionHandler regularExpr = new RegularExpressionHandler(input, macroHandler);
    	System.out.println("regular expression after expanded: ");
    	for (int i = 0; i < regularExpr.getRegularExpressionCount(); i++) {
    		System.out.println(regularExpr.getRegularExpression(i));	
    	}
    	
    }
    
    private void renewInputBuffer() {
    	input.ii_newFile(null); //控制台输入
    	input.ii_advance(); //更新缓冲区
    	input.ii_pushback(1);
    }
    public static void main(String[] args) throws Exception {
    	ThompsonConstruction construction = new ThompsonConstruction();
    	construction.runMacroExample();
    	construction.runMacroExpandExample();
    }
}