package Demo49.backend;

import Demo49.frontend.CTokenType;

public class ICodeFactory {
    
    public static ICodeNode createICodeNode(CTokenType type) {
    	return new ICodeNodeImpl(type);
    }
}
