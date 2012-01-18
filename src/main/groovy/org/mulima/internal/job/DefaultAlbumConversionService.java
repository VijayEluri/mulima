package org.mulima.internal.job;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.mulima.api.job.AlbumConversionService;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.service.MulimaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Default implementation of an album conversion service.
 * @author Andrew Oberstar
 * @since 0.1.0
 */
@Service
public class DefaultAlbumConversionService implements AlbumConversionService {
	private final Logger LOGGER = LoggerFactory.getLogger(DefaultAlbumConversionService.class);
	private final MulimaService service;
	private final ExecutorService executor;
	
	/**
	 * Constructs a service from the parameters.
	 * @param service the service to use during conversion execution
	 */
	@Autowired
	public DefaultAlbumConversionService(MulimaService service) {
		this.service = service;
		this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Boolean> submit(LibraryAlbum source, Set<LibraryAlbum> dests) {
		LOGGER.debug("Submitting conversion for: " + source.getName());
		return executor.submit(new AlbumConversionJob(service, source, dests));
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
	public void shutdown(long timeout, TimeUnit unit) {
		shutdown();
		try {
			if (!executor.awaitTermination(timeout, unit)) {
				shutdownNow();
			}
		} catch (InterruptedException e) {
			shutdownNow();
			Thread.currentThread().interrupt();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Runnable> shutdownNow() {
		List<Runnable> tasks = executor.shutdownNow();
		if (!executor.isTerminated()) {
			LOGGER.warn("Conversion service did not terminate.");
		}
		return tasks;
	}
}
