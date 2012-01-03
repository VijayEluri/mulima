package org.mulima.internal.library;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.mulima.api.freedb.FreeDbDao;
import org.mulima.api.job.AlbumConversionService;
import org.mulima.api.library.Library;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.library.LibraryManager;
import org.mulima.api.library.ReferenceLibrary;
import org.mulima.api.meta.Album;
import org.mulima.api.meta.CueSheet;
import org.mulima.api.meta.Disc;
import org.mulima.api.meta.GenericTag;
import org.mulima.api.service.MulimaService;
import org.mulima.internal.meta.DefaultAlbum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import z.org.mulima.library.util.Chooser;
import z.org.mulima.library.util.DiscCliChooser;

/**
 * Default implementation of a library manager.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
@Service
public class DefaultLibraryManager implements LibraryManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLibraryManager.class);
	private final MulimaService service;
	private final AlbumConversionService conversionService;
	private final FreeDbDao freeDbDao;
	
	/**
	 * Constructs a library manager from the parameters.
	 * @param libraryService the service to use when interacting with the libraries
	 * @param conversionService the service to convert albums between formats
	 */
	@Autowired
	public DefaultLibraryManager(MulimaService service, AlbumConversionService conversionService, FreeDbDao freeDbDao) {
		this.service = service;
		this.conversionService = conversionService;
		this.freeDbDao = freeDbDao;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processNew() {
		for (ReferenceLibrary refLib : service.getLibraryService().getRefLibs()) {
			for (LibraryAlbum refAlbum : refLib.getNew()) {
				Album album = new DefaultAlbum();
				for (CueSheet cue : refAlbum.getCueSheets()) {
					LOGGER.info("*** Searching for disc ***");
					LOGGER.info("Cue: DiscId: " + cue.getFlat(GenericTag.CDDB_ID)
							+ "\tArtist: " + cue.getFlat(GenericTag.ARTIST) 
							+ "\tAlbum: " + cue.getFlat(GenericTag.ALBUM));
					
					List<String> cddbIds = cue.getAll(GenericTag.CDDB_ID);
					List<Disc> candidates = freeDbDao.getDiscsById(cddbIds);

					Chooser<Disc> chooser = new DiscCliChooser(cue);
					Disc choice = chooser.choose(candidates);

					if (choice == null) {
						LOGGER.warn("Disc not found: Artist: "
							+ cue.getFlat(GenericTag.ARTIST) + "\tAlbum: "
							+ cue.getFlat(GenericTag.ALBUM));
					} else {
						LOGGER.info("Disc found: Artist: " + cue.getFlat(GenericTag.ARTIST)
							+ "\tAlbum: " + cue.getFlat(GenericTag.ALBUM));
						choice.add(GenericTag.DISC_NUMBER, Integer.toString(cue.getNum()));
						album.getDiscs().add(choice);
					}
				}
				service.getFileService().getComposer(Album.class).compose(new File(refAlbum.getDir(), "album.xml"), album);
				service.getDigestService().write(refAlbum, null);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateAll() {
		update(service.getLibraryService().getDestLibs());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(Library lib) {
		if (!service.getLibraryService().getDestLibs().contains(lib)) {
			throw new IllegalArgumentException("Cannot update a library that doesn't belong to this manager.");
		}
		Set<Library> libs = new HashSet<Library>();
		libs.add(lib);
		update(libs);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(Set<Library> libs) {
		Collection<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
		for (ReferenceLibrary refLib : service.getLibraryService().getRefLibs()) {
			for (LibraryAlbum refAlbum : refLib.getAll()) {
				Set<LibraryAlbum> destAlbums = new HashSet<LibraryAlbum>();
				for (Library destLib : libs) {
					destAlbums.add(destLib.getSourcedFrom(refAlbum));
				}
				futures.add(conversionService.submit(refAlbum, destAlbums));	
			}
		}
		
		boolean cancelAll = false;
		boolean anyRunning = true;
		while (anyRunning) {
			anyRunning = false;
			for (Future<Boolean> future : futures) {
				if (future.isDone()) {
					try {
						future.get();
					} catch (ExecutionException e) {
						LOGGER.error("Conversion failed.", e);
					} catch (InterruptedException e) {
						LOGGER.error("Conversion interrupted.", e);
					}
				} else {
					if (cancelAll) {
						future.cancel(true);
					}
					anyRunning = true;
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				conversionService.shutdownNow();
				Thread.currentThread().interrupt();
			}
		}
		conversionService.shutdown(5, TimeUnit.SECONDS);
	}
}
