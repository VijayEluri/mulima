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
package org.mulima.api.library;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.mulima.api.audio.AudioFileType;

/**
 * A library is a copy of your music collection somewhere on your machine.
 * It is defined by a root directory and a certain format of files that it 
 * houses.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public interface Library {
	/**
	 * Gets the name of the library.
	 * @return the name
	 */
	String getName();
	
	/**
	 * Sets the name of the library.
	 * @param name the name
	 */
	void setName(String name);
	
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
	 * @throws IOException if there is a problem processing
	 * the albums
	 */
	void scanAlbums() throws IOException;
	
	/**
	 * Gets all albums in the library.  Must call {@link #scanAlbums(MetadataFileDao, MetadataFileDao)}
	 * beforehand.
	 * @return list of all albums in the library
	 */
	List<LibraryAlbum> getAll();
	
	/**
	 * Gets all albums need updating.
	 * @return list of modified albums
	 */
	List<LibraryAlbum> getOutdated();
	
	/**
	 * Gets the album with the specified ID.
	 * @param id the ID of the album
	 * @return the album or <code>null</code> if
	 * it does not exist
	 */
	LibraryAlbum get(UUID id);
	
	/**
	 * Gets the album sourced from the given
	 * ID.
	 * @param id the ID of the source album
	 * @return the album or <code>null</code> if
	 * it does not exist
	 */
	LibraryAlbum getWithSource(UUID id);
	
	/**
	 * Creates a new album based on the parameter.
	 * @param libAlbum the library album to base this one on
	 * @return a new library album
	 */
	LibraryAlbum newAlbum(LibraryAlbum libAlbum);
}
