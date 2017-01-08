/*
 * Copyright 2010-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mulima.internal.service;

import java.util.List;

import org.mulima.api.service.MulimaProperties;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class MulimaPropertiesSupport {
  private MulimaProperties properties;

  public MulimaProperties getProperties() {
    return properties;
  }

  @Autowired
  public void setProperties(MulimaProperties properties) {
    this.properties = properties;
    for (String level : getScope()) {
      this.properties = this.properties.withScope(level);
    }
  }

  protected abstract List<String> getScope();
}
