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
package org.mulima.library.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mulima.audio.AudioFile;
import org.mulima.audio.AudioFileType;
import org.mulima.library.Library;
import org.mulima.library.LibraryAlbum;
import org.mulima.meta.Album;
import org.mulima.meta.CueSheet;
import org.mulima.meta.GenericTag;
import org.mulima.meta.dao.MetadataFileDao;
import org.mulima.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class LibraryImpl implements Library {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private File rootDir = null;
	private AudioFileType type = null;
	private List<LibraryAlbum> albums = null;
	private MetadataFileDao<CueSheet> cueDao = null;
	private MetadataFileDao<Album> albumDao = null;
	
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
	public List<LibraryAlbum> getModified() {
		//TODO implement
		throw new UnsupportedOperationException("Method not implemented.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<LibraryAlbum> getOld() {
		//TODO implement
		throw new UnsupportedOperationException("Method not implemented.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void scanAlbums() {
		this.albums = new ArrayList<LibraryAlbum>();
		List<File> dirs = FileUtil.listDirsRecursive(getRootDir());
		for (File dir : dirs)  {
			LibraryAlbum album = processDir(dir);
			if (album != null) {
				albums.add(album);
			}
		}
	}
	
	protected LibraryAlbum processDir(File dir) {
		LibraryAlbum libAlbum = new LibraryAlbum();
		libAlbum.setLib(this);
		libAlbum.setDir(dir);
		
		for (File file : dir.listFiles()) {
			if (getType().isOfType(file)) {
				AudioFile aud = new AudioFile(file);
				libAlbum.getAudioFiles().add(aud);
			} else if (file.getName().endsWith(".cue")) {
				try {
					CueSheet cue = cueDao.read(file);
					libAlbum.getAlbum().getCues().add(cue);
				} catch (Exception e) {
					logger.error("Problem reading cue sheet: " + FileUtil.getSafeCanonicalPath(file), e);
				}
			} else if ("album.xml".equals(file.getName())) {
				try {
					Album album = albumDao.read(file);
					libAlbum.setAlbum(album);
				} catch (Exception e) {
					logger.error("Problem parsing album: " + FileUtil.getSafeCanonicalPath(file), e);
				}
			}
		}
		
		if (libAlbum.getAudioFiles().isEmpty()) {
			return null;
		} else {
			return libAlbum;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LibraryAlbum newAlbum(LibraryAlbum libAlbum) {
		String relPath = libAlbum.getAlbum().getFlat(GenericTag.ARTIST) + File.separator + libAlbum.getAlbum().getFlat(GenericTag.ALBUM);
		LibraryAlbum newAlbum = new LibraryAlbum();
		newAlbum.setDir(new File(this.getRootDir(), relPath));
		albums.add(newAlbum);
		return newAlbum;
	}
}
