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
import java.util.List;

import org.mulima.audio.AudioFileType;
import org.mulima.meta.Album;
import org.mulima.meta.CueSheet;
import org.mulima.meta.dao.MetadataFileDao;

/**
 * A library is a copy of your music collection somewhere on your machine.
 * It is defined by a root directory and a certain format of files that it 
 * houses.
 */
public interface Library {
	/**
	 * Returns the root directory of this library.
	 * @return the root directory
	 */
	File getRootDir();
	
	/**
	 * Sets the root directory of this library.
	 * @param rootDir the root directory
	 */
	void setRootDir(File rootDir);
	
	/**
	 * Gets the format of audio file this library houses.
	 * @return the audio file type
	 */
	AudioFileType getType();
	
	/**
	 * Sets the format of audio files the library houses.
	 * @param type the audio file type
	 */
	void setType(AudioFileType type);
	
	/**
	 * Scans the library for albums.
	 * @param cueDao dao to process cues
	 * @param albumDao dao to process albums
	 */
	void scanAlbums(MetadataFileDao<CueSheet> cueDao, MetadataFileDao<Album> albumDao);
	
	/**
	 * Gets all albums in the library.  Must call {@link #scanAlbums(MetadataFileDao, MetadataFileDao)}
	 * beforehand.
	 * @return list of all albums in the library
	 */
	List<LibraryAlbum> getAll();
	
	/**
	 * Gets all albums that have been changed outside of the
	 * normal update process.
	 * @return list of modified albums
	 */
	List<LibraryAlbum> getModified();
	
	/**
	 * Gets all albums that need to be updated from the reference.
	 * @return list of out of date albums.
	 */
	List<LibraryAlbum> getOld();
	
	/**
	 * Creates a new album based on the parameter.
	 * @param libAlbum the library album to base this one on
	 * @return a new library album
	 */
	LibraryAlbum newAlbum(LibraryAlbum libAlbum);
}
