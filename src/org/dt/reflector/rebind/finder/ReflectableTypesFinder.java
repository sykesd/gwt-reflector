package org.dt.reflector.rebind.finder;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.dt.reflector.rebind.ReflectorGenerator;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.ConfigurationProperty;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;

public class ReflectableTypesFinder implements TypesToReflectFinder {

  private static final String REFLECTOR_TYPE_PROPERTY = "org.dt.reflector.rebind.finder.ReflectableTypesFinder.type";

  @Override
  public Set<TypeToReflect> findTypes(TreeLogger logger, GeneratorContext context) throws UnableToCompleteException {
    Set<TypeToReflect> typesToReflect = new HashSet<TypeToReflect>();

    try {
      List<JClassType> markerTypes = getMarkerInterfaces(context);
      for (JClassType type : context.getTypeOracle().getTypes()) {
        for(JClassType markerType: markerTypes) {
          if (hasMarkerInterface(type, markerType)) {
            typesToReflect.add(getTypeToReflect(logger, context, type));
            for (JClassType nestedType : type.getNestedTypes()) {
              if (hasMarkerInterface(nestedType, markerType)) {
                typesToReflect.add(getTypeToReflect(logger, context, nestedType));
              }
            }
          }
        }
      }
    } catch (BadPropertyValueException e) {
      logger.log(TreeLogger.Type.ERROR, "Error reading configuration property: " + REFLECTOR_TYPE_PROPERTY, e);
      throw new UnableToCompleteException();
    }
    
    return typesToReflect;
  }

  private TypeToReflect getTypeToReflect(TreeLogger logger, GeneratorContext context, JClassType type) throws UnableToCompleteException {
    String qualifiedSourceName = type.getQualifiedSourceName();
    String reflectorQualifiedSourceName = new ReflectorGenerator().generate(logger, context, qualifiedSourceName);
    return new TypeToReflect(qualifiedSourceName, reflectorQualifiedSourceName);
  }

  private List<JClassType> getMarkerInterfaces(GeneratorContext context) throws BadPropertyValueException {
    List<JClassType> classTypes = new LinkedList<JClassType>();
    ConfigurationProperty configurationProperty = context.getPropertyOracle().getConfigurationProperty(REFLECTOR_TYPE_PROPERTY);
    for (String s : configurationProperty.getValues()) {
      classTypes.add(context.getTypeOracle().findType(s));
    }
    return classTypes;
  }
  
  /**
   * Does the given class implement the requested marker interface?
   * 
   * @param type the type we are checking
   * @param markerIntf the marker interface we are looking for
   * @return true if the given type implements the requested marker interface
   */
  private boolean hasMarkerInterface(JClassType type, JClassType markerIntf) {
    for (JClassType intf : type.getImplementedInterfaces()) {
      if (intf == markerIntf) return true;
      if (hasMarkerInterface(intf, markerIntf)) return true;
    }

    if (type.getSuperclass() != null) {
      return hasMarkerInterface(type.getSuperclass(), markerIntf);
    }

    return false;
  }
  
}
