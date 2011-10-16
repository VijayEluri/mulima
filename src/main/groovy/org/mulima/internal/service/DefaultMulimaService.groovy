package org.mulima.internal.service

import java.util.Set

import org.mulima.api.audio.AudioFormat
import org.mulima.api.audio.action.Codec
import org.mulima.api.audio.action.Joiner
import org.mulima.api.audio.action.Splitter
import org.mulima.api.audio.action.Tagger
import org.mulima.api.audio.file.AudioFileFactory
import org.mulima.api.file.CachedFileFactory
import org.mulima.api.file.DigestService
import org.mulima.api.file.FileComposer
import org.mulima.api.file.FileParser
import org.mulima.api.file.TempDir
import org.mulima.api.job.AlbumConversionService
import org.mulima.api.library.Library
import org.mulima.api.library.LibraryAlbum
import org.mulima.api.library.LibraryAlbumFactory
import org.mulima.api.library.ReferenceLibrary
import org.mulima.api.service.MulimaService

class DefaultMulimaService implements MulimaService {
	private final Set<Library> allLibs = [] as Set
	private final Set<ReferenceLibrary> refLibs = [] as Set
	private final Set<Library> destLibs = [] as Set
	private final Map<AudioFormat, Codec> codecs = [:]
	private final Map<AudioFormat, Tagger> taggers = [:]
	private final Map<Class, FileParser> parsers = [:]
	private final Map<Class, FileComposer> composers = [:]
	TempDir tempDir = null
	CachedFileFactory cachedFileFactory = null
	DigestService digestService = null
	AlbumConversionService conversionService = null
	LibraryAlbumFactory libraryAlbumFactory = null
	AudioFileFactory audioFileFactory = null
	Splitter splitter = null
	Joiner joiner = null
	
	@Override
	Set<ReferenceLibrary> getRefLibs() {
		return Collections.unmodifiableSet(refLibs)
	}
	
	void addRefLib(ReferenceLibrary refLib) {
		this.refLibs << refLib
		this.allLibs << refLib
	}
	
	@Override
	Set<Library> getDestLibs() {
		return Collections.unmodifiableSet(destLibs)
	}
	
	void addDestLib(Library lib) {
		this.destLibs << lib
		this.allLibs << lib
	}
	
	@Override
	Library getLibFor(File dir) {
		allLibs.each { lib ->
			File rootDir = lib.rootDir
			File temp = dir
			while (temp.parentFile) {
				temp = temp.parentFile
				if (temp == rootDir) {
					return lib
				}
			}
		}
		return null
	}

	@Override
	LibraryAlbum getAlbumById(UUID id) {
		allLibs.each { lib ->
			LibraryAlbum album = lib.getById(id);
			if (album != null) {
				return album;
			}
		}
		return null;
	}
	
	Codec getCodec(AudioFormat format) {
		return codecs[format]
	}
	
	void registerCodec(Codec codec) {
		codecs[codec.format] = codec
	}
	
	Tagger getTagger(AudioFormat format) {
		return taggers[format]
	}
	
	void registerTagger(Tagger tagger) {
		taggers[tagger.format] = tagger
	}
	
	public <T> FileParser<T> getParser(Class<T> type) {
		return parsers[type]
	}
	
	public <T> void registerParser(Class<T> type, FileParser<T> parser) {
		parsers[type] = parser
	}
	
	public <T> FileComposer<T> getComposer(Class<T> type) {
		return composers[type]
	}
	
	public <T> void registerComposer(Class<T> type, FileComposer<T> composer) {
		composers[type] = composer
	}
}
