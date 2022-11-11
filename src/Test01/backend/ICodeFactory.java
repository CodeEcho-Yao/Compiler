package Test01.backend;

import Test01.frontend.CTokenType;

public class ICodeFactory {
    
    public static ICodeNode createICodeNode(CTokenType type) {
    	return new ICodeNodeImpl(type);
    }
}
