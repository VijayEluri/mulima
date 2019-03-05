package org.mulima.audio.tool;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.mulima.file.audio.AudioFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ToolService {
  private final Map<AudioFormat, Codec> codecs = new HashMap<>();
  private final Map<AudioFormat, Tagger> taggers = new HashMap<>();
  private Splitter splitter = null;
  private Joiner joiner = null;

  public Codec getCodec(AudioFormat type) {
    return codecs.get(type);
  }

  @Autowired(required = false)
  public void setCodecs(Collection<Codec> codecs) {
    for (var codec : codecs) {
      this.codecs.put(codec.getFormat(), codec);
    }
  }

  public Tagger getTagger(AudioFormat type) {
    return taggers.get(type);
  }

  @Autowired(required = false)
  public void setTaggers(Collection<Tagger> taggers) {
    for (var tagger : taggers) {
      this.taggers.put(tagger.getFormat(), tagger);
    }
  }

  public Splitter getSplitter() {
    return splitter;
  }

  @Autowired(required = false)
  public void setSplitter(Splitter splitter) {
    this.splitter = splitter;
  }

  public Joiner getJoiner() {
    return joiner;
  }

  @Autowired(required = false)
  public void setJoiner(Joiner joiner) {
    this.joiner = joiner;
  }
}
