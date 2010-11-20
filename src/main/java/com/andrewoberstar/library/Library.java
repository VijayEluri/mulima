package com.andrewoberstar.library;

import java.io.File;

import com.andrewoberstar.library.audio.AudioFileType;

public interface Library {
	File getRootDir();
	void setRootDir(File file);
	AudioFileType getType();
	void setType(AudioFileType type);
}
