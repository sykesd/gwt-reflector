package org.dt.reflector.rebind.finder;

import java.util.Set;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;

public interface TypesToReflectFinder {

  /**
   * Find a set of types to generate reflection implementations for.
   * <p>
   * An implementation may return an empty set, if it cannot find any types to reflect.
   * <p>
   * 
   * @param logger the logger
   * @param context the generation context in which we are running
   * @return the set of types to generate reflection for
   */
  Set<TypeToReflect> findTypes(TreeLogger logger, GeneratorContext context) throws UnableToCompleteException;
  
}
