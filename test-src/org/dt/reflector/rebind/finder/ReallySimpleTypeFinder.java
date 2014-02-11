package org.dt.reflector.rebind.finder;

import java.util.HashSet;
import java.util.Set;

import org.dt.reflector.rebind.ReflectorGenerator;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;

public class ReallySimpleTypeFinder implements TypesToReflectFinder {

  @Override
  public Set<TypeToReflect> findTypes(TreeLogger logger, GeneratorContext context) throws UnableToCompleteException {
    Set<TypeToReflect> types = new HashSet<TypeToReflect>();
    JClassType type = context.getTypeOracle().findType("org.dt.reflector.client.ReallySimpleBean");
    
    String qualifiedSourceName = type.getQualifiedSourceName();
    String reflectorQualifiedSourceName = new ReflectorGenerator().generate(logger, context, qualifiedSourceName);
    
    types.add(new TypeToReflect(qualifiedSourceName, reflectorQualifiedSourceName));
    
    return types;
  }

}
