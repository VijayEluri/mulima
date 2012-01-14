package org.mulima.api.file.audio;

import java.io.File;

import org.springframework.util.StringUtils;

public enum AudioFormat {
	WAVE("wav", true),
	FLAC("flac", true),
	VORBIS("ogg", false),
	AAC("m4a", false),
	MP3("mp3", false);
	
	private final String ext;
	private final boolean lossless;
	
	/**
	 * Constructs an audio file type from an extension.
	 * @param ext the extension
	 */
	private AudioFormat(String ext, boolean lossless) {
		this.ext = ext;
		this.lossless = lossless;
	}

	/**
	 * Gets the file extension used for this type.
	 * @return file extension
	 */
	public String getExtension() {
		return ext;
	}
	
	public boolean isLossless() {
		return lossless;
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
	
	/**
	 * Determines whether the given file is a supported
	 * audio file.
	 * @param file the file to check
	 * @return {@code true} if it is an audio file
	 */
	public static boolean isAudioFile(File file) {
		try {
			return AudioFormat.valueOf(file) != null;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
}
