package Test01.backend;

import java.util.ArrayList;

import Test01.frontend.CGrammarInitializer;

public class ArgsExecutor extends BaseExecutor {

	@Override
	public Object Execute(ICodeNode root) {
		int production = (Integer)root.getAttribute(ICodeKey.PRODUCTION);
		ArrayList<Object> argList = new ArrayList<Object>();
		ArrayList<Object> symList = new ArrayList<Object>();
		ICodeNode child ;
		switch (production) {
		case CGrammarInitializer.NoCommaExpr_TO_Args:
			child = (ICodeNode)executeChild(root, 0);
			Object objVal = child.getAttribute(ICodeKey.VALUE);
			argList.add(objVal);
			objVal = child.getAttribute(ICodeKey.SYMBOL);
			symList.add(objVal);
			break;
			
		case CGrammarInitializer.NoCommaExpr_Comma_Args_TO_Args:
			child = executeChild(root, 0);
			objVal = child.getAttribute(ICodeKey.VALUE);
			argList.add(objVal);
			objVal = child.getAttribute(ICodeKey.SYMBOL);
			symList.add(objVal);
			
			child = (ICodeNode)executeChild(root, 1);
			ArrayList<Object> list = (ArrayList<Object>)child.getAttribute(ICodeKey.VALUE);
			argList.addAll(list);
			list = (ArrayList<Object>)child.getAttribute(ICodeKey.SYMBOL);
			symList.add(list);
			break;
		}
		
		root.setAttribute(ICodeKey.VALUE, argList);
		root.setAttribute(ICodeKey.SYMBOL, symList);
		return root;
	}

}
