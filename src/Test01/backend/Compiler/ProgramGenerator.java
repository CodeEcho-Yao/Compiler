package Test01.backend.Compiler;

public class ProgramGenerator extends CodeGenerator {
	private static ProgramGenerator instance = null;
	
	public static ProgramGenerator getInstance() {
		if (instance == null) {
			instance = new ProgramGenerator();
		}
		
		return instance;
	}
	
    private ProgramGenerator()  {
    	
    }
    
    public void generate() {
    	emitDirective(Directive.CLASS_PUBLIC, programName);
    	emitDirective(Directive.SUPER, "java/lang/Object");
    	generateMainMethod();
    }
    
    private void generateMainMethod() {
    	emitBlankLine();
    	emitDirective(Directive.METHOD_PUBBLIC_STATIC, "main([Ljava/lang/String;)V");
    	
    }
    
    public void finish() {
    	emitDirective(Directive.END_METHOD);
    	emitDirective(Directive.END_CLASS);
    	
    	super.finish();
    }
}
