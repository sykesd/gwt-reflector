package org.dt.reflector.client;

import java.lang.annotation.Annotation;

/*
 * Copyright (c) 2011, David Sykes and Tomasz Orzechowski 
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
 * The ReflectorGenerator creates an instance that implements this class, which is
 * what gets returned by a call to GWT.create(TypeThatImplementsReflectable.class)
 * 
 * This interface provides methods which allows you to reflectively get information
 * about a type, and to get and set property values on that type
 * 
 * @author David Sykes
 * @author Tomasz Orzechowski
 * @since 0.1
 * 
 */
public interface Reflector {

  /**
   * Get the class using reflection
   * @return the class
   */
  Class<?> type();
  
  /**
   * Get the type of the property of the given name
   * 
   * Only properties that provide a public getter in the Java Beans style will be
   * reflected. That is, if you define a property called "name", you must provide
   * a public method getName() for it to be reflected.
   * 
   * If the type of the property is Boolean, then the prefix is "is", not "get".
   * e.g. for the Boolean property "active", the public method must be isActive()
   * 
   * @param propertyName the name of the property for which we want the type
   * @return the type of that property
   */
  Class<?> type(String propertyName);
  
  /**
   * Check to see if the given property is annotated by the given annotation type.
   * 
   * TODO describe what retention type we support
   * 
   * @param propertyName the property we wish to check
   * @param annotationClass the annotation we are checking for
   * @return the annotation instance if the property has one, or null of the property is annotated by the given annotation
   */
  <T extends Annotation> T hasAnnotation(String propertyName, Class<T> annotationClass);


  /**
   * <p>Create a new instance of type</p>
   * 
   * <p>The class we are reflecting must have a no-argument constructor for this to work</p>
   * 
   * TODO Investigate whether it would be possible to support classes without no-arg constructors
   * e.g. we provide an method like newInstance(Object...args) and we match to the constructor and call it
   * 
   * @return a new instance of the type
   */
  Object newInstance();
  
  /**
   * List properties from the given instance
   * 
   * See the comment on type(String propertyName) for an explanation
   * of which types are reflected
   * 
   * @param o the object we want to read the property of
   * @return the list of the property
   */
  String[] list(Object o);

  /**
   * Get the value of the given property from the given instance
   * 
   * See the comment on type(String propertyName) for an explanation
   * of which types are reflected
   * 
   * @param o the object we want to read the property of
   * @param propertyName the property we want the value of
   * @return the value of the property
   */
  Object get(Object o, String propertyName);
  
  /**
   * Set the value of the given property on the given instance to the given new value
   * 
   * To be settable a property must have public setter method, e.g. property "name"
   * must have a public method setName() to be settable via the Reflector
   * 
   * @param o the object we want to set the property on
   * @param propertyName the property we want to set
   * @param value the new value to set
   */
  void set(Object o, String propertyName, Object value);

}
