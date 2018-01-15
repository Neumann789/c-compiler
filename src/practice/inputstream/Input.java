package practice.inputstream;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;


public class Input {
	private final int   MAXLOOK = 16; //look ahead 最多字符数
	private final int   MAXLEX = 1024; //分词后字符串的最大长度
	private final int   BUFSIZE =  (MAXLEX * 3 ) + (2 * MAXLOOK); //缓冲区大小
	private int         End_buf = BUFSIZE; //缓冲区的逻辑结束地址
	private final int   DANGER = (End_buf - MAXLOOK);
	private final int   END = BUFSIZE;
	private final byte[]  Start_buf = new byte[BUFSIZE]; //缓冲区
	private int         Next = END; //指向当前要读入的字符位置
	private int         sMark = END; //当前被词法分析器分析的字符串位置
	private int         eMark = END; //当前被词法分析器分析的字符串结束位置
	private int         pMark = END; //上一个被词法分析器分析的字符串起始位置
	private int         pLineno = 0; //上一个被词法分析器分析的字符串所在的行号
	private int         pLength = 0; //上一个被词法分析器分析的字符串长度
	
	private FileHandler fileHandler = null;
	
	private int         Lineno = 1;  //当前被词法分析器分析的字符串的行号
	
	private int         Mline  = 1; 

	
	
	private boolean Eof_read = false; //输入流中是否还有可读信息
	private boolean noMoreChars() {
		/*
		 * 缓冲区中是否还有可读的字符
		 */
		return (Eof_read && Next >= End_buf);
	}
	

	private FileHandler getFileHandler(String fileName) {
		if (fileName != null) {
			return new DiskFileHandler(fileName);
		}
		else {
			return new StdInHandler();
		}
	}
	
	public void ii_newFile(String fileName) {
		
		if (fileHandler != null) {
			fileHandler.Close();
		}
		
		fileHandler = getFileHandler(fileName);
		fileHandler.Open();
		
		Eof_read = false;
		Next     = END;
		sMark    = END;
		eMark    = END;
		End_buf  = END;
		Lineno   = 1;
		Mline    = 1;
	}
	
	public String ii_text() {
		byte[] str = Arrays.copyOfRange(Start_buf, sMark, sMark + ii_length());
		return new String(str, StandardCharsets.UTF_8);
	}
	
	public int ii_length() {
		return eMark - sMark;
	}
	
	public int ii_lineno() {
		return Lineno;
	}
	
	public String ii_ptext() {
		byte[] str = Arrays.copyOfRange(Start_buf, pMark, pMark + pLength);
		return new String(str, StandardCharsets.UTF_8);
	}
	
	public int ii_plength() {
		return pLength;
	}
	
	public int ii_plineno() {
		return pLineno;
	}
	
	public int ii_mark_start() {
		Mline = Lineno;
		eMark = sMark = Next;
		return sMark;
	}
	
	public int ii_mark_end() {
		Mline = Lineno;
		eMark = Next;
		return eMark;
	}
	
	public int ii_move_start() {
		if (sMark >= eMark) {
			return -1;
		}
		else {
			sMark++;
			return sMark;
		}
	}
	
	public int ii_to_mark() {
		Lineno = Mline;
		Next = eMark;
		return Next;
	}
	
	public int ii_mark_prev() {
		/*
		 * 执行这个函数后，上一个被词法解析器解析的字符串将无法在缓冲区中找到
		 */
		pMark = sMark;
		pLineno = Lineno;
		pLength = eMark - sMark;
		return pMark;
	}
	

	public byte ii_advance() {
		/*
		 * ii_advance() 是真正的获取输入函数，他将数据从输入流中读入缓冲区，并从缓冲区中返回要读取的字符
		 * 并将Next加一，从而指向下一个要读取的字符, 如果Next的位置距离缓冲区的逻辑末尾(End_buf)不到
		 * MAXLOOK 时， 将会对缓冲区进行一次flush 操作
		 */
		
		
		
		if (noMoreChars()) {
			return 0;
		}
		
		if (Eof_read == false && ii_flush(false) < 0) {
			/*
			 * 从输入流读入数据到缓冲区时出错
			 */
			return -1;
		}
		
		if (Start_buf[Next] == '\n') {
			Lineno++;
		}
		
		return Start_buf[Next++];
	}
	
	public static int NO_MORE_CHARS_TO_READ = 0;
	public static int FLUSH_OK = 1;
	public static int FLUSH_FAIL = -1;
	
	
	private int ii_flush(boolean force) {
		/*
		 * flush 缓冲区，如果Next 没有越过Danger的话，那就什么都不做
		 * 要不然像上一节所说的一样将数据进行平移，并从输入流中读入数据，写入平移后
		 * 所产生的空间
		 *                            pMark                     DANGER
		 *                              |                          |
		 *     Start_buf              sMark         eMark          | Next  End_buf
		 *         |                    | |           |            |  |      |
		 *         V                    V V           V            V  V      V
		 *         +---------------------------------------------------------+---------+
		 *         | 已经读取的区域        |          未读取的区域                 | 浪费的区域|
		 *         +--------------------------------------------------------------------
		 *         |<---shift_amt------>|<-----------copy_amt--------------->|
		 *         |<-------------------------BUFSIZE---------------------------------->|
		 * 
		 *  未读取区域的左边界是pMark或sMark(两者较小的那个),把 未读取区域平移到最左边覆盖已经读取区域，返回1
		 *  如果flush操作成功，-1如果操作失败，0 如果输入流中已经没有可以读取的多余字符。如果force 为 true
		 *  那么不管Next有没有越过Danger,都会引发Flush操作
		 */
		
		int copy_amt, shift_amt, left_edge;
		if (noMoreChars()) {
			return NO_MORE_CHARS_TO_READ;
		}
		
		if (Eof_read) {
			//输入流已经没有多余信息了
			return FLUSH_OK;
		}
		
		if (Next > DANGER || force) {
			left_edge = pMark < sMark ? pMark : sMark;
			shift_amt = left_edge;
			if (shift_amt < MAXLEX) {
				if (!force) {
					return FLUSH_FAIL;
				}
				
				left_edge = ii_mark_start();
				ii_mark_prev();
				shift_amt = left_edge;
			}
			
			copy_amt = End_buf - left_edge;
			System.arraycopy(Start_buf, 0, Start_buf, left_edge, copy_amt);
			
			if (ii_fillbuf(copy_amt) == 0) {
				System.err.println("Internal Error, ii_flush: Buffer full, can't read");
			}
			
			if (pMark != 0) {
				pMark -= shift_amt;
			}
			
			sMark -= shift_amt;
			eMark -= shift_amt;
			Next  -= shift_amt;
		}
		
		return FLUSH_OK;
	}
	
	private int ii_fillbuf(int starting_at) {
		/*
		 * 从输入流中读取信息，填充缓冲区平移后的可用空间，可用空间的长度是从starting_at一直到End_buf
		 * 每次从输入流中读取的数据长度是MAXLEX写整数倍
		 * 
		 */
		
		int need; //需要从输入流中读入的数据长度
		int got = 0; //实际上从输入流中读到的数据长度
		need = ((END - starting_at) / MAXLEX) * MAXLEX;
		if (need < 0) {
			System.err.println("Internal Error (ii_fillbuf): Bad read-request starting addr.");
		}
		
		if (need == 0) {
			return 0;
		}
		
		if ((got = fileHandler.Read(Start_buf, starting_at, need)) == -1) {
			System.err.println("Can't read input file");
		}
		
		End_buf = starting_at + got;
		if (got < need) {
			//输入流已经到末尾
			Eof_read = true;
		}
		
		return got;
	}

}
