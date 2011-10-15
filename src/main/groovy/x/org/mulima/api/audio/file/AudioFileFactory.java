package x.org.mulima.api.audio.file;

import java.io.File;

import x.org.mulima.api.audio.AudioFormat;

public interface AudioFileFactory {
	DiscFile createDiscFile(File file);
	DiscFile createDiscFile(DiscFile source, File newDir, AudioFormat newFormat);
	TrackFile createTrackFile(File file);
	TrackFile createTrackFile(TrackFile source, File newDir, AudioFormat newFormat);
	AudioFile createAudioFile(File file);
	AudioFile createAudioFile(AudioFile source, File newDir, AudioFormat newFormat);
}
