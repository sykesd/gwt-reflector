package org.dt.reflector.client;

import java.math.BigDecimal;

public class SimpleBean implements Reflectable {

  private String name;
  private BigDecimal largeValue;
  private Integer smallValue;
  
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getLargeValue() {
    return largeValue;
  }

  public void setLargeValue(BigDecimal largeValue) {
    this.largeValue = largeValue;
  }

  public Integer getSmallValue() {
    return smallValue;
  }

  public void setSmallValue(Integer smallValue) {
    this.smallValue = smallValue;
  }

}
