package org.mulima.api.audio.file;

import java.io.File;

import org.mulima.api.audio.AudioFormat;
import org.mulima.api.file.FileParser;


public interface AudioFileFactory extends FileParser<AudioFile> {
	DiscFile createDiscFile(File file);
	DiscFile createDiscFile(DiscFile source, File newDir, AudioFormat newFormat);
	TrackFile createTrackFile(File file);
	TrackFile createTrackFile(TrackFile source, File newDir, AudioFormat newFormat);
	AudioFile createAudioFile(File file);
	AudioFile createAudioFile(AudioFile source, File newDir, AudioFormat newFormat);
}
