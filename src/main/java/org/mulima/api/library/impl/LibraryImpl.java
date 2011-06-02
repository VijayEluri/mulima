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
package org.mulima.api.library.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mulima.api.audio.AudioFile;
import org.mulima.api.audio.AudioFileType;
import org.mulima.api.library.Library;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.meta.Album;
import org.mulima.api.meta.CueSheet;
import org.mulima.api.meta.Disc;
import org.mulima.api.meta.GenericTag;
import org.mulima.meta.dao.MetadataFileDao;
import org.mulima.util.FileUtil;
import org.mulima.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic version of a library.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class LibraryImpl implements Library {
	private static final Pattern IMAGE_REGEX = Pattern.compile(".*\\(([0-9])\\)\\.[^\\.]+$");
	private static final Pattern TRACK_REGEX = Pattern.compile("^D([0-9]+)T([0-9]+).*");
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private MetadataFileDao<Album> albumDao;
	private MetadataFileDao<CueSheet> cueDao;
	private File rootDir = null;
	private AudioFileType type = null;
	private List<LibraryAlbum> albums = null;
	
	/**
	 * Sets the DAO that will parse album.xml files.
	 * @param albumDao
	 */
	public void setAlbumDao(MetadataFileDao<Album> albumDao) {
		this.albumDao = albumDao;
	}

	/**
	 * Sets the DAO that will parse cue sheet files.
	 * @param cueDao
	 */
	public void setCueDao(MetadataFileDao<CueSheet> cueDao) {
		this.cueDao = cueDao;
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
	 * @throws IOException if there is a problem reading the cue
	 */
	@Override
	public void scanAlbums() throws IOException {
		this.albums = new ArrayList<LibraryAlbum>();
		List<File> dirs = FileUtil.listDirsRecursive(getRootDir());
		for (File dir : dirs)  {
			LibraryAlbum album = processDir(dir);
			if (album != null) {
				albums.add(album);
			}
		}
	}
	
	/**
	 * Processes a directory to generate a library album.
	 * @param dir directory to process
	 * @return library album representing this directory
	 */
	protected LibraryAlbum processDir(File dir) throws IOException {
		LibraryAlbum libAlbum = new LibraryAlbum();
		libAlbum.setLib(this);
		libAlbum.setDir(dir);
		
		for (File file : dir.listFiles()) {
			if (getType().isOfType(file)) {
				libAlbum.getAudioFiles().add(processAudioFile(file));
			} else if (file.getName().endsWith(".cue")) {
				libAlbum.getCues().add(processCueSheet(file));
			} else if ("album.xml".equals(file.getName())) {
				libAlbum.setAlbum(processAlbum(file));
			}
		}
		
		if (libAlbum.getAudioFiles().isEmpty()) {
			return null;
		} else {
			return libAlbum;
		}
	}
	
	/**
	 * Processes a file to generate an audio file.
	 * @param file file to process
	 * @return audio file representing this file
	 */
	protected AudioFile processAudioFile(File file) {
		AudioFile aud = new AudioFile(file);
		
		Matcher trackM = TRACK_REGEX.matcher(file.getName());
		Matcher imageM = IMAGE_REGEX.matcher(file.getName());
		
		if (trackM.find()) {
			aud.setDiscNum(Integer.parseInt(trackM.group(1)));
			aud.setTrackNum(Integer.parseInt(trackM.group(2)));
		} else if (imageM.find()) {
			aud.setDiscNum(Integer.parseInt(imageM.group(1)));
		} else {
			aud.setDiscNum(1);
			aud.setTrackNum(1);
		}
		
		return aud;
	}
	
	/**
	 * Processes a file to generate a cue sheet.
	 * @param file file to process
	 * @return cue sheet representing this file
	 * @throws IOException if there is a problem reading the cue
	 */
	protected CueSheet processCueSheet(File file) throws IOException {
		try {
			return cueDao.read(file);
		} catch (IOException e) {
			logger.error("Problem reading cue sheet: {}",
				FileUtil.getSafeCanonicalPath(file), e);
			throw e;
		}
	}
	
	/**
	 * Processes a file to generate an album.
	 * @param file file to process
	 * @return album representing this file
	 * @throws IOException if there is a problem reading the album
	 */
	protected Album processAlbum(File file) throws IOException {
		try {
			return albumDao.read(file);
		} catch (IOException e) {
			logger.error("Problem parsing album: {}",
				FileUtil.getSafeCanonicalPath(file), e);
			throw e;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LibraryAlbum newAlbum(LibraryAlbum libAlbum) {
		String album = null;
		if (libAlbum.getAlbum().isSet(GenericTag.ALBUM)) {
			album = libAlbum.getAlbum().getFlat(GenericTag.ALBUM);
		} else {
			for (Disc disc : libAlbum.getAlbum().getDiscs()) {
				if (disc.isSet(GenericTag.ALBUM)) {
					if (album == null) {
						album = disc.getFlat(GenericTag.ALBUM);
					} else {
						album = StringUtil.commonString(album, disc.getFlat(GenericTag.ALBUM));
					}
				}
			}
		}
		String relPath = StringUtil.makeSafe(libAlbum.getAlbum().getFlat(GenericTag.ARTIST))
			+ File.separator + StringUtil.makeSafe(album);
		LibraryAlbum newAlbum = new LibraryAlbum();
		newAlbum.setAlbum(libAlbum.getAlbum());
		newAlbum.setLib(this);
		newAlbum.setDir(new File(this.getRootDir(), relPath));
		if (!newAlbum.getDir().exists() && !newAlbum.getDir().mkdirs()) {
			logger.error("Problem making directory: " + newAlbum.getDir());
		}
		albums.add(newAlbum);
		return newAlbum;
	}
}
