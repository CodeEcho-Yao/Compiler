package Test02.backend;

import java.util.ArrayList;
import java.util.Collections;

import Test02.backend.Compiler.Directive;
import Test02.backend.Compiler.ProgramGenerator;
import Test02.frontend.CGrammarInitializer;
import Test02.frontend.Declarator;
import Test02.frontend.Specifier;
import Test02.frontend.Symbol;
import Test02.frontend.TypeSystem;

public class FunctDeclExecutor extends BaseExecutor {
    private ArrayList<Object> argsList = null;
    private ICodeNode currentNode;
    ProgramGenerator generator = ProgramGenerator.getInstance();
    
	@Override
	public Object Execute(ICodeNode root) {
		int production = (Integer)root.getAttribute(ICodeKey.PRODUCTION);
		Symbol symbol = null;
		currentNode = root;
		
		switch (production) {
		case CGrammarInitializer.NewName_LP_RP_TO_FunctDecl:
			root.reverseChildren();
			ICodeNode n = root.getChildren().get(0);
			String name = (String)n.getAttribute(ICodeKey.TEXT);
			if (name != null && name.equals("main") != true) {
				String declaration = name+"()V";
				generator.emitDirective(Directive.METHOD_PUBBLIC_STATIC, declaration);
				generator.setNameAndDeclaration(name, declaration);
			}
			copyChild(root, root.getChildren().get(0));
			break;
			
		case  CGrammarInitializer.NewName_LP_VarList_RP_TO_FunctDecl:
			n = root.getChildren().get(0);
			name = (String)n.getAttribute(ICodeKey.TEXT);
			if (name != null && name.equals("main") != true) {
				String declaration = name + emitArgs();
				generator.emitDirective(Directive.METHOD_PUBBLIC_STATIC, declaration);
				generator.setNameAndDeclaration(name, declaration);
			}
			
			symbol = (Symbol)root.getAttribute(ICodeKey.SYMBOL);
			//获得参数列表
			Symbol args = symbol.getArgList();
			initArgumentList(args);
			
			
			if (args == null || argsList == null || argsList.isEmpty()) {
				//如果参数为空，那就是解析错误
				System.err.println("Execute function with arg list but arg list is null");
				System.exit(1);
			}
		
 			break;
		}
		return root;
	}
	
	private void initArgumentList(Symbol args) {
		if (args == null) {
			return;
		}
		
		
		argsList = FunctionArgumentList.getFunctionArgumentList().getFuncArgList(true);
		Symbol eachSym = args;
		int count = 0;
		while (eachSym != null) {
			IValueSetter setter = (IValueSetter)eachSym;
			try {
				/*
				 * 将每个输入参数设置为对应值并加入符号表
				 */
				setter.setValue(argsList.get(count));
				count++;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			eachSym = eachSym.getNextSymbol();
		}
	}
	
	
	private String emitArgs() {
		argsList = FunctionArgumentList.getFunctionArgumentList().getFuncArgList(true);
		String args = "(";
		for (int i = 0; i < argsList.size(); i++) {
			Symbol symbol = (Symbol)argsList.get(i);
			String arg = "";
			if (symbol.getDeclarator(Declarator.ARRAY) != null) {
				arg += "[";
			}
			
			if (symbol.hasType(Specifier.INT)) {
				arg += "I";
			}
			
			args += arg;
		}
		
		args += ")V";
		
		generator.emitString(args);
		
		return args;
	}

	
}
