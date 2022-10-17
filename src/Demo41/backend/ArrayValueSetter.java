package Demo41.backend;

import Demo41.frontend.Declarator;
import Demo41.frontend.Symbol;

public class ArrayValueSetter implements IValueSetter{
	private Symbol symbol;
	private int index = 0;
    @Override
    public void setValue(Object obj) {
    	Declarator declarator = symbol.getDeclarator(Declarator.ARRAY);
    	try {
			declarator.addElement(index, obj);
			
			System.out.println("Set Value of " + obj.toString() + " to Array of name " + symbol.getName() + " with index of " + index);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
    	
    }
    
    public ArrayValueSetter(Symbol symbol, int index) {
    	this.symbol = symbol;
    	this.index = index;
    }
}
