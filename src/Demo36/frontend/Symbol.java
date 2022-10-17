package Demo36.frontend;

public class Symbol {
    String  name;
    String  rname;
    
    int       level;  //变量的层次
    boolean   implicit;  //是否是匿名变量
    boolean   duplicate;   //是否是同名变量
    
    Symbol    args;   //如果该符号对应的是函数名,那么args指向函数的输入参数符号列表
    
    private Symbol    next = null;  //指向下一个同层次的变量符号
    
    private Object value = null;
    
    TypeLink  typeLinkBegin = null;
    TypeLink  typeLinkEnd = null;
    
    public Symbol(String name, int level) {
    	this.name = name;
    	this.level = level;
    }
    
    public String getName() {
    	return name;
    }
    
    public void setValue(Object obj) {
    	this.value = obj;
    }
    
    public Object getValue() {
    	return value;
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
    
    public TypeLink getTypeHead() {
    	return typeLinkBegin;
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
}
