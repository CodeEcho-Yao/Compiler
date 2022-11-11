package Test01.backend;

import Test01.frontend.Symbol;

public interface IValueSetter {
   public void setValue(Object obj) throws Exception;
   public Symbol getSymbol();
}
