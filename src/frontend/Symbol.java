package frontend;

import backend.ArrayValueSetter;
import backend.BaseExecutor;
import backend.IValueSetter;
import backend.Compiler.Instruction;
import backend.Compiler.ProgramGenerator;

public class Symbol implements IValueSetter{
    String  name;
    String  rname;
    
    int       level;  //变量的层次
    boolean   implicit;  //是否是匿名变量
    boolean   duplicate;   //是否是同名变量
    
    Symbol    args = null;   //如果该符号对应的是函数名,那么args指向函数的输入参数符号列表
    
    private Symbol    next = null;  //指向下一个同层次的变量符号
    
    private Object value = null;
    private Object valueSetter = null;
    TypeLink  typeLinkBegin = null;
    TypeLink  typeLinkEnd = null;
    
    private String symbolScope = LRStateTableParser.GLOBAL_SCOPE;
    
    private boolean isMember = false;
    private Symbol  structParent = null;
    
    public Symbol(String name, int level) {
    	this.name = name;
    	this.level = level;
    }
    
    public void addValueSetter(Object setter) {
    	this.valueSetter = setter;
    }
    
    public Object getValueSetter() {
    	return this.valueSetter;
    }
    
    public Symbol copy() {
    	Symbol symbol = new Symbol(this.name, this.level);
    	symbol.args = this.args;
    	symbol.next = this.next;
    	symbol.value = this.value;
    	symbol.typeLinkBegin = this.typeLinkBegin;
    	symbol.typeLinkEnd = this.typeLinkEnd;
    	symbol.symbolScope = this.symbolScope;
    	
    	return symbol;
    }
    
    public void addScope(String scope) {
    	this.symbolScope = scope;
    }
    
    public String getScope() {
    	return symbolScope;
    }
   
   public void setStructParent(Symbol parent) {
	   this.isMember = true;
	   this.structParent = parent;
   }
   
   public Symbol getStructSymbol() {
	   return this.structParent;   
   }
   
   public boolean isStructMember() {
	   return isMember;
   }
    
    public boolean equals(Symbol symbol) {
    	if (this.name.equals(symbol.name) && this.level == symbol.level && 
    			this.symbolScope.equals(symbol.symbolScope)) {
    		return true;
    	}
    	
    	return false;
    }
    
    public String getName() {
    	return name;
    }
    
    @Override
    public void setValue(Object obj) {
    	if (obj != null) {
    		System.out.println("Assign Value of " + obj.toString() + " to Variable " + name);	
    	} else {
    		return;
    	}
    	
    	ProgramGenerator generator = ProgramGenerator.getInstance();
    	
    	if (BaseExecutor.resultOnStack == true) {
    		/*
    		 * 如果结果已经存在堆栈上了，那么赋值时无需再生成其他指令，例如j = i - 1
    		 * i-1 的结果会直接放置到堆栈顶部，因此我们只需要直接通过istore指令把堆栈顶部的数值存储到
    		 * 该变量所在的局部变量队列的位置即可
    		 */
    		this.value = obj;
    		BaseExecutor.resultOnStack = false;
    	}
    	else if (obj instanceof ArrayValueSetter) {
    		/*
        	 * 处理 i = a[2] 这种用数组元素赋值的情形,此时要把数组元素的读取生成jvm字节码
        	 */

    		ArrayValueSetter setter = (ArrayValueSetter)obj;
    		Symbol symbol = setter.getSymbol();
    		Object index = setter.getIndex();
    		if (symbol.getSpecifierByType(Specifier.STRUCTURE) == null) {
				//如果是结构体数组，这里不做处理，留给下一步处理
				if (index instanceof Symbol) {
			            ProgramGenerator.getInstance().readArrayElement(symbol, index);
			            if (((Symbol)index).getValue() != null) {
			            int i = (int)((Symbol)index).getValue();
			            try {
						    this.value = symbol.getDeclarator(Declarator.ARRAY).getElement(i);
					    } catch (Exception e) {
						// TODO Auto-generated catch block
						    e.printStackTrace();
					    }
			        }
				} else {
					int i = (int)index;
					 try {
							this.value = symbol.getDeclarator(Declarator.ARRAY).getElement(i);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					 
					ProgramGenerator.getInstance().readArrayElement(symbol, index);
				}
			}
    		
    		
    	}
    	else if (obj instanceof Symbol) {
    		Symbol symbol = (Symbol)obj;
    		this.value = symbol.value;
    		int i = generator.getLocalVariableIndex(symbol);
    		generator.emit(Instruction.ILOAD, "" + i);
    	} else if (obj instanceof Integer){
    		Integer val = (Integer)obj;
    		generator.emit(Instruction.SIPUSH, "" + val);
    	    this.value = obj;
    	}
    	
    	//change here
    	if (this.value != null || BaseExecutor.isCompileMode == true) {
    		/*
    		 * 先判断该变量是否是一个结构体的成员变量，如果是，那么需要通过assignValueToStructMember来实现成员变量
    		 * 的赋值，如果不是，那么就直接通过IStore语句直接赋值
    		 */
    		if (this.isStructMember() == false) {
        		int idx = generator.getLocalVariableIndex(this);
        		if (generator.isPassingArguments() == false) {
        			generator.emit(Instruction.ISTORE, "" + idx);	
        		}	
    		} else {
    			
    			if (this.getStructSymbol().getValueSetter() != null) {
    				generator.assignValueToStructMemberFromArray(this.getStructSymbol().getValueSetter(), this, this.value);
    			} else {
    				generator.assignValueToStructMember(this.getStructSymbol(), this, this.value);	
    			}
    			
    		}
    		
    	}
    	
    }
    
    public Object getValue() {
    	return value;
    }
    
    public Symbol getArgList() {
    	return args;
    }
    
    public void setArgList(Symbol symbol) {
    	this.args = symbol;
    }
    
    public void addDeclarator(TypeLink type) {
    	if (typeLinkBegin == null) {
    		typeLinkBegin = type;
    		typeLinkEnd = type;
    	} else {
    		type.setNextLink(typeLinkBegin);
    		typeLinkBegin = type;
    	}
    }
    
    public Declarator getDeclarator(int type) {
        TypeLink begin = typeLinkBegin;
        while (begin != null && begin.getTypeObject() != null) {
        	if (begin.isDeclarator) {
        		Declarator declarator = (Declarator)begin.getTypeObject();
        		if (declarator.getType() == type) {
        			return declarator;
        		}
        	}
        	
        	begin = begin.toNext();
        }
        
        return null;
    }
    
    public TypeLink getTypeHead() {
    	return typeLinkBegin;
    }
    
    public int getByteSize() {
    	int size = 0;
    	TypeLink head = typeLinkBegin;
    	while (head != null) {
    		if (head.isDeclarator != true) {
    			Specifier sp = (Specifier)head.typeObject;
    			if (sp.getLong() == true || sp.getType() == Specifier.INT ||
    			   getDeclarator(Declarator.POINTER) != null) {
    				size = 4;
    				break;
    			} else {
    				size = 1;
    				break;
    			}
    		}
    		
    		head = head.toNext();
    	}
    	
    	
    	return size;
    }
    
    public boolean hasType(int type) {
    	TypeLink  head = typeLinkBegin;
    	while (head != null) {
    		if (head.isDeclarator != true) {
    			Specifier sp = (Specifier)head.typeObject;
    			if (sp.getType() == type) {
    				return true;
    			}
    		}
    		
    		head = head.toNext();
    	}
    	
    	
    	return false;
    }
    
    public Specifier getSpecifierByType(int type) {
    	TypeLink  head = typeLinkBegin;
    	while (head != null) {
    		if (head.isDeclarator != true) {
    			Specifier sp = (Specifier)head.typeObject;
    			if (sp.getType() == type) {
    				return sp;
    			}
    		}
    		
    		head = head.toNext();
    	}
    	
    	
    	return null;
    }
    
    public void addSpecifier(TypeLink type) {
    	if (typeLinkBegin == null) {
    		typeLinkBegin = type;
    		typeLinkEnd = type;
    	} else {
    		typeLinkEnd.setNextLink(type);
    		typeLinkEnd = type;
    	}
    }
    
    public void setNextSymbol(Symbol symbol) {
    	this.next = symbol;
    }
    
    public Symbol getNextSymbol() {
    	return next;
    }
    
    public int getLevel() {
    	return level;
    }

	@Override
	public Symbol getSymbol() {
		// TODO Auto-generated method stub
		return this;
	}
}
