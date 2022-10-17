package Demo36.frontend;
import java.util.HashMap;
import java.util.Stack;

import Demo36.backend.CodeTreeBuilder;
import Demo36.backend.ICodeFactory;
import Demo36.backend.ICodeNode;


public class LRStateTableParser {
	private Lexer lexer;
	int    lexerInput = 0;
	int    nestingLevel = 0;
	int    enumVal = 0;
	String text = "";

	private Object attributeForParentNode = null;
	private TypeSystem typeSystem = TypeSystem.getTypeSystem();
	private Stack<Integer> statusStack = new Stack<Integer>();
	HashMap<Integer, HashMap<Integer, Integer>> lrStateTable = null;
	CodeTreeBuilder codeTreeBuilder = CodeTreeBuilder.getCodeTreeBuilder();
	
    public LRStateTableParser(Lexer lexer) {
    	this.lexer = lexer;
    	statusStack.push(0);
    	valueStack.push(null);
    	lexer.advance();
    	lexerInput = CTokenType.EXT_DEF_LIST.ordinal();
    	lrStateTable = GrammarStateManager.getGrammarManager().getLRStateTable();
    	codeTreeBuilder.setParser(this);
    }
    
    private Stack<Object> valueStack = new Stack<Object>();
    private Stack<Integer> parseStack = new Stack<Integer>();
    
    public TypeSystem getTypeSystem() {
    	return typeSystem;
    }
    
    public int getCurrentLevel() {
    	return nestingLevel;
    }
    
    private void showCurrentStateInfo(int stateNum) {
    	System.out.println("current input is :" + CTokenType.getSymbolStr(lexerInput));
    	
    	System.out.println("current state is: ");
		GrammarState state = GrammarStateManager.getGrammarManager().getGrammarState(stateNum);
		state.print();
    }
    
    public void parse() {
    
        while (true) {
        	
        	Integer action = getAction(statusStack.peek(), lexerInput);
        	
        	if (action == null) {
        		//解析出错
        		System.out.println("Shift for input: " + CTokenType.values()[lexerInput].toString());
        		System.err.println("The input is denied");
    			return;
        	}
        	
        	if (action > 0) {
        		//showCurrentStateInfo(action);
        		
        		//shift 操作
                statusStack.push(action);
    			text = lexer.yytext;
    			
    			parseStack.push(lexerInput);
    			
    			if (CTokenType.isTerminal(lexerInput)) {
    				System.out.println("Shift for input: " + CTokenType.values()[lexerInput].toString());
    				Object obj = takeActionForShift(lexerInput);
    			
    				lexer.advance();
        			lexerInput = lexer.lookAhead;
        			valueStack.push(obj);
    			} else {
    				lexerInput = lexer.lookAhead;
    			}
    			
        	} else {
        		if (action == 0) {
        			System.out.println("The input can be accepted");
        			return;
        		}
        		
        		int reduceProduction = - action;
        		Production product = ProductionManager.getProductionManager().getProductionByIndex(reduceProduction);
        		System.out.println("reduce by product: ");
        		product.print();
        		
        		takeActionForReduce(reduceProduction);
        	
        		
        		int rightSize = product.getRight().size();
        		while (rightSize > 0) {
        			parseStack.pop();
        			valueStack.pop();
        			statusStack.pop();
        			rightSize--;
        		}
        		
        		lexerInput = product.getLeft();
    			parseStack.push(lexerInput);
    			valueStack.push(attributeForParentNode);
        	}
        }
    }
    
    private Object takeActionForShift(int token) {
    	if (token == CTokenType.LP.ordinal() || token == CTokenType.LC.ordinal()) {
    		nestingLevel++;
    	}
    	if (token == CTokenType.RP.ordinal() || token == CTokenType.RC.ordinal()) {
    		nestingLevel--;
    	}
    	
    	
    	return null;
    	
    }
    
    private void takeActionForReduce(int productNum) {
    	switch(productNum) {
    	case CGrammarInitializer.TYPE_TO_TYPE_SPECIFIER:
    		attributeForParentNode = typeSystem.newType(text);
    		break;
    	case CGrammarInitializer.EnumSpecifier_TO_TypeSpecifier:
    		attributeForParentNode = typeSystem.newType("int");
    		break;
    	case CGrammarInitializer.StructSpecifier_TO_TypeSpecifier:
    		attributeForParentNode = typeSystem.newType(text);
    		TypeLink link = (TypeLink)attributeForParentNode;
    		Specifier sp = (Specifier)link.getTypeObject();
    		sp.setType(Specifier.STRUCTURE);
    		StructDefine struct = (StructDefine)valueStack.get(valueStack.size() - 1);
    		sp.setStructObj(struct);
    		break;
    	
    	case CGrammarInitializer.SPECIFIERS_TypeOrClass_TO_SPECIFIERS:
    		attributeForParentNode = valueStack.peek();
    		Specifier last = (Specifier)((TypeLink)valueStack.get(valueStack.size() - 2)).getTypeObject();
    		Specifier dst = (Specifier)((TypeLink)attributeForParentNode).getTypeObject();
    		typeSystem.specifierCpy(dst, last);
    		break;
    	case CGrammarInitializer.NAME_TO_NewName:
    	case CGrammarInitializer.Name_TO_NameNT:
    		attributeForParentNode = typeSystem.newSymbol(text, nestingLevel);
    		break;
    	case CGrammarInitializer.START_VarDecl_TO_VarDecl:
    	case CGrammarInitializer.Start_VarDecl_TO_VarDecl:
    		typeSystem.addDeclarator((Symbol)attributeForParentNode, Declarator.POINTER);
    		break;
    	case CGrammarInitializer.ExtDeclList_COMMA_ExtDecl_TO_ExtDeclList:
    	case CGrammarInitializer.VarList_COMMA_ParamDeclaration_TO_VarList:
    	case CGrammarInitializer.DeclList_Comma_Decl_TO_DeclList:
    	case CGrammarInitializer.DefList_Def_TO_DefList:
    	{
    		
    		Symbol currentSym = (Symbol)attributeForParentNode;
    		Symbol lastSym = null;
    		if (productNum == CGrammarInitializer.DefList_Def_TO_DefList) {
    			//这个表达式中没有逗号,所以减2，其他的有逗号，所以减3
    			 lastSym = (Symbol)valueStack.get(valueStack.size() - 2);
    		} else {
    			lastSym = (Symbol)valueStack.get(valueStack.size() - 3);	
    		}
    		
    		currentSym.setNextSymbol(lastSym);
    	}
    		break;
    	case CGrammarInitializer.OptSpecifier_ExtDeclList_Semi_TO_ExtDef:
    	case CGrammarInitializer.TypeNT_VarDecl_TO_ParamDeclaration:
    	case CGrammarInitializer.Specifiers_DeclList_Semi_TO_Def:
    		Symbol symbol = (Symbol)attributeForParentNode;
    		TypeLink specifier = (TypeLink)(valueStack.get(valueStack.size() - 3));
    		typeSystem.addSpecifierToDeclaration(specifier, symbol);
    		typeSystem.addSymbolsToTable(symbol);
    		break;
    	case CGrammarInitializer.NewName_LP_VarList_RP_TO_FunctDecl:
    		setFunctionSymbol(true);
    		Symbol argList = (Symbol)valueStack.get(valueStack.size() - 2);
    		((Symbol)attributeForParentNode).args = argList;
    		break;
    		
    	case CGrammarInitializer.NewName_LP_RP_TO_FunctDecl:
    		setFunctionSymbol(false);
    		break;
    		
    	case CGrammarInitializer.Name_To_Tag:
    		attributeForParentNode = typeSystem.getStructObjFromTable(text);
    		if (attributeForParentNode == null) {
    			attributeForParentNode = new StructDefine(text, nestingLevel, null);
        		typeSystem.addStructToTable((StructDefine)attributeForParentNode);	
    		}
    		
    		break;
    		
    	case CGrammarInitializer.Struct_OptTag_LC_DefList_RC_TO_StructSpecifier:
    		Symbol defList = (Symbol)valueStack.get(valueStack.size() - 2);
    		StructDefine structObj = (StructDefine)valueStack.get(valueStack.size() - 4);
    		structObj.setFields(defList);
    		attributeForParentNode = structObj;
    		break;
    	
    	case CGrammarInitializer.Enum_TO_EnumNT:
    		enumVal = 0;
    		break;
    		
    	case CGrammarInitializer.NameNT_TO_Emurator:
    		doEnum();
    		break;
    	case CGrammarInitializer.Name_Eequal_ConstExpr_TO_Enuerator:
    		enumVal = (Integer)(valueStack.get(valueStack.size() - 1));
    		attributeForParentNode = (Symbol)(valueStack.get(valueStack.size() - 3));
    		doEnum();
    		break;
    	case CGrammarInitializer.Number_TO_ConstExpr:
    		attributeForParentNode = Integer.valueOf(text);
    		break;
    	}
    	
    	codeTreeBuilder.buildCodeTree(productNum, text);
    }
    
  
    
    private void doEnum() {
    	Symbol symbol = (Symbol)attributeForParentNode;
    	if (convSymToIntConst(symbol, enumVal)) {
    		typeSystem.addSymbolsToTable(symbol);
    		enumVal++;
    	}
    	else {
    		System.err.println("enum symbol redefinition: " + symbol.name);
    	}
    }
    
    private boolean convSymToIntConst(Symbol symbol, int val) {
    	if (symbol.getTypeHead() != null) {
    		return false;
    	}
    	
    	TypeLink typeLink = typeSystem.newType("int");
    	Specifier specifier = (Specifier)typeLink.getTypeObject();
    	specifier.setConstantVal(val);
    	specifier.setType(Specifier.CONSTANT);
    	symbol.addSpecifier(typeLink);
    	
    	return true;
    }
    
    private void setFunctionSymbol(boolean hasArgs) {
    	Symbol funcSymbol = null;
    	if (hasArgs) {
    		funcSymbol = (Symbol)valueStack.get(valueStack.size() - 4);
    	} else {
    		funcSymbol = (Symbol)valueStack.get(valueStack.size() - 3);
    	}
    	 
		typeSystem.addDeclarator(funcSymbol, Declarator.FUNCTION);
		attributeForParentNode = funcSymbol;
    }
    
    private Integer getAction(Integer currentState, Integer currentInput) {
    	HashMap<Integer, Integer> jump = lrStateTable.get(currentState);
    	if (jump != null) {
    		Integer next = jump.get(currentInput);
    		if (next != null) {
    			return next;
    		}
    	}
    	
    	return null;
    }
    
}
