package org.mulima.internal.file;

import java.io.File;
import java.io.FileFilter;

/**
 * A filter to select only leaf directories.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class LeafDirFilter implements FileFilter {
	/**
	 * Only accepts directories that have no child
	 * directories.
	 * @param file the file to test
	 * @return {@code true} if the file is a leaf
	 * dir, {@code false} otherwise
	 */
	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				if (child.isDirectory()) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

}
