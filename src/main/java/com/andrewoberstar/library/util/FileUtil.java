package com.andrewoberstar.library.util;

import java.io.File;
import java.io.IOException;

/**
 * Helper methods for <code>File</code> operations.
 */
public class FileUtil {
	/**
	 * This class should never be instantiated.
	 * @throws UnsupportedOperationException always
	 */
	protected FileUtil() {
		throw new UnsupportedOperationException("Cannot instantiate this class.");
	}
	
	/**
	 * Returns a <code>File</code> in the same location, but with a
	 * different extension than the parameter.
	 * @param original the original <code>File</code>
	 * @param extension the new extension
	 * @return a new <code>File</code> with specified extension
	 */
	public static File changeExtension(File original, String extension) {
		String ext = extension.startsWith(".") ? extension : "." + extension;
		String path = original.getPath().replaceAll("\\.[^\\.]+$", ext);
		return new File(path);
	}
	
	/**
	 * Gets the name of the <code>File</code> without
	 * the extension.
	 * @param file the <code>File</code> to get base name of
	 * @return the name without the extension of <code>file</code>
	 */
	public static String getBaseName(File file) {
		String name = file.getName();
		int index = name.lastIndexOf('.');
		return name.substring(0, index);
	}
	
	/**
	 * Creates an empty directory in the default temporary-file directory,
	 * using the given prefix and suffix to generate its name.
	 * @param prefix The prefix string to be used in generating the dir's
	 * name; must be at least three characters long
	 * @param suffix The suffix string to be used in generating the dir's
	 * name; may be null, in which case the suffix ".tmp" will be used
	 * @return An abstract pathname denoting a newly-created empty dir
	 * @throws IOException - If a dir could not be created
	 * @see File#createTempFile(String, String)
	 */
	public static File createTempDir(String prefix, String suffix) throws IOException {
		File tempFile = File.createTempFile(prefix, suffix);
		if (!tempFile.delete()) {
			throw new IOException("Could not delete file: " + tempFile.getCanonicalPath());
		} else if (tempFile.exists()) {
			throw new IOException("Did not delete file: " + tempFile.getCanonicalPath());
		} else if (!tempFile.mkdirs()) {
			throw new IOException("Could not create dir: " + tempFile.getCanonicalPath());
		} else {
			tempFile.deleteOnExit();
			return tempFile;
		}
	}
}
