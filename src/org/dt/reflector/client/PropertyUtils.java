package org.dt.reflector.client;

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
 * Provide a simple set of API methods, similar in form to Apache Commons BeanUtils PropertyUtils class
 * for setting/getting property values on instances of beans that implement {@link Reflectable}
 * 
 * @author David Sykes
 * @author Tomasz Orzechowski
 * @since 0.1
 * 
 */
public class PropertyUtils {

  /**
   * Get the current value of the property with the given name from the given {@link Reflectable} instance
   * 
   * <p>
   * This just calls the {@link Reflector#get(Object, String)} method to retrieve the value.
   * 
   * @param r the {@link Reflectable} from which we want to read the property value
   * @param name the property we want to read
   * @return the current value of the requested property
   */
  public static Object getProperty(Reflectable r, String name) {
    return getReflector(r.getClass()).get(r, name);
  }
  
  /**
   * Set the current value of the property with the given name on the given Reflectable instance
   * 
   * <p>
   * This just calss the {@link Reflector#set(Object, String, Object)} method to set the value
   * 
   * @param r the {@link Reflectable} on which we want to set the property value
   * @param name the property we want to set
   * @param value the new value to set the property to
   */
  public static void setProperty(Reflectable r, String name, Object value) {
    getReflector(r.getClass()).set(r, name, value);
  }
  

  /**
   * Convenience method to get access to the {@link Reflector} for the given type
   * 
   * @param type the type for which we want the reflector
   * @return the {@link Reflector} for the given type
   */
  public static Reflector getReflector(Class<? extends Reflectable> type) {
    return ReflectionOracle.Util.getReflector(type.getName());
  }
}
