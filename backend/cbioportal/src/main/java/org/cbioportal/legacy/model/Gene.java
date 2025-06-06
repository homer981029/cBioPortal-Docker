package org.cbioportal.legacy.model;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public class Gene implements Serializable {

  @NotNull private Integer geneticEntityId;
  @NotNull private Integer entrezGeneId;
  @NotNull private String hugoGeneSymbol;
  private String type;

  public Integer getGeneticEntityId() {
    return geneticEntityId;
  }

  public void setGeneticEntityId(Integer geneticEntityId) {
    this.geneticEntityId = geneticEntityId;
  }

  public Integer getEntrezGeneId() {
    return entrezGeneId;
  }

  public void setEntrezGeneId(Integer entrezGeneId) {
    this.entrezGeneId = entrezGeneId;
  }

  public String getHugoGeneSymbol() {
    return hugoGeneSymbol;
  }

  public void setHugoGeneSymbol(String hugoGeneSymbol) {
    this.hugoGeneSymbol = hugoGeneSymbol;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
