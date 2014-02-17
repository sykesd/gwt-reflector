package org.dt.reflector.client;

import java.math.BigDecimal;

import com.google.gwt.junit.client.GWTTestCase;

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

public class ReflectorTest extends GWTTestCase {

  @Override
  public String getModuleName() { return "org.dt.reflector.Reflector"; }
  
  public void testOracle() {
    assertNotNull(ReflectionOracle.Util.getReflector(SimpleBean.class.getName()));
  }
  
  public void testSimpleGenerator() {
    SimpleBean bean = createSampleBean();
    
    assertEquals("Test", PropertyUtils.getProperty(bean, "name"));
    assertEquals(0, new BigDecimal("123456789123456789").compareTo((BigDecimal) PropertyUtils.getProperty(bean, "largeValue")));
    assertEquals(new Integer(32000), PropertyUtils.getProperty(bean, "smallValue"));
  }

  
  public void testReflectedCreation() {
    SimpleBean bean = (SimpleBean) ReflectionOracle.Util.getReflector(SimpleBean.class.getName()).newInstance();
    PropertyUtils.setProperty(bean, "name", "Tester");
    PropertyUtils.setProperty(bean, "largeValue", BigDecimal.valueOf(321));
    PropertyUtils.setProperty(bean, "smallValue", Integer.valueOf(128));
    
    assertEquals("Tester", bean.getName());
    assertEquals(Integer.valueOf(128), bean.getSmallValue());
    assertEquals(0, BigDecimal.valueOf(321).compareTo(bean.getLargeValue()));
  }
  
  public void testSubclassedMarker() {
    SubSimpleBean bean = createSubSampleBean();
    
    assertEquals("Test", PropertyUtils.getProperty(bean, "name"));
    assertEquals(0, new BigDecimal("123456789123456789").compareTo((BigDecimal) PropertyUtils.getProperty(bean, "largeValue")));
    assertEquals(new Integer(32000), PropertyUtils.getProperty(bean, "smallValue"));
  }
  
  private SimpleBean createSampleBean() {
    SimpleBean bean = new SimpleBean();
    bean.setName("Test");
    bean.setLargeValue(new BigDecimal("123456789123456789"));
    bean.setSmallValue(32000);
    return bean;
  }
  
  private SubSimpleBean createSubSampleBean() {
    SubSimpleBean bean = new SubSimpleBean();
    bean.setName("Test");
    bean.setLargeValue(new BigDecimal("123456789123456789"));
    bean.setSmallValue(32000);
    return bean;
  }
}
