package org.mulima.api.job;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.mulima.api.library.LibraryAlbum;

/**
 * A service to convert albums between formats.
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public interface AlbumConversionService {
	/**
	 * Submits a conversion from {@code source} to
	 * {@code dests}.
	 * @param source the source album
	 * @param dests the destination albums
	 * @return a future representing the execution of the conversion
	 */
	Future<Boolean> submit(LibraryAlbum source, Set<LibraryAlbum> dests);
	
	/**
	 * Shuts down the service.
	 */
	void shutdown();
	
	/**
	 * Shuts down the service and waits for termination.
	 * @param timeout the maximum time to wait
	 * @param unit the time unit of the timeout arg
	 */
	void shutdown(long timeout, TimeUnit unit);
	
	/**
	 * Shuts the service down now.
	 * @return runnables that were still executing
	 */
	List<Runnable> shutdownNow();
}
