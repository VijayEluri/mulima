package org.mulima.internal.audio.tool;

import java.util.HashMap;
import java.util.Map;

import org.mulima.api.audio.AudioFormat;
import org.mulima.api.audio.tool.Codec;
import org.mulima.api.audio.tool.Joiner;
import org.mulima.api.audio.tool.Splitter;
import org.mulima.api.audio.tool.Tagger;
import org.mulima.api.audio.tool.ToolService;
import org.springframework.stereotype.Service;

@Service
public class DefaultToolService implements ToolService {
	private final Map<AudioFormat, Codec> codecs = new HashMap<AudioFormat, Codec>();
	private final Map<AudioFormat, Tagger> taggers = new HashMap<AudioFormat, Tagger>();
	private Splitter splitter = null;
	private Joiner joiner = null;
	
	@Override
	public Codec getCodec(AudioFormat type) {
		return codecs.get(type);
	}
	
	public void registerCodec(Codec codec) {
		codecs.put(codec.getFormat(), codec);
	}

	@Override
	public Tagger getTagger(AudioFormat type) {
		return taggers.get(type);
	}
	
	public void registerTagger(Tagger tagger) {
		taggers.put(tagger.getFormat(), tagger);
	}

	@Override
	public Splitter getSplitter() {
		return splitter;
	}
	
	public void registerSplitter(Splitter splitter) {
		this.splitter = splitter;
	}

	@Override
	public Joiner getJoiner() {
		return joiner;
	}
	
	public void registerJoiner(Joiner joiner) {
		this.joiner = joiner;
	}
}
