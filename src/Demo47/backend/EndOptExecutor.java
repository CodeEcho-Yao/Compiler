package Demo47.backend;

public class EndOptExecutor extends BaseExecutor {

	@Override
	public Object Execute(ICodeNode root) {
		
		return executeChild(root, 0);
	}

}
