package org.dt.reflector.client;

import java.lang.annotation.Annotation;
import java.util.*;

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
 * Provide a simple set of API methods, similar in form to Apache Commons BeanUtils PropertyUtils class
 * for setting/getting property values on instances of beans for which a {@link Reflector} instance exists.
 * 
 * @author David Sykes
 * @author Tomasz Orzechowski
 * @since 0.1
 * 
 */
public class PropertyUtils {

  /**
   * Get the current value of the property with the given name from the given {@link Object} instance
   * 
   * <p>
   * This just calls the {@link Reflector#get(Object, String)} method to retrieve the value.
   * 
   * @param r the {@link Object} from which we want to read the property value
   * @param name the property we want to read
   * @return the current value of the requested property
   */
  public static Object getProperty(Object r, String name) {
    return getReflector(r.getClass()).get(r, name);
  }
  
  /**
   * Set the current value of the property with the given name on the given Object instance
   * 
   * <p>
   * This just calls the {@link Reflector#set(Object, String, Object)} method to set the value
   * 
   * @param r the {@link Object} on which we want to set the property value
   * @param name the property we want to set
   * @param value the new value to set the property to
   */
  public static void setProperty(Object r, String name, Object value) {
    getReflector(r.getClass()).set(r, name, value);
  }
  
  /**
   * Get the type of property with the given name on the given Object instance
   * 
   * This just calls the {@link Reflector#type(String)} method
   * 
   * @param r the {@link Object} on which we want to get the property type
   * @param name the property we want the type of
   * @return the type of the property on the given Reflectable instance
   */
  public static Class<?> getType(Object r, String name) {
    return getReflector(r.getClass()).type(name);
  }
  
  /**
   * Does the given property have the given annotation?
   * 
   * This just calls the {@link Reflector#hasAnnotation(String, Class)} method 
   * 
   * @param r the {@link Object} on which we want to get the annotation
   * @param name the property we want the annotation for
   * @param annotationClass the annotation we want
   * @return the annotation instance if one exists, or null if not
   */
  public static <T extends Annotation> T hasAnnotation(Object r, String name, Class<T> annotationClass) {
    return getReflector(r.getClass()).hasAnnotation(name, annotationClass);
  }

  /**
   * Convenience method to get access to the {@link Reflector} for the given type
   * 
   * @param type the type for which we want the reflector
   * @return the {@link Reflector} for the given type
   */
  public static Reflector getReflector(Class<?> type) {
    return ReflectionOracle.Util.getReflector(type.getName());
  }

  /**
   * Convenience method to make a deep clone of a given type.
   * <p>
   *   WARNING: This method may not actually return a deep copy, if the type of <code>o</code> is not reflectable.
   *   It is assumed the caller is aware of this restriction and uses this method accordingly.
   * </p>
   * <p>
   *   If the type of <code>o</code> is reflectable then a deep clone will be made using its {@link org.dt.reflector.client.Reflector}.
   *   However, if <code>o</code> is not of a reflectable type, then <code>o</code> itself is returned.
   * </p>
   *
   * @param o the {@link Object} to make a deep clone of
   * @return a deep copy of <code>o</code>
   */
  public static final <T> T deepClone(Object o) {
    if (o == null) {
      // can't determine its type - nothing we can do
      return null;
    }

    Reflector reflector = getReflector(o.getClass());
    if (reflector == null) {
      return (T)o;
    }

    return (T)reflector.deepClone(o);
  }

  public static final <T> List<T> deepCloneList(Collection<T> src) {
    if (src == null) return null;
    List<T> dest = new ArrayList<T>();
    deepCloneCollection(src, dest);
    return dest;
  }

  public static final <T> ArrayList<T> deepCloneArrayList(ArrayList<T> src) {
    if (src == null) return null;
    ArrayList<T> dest = new ArrayList<T>();
    deepCloneCollection(src, dest);
    return dest;
  }

  public static final <T> Set<T> deepCloneSet(Set<T> src) {
    if (src == null) return null;
    Set<T> dest = new HashSet<T>();
    deepCloneCollection(src, dest);
    return dest;
  }

  public static final <T> HashSet<T> deepCloneHashSet(HashSet<T> src) {
    if (src == null) return null;
    HashSet<T> dest = new HashSet<T>();
    deepCloneCollection(src, dest);
    return dest;
  }

  public static final <T> void deepCloneCollection(Collection<T> src, Collection<T> dest) {
    for (Object e : src) {
      dest.add((T) deepClone(e));
    }
  }
}
