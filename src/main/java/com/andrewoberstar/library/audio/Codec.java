package com.andrewoberstar.library.audio;

import java.util.concurrent.Callable;

public interface Codec {
	AudioFile encode(AudioFile source, AudioFile dest) throws Exception;
	AudioFile decode(AudioFile source, AudioFile dest) throws Exception;
	Callable<AudioFile> encodeLater(AudioFile source, AudioFile dest);
	Callable<AudioFile> decodeLater(AudioFile source, AudioFile dest);
}
