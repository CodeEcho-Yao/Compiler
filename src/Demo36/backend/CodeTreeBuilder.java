package Demo36.backend;

import java.util.ArrayList;
import java.util.Stack;

import Demo36.frontend.CGrammarInitializer;
import Demo36.frontend.CTokenType;
import Demo36.frontend.LRStateTableParser;

import Demo36.frontend.Symbol;
import Demo36.frontend.TypeSystem;

public class CodeTreeBuilder {
    private Stack<ICodeNode> codeNodeStack = new Stack<ICodeNode>();
    private LRStateTableParser parser = null;
    private TypeSystem typeSystem = null;
    
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
    			symbol = getSymbolByText(text);
    			node.setAttribute(ICodeKey.SYMBOL, symbol);
    		} 
    		
    		node.setAttribute(ICodeKey.TEXT, text);
    		break;
    		
    	case CGrammarInitializer.Uanry_TO_Binary:
    		node = ICodeFactory.createICodeNode(CTokenType.BINARY);
    		ICodeNode child = codeNodeStack.pop();
    		String t = (String)child.getAttribute(ICodeKey.TEXT);
    		node.setAttribute(ICodeKey.TEXT, child.getAttribute(ICodeKey.TEXT));
    		Symbol sym = (Symbol)child.getAttribute(ICodeKey.SYMBOL);
    		node.addChild(child);
    		break;
    		
    	case CGrammarInitializer.Binary_TO_NoCommaExpr:
    	case CGrammarInitializer.NoCommaExpr_Equal_NoCommaExpr_TO_NoCommaExpr:
    		node = ICodeFactory.createICodeNode(CTokenType.NO_COMMA_EXPR);
    		child = codeNodeStack.pop();
    		t = (String)child.getAttribute(ICodeKey.TEXT);
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
    
    
    
    private Symbol getSymbolByText(String text) {
    	ArrayList<Symbol> symbolList = typeSystem.getSymbol(text);
    	int i = 0;
    	int level = parser.getCurrentLevel();
    	while (i < symbolList.size()) {
    		if (symbolList.get(i).getLevel() == level) {
    			return symbolList.get(i);
    		}
    	}
    	
    	return null;
    }
    
    public ICodeNode getCodeTreeRoot() {
    	return codeNodeStack.pop();
    }
}
