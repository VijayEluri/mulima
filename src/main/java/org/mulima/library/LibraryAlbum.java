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
package org.mulima.library;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mulima.audio.AudioFile;
import org.mulima.meta.Album;
import org.mulima.meta.CueSheet;

/**
 *
 */
public class LibraryAlbum extends Album {
	private Library lib = null;
	private File dir = null;
	private List<AudioFile> audioFiles = new ArrayList<AudioFile>();
	private List<CueSheet> cues = new ArrayList<CueSheet>();
	
	/**
	 * @param lib the lib to set
	 */
	public void setLib(Library lib) {
		this.lib = lib;
	}

	/**
	 * @return the lib
	 */
	public Library getLib() {
		return lib;
	}

	/**
	 * @return the dir
	 */
	public File getDir() {
		return dir;
	}
	
	/**
	 * @param dir the dir to set
	 */
	public void setDir(File dir) {
		this.dir = dir;
	}
	
	/**
	 * @return the audioFiles
	 */
	public List<AudioFile> getAudioFiles() {
		return audioFiles;
	}
	/**
	 * @param audioFiles the audioFiles to set
	 */
	public void setAudioFiles(List<AudioFile> audioFiles) {
		this.audioFiles = audioFiles;
	}
	
	/**
	 * @return the cues
	 */
	public List<CueSheet> getCues() {
		return cues;
	}
	
	/**
	 * @param cues the cues to set
	 */
	public void setCues(List<CueSheet> cues) {
		this.cues = cues;
	}
}
