package org.dt.reflector.client;

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
 * <p>
 *   Unlike {@link org.dt.reflector.client.PropertyUtils} this utility class will traverse the entire object
 *   graph as necessary to return the requested value.
 * </p>
 *
 * @author David Sykes
 * @author Tomasz Orzechowski
 * @since 0.1
 *
 */
public class ObjectGraphUtils {

  /**
   * Get the type of the property referenced by <code>propertyReference</code>.
   * <p>
   *   Traverse the object graph as far as is necessary and possible to determine
   *   the answer. For exmaple, a <code>propertyReference</code> value of <code>bean.name</code>
   *   will find the type of the property <code>bean</code> using the {@link Reflector} for <code>type</code>.
   *   Once we have that, we will then try and get a {@link Reflector} for its type and then lookup the
   *   type of its property <code>name</code>.
   * </p>
   *
   * @param type the type to reflect on to determine the property type
   * @param propertyReference the reference to the property for which we want the property type
   * @return the type of the requested property, or null if it cannot be determined
   */
  public static Class<?> getType(Class<?> type, String propertyReference) {
    if (propertyReference == null || type == null) {
      return null;
    }

    Reflector reflector = PropertyUtils.getReflector(type);
    if (reflector == null) {
      return null;
    }

    int dotPos = propertyReference.indexOf('.');
    if (dotPos >= 0) {
      /*
       * The property name is actually a path through the object graph Extract
       * the name of the property of the given object that defines the next step
       * in the graph, get the object it references and then continue the
       * traversal
       */
      String objectProperty = propertyReference.substring(0, dotPos);
      propertyReference = propertyReference.substring(dotPos + 1);
      return getType(reflector.type(objectProperty), propertyReference);
    }

    return reflector.type(propertyReference);
  }

  /**
   * Get the type of the property referenced by <code>propertyReference</code>.
   * <p>
   *   Same as {@link #getType(Class, String)} but starts from an object instance rather than a {@link java.lang.Class}.
   * </p>
   *
   * @param o the object, on whose type we should begin searching for the property reference
   * @param propertyReference the reference to the property for which we want the property type
   * @return the type of the requested property, or null if it cannot be determined
   */
  public static Class<?> getType(Object o, String propertyReference) {
    if (o == null) {
      return null;
    }
    return getType(o.getClass(), propertyReference);
  }

  /**
   * Get the current value of the property with the given name from the given {@link Object} instance.
   *
   * <p>
   * This works like {@link org.dt.reflector.client.PropertyUtils#getProperty(Object, String)} except that it
   * will traverse the object graph as necessary to find the requested value.
   *
   * @param o the {@link Object} from which we want to read the property value
   * @param propertyReference the reference to the property for which we want the property value
   * @return the current value of the requested property
   */
  public static Object getProperty(Object o, String propertyReference) {
    if (o == null) {
      return null;
    }

    if (propertyReference == null) {
      return null;
    }

    int dotPos = propertyReference.indexOf('.');
    if (dotPos >= 0) {
      /*
       * The property name is actually a path through the object graph. Extract
       * the name of the property of the given object that defines the next step
       * in the graph, get the object it references and then continue the
       * traversal
       */
      String objectProperty = propertyReference.substring(0, dotPos);
      propertyReference = propertyReference.substring(dotPos + 1);
      return getProperty(PropertyUtils.getProperty(o, objectProperty), propertyReference);
    }

    return PropertyUtils.getProperty(o, propertyReference);
  }

  /**
   * Set the value of the property referenced by <code>propertyReference</code> on the given {@link Object} instance
   * to the new <code>value</code>.
   * <p>
   *   This works like {@link org.dt.reflector.client.PropertyUtils#setProperty(Object, String, Object)} except that it
   *   will traverse the object graph to find the actual property to set.
   * </p>
   * <p>
   *   <b>NOTE:</b> If an intermediate step through the object graph results in a null, this method will attempt to create
   *   a new instance of the required type and continue on.
   * </p>
   *
   * @param o the {@link Object} on which we want to set the property value
   * @param propertyReference the reference to the property we want to set the property value
   * @param value the new value to set
   */
  public static void setProperty(Object o, String propertyReference, Object value) {
    if (o == null) {
      return;
    }

    if (propertyReference == null) {
      return;
    }

    int dotPos = propertyReference.indexOf('.');
    if (dotPos >= 0) {
      /*
       * The property name is actually a path through the object graph. Extract
       * the name of the property of the given object that defines the next step
       * in the graph, get the object it references and then continue the
       * traversal
       */
      String objectProperty = propertyReference.substring(0, dotPos);
      Object bean = PropertyUtils.getProperty(o, objectProperty);
      if (bean == null) {
        Reflector reflector = PropertyUtils.getReflector( PropertyUtils.getType(o, objectProperty) );
        if (reflector != null) {
          bean = reflector.newInstance();
          PropertyUtils.setProperty(o, objectProperty, bean);
        }
      }

      if (bean == null) {
        // we could not create the intermediate value we needed - we can't continue
        return;
      }

      propertyReference = propertyReference.substring(dotPos + 1);
      setProperty(bean, propertyReference, value);
      return;
    }

    PropertyUtils.setProperty(o, propertyReference, value);
    return;
  }

}
