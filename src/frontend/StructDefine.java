package frontend;
/*
 * struct argotiers {
 *     int  (*Clopin)();
 *     double  Mathias[5];
 *     struct  argotiers*  Guillaume;
 *     struct  pstruct {int a;} Pierre; 
 * }
 */
public class StructDefine {
    private String tag; //结构体的名称,例如上面的例子中，对应该变量的值为 "argotiers"
    private int  level; //结构体的间套层次
    private Symbol fields; //对应结构体里的各个变量类型
    
    public StructDefine(String tag, int level, Symbol fields) {
    	this.tag = tag;
    	this.level = level;
    	this.fields = fields;
    }
    
    public void setFields(Symbol fields) {
    	this.fields = fields;
    }
    
    public String getTag() {
    	return tag;
    }
    
    public int getLevel() {
    	return level;
    }
    
    public Symbol getFields() {
    	return fields;
    }
}
