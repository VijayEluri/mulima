package com.andrewoberstar.library.audio;

import java.io.File;
import java.io.IOException;

import com.andrewoberstar.library.util.FileUtil;


public class AudioFile {
	private AudioFileType type;
	private File file;
	
	public AudioFile() {
		this.type = null;
		this.file = null;
	}
	
	public AudioFile(String path) {
		setFile(path);
	}
	
	public AudioFile(File file) {
		setFile(file);
	}
	
	public File getFile() {
		return file;
	}
	
	public void setFile(String path) {
		setFile(new File(path));
	}
	
	public void setFile(File file) {
		this.type = AudioFileType.valueOf(file);
		this.file = file;
	}
	
	public AudioFileType getType() {
		return type;
	}
	
	public static AudioFile createTempFile(AudioFileType type) throws IOException {
		File temp = File.createTempFile("library", "." + type.getExtension());
		temp.deleteOnExit();
		return new AudioFile(temp);
	}
	
	public static AudioFile createAudioFile(File dir, AudioFile model, AudioFileType type) throws IOException {
		String name = FileUtil.getBaseName(model.getFile());
		File temp = new File(dir, name + "." + type.getExtension());
		return new AudioFile(temp);
	}
}
