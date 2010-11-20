package com.andrewoberstar.library.audio.impl;

import java.util.HashMap;
import java.util.Map;

import com.andrewoberstar.library.audio.AudioFile;
import com.andrewoberstar.library.audio.AudioFileType;
import com.andrewoberstar.library.audio.AudioFileUtil;
import com.andrewoberstar.library.audio.Codec;
import com.andrewoberstar.library.audio.CodecConfig;
import com.andrewoberstar.library.audio.MetadataUtil;

public class CodecConfigImpl implements CodecConfig {
	private Map<AudioFileType, Codec> codecs = new HashMap<AudioFileType, Codec>();
	private Map<AudioFileType, MetadataUtil> metaUtils = new HashMap<AudioFileType, MetadataUtil>();
	private AudioFileUtil audioUtil = null;
	
	/**
	 * @param codecs the codecs to set
	 */
	public void setCodecs(Map<AudioFileType, Codec> codecs) {
		this.codecs = codecs;
	}

	/**
	 * @param metaUtils the metaUtils to set
	 */
	public void setMetaUtils(Map<AudioFileType, MetadataUtil> metaUtils) {
		this.metaUtils = metaUtils;
	}

	/**
	 * @param audioUtil the audioUtil to set
	 */
	public void setAudioUtil(AudioFileUtil audioUtil) {
		this.audioUtil = audioUtil;
	}

	@Override
	public Codec getCodec(AudioFileType type) {
		return codecs.get(type);
	}

	@Override
	public Codec getCodec(AudioFile file) {
		return getCodec(file.getType());
	}

	@Override
	public MetadataUtil getMetadataUtil(AudioFileType type) {
		return metaUtils.get(type);
	}

	@Override
	public MetadataUtil getMetadataUtil(AudioFile file) {
		return getMetadataUtil(file.getType());
	}

	@Override
	public AudioFileUtil getAudioFileUtil() {
		return audioUtil;
	}

}
