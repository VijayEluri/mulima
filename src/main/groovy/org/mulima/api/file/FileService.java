package org.mulima.api.file;

import java.io.File;
import java.io.FileFilter;

import org.mulima.api.file.audio.AudioFile;
import org.mulima.api.file.audio.AudioFormat;
import org.mulima.api.file.audio.DiscFile;
import org.mulima.api.file.audio.TrackFile;

public interface FileService {
	<T> FileParser<T> getParser(Class<T> type);
	<T> FileComposer<T> getComposer(Class<T> type);
	<T> CachedFile<T> createCachedFile(Class<T> type, File file);
	<T> CachedDir<T> createCachedDir(Class<T> type, File dir);
	<T> CachedDir<T> createCachedDir(Class<T> type, File dir, FileFilter filter);
	DiscFile createDiscFile(File file);
	DiscFile createDiscFile(DiscFile source, File newDir, AudioFormat newFormat);
	TrackFile createTrackFile(File file);
	TrackFile createTrackFile(TrackFile source, File newDir, AudioFormat newFormat);
	AudioFile createAudioFile(File file);
	AudioFile createAudioFile(AudioFile source, File newDir, AudioFormat newFormat);
}
