package com.andrewoberstar.library.audio;

public interface CodecConfig {
	Codec getCodec(AudioFileType type);
	Codec getCodec(AudioFile file);
	MetadataUtil getMetadataUtil(AudioFileType type);
	MetadataUtil getMetadataUtil(AudioFile file);
	AudioFileUtil getAudioFileUtil();
}
