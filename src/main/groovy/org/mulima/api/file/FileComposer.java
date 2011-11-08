package org.mulima.api.file;

import java.io.File;

/**
 * Interface describing the operations of a file composer.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 * @param <T> the type of value to compose
 */
public interface FileComposer<T> {
	/**
	 * Writes out the value of the object
	 * to the file.
	 * @param file the file to write to
	 * @param object the object value to write
	 */
	void compose(File file, T object);
}
