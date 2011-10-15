package x.org.mulima.api;

import x.org.mulima.api.audio.AudioFormat;
import x.org.mulima.api.audio.action.Codec;
import x.org.mulima.api.audio.action.Joiner;
import x.org.mulima.api.audio.action.Splitter;
import x.org.mulima.api.audio.action.Tagger;
import x.org.mulima.api.audio.file.AudioFileFactory;
import x.org.mulima.api.file.DigestService;
import x.org.mulima.api.file.TempDir;
import x.org.mulima.api.job.AlbumConversionService;
import x.org.mulima.api.library.LibraryAlbumFactory;

public interface MulimaService {
	TempDir getTempDir();
	DigestService getDigestService();
	AlbumConversionService getConversionService();
	LibraryAlbumFactory getLibraryAlbumFactory();
	AudioFileFactory getAudioFileFactory();
	Codec getCodec(AudioFormat type);
	Tagger getTagger(AudioFormat type);
	Splitter getSplitter();
	Joiner getJoiner();
}
