package backend;

import java.util.Map;

import frontend.Declarator;
import frontend.Symbol;
import frontend.TypeLink;

public class PointerValueSetter implements IValueSetter {
	private Symbol symbol;
	private int index = 0;
	
	@Override
	public void setValue(Object obj) throws Exception {
		int addr = (Integer)symbol.getValue();
		MemoryHeap memHeap = MemoryHeap.getInstance();
		Map.Entry<Integer, byte[]> entry = memHeap.getMem(addr);
		byte[] content = entry.getValue();
		Integer i = (Integer)obj;
		try {
			//if it is a struct pointer, then we write one byte each time
			int sz = symbol.getByteSize();
			if (symbol.getDeclarator(Declarator.POINTER) != null &&
					symbol.getArgList() != null) {
				sz = 1;
			}
			
			if (sz == 4) {
				content[index] = (byte)((i>>24) & 0xFF);
				content[index + 1] = (byte)((i>>16) & 0xFF);
				content[index + 2] = (byte)((i>>8) & 0xFF);
				content[index + 3] = (byte)(i & 0xFF);
			} else {
				content[index] = (byte)(i & 0xFF);
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

	public PointerValueSetter(Symbol symbol, int index) {
		this.symbol = symbol;
		this.index = index;
	}

	@Override
	public Symbol getSymbol() {
		// TODO Auto-generated method stub
		return symbol;
	}
}
