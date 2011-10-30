package org.mulima.api.library;

import java.io.File;
import java.util.Set;
import java.util.UUID;

import org.mulima.api.file.Digest;
import org.mulima.api.file.audio.AudioFile;
import org.mulima.api.meta.Album;


public interface LibraryAlbum {
	UUID getId();
	UUID getSourceId();
	File getDir();
	Library getLib();
	Album getAlbum();
	Set<AudioFile> getAudioFiles();
	Digest getDigest();
	Digest getSourceDigest();
}
