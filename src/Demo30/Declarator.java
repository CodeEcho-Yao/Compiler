package Demo30;

public class Declarator {
    public static int  POINTER = 0;
    public static int  ARRAY = 1;
    public static int  FUNCTION = 2;
    
    private int  declareType = POINTER;
    private int  numberOfElements = 0;
    
    public Declarator(int type) {
    	if (type < POINTER) {
    		declareType = POINTER;
    	} 
    	
    	if (type > FUNCTION) {
    		declareType = FUNCTION;
    	}
    }
    
    public void setElementNum(int num) {
    	if (num < 0) {
    		numberOfElements = 0;
    	} else {
    		numberOfElements = num;
    	}
    }
    
    public int getType() {
    	return declareType ;
    }
    
    public int getElementNum() {
    	return numberOfElements;
    }
}
