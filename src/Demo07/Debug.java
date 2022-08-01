package Demo07;

public class Debug {
    private static int level = 0;
    
    public static void Enter(String func) {
    	
    	printSpace();
    	
    	System.out.print(func);
    	
    	level++;
    }
    
    public static void Leave(String func) {
    	printSpace();
    	
    	System.out.print(func);
    	
    	level--;
    }
    
    private static void  printSpace() {
    	for (int i = 0; i < level * 4; i++) {
    		System.out.print(" ");
    	}
    }
}
