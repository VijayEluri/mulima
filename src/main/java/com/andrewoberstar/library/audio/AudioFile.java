/*  
 *  Copyright (C) 2010  Andrew Oberstar.  All rights reserved.
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

package com.andrewoberstar.library.audio;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	public static AudioFile createTempFile(AudioFile model) throws IOException {
		File temp = File.createTempFile("library", " (" + parseNum(model.getFile()) + ")." + model.getType().getExtension());
		temp.deleteOnExit();
		return new AudioFile(temp);
	}
	
	/**
	 * Parses the disc/cue number out of a file name.
	 * @param file the file to parse
	 * @return the number of the file.
	 */
	private static int parseNum(File file) {
		Matcher m = Pattern.compile(".*\\(([0-9]+)\\)\\.(flac|cue)").matcher(file.getName());
		return m.matches() ? Integer.valueOf(m.group(1)) : 1;
	}
	
	public static AudioFile createAudioFile(File dir, AudioFile model, AudioFileType type) throws IOException {
		String name = FileUtil.getBaseName(model.getFile());
		File temp = new File(dir, name + "." + type.getExtension());
		return new AudioFile(temp);
	}
}
