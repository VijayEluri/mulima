package com.andrewoberstar.library.audio;

import java.util.concurrent.Callable;

import com.andrewoberstar.library.meta.Metadata;

public interface MetadataUtil {
	AudioFile write(AudioFile file, Metadata meta);
	Metadata read(AudioFile file);
	Callable<AudioFile> writeLater(AudioFile file, Metadata meta);
	Callable<Metadata> readLater(AudioFile file);
}
