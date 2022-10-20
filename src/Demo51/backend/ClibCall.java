package Demo51.backend;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ClibCall {
	private Set<String> apiSet;
	
    private ClibCall() {
    	apiSet = new HashSet<String>();
    	apiSet.add("printf");
    	apiSet.add("malloc");
    }
    private static ClibCall instance = null;
    
    public static ClibCall getInstance() {
    	
        if (instance == null) {
        	instance = new ClibCall();
        }
        
        return instance;
    }
    
    public boolean isAPICall(String funcName) {
    	return apiSet.contains(funcName);
    }
    
    public Object invokeAPI(String funcName) {
    	switch (funcName) {
    	
    	case "printf":
    		return handlePrintfCall();
    	
    	case "malloc":
    		return handleMallocCall();
    	default:
    		return null;
    	}
    }
    
    private Object handleMallocCall() {
    	ArrayList<Object> argsList = FunctionArgumentList.getFunctionArgumentList().getFuncArgList(false);
    	int size = (Integer)argsList.get(0);
    	int addr = 0;
    	
    	if (size > 0) {
    		MemoryHeap memHeap = MemoryHeap.getInstance();
        	addr = memHeap.allocMem(size);	
    	} 
    	
    	return addr;
    	
    }
    
    private Object handlePrintfCall() {
    	ArrayList<Object> argsList = FunctionArgumentList.getFunctionArgumentList().getFuncArgList(false);
    	String argStr = (String)argsList.get(0);
    	String formatStr = "";
    	
    	int i = 0;
    	int argCount = 1;
    	while (i < argStr.length()) {
    		if (argStr.charAt(i) == '%' && i+1 < argStr.length() && 
    				argStr.charAt(i+1) == 'd') {
    			i += 2;
    			formatStr += argsList.get(argCount);
    			argCount++;
    		} else {
    			formatStr += argStr.charAt(i);
    			i++;
    		}
    	}
    	
    	System.out.println(formatStr);
    	
    	return null;
    }
}
