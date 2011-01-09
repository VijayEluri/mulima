/*  
 *  Copyright (C) 2011  Andrew Oberstar.  All rights reserved.
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mulima.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	
	/**
	 * Returns a list of directories underneath this directory.  This
	 * <strong>is</strong> recursive.
	 * @param dir the directory to search
	 * @return a list of all child directories
	 */
	public static List<File> listDirsRecursive(File dir) {
		return listDirsRecursive(dir, new ArrayList<File>());
	}
	
	/**
	 * Helper method for recursion of {@link #listDirsRecursive(File)}.
	 * @param dir directory to search for child directories
	 * @param dirs list of directories to add children to
	 * @return list of child directories (including any already included in <code>dirs</code>)
	 */
	private static List<File> listDirsRecursive(File dir, List<File> dirs) {
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("Must pass a directory in as \"dir\".");
		}
		dirs.add(dir);
		for (File child : dir.listFiles()) {
			if (child.isDirectory()) {
				listDirsRecursive(child, dirs);
			}
		}
		return dirs;
	}
}
