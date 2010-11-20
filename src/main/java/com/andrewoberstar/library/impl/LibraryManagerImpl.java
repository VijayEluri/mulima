package com.andrewoberstar.library.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.andrewoberstar.library.AlbumFolder;
import com.andrewoberstar.library.Library;
import com.andrewoberstar.library.LibraryManager;
import com.andrewoberstar.library.ReferenceLibrary;
import com.andrewoberstar.library.audio.AudioConversionService;
import com.andrewoberstar.library.audio.CodecConfig;
import com.andrewoberstar.library.audio.CodecService;

public class LibraryManagerImpl implements LibraryManager {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private ReferenceLibrary refLib = null;
	private List<Library> libs = new ArrayList<Library>();
	private AudioConversionService service;

	public ReferenceLibrary getRefLib() {
		return refLib;
	}
	
	public void setRefLib(ReferenceLibrary refLib) {
		this.refLib = refLib;
	}

	public void setLibs(List<Library> libs) {
		this.libs = libs;
	}
	
	public void setCodecConfig(CodecConfig config) {
		CodecService codecSrv = new CodecService(config);
		service = new AudioConversionService(codecSrv);
	}
	
	/**
	 * @param service to set
	 */
	public void setService(AudioConversionService service) {
		this.service = service;
	}

	@Override
	public void updateLibraries() {
		List<AlbumFolder> refAlbums = refLib.getAllAlbums();
		List<Future<List<AlbumFolder>>> futures = new ArrayList<Future<List<AlbumFolder>>>();
		for (AlbumFolder refFolder : refAlbums) {
			futures.add(service.submitConvert(refFolder, libs));
		}
		
		for (Future<List<AlbumFolder>> future : futures) {
			try {
				future.get();
			} catch (ExecutionException e) {
				logger.error("Error converting folder.", e.getCause());
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
