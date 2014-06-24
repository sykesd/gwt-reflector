package org.dt.reflector.client;

import com.google.gwt.junit.client.GWTTestCase;

import java.math.BigDecimal;

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

public class ObjectGraphTest extends GWTTestCase {

  @Override
  public String getModuleName() { return "org.dt.reflector.Reflector"; }

  public void testType() {
    NestedBean bean = createSampleBean(true);

    assertEquals(String.class, ObjectGraphUtils.getType(NestedBean.class, "name"));
    assertEquals(SimpleBean.class, ObjectGraphUtils.getType(NestedBean.class, "bean"));
    assertEquals(String.class, ObjectGraphUtils.getType(NestedBean.class, "bean.name"));
  }

  public void testGet() {
    NestedBean bean = createSampleBean(true);

    assertEquals("Parent", ObjectGraphUtils.getProperty(bean, "name"));

    assertNotNull(ObjectGraphUtils.getProperty(bean, "bean"));
    assertEquals(SimpleBean.class, ObjectGraphUtils.getProperty(bean, "bean").getClass());

    assertEquals("Test", ObjectGraphUtils.getProperty(bean, "bean.name"));

    assertNull(ObjectGraphUtils.getProperty(bean, "doesNotExist"));

  }

  public void testSet() {
    NestedBean bean = createSampleBean(false);

    ObjectGraphUtils.setProperty(bean, "name", "The Parent");
    assertEquals("The Parent", PropertyUtils.getProperty(bean, "name"));

    ObjectGraphUtils.setProperty(bean, "bean.name", "The Child");
    assertEquals("The Child", PropertyUtils.getProperty(bean.getBean(), "name"));
  }

  private NestedBean createSampleBean(boolean deepCreation) {
    NestedBean nested = new NestedBean();
    nested.setName("Parent");

    if (deepCreation) {
      SimpleBean bean = new SimpleBean();
      bean.setName("Test");
      bean.setLargeValue(new BigDecimal("123456789123456789"));
      bean.setSmallValue(32000);
      nested.setBean(bean);
    }

    return nested;
  }

}
