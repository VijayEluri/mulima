package org.mulima.api.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.mulima.exception.UncheckedIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Stores properties used to configure Mulima.  This object
 * can be scoped to a specific set of properties.
 * 
 * For example:
 * <pre>
 *   MulimaProperties mulima = new MulimaProperties(props, null);
 *   mulima.getProperty("propName"); //returns value for propName
 *   mulime.withScope("level1").getProperty; //returns value for level1.propName
 *   //etc.
 * </pre>
 * @since 0.1.0
 */
@Component
public class MulimaProperties {	
	private static final Logger LOGGER = LoggerFactory.getLogger(MulimaProperties.class);
	private final Properties properties;
	private final List<String> scope;
	
	public MulimaProperties() {
		this(new File(System.getenv("APP_HOME"), "config" + File.separator + "mulima.properties"));
	}
	
	/**
	 * Creates properties read in from the given file.
	 * @param file the file to read properties from
	 */
	public MulimaProperties(File file) {
		InputStream stream = null;
		try {
			this.properties = new Properties();
			stream = new FileInputStream(file);
			this.properties.load(stream);
			this.scope = Collections.emptyList();
		} catch(IOException e) {
			throw new UncheckedIOException("Cannot read Mulima properties.", e);
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e2) {
				LOGGER.warn("Problem closing properties output stream.", e2);
			}
		}
	}
	
	/**
	 * Creates properties from the given properties and scope.
	 * @param properties the properties to use
	 * @param scope the scope to read properties from
	 */
	public MulimaProperties(Properties properties, List<String> scope) {
		this.properties = properties;
		if (scope == null) {
			this.scope = Collections.emptyList();
		} else {
			this.scope = Collections.unmodifiableList(scope);
		}
	}
	
	/**
	 * Gets a property with the given name from this properties' scope.
	 * @param property the property to get.
	 * @return the properties value, {@code null} if not set
	 */
	public String getProperty(String property) {
		return properties.getProperty(getName(property));
	}
	
	/**
	 * Gets a property with the given name from this properties' scope.
	 * If not set, it will return the default value.
	 * @param property the property to get
	 * @param defaultValue the default value if not set
	 * @return the properties value, or {@code defaultValue} if not set
	 */
	public String getProperty(String property, String defaultValue) {
		return properties.getProperty(getName(property), defaultValue);
	}
	
	/**
	 * Checks if a property with the given name exists in this
	 * properties' scope.
	 * @param property the property to get
	 * @return {@code true} if the property exists, {@code false} otherwise
	 */
	public boolean hasProperty(String property) {
		return properties.containsKey(getName(property));
	}
	
	/**
	 * Gets all property names in this scope.
	 * @return all property names
	 */
	public Set<String> getPropertyNames() {
		Set<String> names = new HashSet<String>();
		String scope = getName("");
		for (String property : properties.stringPropertyNames()) {
			if (property.startsWith(scope)) {
				names.add(property.substring(scope.length()));
			}
		}
		return names;
	}
	
	/**
	 * Gets all possible scopes underneath the current one.
	 * @return all possible scopes
	 */
	public Set<String> getSubScopes() {
		Set<String> scopes = new HashSet<String>();
		for (String property : getPropertyNames()) {
			scopes.add(property.split("\\.")[0]);
		}
		return scopes;
	}
	
	/**
	 * Gets the full name of the given property
	 * within this properties scope.
	 * @param property the property to get the name of
	 * @return the full name of the property
	 */
	private String getName(String property) {
		StringBuilder name = new StringBuilder();
		for (String level : scope) {
			name.append(level);
			name.append(".");
		}
		name.append(property);
		return name.toString();
	}
	
	/**
	 * Returns a new object scoped another level below
	 * this.
	 * @param level the level to add to the scope
	 * @return a new properties object with the given
	 * level added to the scope
	 */
	public MulimaProperties withScope(String level) {
		List<String> newScope = new ArrayList<String>(scope);
		newScope.add(level);
		return new MulimaProperties(properties, newScope);
	}
}
