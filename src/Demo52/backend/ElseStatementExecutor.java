package Demo52.backend;

import java.util.Collections;

public class ElseStatementExecutor extends BaseExecutor {

	@Override
	public Object Execute(ICodeNode root) {
		 
		//先执行if 部分
    	 ICodeNode res = executeChild(root, 0);
    	 Object obj = res.getAttribute(ICodeKey.VALUE);
    	 if ((Integer)obj == 0) {
    		 //if 部分没有执行，所以执行else部分
    		 res = executeChild(root, 1); 
    	 }
    	 
    	 copyChild(root, res);
    	 
    	 return root;
	}

}
