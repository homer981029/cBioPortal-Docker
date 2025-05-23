package org.cbioportal.legacy.model;

import com.fasterxml.jackson.annotation.JsonRawValue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public class DiscreteCopyNumberData extends Alteration implements Serializable {
  @NotNull private Integer alteration;

  @JsonRawValue
  @Schema(type = "java.util.Map")
  private Object annotationJson;

  public Integer getAlteration() {
    return alteration;
  }

  public void setAlteration(Integer alteration) {
    this.alteration = alteration;
  }

  public Object getAnnotationJson() {
    return annotationJson;
  }

  public void setAnnotationJson(String annotationJson) {
    this.annotationJson = annotationJson;
  }
}
