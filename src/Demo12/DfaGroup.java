package Demo12;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import java.util.Iterator;
import java.util.List;



public class DfaGroup {
    private static int GROUP_COUNT = 0;
    
    private int group_num = 0;
    
    List<Dfa> dfaGroup = new ArrayList<Dfa>();
    List<Dfa> tobeRemove = new ArrayList<Dfa>();
    
    private DfaGroup() {
    	group_num = GROUP_COUNT;
    }
    
    public static DfaGroup createDfaGroup() {
    	DfaGroup group = new DfaGroup();
    	GROUP_COUNT++;
    	return group;
    }
    
    public void add(Dfa dfa) {
    	dfaGroup.add(dfa);
    }
    
    public void tobeRemove(Dfa dfa) {
    	tobeRemove.add(dfa);
    }
    
    public void commitRemove() {
    	Iterator it = tobeRemove.iterator();
    	while (it.hasNext()) {
    		dfaGroup.remove(it.next());
    	}
    	
    	tobeRemove.clear();
    }
    
    public boolean contains(Dfa dfa) {
    	return dfaGroup.contains(dfa);
    }
    
   public int size() {
	   return dfaGroup.size();
   }
   
   public Dfa get(int n) {
	   if (n < dfaGroup.size()) {
	       return dfaGroup.get(n);
	   }
	   
	   return null;
   }
    
    public int groupNum() {
    	return group_num;
    }
    
    public void printGroup() {
    	/*
    	 * 排序是为了调试演示方便，可以去掉，不影响逻辑
    	 */
    	Collections.sort(dfaGroup, new Comparator() {
    		@Override
    	    public int compare(Object o1, Object o2) {
    		    Dfa dfa1 = (Dfa)o1;
    		    Dfa dfa2 = (Dfa)o2;
    		    
    		    if (dfa1.stateNum > dfa2.stateNum) {
    		    	return 1;
    		    }
    		    
    		    return 0;
    		}
    	});
    	
    	System.out.println("Dfa Group num: " + group_num + " it has dfa: ");
    	Iterator it = dfaGroup.iterator();
    	while(it.hasNext()) {
    		Dfa dfa = (Dfa)it.next();
    		System.out.print(dfa.stateNum + " ");
    	}
    	
    	System.out.print("\n");
    }
}
