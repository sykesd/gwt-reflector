package org.dt.reflector.rebind;

import java.util.HashSet;
import java.util.Set;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;

import org.dt.reflector.client.Reflector;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JAnnotationMethod;
import com.google.gwt.core.ext.typeinfo.JAnnotationType;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.JTypeParameter;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/*
 * Copyright (c) 2011-2014, David Sykes and Tomasz Orzechowski 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * - Neither the name David Sykes nor Tomasz Orzechowski may be used to endorse
 * or promote products derived from this software without specific prior written
 * permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE. * @author Administrator
 * 
 * 
 */


/**
 * <p>Generator to generate an implementation of the Reflector interface for a particular type</p>
 * 
 * <p>TODO describe the pre-requisites and limitations</p>
 * 
 * @author David Sykes
 * @author Tomasz Orzechowski
 * @version 0.1
 *
 */
public class ReflectorGenerator {

  public static String getReflectorName(GeneratorContext context, String typeName) {
    JClassType typeToReflect = context.getTypeOracle().findType(typeName);

    return  typeToReflect.getName().replace('.', '_')+"_ReflectorImpl";
  }

  public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
    JClassType typeToReflect = context.getTypeOracle().findType(typeName);
    
    String implPackageName = typeToReflect.getPackage().getName();
    String implTypeName = getReflectorName(context, typeName);
    
    ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(implPackageName, implTypeName);

    composerFactory.addImport(Annotation.class.getName());
    composerFactory.addImplementedInterface(Reflector.class.getName());

    PrintWriter printWriter = context.tryCreate(logger, implPackageName, implTypeName);
    if (printWriter != null) {
      SourceWriter sourceWriter = composerFactory.createSourceWriter(context, printWriter);
      
      composeConstructor(sourceWriter, typeToReflect);
      composeArrayConstructor(sourceWriter, typeToReflect);
      composeIsAbstract(sourceWriter, typeToReflect);
      composeSubClasses(sourceWriter, typeToReflect);
      composeClassType(sourceWriter, typeToReflect);
      composePropertyType(sourceWriter, typeToReflect);
      composeHasTypeAnnotations(sourceWriter, context, typeToReflect);
      composeHasAnnotations(sourceWriter, context, typeToReflect);
      composeList(sourceWriter, typeToReflect);
      composeGet(sourceWriter, typeToReflect);
      composeSet(sourceWriter, typeToReflect);
      composeDeepClone(sourceWriter, typeToReflect);
      sourceWriter.commit(logger);
    }
    
    return implPackageName + "." + implTypeName;
  }

  private static JType getFieldBaseType(JField field) {
    JType type = field.getType();
    JTypeParameter typeParameter = type.isTypeParameter();
    if(typeParameter != null) {
      return typeParameter.getBaseType();
    }
    return type;
  }
  
  /**
   * The "constructor" is the implementation of Reflector.newInstance()
   * We require that the type we are reflecting has a no-args constructor, so the implementation
   * is a simple one-line method that creates a new instance of this type
   * 
   * If it does not have a public no-args constructor, the implementation will throw an exception
   * 
   * @param out the writer on which we are generating the source
   * @param typeToReflect the type we are reflecting
   */
  private void composeConstructor(SourceWriter out, JClassType typeToReflect) {
    if (ReflectionUtil.hasPublicNoArgsConstructor(typeToReflect)) {
      out.println("\n@Override");
      out.println("public Object newInstance() { return com.google.gwt.core.client.GWT.create( " + typeToReflect.getQualifiedSourceName() + ".class ); }");
      return;
    }
    
    // no constructor - have the implementation throw an exception
    // it will still be possible to reflect on an instance, assuming you get the instance from somewhere else
    out.println("\n@Override");
    out.println("public Object newInstance() { throw new RuntimeException(\"No public no-args constructor!\"); }");
  }

  private void composeArrayConstructor(SourceWriter out, JClassType typeToReflect) {
    out.println("\n@Override");
    out.println("public Object[] newArray(int length) { return new " + typeToReflect.getQualifiedSourceName() + "[length]; }");
    return;
  }

  /**
   * Generate the implementation of Reflector.isAbstract(), which returns true if the type is abstract
   *
   * @param out the writer on which we are generating the source
   * @param typeToReflect the type we are reflecting
   */
  private void composeIsAbstract(SourceWriter out, JClassType typeToReflect) {
    out.println("\n@Override");
    out.println("public boolean isAbstract() { return " + (typeToReflect.isAbstract() ? "true" : "false") + "; }");
  }


  /**
   * Generate the implementation of Reflector.isAbstract(), which returns true if the type is abstract
   *  @param out the writer on which we are generating the source
   * @param typeToReflect the type we are reflecting
   * @param typeOracle
   */
  private void composeSubClasses(SourceWriter out, JClassType typeToReflect) {
    JClassType[] subtypes = typeToReflect.getSubtypes();
    out.println("private static final Class<?>[] __subclasses = new Class<?>[] {");
    for (JClassType subtype : subtypes) {
      out.println(subtype.getQualifiedSourceName() + ".class,");
    }
    out.println("};");
    out.println("\n@Override");
    out.println("public Class<?>[] getSubClasses() { return __subclasses; }");
  }


  /**
   * Generate the implementation of Reflector.type(), which returns the type we are reflecting
   * 
   * @param out the writer on which we are generating the source
   * @param typeToReflect the type we are reflecting
   */
  private void composeClassType(SourceWriter out, JClassType typeToReflect) {
    out.println("\n@Override");
    out.println("public Class<?> type() { return " + typeToReflect.getQualifiedSourceName()+ ".class; }");
  }
  
  /**
   * Generate the implementation of the Reflector.type(String propertyName) method
   * 
   * We take a very simple approach and generate a list of if statements that look
   * for the requested property name and return the type of that property
   * 
   * @param out the writer on which we are generating the source
   * @param typeToReflect the type we are reflecting
   */
  private void composePropertyType(SourceWriter out, JClassType typeToReflect) {
    out.println("\n@Override");
    out.println("public Class<?> type(String propertyName) {");
    
    composeTypeGetters(out, typeToReflect);
    
    out.println("  return null;");
    out.println("}");
  }

  private void composeTypeGetters(SourceWriter out, JClassType typeToReflect) {
    for (JField field : typeToReflect.getFields()) {
      String getterMethod = ReflectionUtil.isPublicReadable(field, typeToReflect);
      if (getterMethod != null) {
        out.println("  if (propertyName.equals(\"" + field.getName() + "\")) {");
        out.println("    return " + getFieldBaseType(field).getQualifiedSourceName() + ".class;");
        out.println("  }");
      }
    }
    
    /*
     * Recurse through all of the super classes to make sure we get a complete 
     * list of all the properties
     */
    JClassType superType = typeToReflect.getSuperclass();
    if (superType != null && !superType.getSimpleSourceName().equals("Object")) {
      composeTypeGetters(out, superType);
    }
  }

  private void _composeList(JClassType typeToReflect, Set<String> properties) {
    for(JField field: typeToReflect.getFields()) {
      String getterMethod = ReflectionUtil.isPublicReadable(field, typeToReflect);
      if (getterMethod != null) {
        properties.add(field.getName());
      }
    }
    /*
     * Recurse through all super classes to make sure we get a complete 
     * list of all the properties
     */
    JClassType superType = typeToReflect.getSuperclass();
    if (superType != null && !superType.getSimpleSourceName().equals("Object")) {
      _composeList(superType, properties);
    }
  }

  /**
   * Generate the implementation of Reflector.list(Object)
   * 
   * @param out the writer on which we are generating the source
   * @param typeToReflect the type we are reflecting
   */
  private void composeList(SourceWriter out, JClassType typeToReflect) {
    
    /* Static variable */
    out.print("private String [] propertyList = new String[] {");
    boolean first = true;
    Set<String> properties = new HashSet<String>();
    _composeList(typeToReflect, properties);
    for(String property: properties) {
      if(first) {
        out.println("");
      } else {
        out.println(",");
      }
      out.print("   \"" + property + "\"");
      first = false;
    }
    out.println("\n};");

    out.println("\n@Override");
    out.println("public String[] list(Object rawInstance) {");
    out.println("  return propertyList;");
    out.println("}");
  }
  
  /**
   * Generate the implementation of Reflector.get(Object, String)
   * 
   * We take a very simple approach and generate a list of if statements that look
   * for the requested property name and then call the public getter for that property
   * 
   * @param out the writer on which we are generating the source
   * @param typeToReflect the type we are reflecting
   */
  private void composeGet(SourceWriter out, JClassType typeToReflect) {
    out.println("\n@Override");
    out.println("public Object get(Object rawInstance, String propertyName) {");
    
    out.println("  "+typeToReflect.getQualifiedSourceName()+" instance = ("+typeToReflect.getQualifiedSourceName()+") rawInstance;");
    
    composeGetters(out, typeToReflect);
    
    out.println("  return null;");
    out.println("}");
  }

  private void composeGetters(SourceWriter out, JClassType typeToReflect) {
    for (JField field : typeToReflect.getFields()) {
      String wrapBegin = "";
      String wrapEnd = "";
      JType type = getFieldBaseType(field);
      JPrimitiveType primitive = type.isPrimitive();
      if (primitive != null) {
        wrapBegin = "new " + primitive.getQualifiedBoxedSourceName() + "(";
        wrapEnd = ")";
      }
      String getterMethod = ReflectionUtil.isPublicReadable(field, typeToReflect);
      if (getterMethod != null) {
        out.println("  if (propertyName.equals(\"" + field.getName() + "\")) {");
        out.println("    return " + wrapBegin + "instance."+getterMethod+"()" + wrapEnd + ";");
        out.println("  }");
      }
    }

    /*
     * Recurse through all super classes to make sure we get a complete 
     * list of all the properties
     */
    JClassType superType = typeToReflect.getSuperclass();
    if (superType != null && !superType.getSimpleSourceName().equals("Object")) {
      composeGetters(out, superType);
    }
  }

  /**
   * Generate the implementation of Reflector.set(Object, String, Object)
   * 
   * We take a very simple approach and generate a list of if statements that look
   * for the requested property name and then call the public setter for that property
   * 
   * @param out the writer on which we are generating the source
   * @param typeToReflect the type we are reflecting
   */
  private void composeSet(SourceWriter out, JClassType typeToReflect) {
    out.println("@Override");
    out.println("public void set(Object rawInstance, String propertyName, Object value) {");
    
    out.println("  "+typeToReflect.getQualifiedSourceName()+" instance = ("+typeToReflect.getQualifiedSourceName()+") rawInstance;");
    
    composeSetters(out, typeToReflect);
    
    out.println("}");
  }

  private void composeSetters(SourceWriter out, JClassType typeToReflect) {
    for (JField field : typeToReflect.getFields()) {
      JType type = getFieldBaseType(field);
      String sourceName = type.getQualifiedSourceName();
      JPrimitiveType primitive = type.isPrimitive();
      if (primitive != null) {
        sourceName = primitive.getQualifiedBoxedSourceName();
      }
      
      String setterMethod = ReflectionUtil.isPublicWriteable(field, typeToReflect);
      if (setterMethod != null) {
        out.println("  if (propertyName.equals(\"" + field.getName() + "\")) {");
        out.println("    instance."+setterMethod+"( (" + sourceName + ") value);");
        out.println("  }");
      }
    }
    
    JClassType superType = typeToReflect.getSuperclass();
    if (superType != null && !superType.getSimpleSourceName().equals("Object")) {
      composeSetters(out, superType);
    }
  }

  /**
   * Generate the implementation of Reflector.hasAnnotation(Class)
   * 
   * @param out the writer on which we are generating the source
   * @param context the generation context
   * @param typeToReflect the type we are reflecting
   */
  private void composeHasTypeAnnotations(SourceWriter out, GeneratorContext context, JClassType typeToReflect) {
    out.println("\n@Override");
    out.println("public <T extends Annotation> T hasAnnotation(Class<T> annotationClass) {");
    
    composeAnnotationsType(out, context, typeToReflect);
    
    out.println("  return null;");
    out.println("}");
  }

  /**
   * Generate the implementation of Reflector.hasAnnotation(String, Class)
   *
   * We take a very simple approach and generate a list of if statements that look
   * for the requested property name and then call the public setter for that property
   *
   * @param out the writer on which we are generating the source
   * @param context the generation context
   * @param typeToReflect the type we are reflecting
   */
  private void composeHasAnnotations(SourceWriter out, GeneratorContext context, JClassType typeToReflect) {
    out.println("\n@Override");
    out.println("public <T extends Annotation> T hasAnnotation(String propertyName, Class<T> annotationClass) {");

    composeAnnotationGetters(out, context, typeToReflect);

    out.println("  return null;");
    out.println("}");
  }

  private void composeAnnotationGetters(SourceWriter out, GeneratorContext context, JClassType typeToReflect) {
    for (JField field : typeToReflect.getFields()) {
      String getterMethod = ReflectionUtil.isPublicReadable(field, typeToReflect);
      if (getterMethod != null) {
        Annotation[] annotations = field.getAnnotations();
        if (annotations == null || annotations.length == 0) {
          // this field does not actually have any annotations, don't bother generating a case for it
          continue;
        }

        out.println("  if (propertyName.equals(\"" + field.getName() + "\")) {");

        for (Annotation annotation : annotations) {
          JClassType type = context.getTypeOracle().findType(annotation.annotationType().getName());
          if(type != null) {
            out.println("    if (annotationClass == " + annotation.annotationType().getName() + ".class) {");
            generateAnnotationImpl(out, type, annotation);
            out.println("    }");
          }
        }
        
        out.println("  }");
      }
    }
    
    JClassType superType = typeToReflect.getSuperclass();
    if (superType != null && !superType.getSimpleSourceName().equals("Object")) {
      composeAnnotationGetters(out, context, superType);
    }
  }

  private void composeAnnotationsType(SourceWriter out, GeneratorContext context, JClassType typeToReflect) {
      for (Annotation annotation : typeToReflect.getAnnotations()) {
        JClassType type = context.getTypeOracle().findType(annotation.annotationType().getName());
        if(type != null) {
          out.println("    if (annotationClass == " + annotation.annotationType().getName() + ".class) {");
          generateAnnotationImpl(out, type, annotation);
          out.println("    }");
        }
      }

    JClassType superType = typeToReflect.getSuperclass();
    if (superType != null && !superType.getSimpleSourceName().equals("Object")) {
      composeAnnotationsType(out, context, superType);
    }
  }
  
  private void generateAnnotationImpl(SourceWriter out, JClassType rawAnnoType, Annotation annotation) {
    JAnnotationType annoType = rawAnnoType.isAnnotation();
    
    out.println("  return (T) new "+annoType.getQualifiedSourceName()+"() {");
    out.println("      @Override public Class<? extends Annotation> annotationType() { return "+annoType.getQualifiedSourceName()+".class; }");
    
    for (JMethod method : annoType.getMethods()) {
      JAnnotationMethod annoMethod = method.isAnnotationMethod();
      if (annoMethod == null) continue;

      JType returnType = annoMethod.getReturnType();
      JPrimitiveType primitiveType = returnType.isPrimitive();
      // FIXME for now we only support integer properties
      try {
        if (primitiveType == JPrimitiveType.INT) {
          Object annoValue = annotation.annotationType().getMethod(annoMethod.getName()).invoke(annotation);
          out.println("      @Override public int "+annoMethod.getName()+"() { return "+ annoValue + "; }");
        }
      }
      catch (Exception ex) {
        throw new IllegalArgumentException("Could not generate annotation method: "+annoMethod.getName(), ex);
      }
    }
    out.println("    };");
  }

  private void composeDeepClone(SourceWriter out, JClassType typeToReflect) {
    if (ReflectionUtil.hasPublicNoArgsConstructor(typeToReflect)) {
      out.println("@Override");
      out.println("public Object deepClone(Object rawO) {");

      out.println("  if (rawO == null) return null;");
      out.println("  if (!(rawO instanceof " + typeToReflect.getQualifiedSourceName() + ")) throw new IllegalArgumentException(\"Expected type " + typeToReflect.getQualifiedSourceName() + " but was given \"+rawO.getClass().getName());");

      out.println("  " + typeToReflect.getQualifiedSourceName() + " src = (" + typeToReflect.getQualifiedSourceName() + ") rawO;");
      out.println("  " + typeToReflect.getQualifiedSourceName() + " dest = new " + typeToReflect.getQualifiedSourceName() + "();");

      composeCloners(out, typeToReflect);

      out.println("  return dest;");
      out.println("}");
      return;
    }

    out.println("\n@Override");
    out.println("public Object deepClone(Object rawO) { throw new RuntimeException(\"No public no-args constructor!\"); }");
  }

  private void composeCloners(SourceWriter out, JClassType typeToReflect) {
    for (JField field : typeToReflect.getFields()) {
      String setterMethod = ReflectionUtil.isPublicWriteable(field, typeToReflect);
      if (setterMethod == null) {
        // we can't actually write this property - don't bother generating copy code for it
        continue;
      }

      String getterMethod = ReflectionUtil.isPublicReadable(field, typeToReflect);
      if (getterMethod == null) {
        // unlikely, but here for defensiveness/completion - public writeable but NOT readable
        continue;
      }


      String sourceName = getFieldBaseType(field).getQualifiedSourceName();
      if (getFieldBaseType(field).isPrimitive() != null
              || "java.lang.String".equals(sourceName)
              || "java.lang.Byte".equals(sourceName)
              || "java.lang.Short".equals(sourceName)
              || "java.lang.Integer".equals(sourceName)
              || "java.lang.Long".equals(sourceName)
              || "java.lang.Float".equals(sourceName)
              || "java.lang.Double".equals(sourceName)
              || "java.lang.BigInteger".equals(sourceName)
              || "java.math.BigDecimal".equals(sourceName)
              || "java.util.Timestamp".equals(sourceName)
              || "java.util.Date".equals(sourceName)
              ) {
        // this is an immutable type that we know how to handle - just copy the value directly
        out.println("  dest."+setterMethod+"( src."+getterMethod+"() );");
      }

      else if ("java.util.Collection".equals(sourceName)) {
        getFieldBaseType(field).isParameterized().getTypeArgs();
        out.println("  dest."+setterMethod+"( org.dt.reflector.client.PropertyUtils.deepCloneList(src."+getterMethod+"()) );");
      }
      else if ("java.util.List".equals(sourceName)) {
        out.println("  dest."+setterMethod+"( org.dt.reflector.client.PropertyUtils.deepCloneList(src."+getterMethod+"()) );");
      }
      else if ("java.util.ArrayList".equals(sourceName)) {
        out.println("  dest."+setterMethod+"( org.dt.reflector.client.PropertyUtils.deepCloneArrayList(src."+getterMethod+"()) );");
      }
      else if ("java.util.Set".equals(sourceName)) {
        out.println("  dest."+setterMethod+"( org.dt.reflector.client.PropertyUtils.deepCloneSet(src."+getterMethod+"()) );");
      }
      else if ("java.util.HashSet".equals(sourceName)) {
        out.println("  dest."+setterMethod+"( org.dt.reflector.client.PropertyUtils.deepCloneHashSet(src."+getterMethod+"()) );");
      }

      else {
        out.println("  dest."+setterMethod+"( ("+sourceName+") org.dt.reflector.client.PropertyUtils.deepClone(src."+getterMethod+"()) );");
      }
    }

    JClassType superType = typeToReflect.getSuperclass();
    if (superType != null && !superType.getSimpleSourceName().equals("Object")) {
      composeCloners(out, superType);
    }
  }
}
