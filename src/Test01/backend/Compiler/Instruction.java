package Test01.backend.Compiler;

public enum Instruction {
    LDC("ldc"),
    
    GETSTATIC("getstatic"),
    
    INVOKEVIRTUAL("invokevirtual"),
    RETURN("return");
    
    private String text;
    Instruction(String s) {
    	this.text = s;
    }
    
    public String toString() {
    	return text;
    }
}
