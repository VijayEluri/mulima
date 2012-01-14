package org.mulima.internal.library;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.mulima.api.freedb.FreeDbDao;
import org.mulima.api.job.AlbumConversionService;
import org.mulima.api.library.Library;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.library.LibraryManager;
import org.mulima.api.library.ReferenceLibrary;
import org.mulima.api.meta.Album;
import org.mulima.api.meta.CuePoint;
import org.mulima.api.meta.CueSheet;
import org.mulima.api.meta.Disc;
import org.mulima.api.meta.GenericTag;
import org.mulima.api.meta.Track;
import org.mulima.api.service.MulimaService;
import org.mulima.internal.meta.DefaultAlbum;
import org.mulima.internal.proc.FutureHandler;
import org.mulima.internal.ui.Chooser;
import org.mulima.internal.ui.DiscCliChooser;
import org.mulima.util.MetadataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
	public void processNew(boolean prompt) {
		for (ReferenceLibrary refLib : service.getLibraryService().getRefLibs()) {
			for (LibraryAlbum refAlbum : refLib.getNew()) {
				Album album = refAlbum.getAlbum();
				if (album == null) {
					album = new DefaultAlbum();
					for (CueSheet cue : refAlbum.getCueSheets()) {
						LOGGER.debug("Searching for: Cue: DiscId: " + cue.getFlat(GenericTag.CDDB_ID)
								+ "\tArtist: " + cue.getFlat(GenericTag.ARTIST) 
								+ "\tAlbum: " + cue.getFlat(GenericTag.ALBUM));
						
						List<String> cddbIds = cue.getAll(GenericTag.CDDB_ID);
						List<Disc> candidates = freeDbDao.getDiscsById(cddbIds);
	
						int min = Integer.MAX_VALUE;
						Disc choice = null;
						for (Disc cand : candidates) {
							int dist = MetadataUtil.discDistance(cue, cand);
							if (dist < min) {
								min = dist;
								choice = cand;
							}
						}
						
						if (!candidates.isEmpty() && min > 10) {
							if (prompt) {
								Chooser<Disc> chooser = new DiscCliChooser(cue);
								choice = chooser.choose(candidates);
							} else {
								choice = null;
							}
						}
						
						if (choice == null) {
							LOGGER.warn("Disc not found: Artist: "
								+ cue.getFlat(GenericTag.ARTIST) + "\tAlbum: "
								+ cue.getFlat(GenericTag.ALBUM));
						} else {
							LOGGER.debug("Disc found: Artist: " + cue.getFlat(GenericTag.ARTIST)
								+ "\tAlbum: " + cue.getFlat(GenericTag.ALBUM));
							choice.add(GenericTag.DISC_NUMBER, Integer.toString(cue.getNum()));
							for (Track track : choice.getTracks()) {
								CuePoint startPoint = null;
								for (CuePoint point : cue.getCuePoints()) {
									if (point.getTrack() == track.getNum()) {
										startPoint = point;
									}
								}
								track.setStartPoint(startPoint);
							}
							album.getDiscs().add(choice);
						}
					}
				}
				if (album.getDiscs().size() == refAlbum.getCueSheets().size()) {
					service.getFileService().getComposer(Album.class).compose(new File(refAlbum.getDir(), "album.xml"), album);
					service.getDigestService().write(refAlbum, null);
				}
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
				if (refAlbum.getId() == null) {
					LOGGER.debug("Skipping {}.  It has no ID.", refAlbum.getName());
					continue;
				}
				Set<LibraryAlbum> destAlbums = new HashSet<LibraryAlbum>();
				for (Library destLib : libs) {
					destAlbums.add(destLib.getSourcedFrom(refAlbum));
				}
				futures.add(conversionService.submit(refAlbum, destAlbums));	
			}
		}
		
		try {
			new FutureHandler().handle("Conversion", futures);
		} catch (InterruptedException e) {
			conversionService.shutdownNow();
			Thread.currentThread().interrupt();
		} finally {
			conversionService.shutdown(5, TimeUnit.SECONDS);
		}
	}
}
