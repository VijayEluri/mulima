package x.org.mulima.internal.file;

import java.io.File;
import java.io.FileFilter;

public class LeafDirFilter implements FileFilter {

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
