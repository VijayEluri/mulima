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

package com.andrewoberstar.library;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.andrewoberstar.library.audio.AudioFile;
import com.andrewoberstar.library.meta.Album;
import com.andrewoberstar.library.meta.CueSheet;
import com.andrewoberstar.library.meta.GenericTag;

/**
 * Represents a filesystem folder with it's associated <code>Album</code>,
 * <code>AudioFile</code>s, and <code>CueSheet</code>s.
 */
public class AlbumFolder {
	private Album album = null;
	private File folder = null;
	private List<AudioFile> audioFiles = new ArrayList<AudioFile>();
	private List<CueSheet> cues = new ArrayList<CueSheet>();
	
	/**
	 * Constructs an <code>AlbumFolder</code> with no associated folder.
	 */
	public AlbumFolder() {
		this(null);
	}
	
	/**
	 * Constructs an <code>AlbumFolder</code> with an associated folder.
	 * @param folder the folder to associate with this object.
	 */
	public AlbumFolder(File folder) {
		setFolder(folder);
	}
	
	/**
	 * @return the <code>Album</code> associated with this folder.
	 */
	public Album getAlbum() {
		return album;
	}
	
	/**
	 * @param album the <code>Album</code> associated with this folder.
	 */
	public void setAlbum(Album album) {
		this.album = album;
	}
	
	/** 
	 * @return the filesystem folder 
	 */
	public File getFolder() {
		return folder;
	}
	
	/**
	 * 
	 * @param folder the filesystem folder
	 */
	public void setFolder(File folder) {
		this.folder = folder;
	}
	
	/**
	 * @return the audio files in this folder.
	 */
	public List<AudioFile> getAudioFiles() {
		return audioFiles;
	}
	
	/**
	 * @param audioFiles the audio files in this folder.
	 */
	public void setAudioFiles(List<AudioFile> audioFiles) {
		this.audioFiles = audioFiles;
	}
	
	/**
	 * Gets the <code>AudioFile</code> associated with a
	 * <code>CueSheet</code>. 
	 * @param cue the cue to check against
	 * @return the audio file associated with the cue
	 */
	public AudioFile getAudioFile(CueSheet cue) {
		for (AudioFile file : getAudioFiles()) {
			int num = parseNum(file.getFile());
			if (num == cue.getNum()) {
				return file;
			}
		}
		return null;
	}
	
	/**
	 * @return the cues in this folder. 
	 */
	public List<CueSheet> getCues() {
		return cues;
	}
	
	/**
	 * @param cues the cues in this folder.
	 */
	public void setCues(List<CueSheet> cues) {
		this.cues = cues;
	}
	
	/**
	 * Gets the <code>CueSheet</code> that an <code>AudioFile</code>
	 * is associated with.
	 * @param file audio file to check.
	 * @return the corresponding cue sheet.
	 */
	public CueSheet getCue(AudioFile file) {
		int num = parseNum(file.getFile());
		for (CueSheet cue : getCues()) {
			if (num == cue.getNum()) {
				return cue;
			}
		}
		return null;
	}
	
	/**
	 * Creates a new <code>AlbumFolder</code> from the parameters.
	 * The folder will correspond to:
	 * <pre><code>
	 *   rootdir/artist/album
	 * </code></pre>
	 * @param rootDir the root directory of the library
	 * @param album the album to create the folder for
	 * @return the new instance
	 * @throws IOException if it could not make the directory
	 */
	public static AlbumFolder createAlbumFolder(File rootDir, Album album) throws IOException {
		String artist = album.getTags().getFlat(GenericTag.ARTIST);
		String title = album.getTags().getFlat(GenericTag.ALBUM);
		File dir = new File(rootDir, artist + File.separator + title);
		if (!dir.mkdirs()) {
			throw new IOException("Could not make directory: " + dir.getCanonicalPath());
		}
		
		AlbumFolder temp = new AlbumFolder(dir);
		temp.setAlbum(album);
		return temp;
	}
	
	/**
	 * Parses the disc/cue number out of a file name.
	 * @param file the file to parse
	 * @return the number of the file.
	 */
	private int parseNum(File file) {
		Matcher m = Pattern.compile(".*\\(([0-9]+)\\)\\.(flac|cue)").matcher(file.getName());
		return m.matches() ? Integer.valueOf(m.group(1)) : 1;
	}
}
