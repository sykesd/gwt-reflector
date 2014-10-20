package org.dt.reflector.client;

import java.util.List;

public class CloneableBean implements Reflectable {
  private int id;
  private String name;
  private SimpleBean simple;
  private NotCloneableBean bean;

  private List<SimpleBean> children;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public SimpleBean getSimple() {
    return simple;
  }

  public void setSimple(SimpleBean simple) {
    this.simple = simple;
  }

  public NotCloneableBean getBean() {
    return bean;
  }

  public void setBean(NotCloneableBean bean) {
    this.bean = bean;
  }

  public List<SimpleBean> getChildren() {
    return children;
  }

  public void setChildren(List<SimpleBean> children) {
    this.children = children;
  }
}
