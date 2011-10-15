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
package z.org.mulima.api.library.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.mulima.api.audio.AudioFileType;
import org.mulima.api.library.Library;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.library.LibraryAlbumFactory;
import org.mulima.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic version of a library.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class LibraryImpl implements Library {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String name;
	private File rootDir = null;
	private AudioFileType type = null;
	private LibraryAlbumFactory factory = null;
	private List<LibraryAlbum> albums = null;
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	public void setFactory(LibraryAlbumFactory factory) {
		this.factory = factory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public File getRootDir() {
		return rootDir;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setRootDir(File rootDir) {
		this.rootDir = rootDir;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AudioFileType getType() {
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setType(AudioFileType type) {
		this.type = type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<LibraryAlbum> getAll() {
		return albums;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<LibraryAlbum> getOutdated() {
		//TODO implement
		throw new UnsupportedOperationException("Method not implemented.");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LibraryAlbum get(UUID id) {
		if (id == null) {
			throw new NullPointerException("Must pass a non-null ID.");
		}
		for (LibraryAlbum album : getAll()) {
			if (id.equals(album.getId())) {
				return album;
			}
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LibraryAlbum getSourcedFrom(UUID id) {
		for (LibraryAlbum album : getAll()) {
			if (album.getSourceDigest() != null && album.getSourceDigest().getId().equals(id)) {
				return album;
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @throws IOException if there is a problem reading the cue
	 */
	@Override
	public void scanAlbums() throws IOException {
		this.albums = new ArrayList<LibraryAlbum>();
		List<File> dirs = FileUtil.listDirsRecursive(getRootDir());
		for (File dir : dirs)  {
			LibraryAlbum album = factory.newAlbum(this, dir);
			if (album != null) {
				albums.add(album);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LibraryAlbum newAlbum(LibraryAlbum libAlbum) {
		LibraryAlbum newAlbum = factory.newAlbum(this, libAlbum);
		albums.add(newAlbum);
		return newAlbum;
	}
}
