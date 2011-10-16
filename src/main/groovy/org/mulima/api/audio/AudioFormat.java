package org.mulima.api.audio;

import java.io.File;

import org.springframework.util.StringUtils;

public enum AudioFormat {
	WAVE("wav"),
	FLAC("flac"),
	VORBIS("ogg"),
	AAC("m4a"),
	MP3("mp3");
	
	private final String ext;
	
	/**
	 * Constructs an audio file type from an extension.
	 * @param ext the extension
	 */
	private AudioFormat(String ext) {
		this.ext = ext;
	}

	/**
	 * Gets the file extension used for this type.
	 * @return file extension
	 */
	public String getExtension() {
		return ext;
	}
	
	/**
	 * Tests a file to see if it is of the
	 * same type.  Uses the file extension.
	 * @param file file to test.
	 * @return <code>true</code> if of the same type, <code>false</code> otherwise
	 */
	public boolean isFormatOf(File file) {
		String extension = StringUtils.getFilenameExtension(file.getAbsolutePath());
		return this.getExtension().equals(extension);
	}
	
	/**
	 * Gets the file type of a given file.
	 * @param file the file to get the type of
	 * @return the type of the file
	 */
	public static AudioFormat valueOf(File file) {
		String extension = StringUtils.getFilenameExtension(file.getAbsolutePath());
		for (AudioFormat type : AudioFormat.values()) {
			if (type.getExtension().equals(extension)) {
				return type;
			}
		}
		throw new IllegalArgumentException("No type with extension \"" + extension + "\" exists.");
	}
}
