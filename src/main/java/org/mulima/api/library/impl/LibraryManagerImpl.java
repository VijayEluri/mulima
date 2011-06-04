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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.mulima.api.audio.CodecConfig;
import org.mulima.api.library.Library;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.library.LibraryManager;
import org.mulima.api.library.ReferenceLibrary;
import org.mulima.api.meta.Album;
import org.mulima.api.meta.CueSheet;
import org.mulima.api.meta.Disc;
import org.mulima.api.meta.GenericTag;
import org.mulima.audio.util.AudioConversionService;
import org.mulima.job.Context;
import org.mulima.library.util.Chooser;
import org.mulima.library.util.DiscCliChooser;
import org.mulima.meta.dao.FreeDbDao;
import org.mulima.meta.dao.MetadataFileDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic version of a library manager.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class LibraryManagerImpl implements LibraryManager {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private List<ReferenceLibrary> refLibs = null;
	private List<Library> destLibs = null;
	private FreeDbDao freeDbDao = null;
	private MetadataFileDao<Album> albumDao = null;
	private MetadataFileDao<CueSheet> cueDao = null;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ReferenceLibrary> getRefLibs() {
		return refLibs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setRefLibs(List<ReferenceLibrary> refLibs) {
		this.refLibs = refLibs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Library> getDestLibs() {
		return destLibs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDestLibs(List<Library> destLibs) {
		this.destLibs = destLibs;
	}

	/**
	 * @param codecConfig the codeConfig to set
	 */
	public void setCodecConfig(CodecConfig codecConfig) {
		Context.getRoot().setCodecConfig(codecConfig);
	}

	/**
	 * @param freeDbDao the freeDbDao to set
	 */
	public void setFreeDbDao(FreeDbDao freeDbDao) {
		this.freeDbDao = freeDbDao;
	}

	/**
	 * @param albumDao the albumDao to set
	 */
	public void setAlbumDao(MetadataFileDao<Album> albumDao) {
		this.albumDao = albumDao;
	}

	/**
	 * @param cueDao the cueDao to set
	 */
	public void setCueDao(MetadataFileDao<CueSheet> cueDao) {
		this.cueDao = cueDao;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateAll() {
		updateLibs(getDestLibs());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateLib(Library lib) {
		List<Library> libs = new ArrayList<Library>();
		libs.add(lib);
		updateLibs(libs);
	}
	
	/**
	 * Updates a list of libraries.
	 * @param libs the libraries to update.
	 */
	private void updateLibs(List<Library> libs) {
		List<LibraryAlbum> refAlbums = new ArrayList<LibraryAlbum>();
		for (ReferenceLibrary refLib : getRefLibs()) {
			refAlbums.addAll(refLib.getAll());
		}
		
		List<Future<List<LibraryAlbum>>> futures = new ArrayList<Future<List<LibraryAlbum>>>();
		for (LibraryAlbum refAlbum : refAlbums) {
			List<LibraryAlbum> destAlbums = new ArrayList<LibraryAlbum>();
			for (Library destLib : libs) {
				destAlbums.add(destLib.newAlbum(refAlbum));
			}
			futures.add(AudioConversionService.getInstance().submitConvert(refAlbum, destAlbums));
		}
		
		boolean cancelAll = false;
		while (!futures.isEmpty()) {
			Iterator<Future<List<LibraryAlbum>>> iterator = futures.iterator();
			while (iterator.hasNext()) {
				Future<?> future = iterator.next();
				if (future.isDone()) {
					try {
						future.get();
					} catch (ExecutionException e) {
						logger.error("Problem converting album.", e);
					} catch (InterruptedException e) {
						logger.error("Problem converting album.", e);
					}
					iterator.remove();
				} else if (cancelAll) {
					future.cancel(true);
					iterator.remove();
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				cancelAll = true;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processNew() {
		for (ReferenceLibrary refLib : getRefLibs()) {
			for (LibraryAlbum libAlbum : refLib.getNew()) {
				Album album = new Album();
				
				for (CueSheet cue : libAlbum.getCues()) {
					logger.info("*** Searching for disc ***");
					logger.info("Cue: DiscId: " + cue.getFlat(GenericTag.CDDB_ID)
						+ "\tArtist: " + cue.getFlat(GenericTag.ARTIST) 
						+ "\tAlbum: " + cue.getFlat(GenericTag.ALBUM));
					List<String> cddbIds = cue.getAll(GenericTag.CDDB_ID);
					List<Disc> candidates = freeDbDao.getDiscsById(cddbIds);
					
					Chooser<Disc> chooser = new DiscCliChooser(cue);
					Disc choice = chooser.choose(candidates);
					
					if (choice == null) {
						logger.warn("Disc not found: Artist: "
							+ cue.getFlat(GenericTag.ARTIST) + "\tAlbum: "
							+ cue.getFlat(GenericTag.ALBUM));
					} else {
						logger.info("Disc found: Artist: " + cue.getFlat(GenericTag.ARTIST)
							+ "\tAlbum: " + cue.getFlat(GenericTag.ALBUM));
						choice.add(GenericTag.DISC_NUMBER, Integer.toString(cue.getNum()));
						album.getDiscs().add(choice);
					}
				}
				
				libAlbum.setAlbum(album);
				try {
					albumDao.write(new File(libAlbum.getDir(), "album.xml"), album);
				} catch (Exception e) {
					logger.error("Problem writing album.xml", e);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * @throws IOException if there is a problem processing
	 * the directories
	 */
	@Override
	public void scanAll() throws IOException {
		for (Library lib : getRefLibs()) {
			lib.scanAlbums();
		}
		for (Library lib : getDestLibs()) {
			lib.scanAlbums();
		}
	}
}
