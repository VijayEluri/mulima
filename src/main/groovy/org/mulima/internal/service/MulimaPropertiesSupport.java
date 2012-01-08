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
