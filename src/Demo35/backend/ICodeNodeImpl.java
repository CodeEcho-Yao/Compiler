package Demo35.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import Demo35.frontend.CTokenType;

public class ICodeNodeImpl extends HashMap<ICodeKey, Object> implements ICodeNode{
    private  CTokenType type;
    private  ICodeNode parent;
    private  ArrayList<ICodeNode> children;
    
    public ICodeNodeImpl(CTokenType type) {
    	this.type = type;
    	this.parent = null;
    	this.children = new ArrayList<ICodeNode>();
    	setAttribute(ICodeKey.TokenType, type);
    }
    
    public ICodeNode addChild(ICodeNode node) {
    	if (node != null) {
    		children.add(node);
    		((ICodeNodeImpl)node).parent = this;
    	}
    	
    	return node;
    }
    
   

	@Override
	public ICodeNode getParent() {
		// TODO Auto-generated method stub
		return parent;
	}

	@Override
	public ArrayList<ICodeNode> getChildren() {
		// TODO Auto-generated method stub
		return children;
	}

	@Override
	public void setAttribute(ICodeKey key, Object value) {
		put(key, value);
	}

	@Override
	public Object getAttribute(ICodeKey key) {
		
		return get(key);
	}

	@Override
	public ICodeNode copy() {
		ICodeNodeImpl copy = (ICodeNodeImpl)ICodeFactory.createICodeNode(type);
		Set<Entry<ICodeKey, Object>> attributes = entrySet();
		Iterator <Entry<ICodeKey, Object>> it = attributes.iterator();
		
		while (it.hasNext()) {
			Entry<ICodeKey, Object> attribute = it.next();
			copy.put(attribute.getKey(), attribute.getValue());
		}
		
		return copy;
	}
    
	@Override
	public String toString() {
		return type.toString();
	}
   
}
