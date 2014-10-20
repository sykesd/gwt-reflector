package org.dt.reflector.client;

import com.google.gwt.junit.client.GWTTestCase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DeepCloneTest extends GWTTestCase {

  @Override
  public String getModuleName() { return "org.dt.reflector.Reflector"; }

  public void testDeepClone() {
    NotCloneableBean b = new NotCloneableBean();
    b.setId(10);
    b.setName("The One and only!");

    SimpleBean simple = new SimpleBean();
    simple.setName("Another one in a million");
    simple.setLargeValue(BigDecimal.valueOf(7));

    List<SimpleBean> children = new ArrayList<SimpleBean>();
    children.add( new SimpleBean() );
    children.add( new SimpleBean() );

    CloneableBean bean = new CloneableBean();
    bean.setId(1);
    bean.setName("One in a million");
    bean.setSimple(simple);
    bean.setBean(b);
    bean.setChildren(children);

    CloneableBean clone = PropertyUtils.deepClone(bean);
    assertTrue( clone.getBean() == bean.getBean() );
    assertTrue( clone.getSimple() != bean.getSimple() );
    assertEquals(clone.getId(), bean.getId());
    assertEquals( clone.getName(), bean.getName() );
    assertEquals(clone.getSimple().getName(), bean.getSimple().getName());

    assertNotNull( clone.getChildren() );
    assertEquals( children.size(), clone.getChildren().size() );
    assertTrue( children.get(0) != clone.getChildren().get(0) );
  }
}
