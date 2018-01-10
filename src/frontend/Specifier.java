package frontend;

public class Specifier {
	//type
	public static int  NONE = -1;
    public static int  INT = 0;
    public static int  CHAR = 1;
    public static int  VOID = 2;
    public static int  STRUCTURE = 3;
    public static int  LABEL = 4;
    
    //storage
    public static int  FIXED = 0;
    public static int  REGISTER = 1;
    public static int  AUTO = 2;
    public static int  TYPEDEF = 3;
    public static int  CONSTANT = 4;
    
    public static int  NO_OCLASS = 0;  //如果内存类型是auto, 那么存储类型就是NO_OCLASS
    public static int  PUBLIC = 1;
    public static int  PRIVATE = 2;
    public static int  EXTERN = 3;
    public static int  COMMON = 4;
    
    private  int  basicType;
    public   void setType(int type) {
    	basicType = type;
    }
    public int getType() {
    	return basicType;
    }
    
    private int storageClass;
    public  void setStorageClass(int s) {
    	storageClass = s;
    }
    public int getStorageClass() {
    	return storageClass;
    }
    
    private int outputClass= NO_OCLASS;
    public void setOutputClass(int c) {
    	outputClass = c;
    }
    public int getOutputClass() {
    	return outputClass;
    }
    
    private boolean isLong = false;
    public void setLong(boolean l) {
    	isLong = l;
    }
    public boolean getLong() {
    	return isLong;
    }
    
    private boolean isSigned = false;
    public void setSign(boolean signed) {
    	isSigned = signed;
    }
    public boolean isSign() {
    	return isSigned;
    }
    
    private boolean isStatic = false;
    public void setStatic(boolean s) {
    	isStatic = s;
    }
    public boolean isStatic() {
    	return isStatic;
    }
    
    private boolean isExternal = false;
    public void setExternal(boolean e) {
    	isExternal = e;
    }
    public boolean isExternal() {
    	return isExternal;
    }
    
    private int  constantValue = 0;
    public void setConstantVal(int v) {
    	constantValue = v;
    }
    public int getConstantVal() {
    	return constantValue;
    }
    
    private StructDefine  vStruct = null;
    public void setStructObj(StructDefine struct) {
    	this.vStruct = struct;
    }
    
    public StructDefine getStructObj() {
    	return this.vStruct;
    }
}
