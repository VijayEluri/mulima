package com.andrewoberstar.library.audio;

import java.io.File;

import org.springframework.util.StringUtils;

public enum AudioFileType {
	WAVE("wav"),
	FLAC("flac"),
	VORBIS("ogg"),
	AAC("m4a");
	
	private final String ext;
	
	private AudioFileType(String ext) {
		this.ext = ext;
	}
	
	public String getExtension() {
		return ext;
	}
	
	public boolean isOfType(File file) {
		String ext = StringUtils.getFilenameExtension(file.getAbsolutePath());
		return this.getExtension().equals(ext);
	}
	
	public static AudioFileType valueOf(File file) {
		String ext = StringUtils.getFilenameExtension(file.getAbsolutePath());
		for (AudioFileType type : AudioFileType.values()) {
			if (type.getExtension().equals(ext))
				return type;
		}
		throw new IllegalArgumentException("No type with extension \"" + ext + "\" exists.");
	}
}
