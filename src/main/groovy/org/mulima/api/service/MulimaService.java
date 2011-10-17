package org.mulima.api.service;

import java.io.File;
import java.util.Set;
import java.util.UUID;

import org.mulima.api.audio.AudioFormat;
import org.mulima.api.audio.action.Codec;
import org.mulima.api.audio.action.Joiner;
import org.mulima.api.audio.action.Splitter;
import org.mulima.api.audio.action.Tagger;
import org.mulima.api.audio.file.AudioFileFactory;
import org.mulima.api.file.CachedFileFactory;
import org.mulima.api.file.DigestService;
import org.mulima.api.file.FileComposer;
import org.mulima.api.file.FileParser;
import org.mulima.api.file.TempDir;
import org.mulima.api.job.AlbumConversionService;
import org.mulima.api.library.Library;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.library.LibraryAlbumFactory;
import org.mulima.api.library.ReferenceLibrary;


public interface MulimaService {
	Set<ReferenceLibrary> getRefLibs();
	Set<Library> getDestLibs();
	Library getLibFor(File dir);
	LibraryAlbum getAlbumById(UUID id);
	TempDir getTempDir();
	CachedFileFactory getCachedFileFactory();
	DigestService getDigestService();
	AlbumConversionService getConversionService();
	LibraryAlbumFactory getLibraryAlbumFactory();
	AudioFileFactory getAudioFileFactory();
	Codec getCodec(AudioFormat type);
	Tagger getTagger(AudioFormat type);
	Splitter getSplitter();
	Joiner getJoiner();
	<T> FileParser<T> getParser(Class<T> type);
	<T> FileComposer<T> getComposer(Class<T> type);
}
