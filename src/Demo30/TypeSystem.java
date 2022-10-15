package Demo30;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class TypeSystem {
	private HashMap<String, ArrayList<Symbol>> symbolTable = new HashMap<String, ArrayList<Symbol>>();
	
	public void addSymbolsToTable(Symbol headSymbol) {
		while (headSymbol != null) {
			ArrayList<Symbol> symList = symbolTable.get(headSymbol.name);
			if (symList == null) {
				symList = new ArrayList<Symbol>();
				symList.add(headSymbol);
				symbolTable.put(headSymbol.name, symList);
			}
			else {
				handleDublicateSymbol(headSymbol, symList);
			}
			
			headSymbol = headSymbol.getNextSymbol();
		}
	}
	
	private void handleDublicateSymbol(Symbol symbol, ArrayList<Symbol>symList) {
		boolean harmless = true;
		Iterator it = symList.iterator();
		while (it.hasNext()) {
			Symbol sym = (Symbol)it.next();
			if (sym.level == symbol.level) {
				//TODO, handle duplication here
			}
		}
		
		if (harmless == true) {
			symList.add(symbol);
		}
	}
	
	
	
    public TypeLink newType(String typeText) {
    	Specifier sp = null;
    	int type = Specifier.CHAR;
    	boolean isLong = false, isSigned = true;
    	switch (typeText.charAt(0)) {
    	case 'c': 
    		if (typeText.charAt(1) == 'h') {
    			type = Specifier.CHAR;
    		}
    		break; 
    	case 'd':
    	case 'f':
    		System.err.println("No floating point support");
    		System.exit(1);
    		break;
    	case 'i':
    		type = Specifier.INT;
    		break;
    	case 'l':
    		isLong = true;
    		break;
    	case 'u':
    		isSigned = false;
    		break;
    	case 'v':
    		if (typeText.charAt(2) == 'i') {
    			type = Specifier.VOID;
    		}
    		break;
    	case 's':
    		//ignore short signed
    		break;
    	}
    	
    	sp = new Specifier();
    	sp.setType(type);
    	sp.setLong(isLong);
    	sp.setSign(isSigned);
    	
    	TypeLink link = new TypeLink(false, false, sp);
    	return link;
    }
    
    public void specifierCpy(Specifier dst, Specifier org) {
    	dst.setConstantVal(org.getConstantVal());
    	dst.setExternal(org.isExternal());
    	dst.setLong(org.getLong());
    	dst.setOutputClass(org.getOutputClass());
    	dst.setSign(org.isSign());
    	dst.setStatic(org.isStatic());
    	dst.setStorageClass(org.getStorageClass());
    }
    
    public TypeLink newClass(String classText) {
    	Specifier sp = new Specifier();
    	sp.setType(Specifier.NONE);
    	setClassType(sp, classText.charAt(0));
    	
    	TypeLink link = new TypeLink(false, false, sp);
    	return link;
    }
    
    private void setClassType(Specifier sp, char c) {
    	switch(c) {
    	case 0:
    		sp.setStorageClass(Specifier.FIXED);
    		sp.setStatic(false);
    		sp.setExternal(false);
    		break;
    	case 't':
    		sp.setStorageClass(Specifier.TYPEDEF);
    		break;
    	case 'r':
    		sp.setStorageClass(Specifier.REGISTER);
    		break;
    	case 's':
    		sp.setStatic(true);
    		break;
    	case 'e':
    		sp.setExternal(true);
    		break;
    		
    	default:
    			System.err.println("Internal error, Invalid Class type");
    			System.exit(1);
    			break;
     	}
    }
    
    public Symbol newSymbol(String name, int level) {
    	return new Symbol(name, level);
    }
    
    public void addDeclarator(Symbol symbol, int declaratorType) {
    	Declarator declarator = new Declarator(declaratorType);
    	TypeLink link = new TypeLink(true, false, declarator);
    	symbol.addDeclarator(link);
    }
    
    public void addSpecifierToDeclaration(TypeLink specifier, Symbol symbol) {
    	while (symbol != null) {
    		symbol.addSpecifier(specifier);
    		symbol = symbol.getNextSymbol();
    	}
    }
    
   
    
}
