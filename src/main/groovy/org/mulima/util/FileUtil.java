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

import org.mulima.api.file.FileHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper methods for <code>File</code> operations.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public final class FileUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);
	
	/**
	 * This class should never be instantiated.
	 * @throws AssertionError always
	 */
	private FileUtil() {
		throw new AssertionError("Cannot instantiate this class.");
	}

	/**
	 * Returns a file in the same location, but with its
	 * extension changed to the one provided.
	 * @param original the original file
	 * @param extension the new extension
	 * @return a new file with specified extension
	 */
	public static File changeExtension(FileHolder original, String extension) {
		return changeExtension(original.getFile(), extension);
	}
	
	/**
	 * Returns a file in the same location, but with a
	 * different extension than the parameter.
	 * @param original the original file
	 * @param extension the new extension
	 * @return a new file with specified extension
	 */
	public static File changeExtension(File original, String extension) {
		String ext = extension.charAt(0) == '.' ? extension : "." + extension;
		String path = original.getPath().replaceAll("\\.[^\\.]+$", ext);
		return new File(path);
	}

	/**
	 * Gets the name of the file without the extension.
	 * @param file the file to get base name of
	 * @return the name without the extension of file
	 */
	public static String getBaseName(FileHolder file) {
		return getBaseName(file.getFile());
	}
	
	/**
	 * Gets the name of the file without the extension.
	 * @param file the file to get base name of
	 * @return the name without the extension of file
	 */
	public static String getBaseName(File file) {
		String name = file.getName();
		int index = name.lastIndexOf('.');
		return name.substring(0, index);
	}
	
	/**
	 * Gets the canonical path of a file.  If an exception is thrown
	 * while getting the path, null is returned.
	 * @param file the file to return the path of
	 * @return the canonical path of the file, or null if there
	 * was an exception
	 */
	public static String getSafeCanonicalPath(FileHolder file) {
		return getSafeCanonicalPath(file.getFile());
	}
	
	/**
	 * Gets the canonical path of a file.  If an exception is thrown
	 * while getting the path, null is returned.
	 * @param file the file to return the path of
	 * @return the canonical path of the file, or null if there
	 * was an exception
	 */
	public static String getSafeCanonicalPath(File file) {
		try {
			return file.getCanonicalPath();
		} catch (IOException e) {
			LOGGER.warn("Problem getting canonical path: {}", file.getAbsolutePath());
			return null;
		}
	}
	
	/**
	 * Returns a list of directories underneath this directory.  This
	 * <strong>is</strong> recursive.
	 * @param dir the directory to search
	 * @return a list of all child directories
	 * @throws IlegalArgumentException if {@code dir} is not a directory
	 */
	public static List<File> listDirsRecursive(FileHolder dir) {
		return listDirsRecursive(dir.getFile());
	}
	
	/**
	 * Returns a list of directories underneath this directory.  This
	 * <strong>is</strong> recursive.
	 * @param dir the directory to search
	 * @return a list of all child directories
	 * @throws IlegalArgumentException if {@code dir} is not a directory
	 */
	public static List<File> listDirsRecursive(File dir) {
		return listDirsRecursive(dir, new ArrayList<File>());
	}
	
	/**
	 * Helper method for recursion of {@link #listDirsRecursive(File)}.
	 * @param dir directory to search for child directories
	 * @param dirs list of directories to add children to
	 * @return list of child directories (including any already included in {@code dirs})
	 * @throws IlegalArgumentException if {@code dir} is not a directory
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
