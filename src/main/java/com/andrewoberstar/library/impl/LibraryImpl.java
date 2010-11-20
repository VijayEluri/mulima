package com.andrewoberstar.library.impl;

import java.io.File;

import com.andrewoberstar.library.Library;
import com.andrewoberstar.library.audio.AudioFileType;

public class LibraryImpl implements Library {
	private File rootDir;
	private AudioFileType type;
	
	@Override
	public File getRootDir() {
		return rootDir;
	}

	@Override
	public void setRootDir(File rootDir) {
		this.rootDir = rootDir;
	}
	
	@Override
	public AudioFileType getType() {
		return type;
	}
	
	@Override
	public void setType(AudioFileType type) {
		this.type = type;
	}
}
