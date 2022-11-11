package Test02.backend.Compiler;

public enum Instruction {
    LDC("ldc"),
    
    GETSTATIC("getstatic"),
    SIPUSH("sipush"),
    IADD("iadd"),
    IMUL("imul"),
    INVOKEVIRTUAL("invokevirtual"),
    INVOKESTATIC("invokestatic"),
    RETURN("return"),
    IRETURN("ireturn"),
    ILOAD("iload"),
    ISTORE("istore");
    
    private String text;
    Instruction(String s) {
    	this.text = s;
    }
    
    public String toString() {
    	return text;
    }
}
