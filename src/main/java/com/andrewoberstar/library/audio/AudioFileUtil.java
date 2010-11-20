package com.andrewoberstar.library.audio;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

import com.andrewoberstar.library.meta.CueSheet;

public interface AudioFileUtil {
	List<AudioFile> split(AudioFile image, CueSheet cue, File destDir) throws Exception;
	AudioFile join(List<AudioFile> files, AudioFile dest) throws Exception;
	Callable<List<AudioFile>> splitLater(AudioFile image, CueSheet cue, File destDir);
	Callable<AudioFile> joinLater(List<AudioFile> files, AudioFile dest);
}
