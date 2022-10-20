package Demo52.backend;

import java.util.ArrayList;
import java.util.Collections;

public class FunctionArgumentList {
    private static FunctionArgumentList argumentList = null;
    private ArrayList<Object> funcArgList = new ArrayList<Object>();
    
    public static FunctionArgumentList getFunctionArgumentList() {
    	if (argumentList == null) {
    		argumentList = new FunctionArgumentList();
    	}
    	
    	return argumentList;
    }
    
    public void setFuncArgList(ArrayList<Object> list) {
    	funcArgList = list;
    }
    
    public ArrayList<Object> getFuncArgList(boolean reverse) {
    	
    	if (reverse == true) {
    		Collections.reverse(funcArgList);
    	}
    	
    	return funcArgList;
    }
    
	private FunctionArgumentList() {}
}
