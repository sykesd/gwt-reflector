package org.dt.reflector.client;

public class NestedBean implements Reflectable {

  private String name;

  private SimpleBean bean;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public SimpleBean getBean() {
    return bean;
  }

  public void setBean(SimpleBean bean) {
    this.bean = bean;
  }

}
