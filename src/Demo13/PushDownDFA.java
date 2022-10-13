package Demo13;

import InputSystem.Input;

import java.util.Stack;


public class PushDownDFA {
    enum StateAction {
    	PUSH_1,
    	POP,
    	ERROR,
    	ACCEPT
    };
    
    private  Stack<Integer> pushDownStack = new Stack<Integer>();
    private  StateAction[][] dfaTransformTable = null;

    private  Input input = null;
    private  final int  STATE_0 = 0;
    private  final int  STATE_1 = 1;

    private  boolean lastAccept = false;
    
    public PushDownDFA() {
    	this.input = new Input();
    	initTransformTable();
    	inputFromConsole();
    	
    	pushDownStack.push(STATE_0);
    }
    
    private void initTransformTable() {
    	dfaTransformTable = new StateAction[2][4];
    	
    	dfaTransformTable[0][0] = StateAction.PUSH_1;
    	dfaTransformTable[0][1] = StateAction.ERROR;
    	dfaTransformTable[0][2] = StateAction.ACCEPT;
    	dfaTransformTable[1][0] = StateAction.PUSH_1;
    	dfaTransformTable[1][1] = StateAction.POP;
    	dfaTransformTable[1][2] = StateAction.ERROR;
    }
    
    private void inputFromConsole() {
    	System.out.println("Please input parenthese");
    	input.ii_newFile(null); //控制台输入
    	input.ii_advance(); //更新缓冲区
    	input.ii_pushback(1);
    	
    	//去掉输入中的空格或换行
    	while (Character.isSpaceChar(input.ii_lookahead(1)) || input.ii_lookahead(1) == '\n') {
			input.ii_advance();
		}
    }
    
    public boolean recognizeParenthese() {
        char c = (char)input.ii_advance();
    	
    	while (true) {
    		if (c == '\n') {
    			c = (char)input.ii_advance();
    			continue;
    		}
    		
    		String receive = "";
    		receive += c;
    		if (c == Input.EOF) {
    			receive = "EOF";
    		}
    		System.out.println("receive char: " + receive);
    		int column = getColumnForInputChar(c);
    		int topStackSymbol = pushDownStack.peek();
    		StateAction action = dfaTransformTable[topStackSymbol][column];
    		takeAction(action);
    		c = (char)input.ii_advance();
    		
    		if (c == Input.EOF) {
    			break;
    		}
    	}
    	
    	return lastAccept;
    }
    
    private int getColumnForInputChar(char c) {
    	switch (c) {
    	case '(':
    		return 0;
    	case ')':
    		return 1;
    	case Input.EOF:
    		return 2;
    	default:
    	    System.exit(1);		
    	    return -1;
    	}
    }
    
    private void takeAction(StateAction action) {
    	switch(action) {
    	case PUSH_1:
    		System.out.println("take action by push state 1");
    		pushDownStack.push(STATE_1);
    		
    		break;
    	case POP:
    		System.out.println("take action by pop state from stack");
    		pushDownStack.pop();
    		break;
    	case ERROR:
    		System.out.println("Error! The input string can not be accepted");
    		System.exit(1);
    		break;
    	case ACCEPT:
    		System.out.println("DFA go into accept state");
    		lastAccept = true;
    		break;
    	}
    }
    
    public static void main(String[] args) {
    	PushDownDFA pda = new PushDownDFA();
    	pda.recognizeParenthese();
    }
}
