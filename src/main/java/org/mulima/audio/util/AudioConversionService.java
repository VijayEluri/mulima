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
package org.mulima.audio.util;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.mulima.library.LibraryAlbum;

/**
 * Service that allows the conversion of one library album
 * to its destination libraries.
 */
public class AudioConversionService {
	private static AudioConversionService instance = null;
	private ExecutorService executor;
	private CodecService codecSrv;
	
	/**
	 * Constructs a service without a <code>CodecService</code>.
	 */
	protected AudioConversionService() {
		this(CodecService.getInstance());
	}
	
	/**
	 * Constructs a service with the specified <code>CodecService</code>.
	 * @param codecSrv codec service to use for underlying conversions
	 */
	protected AudioConversionService(CodecService codecSrv) {
		this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		this.codecSrv = codecSrv;
	}
	
	public static AudioConversionService getInstance() {
		if (instance == null) {
			instance = new AudioConversionService();
		}
		return instance;
	}
	
	/**
	 * Sets the codec service to use for underlying conversions.
	 * @param codecSrv the codec service
	 */
	public void setCodecSrv(CodecService codecSrv) {
		this.codecSrv = codecSrv;
	}
	
	/**
	 * Submits a conversion of the specified library album to the
	 * destinations.  Uses the underlying codec service.
	 * @param refAlbum the library album to convert
	 * @param destAlbums the destinations for the converted album
	 * @return a future list of library albums
	 */
	public Future<List<LibraryAlbum>> submitConvert(LibraryAlbum refAlbum, List<LibraryAlbum> destAlbums) {
		return executor.submit(new AudioConversion(codecSrv, refAlbum, destAlbums));
	}
	
	/**
	 * Initiates an orderly shutdown in which previously submitted tasks
	 * are executed, but no new tasks will be accepted. Invocation has 
	 * no additional effect if already shut down.
	 */
	public void shutdown() {
		executor.shutdown();
		codecSrv.shutdown();
	}
	
	/**
	 * Attempts to stop all actively executing tasks, halts the processing of 
	 * waiting tasks, and returns a list of the tasks that were awaiting execution. 
	 * There are no guarantees beyond best-effort attempts to stop processing 
	 * actively executing tasks. For example, typical implementations will 
	 * cancel via Thread.interrupt, so any task that fails to respond to interrupts 
	 * may never terminate.
	 * @return a list of all tasks that weren't executed
	 */
	public List<Runnable> shutdownNow() {
		List<Runnable> left = executor.shutdownNow();
		left.addAll(codecSrv.shutdownNow());
		return left;
	}
}
