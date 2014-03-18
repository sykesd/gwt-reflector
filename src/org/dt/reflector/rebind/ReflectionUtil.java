package org.dt.reflector.rebind;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JConstructor;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;

public class ReflectionUtil {

  /**
   * Does the given type have a public no-args constructor?
   * 
   * @param type the type we want a default constructor on
   * @return true if a default constructor exists, or false otherwise
   */
  public static boolean hasPublicNoArgsConstructor(JClassType type) {
    JConstructor constructor = type.findConstructor(new JClassType[]{});
    return constructor != null && constructor.isPublic();
  }
  
  /**
   * If the given field has a publicly accessible getter method, return the name of the getter
   * 
   * @param field the field for which we want the publicly accessible getter
   * @param typeToReflect the type of which we expect the getter
   * @return the name of the getter method if one is present, or null
   */
  public static String isPublicReadable(JField field, JClassType typeToReflect) {
    // the reflectable property is not one we need to get at runtime, so don't
    // consider it publicly readable
    if ("reflectable".equals(field.getName())) return null;
    
    String getter = getterName(field);
    JMethod method = typeToReflect.findMethod(getter, new JType[]{});
    return (method != null && method.isPublic() ? method.getName() : null);
  }
  
  /**
   * If the given field has a publicly accessible setter method, return the name of the setter
   * 
   * @param field the field for which we want the publicly accessible setter
   * @param typeToReflect the type on which we expect the setter
   * @return the name of the setter method if one is present, or null
   */
  public static String isPublicWriteable(JField field, JClassType typeToReflect) {
    String setter = setterName(field);
    JMethod method = typeToReflect.findMethod(setter, new JType[]{field.getType()});
    return (method != null && method.isPublic() ? method.getName() : null);
  }
  
  /**
   * Return the name of the getter method for the given {@link JField}
   * 
   * @param field the field we want the getter for
   * @return the name of the getter method
   */
  public static String getterName(JField field) {
    String prefix = "get";
    String qsn = field.getType().getQualifiedSourceName();
    if ("java.lang.Boolean".equals(qsn) || "boolean".equals(qsn)) {
      prefix = "is";
    }
    
    String fieldName = field.getName();
    return prefix + fieldName.substring(0, 1).toUpperCase()+fieldName.substring(1); 
  }
  
  /**
   * Return the name of the setter method for the given {@link JField}
   * 
   * @param field the field we want the setter method for
   * @return the name of the setter method
   */
  public static String setterName(JField field) {
    String fieldName = field.getName();
    return "set" + fieldName.substring(0, 1).toUpperCase()+fieldName.substring(1); 
  }
  
}
