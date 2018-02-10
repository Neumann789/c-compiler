package practice.thompson;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;


public class StdInHandler implements FileHandler{
	
	private String input_buffer = "";
	private int curPos = 0;
	
public void Open() {
	
	  Scanner s = new Scanner(System.in);
	    while (true) {
	    	String line = s.nextLine();
	    	if (line.equals("end")) {
	    		break;
	    	}
	    	input_buffer += line;
	    	input_buffer += '\n';
	    }
	   // s.close();
	}
	
	public int Close() {
		return 0;
	}
	
	public int Read(byte[] buf, int begin, int len) {
		
		if (curPos >= input_buffer.length()) {
		    return 0;
		}
		
		int readCnt = 0;
		try {
			byte[] inputBuf = input_buffer.getBytes("UTF8");
			while (curPos + readCnt < input_buffer.length() && readCnt < len) {
				buf[begin + readCnt] = inputBuf[curPos + readCnt];
				readCnt++;
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return readCnt;
		
	}

}
