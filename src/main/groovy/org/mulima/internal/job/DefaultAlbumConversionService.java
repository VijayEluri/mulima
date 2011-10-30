package org.mulima.internal.job;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.mulima.api.job.AlbumConversionService;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.service.MulimaService;

/**
 * Default implementation of an album conversion service.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class DefaultAlbumConversionService implements AlbumConversionService {
	private final MulimaService service;
	private final ExecutorService executor;
	
	/**
	 * Constructs a service from the parameters.
	 * @param service the service to use during conversion execution
	 */
	public DefaultAlbumConversionService(MulimaService service) {
		this.service = service;
		this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Void> submit(LibraryAlbum source, Set<LibraryAlbum> dests) {
		executor.submit(new AlbumConversionJob(service, source, dests));
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void shutdown() {
		executor.shutdown();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Runnable> shutdownNow() {
		return executor.shutdownNow();
	}
}
