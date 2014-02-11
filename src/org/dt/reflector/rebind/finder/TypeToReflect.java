package org.dt.reflector.rebind.finder;

import org.dt.reflector.client.Reflector;

import com.google.gwt.core.ext.typeinfo.JClassType;

public class TypeToReflect {

  private final String qualifiedSourceName;
  private final String reflectorQualifiedSourceName;
  private final boolean viaDeferredBinding;
  
  /**
   * Create a new instance of a TypeToReflect where the expression to create the
   * {@link Reflector} instance is an immediate 'new' expression
   * 
   * @param qualifiedSourceName the type we are reflecting
   * @param reflectorQualifiedSourceName the type that implements the {@link Reflector} interface for the given qualifiedSourceName
   */
  public TypeToReflect(String qualifiedSourceName, String reflectorQualifiedSourceName) {
    this.qualifiedSourceName = qualifiedSourceName;
    this.reflectorQualifiedSourceName = reflectorQualifiedSourceName;
    this.viaDeferredBinding = false;
  }
  
  /**
   * Create a new instance of a TypeToReflect where the expression to create the
   * {@link Reflector} instance is itself a deferred binding 'GWT.create' call.
   * 
   * @param type the type we want to reflect
   */
  public TypeToReflect(JClassType type) {
    this.qualifiedSourceName = type.getQualifiedSourceName();
    this.reflectorQualifiedSourceName = null;
    this.viaDeferredBinding = true;
  }
  
  public String getQualifiedSourceName() { return qualifiedSourceName; }
  
  public String getCreateExpression() {
    if (viaDeferredBinding) {
      return getDeferredCreateExpression();
    }
    return getImmediateCreateExpression();
  }
  
  public String getDeferredCreateExpression() {
    return "GWT.create("+qualifiedSourceName+".class)";
  }
  
  public String getImmediateCreateExpression() {
    return "new "+reflectorQualifiedSourceName+"()";
  }
  
}
