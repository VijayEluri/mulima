package x.org.mulima.internal.job;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import x.org.mulima.api.MulimaService;
import x.org.mulima.api.job.AlbumConversionService;
import x.org.mulima.api.library.LibraryAlbum;

public class DefaultAlbumConversionService implements AlbumConversionService {
	private final MulimaService service;
	private final ExecutorService executor;
	
	public DefaultAlbumConversionService(MulimaService service) {
		this.service = service;
		this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	}
	
	@Override
	public Future<Void> submit(LibraryAlbum source, Set<LibraryAlbum> dests) {
		executor.submit(new AlbumConversionJob(service, source, dests));
		return null;
	}
	
	public void shutdown() {
		executor.shutdown();
	}
	
	public List<Runnable> shutdownNow() {
		return executor.shutdownNow();
	}
}
