package org.mulima.internal.file;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.mulima.api.file.CachedFile;
import org.mulima.api.file.CachedFileFactory;
import org.mulima.api.service.MulimaService;


public class DefaultCachedFileFactory implements CachedFileFactory {
	private final MulimaService service;
	private final Map<Class<?>, Map<File, CachedFile<?>>> caches = new HashMap<Class<?>, Map<File, CachedFile<?>>>();

	public DefaultCachedFileFactory(MulimaService service) {
		this.service = service;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> CachedFile<T> valueOf(File file, Class<T> type) {
		Map<File, CachedFile<?>> cache;
		if (caches.containsKey(type)) {
			cache = caches.get(type);
		} else {
			cache = new HashMap<File, CachedFile<?>>();
			caches.put(type, cache);
		}
		
		CachedFile<T> cachedFile;
		if (cache.containsKey(file)) {
			cachedFile = (CachedFile<T>) cache.get(file);
		} else {
			cachedFile = new DefaultCachedFile<T>(service.getParser(type), file);
			cache.put(file, cachedFile);
		}
		return cachedFile;
	}
}
