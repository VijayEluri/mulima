/*  
 *  Copyright (C) 2011  Andrew Oberstar.  All rights reserved.
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
package org.mulima.audio;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * A wrapper around a traditional <code>File</code> object
 * providing access to the type of file.
 */
public class AudioFile extends File {
	private static final long serialVersionUID = 1L;
	private Integer discNum = null;
	private Integer trackNum = null;

	/**
	 * Constructs audio file from a parent directory
	 * and a child path.
	 * @param parent parent directory
	 * @param child path to child relative to parent
	 */
	public AudioFile(File parent, String child) {
		super(parent, child);
	}

	/**
	 * Constructs audio file from a parent directory
	 * and a child path.
	 * @param parent path to the parent directory
	 * @param child path to child relative to parent
	 */
	public AudioFile(String parent, String child) {
		super(parent, child);
	}

	/**
	 * Constructs an audio file from a pathname.
	 * @param pathname the path
	 */
	public AudioFile(String pathname) {
		super(pathname);
	}

	/**
	 * Constructs an audio file from a URI.
	 * @param uri the URI to the file
	 */
	public AudioFile(URI uri) {
		super(uri);
	}
	
	/**
	 * Constructs an audio file from a file.
	 * @param file the file
	 */
	public AudioFile(File file) {
		super(file.getPath());
	}

	/**
	 * Gets the type of audio file.
	 * @return type of audio file
	 */
	public AudioFileType getType() {
		return AudioFileType.valueOf(this);
	}
	
	/**
	 * @return the discNum
	 */
	public Integer getDiscNum() {
		return discNum;
	}

	/**
	 * @param discNum the discNum to set
	 */
	public void setDiscNum(Integer discNum) {
		this.discNum = discNum;
	}

	/**
	 * @return the trackNum
	 */
	public Integer getTrackNum() {
		return trackNum;
	}

	/**
	 * @param trackNum the trackNum to set
	 */
	public void setTrackNum(Integer trackNum) {
		this.trackNum = trackNum;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	/**
	 * Creates a temporary audio file.
	 * @param model the file to model
	 * @return the temporary file
	 * @throws IOException if the file cannot be created
	 */
	public static AudioFile createTempFile(AudioFile model) throws IOException { 
		File temp = File.createTempFile("library", "." + model.getType().getExtension());
		return new AudioFile(temp);
	}
}
