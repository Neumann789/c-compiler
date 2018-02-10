package practice.thompson;
import java.io.UnsupportedEncodingException;


public class InputSystem {
	private Input input = new Input();
	public void runStdinExampe() {
    	input.ii_newFile(null); //控制台输入
    	
    	input.ii_mark_start();
    	printWord();
    	input.ii_mark_end();
    	input.ii_mark_prev();
    	/*
    	 *   执行上面语句后，缓冲区及相关指针情况如下图
    	 *       sMark
    	 *         |
    	 *       pMark     eMark                   
		 *         |        |                                     
    	 *       Start_buf Next                                   Danger   End_buf
		 *         |        |                                        |       |  
		 *         V        V                                        V       V  
		 *         +---------------------------------------------------------+---------+
		 *         | typedef|          未读取的区域                     |       | 浪费的区域|
		 *         +--------------------------------------------------------------------
		 *         |<-------------------------BUFSIZE---------------------------------->|
    	 * 
    	 */
    	
    	input.ii_mark_start();
    	printWord();
    	input.ii_mark_end();
    	
    	/*
    	 *   执行上面语句后，缓冲区及相关指针情况如下图
    	 *                 sMark
    	 *                  |
    	 *       pMark      |   eMark                   
		 *         |        |    |                                     
    	 *       Start_buf  |   Next                               Danger   End_buf
		 *         |        |    |                                   |       |  
		 *         V        V    V                                   V       V  
		 *         +---------------------------------------------------------+---------+
		 *         | typedef|int|      未读取区域                      |       | 浪费的区域|
		 *         +--------------------------------------------------------------------
		 *         |<-------------------------BUFSIZE---------------------------------->|
    	 * 
    	 */
    	
    	System.out.println("prev word: " + input.ii_ptext());// 打印出 typedef
    	System.out.println("current word: " + input.ii_text()); //打印出int
		
	}
	
	 private void printWord() {
	    	
	    	byte c;
	    	while ((c = input.ii_advance()) != ' ') {
	    		byte[] buf = new byte[1];
	    		buf[0] = c;
	    		try {
					String s = new String(buf, "UTF8");
					System.out.print(s);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		
	    	}
	    	
	    	System.out.println("");
	    }
	
    public static void main(String[] args) {
    	InputSystem input = new InputSystem();
    	input.runStdinExampe();
    }
    
   
}
