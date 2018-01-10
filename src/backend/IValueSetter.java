package backend;

import frontend.Symbol;

public interface IValueSetter {
   public void setValue(Object obj) throws Exception;
   public Symbol getSymbol();
}
