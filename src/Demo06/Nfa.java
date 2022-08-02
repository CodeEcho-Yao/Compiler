package Demo06;

import java.util.HashSet;

enum ANCHOR {
	NONE,
	START,
	END,
	BOTH 
}

public class Nfa {
	public static final int EPSILON = -1; //边对应的是ε
	public static final int CCL = -2; //边对应的是字符集
	public static final int EMPTY = -3; //该节点没有出去的边
	
    private int edge; //记录转换边对应的输入，输入可以是空, ε，字符集(CCL),或空，也就是没有出去的边
    public int getEdge() {
    	return edge;
    }
    
    public void setEdge(int type) {
    	edge = type;
    }
    
    HashSet inputSet; //用来存储字符集类
    Nfa     next;  //跳转的下一个状态，可以是空
    Nfa     next2; //跳转的另一个状态，当状态含有两条ε边时，这个指针才有效
    ANCHOR  anchor;  //对应的正则表达式是否开头含有^, 或结尾含有$,  或两种情况都有
    int     stateNum; //节点编号
    public  void setStateNum(int num) {
    	stateNum = num;
    }
    
    public void clearState() {
    	inputSet.clear();
    	next = next2 = null;
    	anchor = ANCHOR.NONE;
    	stateNum = -1;
    }
   
}
