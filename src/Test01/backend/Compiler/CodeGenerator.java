package Test01.backend.Compiler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class CodeGenerator {
    private static PrintWriter assemblyFile;
    private static int instructionCount = 0;
    
    protected static String programName = "CSourceToJava";
   
    
    public CodeGenerator() {
    	
        String assemblyFileName = programName + ".j";
    	
    	try {
			assemblyFile = new PrintWriter(new PrintStream(new 
					File(assemblyFileName)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    public void emitDirective(Directive directive) {
    	assemblyFile.println(directive.toString());
    	assemblyFile.flush();
    	++instructionCount;
    }
    
    public void emitDirective(Directive directive, String operand) {
    	assemblyFile.println(directive.toString() + " " + operand);
    	assemblyFile.flush();
    	++instructionCount;
    }
    
    public void emitDirective(Directive directive, int operand) {
    	assemblyFile.println(directive.toString() + " " + operand);
    	++instructionCount;
    }
    
    public void emitDirective(Directive directive, String operand1, String operand2) {
    	assemblyFile.println(directive.toString() + " " + operand1 + " " + operand2);
    	++instructionCount;
    }
    
    public void emitDirective(Directive directive, String operand1, String operand2, String operand3) {
    	assemblyFile.println(directive.toString() + " " + operand1 + " " + operand2 + " " + operand3);
    	++instructionCount;
    }
    
    public void emit(Instruction opcode) {
    	assemblyFile.println("\t" + opcode.toString());
    	assemblyFile.flush();
    	++instructionCount;
    }
    
    public void emit(Instruction opcode, String operand) {
    	assemblyFile.println("\t" + opcode.toString() + "\t" + operand);
    	assemblyFile.flush();
    	++instructionCount;
    }
    
    public void emitBlankLine() {
    	assemblyFile.println();
    	assemblyFile.flush();
    }
    
    public void finish() {
    	assemblyFile.close();
    }
    
    
}
