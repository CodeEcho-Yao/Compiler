package Demo41.backend;

import Demo41.frontend.CGrammarInitializer;
import Demo41.frontend.CTokenType;

public class ExecutorFactory {
    private static ExecutorFactory executorFactory = null;
    private ExecutorFactory() {
    	
    }
    
    public static ExecutorFactory getExecutorFactory() {
    	if (executorFactory == null) {
    		executorFactory = new ExecutorFactory();
    	}
    	
    	return executorFactory;
    }
    
    public Executor getExecutor(ICodeNode node) {
    	CTokenType type = (CTokenType)node.getAttribute(ICodeKey.TokenType);
    	switch (type) {
    	case UNARY:
    		return new UnaryNodeExecutor();
    	case BINARY:
    		return new BinaryExecutor();
    	case NO_COMMA_EXPR:
    		return new NoCommaExprExecutor();
    	case EXPR:
    		return new ExprExecutor();
    	case STATEMENT:
    		return new StatementExecutor();
    	case STMT_LIST:
    		return new StatementListExecutor();
    	case TEST:
    		return new TestExecutor();
    	case IF_STATEMENT:
    		return new IfStatementExecutor();
    	case IF_ELSE_STATEMENT:
    		return new ElseStatementExecutor();
    		
    	case OPT_EXPR:
    		return new OptExprExecutor();
    		
    	case END_OPT_EXPR:
    		return new EndOptExecutor();
    		
    	case INITIALIZER:
    		return new InitializerExecutor();
    		
    	}
    	
    	return null;
    }
}
