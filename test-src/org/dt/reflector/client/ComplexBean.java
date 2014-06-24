package org.dt.reflector.client;

import java.util.List;
import java.util.Map;

public class ComplexBean implements Reflectable {
  private String name;
  private List<SimpleBean> beans;

  private Map<String, SimpleBean> mappedBeans;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<SimpleBean> getBeans() {
    return beans;
  }

  public void setBeans(List<SimpleBean> beans) {
    this.beans = beans;
  }

  public Map<String, SimpleBean> getMappedBeans() {
    return mappedBeans;
  }

  public void setMappedBeans(Map<String, SimpleBean> mappedBeans) {
    this.mappedBeans = mappedBeans;
  }

}
