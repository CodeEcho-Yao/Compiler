package Demo52.backend;

import java.util.ArrayList;
import java.util.Collections;

import Demo52.frontend.CGrammarInitializer;
import Demo52.frontend.Symbol;
import Demo52.frontend.TypeSystem;

public class FunctDeclExecutor extends BaseExecutor {
    private ArrayList<Object> argsList = null;
    private ICodeNode currentNode;
	@Override
	public Object Execute(ICodeNode root) {
		int production = (Integer)root.getAttribute(ICodeKey.PRODUCTION);
		Symbol symbol = null;
		currentNode = root;
		
		switch (production) {
		case CGrammarInitializer.NewName_LP_RP_TO_FunctDecl:
			root.reverseChildren();
			copyChild(root, root.getChildren().get(0));
			break;
			
		case  CGrammarInitializer.NewName_LP_VarList_RP_TO_FunctDecl:
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
	
	
	
	
}
