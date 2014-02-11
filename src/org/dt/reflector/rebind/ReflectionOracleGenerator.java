package org.dt.reflector.rebind;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dt.reflector.client.ReflectionOracle;
import org.dt.reflector.rebind.finder.TypeToReflect;
import org.dt.reflector.rebind.finder.TypesToReflectFinder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
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
 * Generator to generate an implementation of the ReflectionOracle interface
 * 
 * <p>
 * TODO describe the pre-requisites and limitations
 * 
 * @author David Sykes
 * @author Tomasz Orzechowski
 * @version 0.1
 *
 */
public class ReflectionOracleGenerator extends Generator {

  @Override
  public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
    JClassType oracleType = context.getTypeOracle().findType(typeName);
    
    String implPackageName = oracleType.getPackage().getName();
    String implTypeName = oracleType.getSimpleSourceName()+"_OracleImpl";
    
    ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(implPackageName, implTypeName);

    composerFactory.addImport(GWT.class.getName());
    composerFactory.addImplementedInterface(ReflectionOracle.class.getName());
    
    PrintWriter printWriter = context.tryCreate(logger, implPackageName, implTypeName);
    if (printWriter != null) {
      SourceWriter out = composerFactory.createSourceWriter(context, printWriter);
      
      out.println("\n@Override");
      out.println("public Reflector getReflector(String typeName) {");
      
      for (TypeToReflect type : findTypesToReflect(logger, context)) {
        out.println("  if (\""+type.getQualifiedSourceName()+"\".equals(typeName)) return "+type.getCreateExpression()+";");
      }
      
      out.println("  return null;");
      out.println("}");
      
      out.commit(logger);
    }
    
    return implPackageName + "." + implTypeName;    
  }

  private Set<TypeToReflect> findTypesToReflect(TreeLogger logger, GeneratorContext context) throws UnableToCompleteException {
    Set<TypeToReflect> typesToReflect = new HashSet<TypeToReflect>();
    
    for (TypesToReflectFinder finder : findTypeFinders(logger, context)) {
      typesToReflect.addAll( finder.findTypes(logger, context) );
    }
    
    return typesToReflect;
  }
  
  private static final String TYPE_FINDER_PROPERTY = "org.dt.reflector.rebind.finder.typefinder";
  
  private List<TypesToReflectFinder> findTypeFinders(TreeLogger logger, GeneratorContext context) {
    List<String> finderClassNames = null;
    try {
      finderClassNames = findTypeFinderClassNames(context);
    }
    catch (BadPropertyValueException ex) {
      logger.log(Type.ERROR, "Error reading configuration property: "+TYPE_FINDER_PROPERTY, ex);
      return Collections.emptyList();
    }
    
    List<TypesToReflectFinder> finders = new ArrayList<TypesToReflectFinder>();
    for (String className : finderClassNames) {
      TypesToReflectFinder finder = null;
      try {
        finder = (TypesToReflectFinder) Class.forName(className).newInstance();
        finders.add( finder );
      }
      catch (Exception ex) {
        logger.log(Type.ERROR, "Error loading TypesToReflectFinder: "+className, ex);
      }
    }
    
    return finders;
  }
  
  private List<String> findTypeFinderClassNames(GeneratorContext context) throws BadPropertyValueException {
    return context.getPropertyOracle().getConfigurationProperty(TYPE_FINDER_PROPERTY).getValues();
  }
  
}
