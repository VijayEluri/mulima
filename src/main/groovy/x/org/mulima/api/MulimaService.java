package x.org.mulima.api;

import java.io.File;
import java.util.Set;
import java.util.UUID;

import x.org.mulima.api.audio.AudioFormat;
import x.org.mulima.api.audio.action.Codec;
import x.org.mulima.api.audio.action.Joiner;
import x.org.mulima.api.audio.action.Splitter;
import x.org.mulima.api.audio.action.Tagger;
import x.org.mulima.api.audio.file.AudioFileFactory;
import x.org.mulima.api.file.DigestService;
import x.org.mulima.api.file.FileComposer;
import x.org.mulima.api.file.FileParser;
import x.org.mulima.api.file.TempDir;
import x.org.mulima.api.job.AlbumConversionService;
import x.org.mulima.api.library.Library;
import x.org.mulima.api.library.LibraryAlbum;
import x.org.mulima.api.library.LibraryAlbumFactory;
import x.org.mulima.api.library.ReferenceLibrary;

public interface MulimaService {
	Set<ReferenceLibrary> getRefLibs();
	Set<Library> getDestLibs();
	Library getLibFor(File dir);
	LibraryAlbum getAlbumById(UUID id);
	TempDir getTempDir();
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
