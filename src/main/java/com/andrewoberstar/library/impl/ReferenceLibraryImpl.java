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

package com.andrewoberstar.library.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.andrewoberstar.library.AlbumFolder;
import com.andrewoberstar.library.ReferenceLibrary;
import com.andrewoberstar.library.audio.AudioFile;
import com.andrewoberstar.library.meta.Album;
import com.andrewoberstar.library.meta.CueSheet;
import com.andrewoberstar.library.meta.Disc;
import com.andrewoberstar.library.meta.GenericTag;
import com.andrewoberstar.library.meta.dao.FileMetadataDao;
import com.andrewoberstar.library.meta.dao.FreeDbDao;
import com.andrewoberstar.library.ui.UICallback;

public class ReferenceLibraryImpl extends LibraryImpl implements ReferenceLibrary {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private List<AlbumFolder> allAlbums = new ArrayList<AlbumFolder>();
	private List<AlbumFolder> newAlbums = new ArrayList<AlbumFolder>();
	private FileMetadataDao<CueSheet> cueDao;
	private FileMetadataDao<Album> albumXmlDao;
	private FreeDbDao freeDbDao;
	
	public void setCueSheetDao(FileMetadataDao<CueSheet> cueDao) {
		this.cueDao = cueDao;
	}
	
	public void setAlbumXmlDao(FileMetadataDao<Album> albumDao) {
		this.albumXmlDao = albumDao;
	}
	
	public void setFreeDbDao(FreeDbDao freeDbDao) {
		this.freeDbDao = freeDbDao;		
	}

	@Override
	public List<AlbumFolder> getAllAlbums() {
		return allAlbums;
	}
	
	@Override
	public List<AlbumFolder> getNewAlbums() {
		return newAlbums;
	}
	
	@Override
	public void findAlbums() {
		this.allAlbums = new ArrayList<AlbumFolder>();
		this.newAlbums = new ArrayList<AlbumFolder>();
		
		List<File> dirs = findDirs(getRootDir(), new ArrayList<File>());
		for (File dir : dirs) {
			AlbumFolder folder = processDir(dir);
			if (folder != null) {
				if (folder.getAlbum() == null) {
					newAlbums.add(folder);
				}
				allAlbums.add(folder);
			}
		}
	}
	
	private AlbumFolder processDir(File dir) {
		AlbumFolder folder = new AlbumFolder(dir);
		
		for (File file : dir.listFiles()) {
			if (this.getType().isOfType(file)) {
				AudioFile aud = new AudioFile(file);
				folder.getAudioFiles().add(aud);
			} else if (file.getName().endsWith(".cue")) {
				CueSheet cue = cueDao.read(file);
				folder.getCues().add(cue);
			} else if ("album.xml".equals(file.getName())) {
				Album album = albumXmlDao.read(file);
				folder.setAlbum(album);
			}
		}
		
		if (folder.getAudioFiles().isEmpty()) {
			return null;
		} else {
			return folder;
		}
	}
	
	private List<File> findDirs(File file, List<File> dirs) {
		if (file.isDirectory()) {
			dirs.add(file);
			for (File child : file.listFiles()) {
				findDirs(child, dirs);
			}
			return dirs;
		} else {
			return dirs;
		}
	}
	
	@Override
	public void processNewAlbums(UICallback<Disc> chooser) {		
		for (AlbumFolder folder : getNewAlbums()) {
			Album album = new Album();
			
			for (CueSheet cue : folder.getCues()) {
				album.getCues().add(cue);
				logger.info("*** Searching for disc ***");
				logger.info("Cue: DiscId: " + cue.getTags().getFlat(GenericTag.CDDB_ID) +
					"\tArtist: " + cue.getTags().getFlat(GenericTag.ARTIST) + 
					"\tAlbum: " + cue.getTags().getFlat(GenericTag.ALBUM));
				List<String> cddbIds = cue.getTags().getAll(GenericTag.CDDB_ID);
				List<Disc> candidates = freeDbDao.getDiscsById(cddbIds);
				
				Map<String, Object> parms = new HashMap<String, Object>();
				parms.put("cue", cue);
				parms.put("candidates", candidates);
				Disc choice = chooser.call(parms);
				
				if (choice == null) {
					logger.warn("Disc not found: Artist: " + cue.getTags().getFlat(GenericTag.ARTIST) + "\tAlbum: " + cue.getTags().getFlat(GenericTag.ALBUM));
				} else {
					logger.info("Disc found: Artist: " + cue.getTags().getFlat(GenericTag.ARTIST) + "\tAlbum: " + cue.getTags().getFlat(GenericTag.ALBUM));
					choice.getTags().add(GenericTag.DISC_NUMBER, Integer.toString(cue.getNum()));
					album.getDiscs().add(choice);
				}
			}
			
			folder.setAlbum(album);
			albumXmlDao.write(new File(folder.getFolder(), "album.xml"), album);
		}
	}
}
