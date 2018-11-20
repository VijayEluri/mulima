package org.mulima.internal.audio.tool;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.mulima.api.audio.tool.Codec;
import org.mulima.api.audio.tool.Joiner;
import org.mulima.api.audio.tool.Splitter;
import org.mulima.api.audio.tool.Tagger;
import org.mulima.api.audio.tool.ToolService;
import org.mulima.api.file.audio.AudioFormat;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired(required = false)
  public void setCodecs(Collection<Codec> codecs) {
    for (Codec codec : codecs) {
      registerCodec(codec);
    }
  }

  @Override
  public Tagger getTagger(AudioFormat type) {
    return taggers.get(type);
  }

  public void registerTagger(Tagger tagger) {
    taggers.put(tagger.getFormat(), tagger);
  }

  @Autowired(required = false)
  public void setTaggers(Collection<Tagger> taggers) {
    for (Tagger tagger : taggers) {
      registerTagger(tagger);
    }
  }

  @Override
  public Splitter getSplitter() {
    return splitter;
  }

  public void registerSplitter(Splitter splitter) {
    this.splitter = splitter;
  }

  @Autowired(required = false)
  public void setSplitter(Splitter splitter) {
    registerSplitter(splitter);
  }

  @Override
  public Joiner getJoiner() {
    return joiner;
  }

  public void registerJoiner(Joiner joiner) {
    this.joiner = joiner;
  }

  @Autowired(required = false)
  public void setJoiner(Joiner joiner) {
    registerJoiner(joiner);
  }
}
