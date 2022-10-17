package Demo37.backend;

import java.util.ArrayList;
import java.util.Stack;

import Demo37.frontend.CGrammarInitializer;
import Demo37.frontend.CTokenType;
import Demo37.frontend.Declarator;
import Demo37.frontend.LRStateTableParser;
import Demo37.frontend.Symbol;
import Demo37.frontend.TypeSystem;

public class CodeTreeBuilder {
    private Stack<ICodeNode> codeNodeStack = new Stack<ICodeNode>();
    private LRStateTableParser parser = null;
    private TypeSystem typeSystem = null;
    private Stack<Object> valueStack = null;
    
    private static CodeTreeBuilder treeBuilder = null;
    public static CodeTreeBuilder getCodeTreeBuilder() {
    	if (treeBuilder == null) {
    		treeBuilder = new CodeTreeBuilder();
    	}
    	
    	return treeBuilder;
    }
    
    public void setParser(LRStateTableParser parser) {
    	this.parser = parser;
    	typeSystem = parser.getTypeSystem();
    	valueStack = parser.getValueStack();
    }
    
    public ICodeNode buildCodeTree(int production, String text) {
    	ICodeNode node = null;
    	Symbol symbol = null;
    	
    	switch (production) {
    	case CGrammarInitializer.Number_TO_Unary:
    	case CGrammarInitializer.Name_TO_Unary:
    	case CGrammarInitializer.String_TO_Unary:
    		node = ICodeFactory.createICodeNode(CTokenType.UNARY);
    		if (production == CGrammarInitializer.Name_TO_Unary) {
    			symbol =typeSystem.getSymbolByText(text, parser.getCurrentLevel());
    			node.setAttribute(ICodeKey.SYMBOL, symbol);
    		} 
    		
    		node.setAttribute(ICodeKey.TEXT, text);
    		break;
    		
    		case CGrammarInitializer.Unary_LB_Expr_RB_TO_Unary:
    			//访问或更改数组元素
    			node = ICodeFactory.createICodeNode(CTokenType.UNARY);
    			node.addChild(codeNodeStack.pop());  //EXPR
    			node.addChild(codeNodeStack.pop());  //UNARY
    			
    		break;
    		
    	case CGrammarInitializer.Uanry_TO_Binary:
    		node = ICodeFactory.createICodeNode(CTokenType.BINARY);
    		ICodeNode child = codeNodeStack.pop();
    		node.setAttribute(ICodeKey.TEXT, child.getAttribute(ICodeKey.TEXT));		
    		node.addChild(child);
    		break;
    		
        
    		
    	case CGrammarInitializer.Binary_TO_NoCommaExpr:
    	case CGrammarInitializer.NoCommaExpr_Equal_NoCommaExpr_TO_NoCommaExpr:
    		node = ICodeFactory.createICodeNode(CTokenType.NO_COMMA_EXPR);
    		child = codeNodeStack.pop();
    		String t = (String)child.getAttribute(ICodeKey.TEXT);
    		node.addChild(child);
    		if (production == CGrammarInitializer.NoCommaExpr_Equal_NoCommaExpr_TO_NoCommaExpr) {
    			child = codeNodeStack.pop();
    			t = (String)child.getAttribute(ICodeKey.TEXT);
    			node.addChild(child);
    		}
    		break;
    		
    	case CGrammarInitializer.Binary_Plus_Binary_TO_Binary:
    		node = ICodeFactory.createICodeNode(CTokenType.BINARY);

    		node.addChild(codeNodeStack.pop());
    		node.addChild(codeNodeStack.pop());
    		break;
    		
    	case CGrammarInitializer.NoCommaExpr_TO_Expr:
    		node = ICodeFactory.createICodeNode(CTokenType.EXPR);
    		node.addChild(codeNodeStack.pop());
    		break;
    	case CGrammarInitializer.Expr_Semi_TO_Statement:
    		node = ICodeFactory.createICodeNode(CTokenType.STATEMENT);
			node.addChild(codeNodeStack.pop());	
			break;
			
    	case CGrammarInitializer.LocalDefs_TO_Statement:
    		node = ICodeFactory.createICodeNode(CTokenType.STATEMENT);
    		break;
    		
    	case CGrammarInitializer.Statement_TO_StmtList:
    		node = ICodeFactory.createICodeNode(CTokenType.STMT_LIST);
    		if (codeNodeStack.size() > 0) {
    			node.addChild(codeNodeStack.pop());	
    		}
    		
    		break;
    		
    		
    	case CGrammarInitializer.StmtList_Statement_TO_StmtList:
    		node = ICodeFactory.createICodeNode(CTokenType.STMT_LIST);
    		node.addChild(codeNodeStack.pop());
    		node.addChild(codeNodeStack.pop());
    		break;
    		
        
    	}
    	
    	
    	
    	if (node != null) {
    		node.setAttribute(ICodeKey.PRODUCTION, production);
    		codeNodeStack.push(node);	
    	}
    	
    	return node;
    }
    
    
    
    
    
    public ICodeNode getCodeTreeRoot() {
    	return codeNodeStack.pop();
    }
}
