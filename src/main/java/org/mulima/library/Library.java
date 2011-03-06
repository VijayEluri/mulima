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

/**
 *
 */
public interface Library {
	File getRootDir();
	void setRootDir(File rootDir);
	AudioFileType getType();
	void setType(AudioFileType type);
	void scanAlbums();
	List<LibraryAlbum> getAll();
	List<LibraryAlbum> getModified();
	List<LibraryAlbum> getOld();
	LibraryAlbum newAlbum(LibraryAlbum libAlbum);
}
