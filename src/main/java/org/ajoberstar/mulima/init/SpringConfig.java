package org.ajoberstar.mulima.init;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import io.micrometer.influx.InfluxConfig;
import io.micrometer.influx.InfluxMeterRegistry;
import org.ajoberstar.mulima.audio.FlacCodec;
import org.ajoberstar.mulima.audio.OpusEncoder;
import org.ajoberstar.mulima.meta.AlbumXmlParser;
import org.ajoberstar.mulima.meta.ArtworkParser;
import org.ajoberstar.mulima.meta.CueSheetParser;
import org.ajoberstar.mulima.meta.MetadataParser;
import org.ajoberstar.mulima.meta.MetadataWriter;
import org.ajoberstar.mulima.meta.MetadataYaml;
import org.ajoberstar.mulima.meta.MetaflacTagger;
import org.ajoberstar.mulima.meta.OpusInfoParser;
import org.ajoberstar.mulima.service.LibraryService;
import org.ajoberstar.mulima.service.MetadataService;
import org.ajoberstar.mulima.service.MusicBrainzService;
import org.ajoberstar.mulima.service.ProcessService;
import org.ajoberstar.mulima.util.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.concurrent.Executors;

@Configuration
@PropertySource("file:///${APPDATA}/mulima/mulima.properties")
public class SpringConfig {
  @Autowired
  private Environment env;

  @Bean
  public MeterRegistry influx() {
    var config = new InfluxConfig() {
      @Override public String get(String key) {
        return env.getProperty("micrometer." + key);
      }
    };
    return InfluxMeterRegistry.builder(config)
        .clock(Clock.SYSTEM)
        .build();
  }

  @Bean
  public ProcessService process() {
    var procs = Runtime.getRuntime().availableProcessors();
    var threads = Math.max(procs - 1, 1);
    var executor = Executors.newFixedThreadPool(threads);
    ExecutorServiceMetrics.monitor(Metrics.globalRegistry, executor, "process-pool");
    return new ProcessService(executor);
  }

  @Bean
  public AlbumXmlParser albumXml() {
    return new AlbumXmlParser();
  }

  @Bean
  public CueSheetParser cueSheet() {
    return new CueSheetParser();
  }

  @Bean
  public ArtworkParser artwork() {
    return new ArtworkParser();
  }

  @Bean
  public MetadataYaml yaml() {
    return new MetadataYaml();
  }

  @Bean
  public MetadataService metadata(List<MetadataParser> parsers, List<MetadataWriter> writers) {
    return new MetadataService(parsers, writers);
  }

  @Bean
  public MetaflacTagger metaflac(ProcessService process) {
    return new MetaflacTagger(env.getProperty("metaflac.path", "metaflac"), process);
  }

  @Bean
  public FlacCodec flac(ProcessService process, MetaflacTagger metaflac) {
    return new FlacCodec(env.getProperty("flac.path", "flac"), Integer.parseInt(env.getProperty("flac.compressionLevel", "5")), env.getProperty("shntool.path", "shntool"), process, metaflac);
  }

  @Bean
  public OpusInfoParser opusinfo(ProcessService process) {
    return new OpusInfoParser(env.getProperty("opusinfo.path", "opusinfo"), process);
  }

  @Bean
  public OpusEncoder opusenc(ProcessService process) {
    return new OpusEncoder(env.getProperty("opusenc.path", "opusenc"), Integer.parseInt(env.getProperty("opusenc.bitrate", "96")), process);
  }

  @Bean
  public MusicBrainzService musicbrainz(ProcessService process) {
    return new MusicBrainzService(HttpClients.rateLimited(5_000), env.getProperty("metaflac.path", "metaflac"), process);
  }

  @Bean
  LibraryService library(MetadataService metadata, MusicBrainzService musicbrainz, FlacCodec flac, OpusEncoder opusenc) {
    return new LibraryService(metadata, musicbrainz, flac, opusenc);
  }
}
