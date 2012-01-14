package org.mulima.internal.file;

import java.io.File;
import java.io.FileFilter;

import org.mulima.api.file.audio.AudioFormat;

public class LeafDirFilter implements FileFilter {

	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
			boolean anyAudioFiles = false;
			for (File child : file.listFiles()) {
				if (child.isDirectory()) {
					return false;
				} else if (!anyAudioFiles && AudioFormat.isAudioFile(child)) {
					anyAudioFiles = true;
				}
			}
			return anyAudioFiles;
		} else {
			return false;
		}
	}

}
