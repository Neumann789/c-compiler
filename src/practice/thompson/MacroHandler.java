package practice.thompson;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class MacroHandler {
	/*
	 * 宏定义如下：
	 * 宏名称 <空格>  宏内容 [<空格>]
	 */
	
	private HashMap<String, String> macroMap = new HashMap<String, String>();
	private Input inputSystem;
	
	public MacroHandler(Input input) {
		this.inputSystem = input;
		while (input.ii_lookahead(1) != input.EOF) {
			newMacro();
		}
	}
	
	private void newMacro() {
		/*
		 * 将宏定义加入哈希表
		 * 哈希表的key 用的是宏定义的名称，哈希表的内容是宏定义的内容
		 * 
		 */
		//去掉开头的空格或空行
		while (Character.isSpaceChar(inputSystem.ii_lookahead(1)) || inputSystem.ii_lookahead(1) == '\n') {
			inputSystem.ii_advance();
		}
		
		String macroName = "";
		char c = (char)inputSystem.ii_lookahead(1);
		while (Character.isSpaceChar(c) == false && c != '\n') {
			macroName += c;
			inputSystem.ii_advance();
			c = (char)inputSystem.ii_lookahead(1);
		}
		
		//忽略宏定义名称后面的空格
		while (Character.isSpaceChar(inputSystem.ii_lookahead(1))) {
			inputSystem.ii_advance();
		}
		
		//获取宏定义的内容
		c = (char)inputSystem.ii_lookahead(1);
		String macroContent = "";
		while (Character.isSpaceChar(c) == false && c != '\n') {
			macroContent += c;
			inputSystem.ii_advance();
			c = (char)inputSystem.ii_lookahead(1);
		}
		
		//越过'\n'
		inputSystem.ii_advance();
		
		//将宏定义加入哈希表
		macroMap.put(macroName, macroContent);
	}
	
	public String expandMacro(String macroName) throws Exception {
		if (macroMap.containsKey(macroName) == false) {
			ErrorHandler.parseErr(ErrorHandler.Error.E_NOMAC);
		}
		else {
			return "(" + macroMap.get(macroName) + ")";
		}
		
		return "ERROR"; //走到这里的话就是bug
	}
	
	public void printMacs() {
		if (macroMap.isEmpty()) {
			System.out.println("There are no macros");
		}
		else {
			Iterator iter = macroMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> entry = (Map.Entry<String, String>)iter.next();
				System.out.println("Macro name: " + entry.getKey() + " Macro content: " + entry.getValue());
			}
		}
	}
}
