package org.mulima.job;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mulima.library.LibraryAlbum;
import org.mulima.service.MulimaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Default implementation of an album conversion service.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
@Service
public class AlbumConversionService {
  private final Logger logger = LogManager.getLogger(AlbumConversionService.class);
  private final MulimaService service;
  private final ExecutorService executor;

  /**
   * Constructs a service from the parameters.
   *
   * @param service the service to use during conversion execution
   */
  @Autowired
  public AlbumConversionService(MulimaService service) {
    this.service = service;
    this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
  }

  /**
   * Submits a conversion from {@code source} to {@code dests}.
   *
   * @param source the source album
   * @param dests the destination albums
   * @return a future representing the execution of the conversion
   */
  public Future<Boolean> submit(LibraryAlbum source, Set<LibraryAlbum> dests) {
    logger.debug("Submitting conversion for: " + source.getName());
    return executor.submit(new AlbumConversionJob(service, source, dests));
  }

  /** Shuts down the service. */
  public void shutdown() {
    executor.shutdown();
  }

  /**
   * Shuts down the service and waits for termination.
   *
   * @param timeout the maximum time to wait
   * @param unit the time unit of the timeout arg
   */
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
   * Shuts the service down now.
   *
   * @return runnables that were still executing
   */
  public List<Runnable> shutdownNow() {
    List<Runnable> tasks = executor.shutdownNow();
    if (!executor.isTerminated()) {
      logger.warn("Conversion service did not terminate.");
    }
    return tasks;
  }
}
