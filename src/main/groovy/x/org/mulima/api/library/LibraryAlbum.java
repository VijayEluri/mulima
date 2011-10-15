package x.org.mulima.api.library;

import java.io.File;
import java.util.Set;
import java.util.UUID;

import x.org.mulima.api.audio.file.AudioFile;
import x.org.mulima.api.file.Digest;

public interface LibraryAlbum {
	UUID getId();
	File getDir();
	Library getLib();
	LibraryAlbum getSource();
	Set<AudioFile> getAudioFiles();
	Digest getDigest();
	Digest getSourceDigest();
	boolean isUpToDate();
	boolean isUpToDate(boolean checkSource);
	boolean isUpToDate(Digest digest);
	boolean isUpToDate(Digest digest, boolean checkSource);
}
