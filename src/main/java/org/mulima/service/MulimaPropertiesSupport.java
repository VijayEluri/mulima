package org.mulima.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class MulimaPropertiesSupport {
  private MulimaProperties properties;

  public MulimaProperties getProperties() {
    return properties;
  }

  @Autowired
  public void setProperties(MulimaProperties properties) {
    this.properties = properties;
    for (var level : getScope()) {
      this.properties = this.properties.withScope(level);
    }
  }

  protected abstract List<String> getScope();
}
