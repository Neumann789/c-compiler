package frontend;

public class TypeLink {
	
    boolean  isDeclarator = true; //true 那么该object 的对象是declarator, false那么object指向的就是specifier
    boolean  isTypeDef = false; //true，那么当前变量的类型是由typedef 定义的
    
    Object   typeObject;
    
    private TypeLink  next = null;
    
    public TypeLink(boolean isDeclarator, boolean typeDef, Object typeObj) {
    	this.isDeclarator = isDeclarator;
    	this.isTypeDef = typeDef;
    	this.typeObject = typeObj;
    }
    
    public Object getTypeObject() {
    	return typeObject;
    }
    
    public TypeLink toNext() {
    	return next;
    }
    
    
    public void setNextLink(TypeLink obj) {
    	this.next = obj;
    }
}
