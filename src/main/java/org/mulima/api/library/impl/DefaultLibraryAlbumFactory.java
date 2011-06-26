package org.mulima.api.library.impl;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mulima.api.audio.AudioFile;
import org.mulima.api.library.Library;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.library.LibraryAlbumFactory;
import org.mulima.api.meta.Album;
import org.mulima.api.meta.CueSheet;
import org.mulima.api.meta.Disc;
import org.mulima.api.meta.GenericTag;
import org.mulima.job.Context;
import org.mulima.meta.dao.MetadataFileDao;
import org.mulima.util.FileUtil;
import org.mulima.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultLibraryAlbumFactory implements LibraryAlbumFactory {
	private static final Pattern IMAGE_REGEX = Pattern.compile(".*\\(([0-9])\\)\\.[^\\.]+$");
	private static final Pattern TRACK_REGEX = Pattern.compile("^D([0-9]+)T([0-9]+).*");
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private MetadataFileDao<Album> albumDao;
	private MetadataFileDao<CueSheet> cueDao;
	
	public void setAlbumDao(MetadataFileDao<Album> albumDao) {
		this.albumDao = albumDao;
	}
	
	public void setCueDao(MetadataFileDao<CueSheet> cueDao) {
		this.cueDao = cueDao;
	}
	
	@Override
	public LibraryAlbum newAlbum(Library lib, LibraryAlbum libAlbum) {
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
		String relPath = StringUtil.makeSafe(libAlbum.getAlbum().getFlat(GenericTag.ARTIST)).trim()
			+ File.separator + StringUtil.makeSafe(album).trim();
		DefaultLibraryAlbum newAlbum = new DefaultLibraryAlbum();
		newAlbum.setId(UUID.randomUUID());
		newAlbum.setAlbum(libAlbum.getAlbum());
		newAlbum.setLib(lib);
		newAlbum.setDir(new File(lib.getRootDir(), relPath));
		if (!newAlbum.getDir().exists() && !newAlbum.getDir().mkdirs()) {
			logger.error("Problem making directory: " + newAlbum.getDir());
		}
		
		return newAlbum;
	}
	
	@Override
	public LibraryAlbum newAlbum(Library lib, File dir) throws IOException {
		LibraryAlbum libAlbum = new DefaultLibraryAlbum();
		libAlbum.setLib(lib);
		libAlbum.setDir(dir);
		
		Context.getCurrent().getDigestService().readDigests(libAlbum);
		if (libAlbum.getDigest() != null) {
			libAlbum.setId(libAlbum.getDigest().getId());
		}
		
		for (File file : dir.listFiles()) {
			if (lib.getType().isOfType(file)) {
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

}
