package org.cbioportal.legacy.model;

import java.io.Serializable;

public class ClinicalAttributeCount implements Serializable {

  private String attrId;
  private Integer count;

  public String getAttrId() {
    return attrId;
  }

  public void setAttrId(String attrId) {
    this.attrId = attrId;
  }

  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }
}
