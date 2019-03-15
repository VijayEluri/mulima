package org.ajoberstar.mulima.init;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.influx.InfluxConfig;
import io.micrometer.influx.InfluxMeterRegistry;
import org.ajoberstar.mulima.MulimaService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.net.http.HttpClient;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

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
    var registry = InfluxMeterRegistry.builder(config)
        .clock(Clock.SYSTEM)
        .build();

    Metrics.addRegistry(registry);
    Metrics.globalRegistry.config().commonTags(
        "application", "mulima",
        "execution", UUID.randomUUID().toString()
    );
    new ClassLoaderMetrics().bindTo(Metrics.globalRegistry);
    new JvmMemoryMetrics().bindTo(Metrics.globalRegistry);
    new JvmGcMetrics().bindTo(Metrics.globalRegistry);
    new ProcessorMetrics().bindTo(Metrics.globalRegistry);
    new JvmThreadMetrics().bindTo(Metrics.globalRegistry);

    return registry;
  }

  @Bean
  public ProcessService process() {
    var procs = Runtime.getRuntime().availableProcessors();
    var threads = Math.max(procs - 1, 1);
    return new ProcessService(threads);
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
  public MusicBrainzService musicbrainz(MetaflacTagger metaflac) {
    return new MusicBrainzService(HttpClient.newHttpClient(), metaflac, Paths.get(System.getenv("APPDATA"), "mulima", "musicbrainz-cache"));
  }

  @Bean
  public LibraryService library(MetadataService metadata, MusicBrainzService musicbrainz, FlacCodec flac, OpusEncoder opusenc) {
    return new LibraryService(metadata, musicbrainz, flac, opusenc);
  }

  @Bean
  public MulimaService mulima(LibraryService library, MetadataService metadata, MusicBrainzService musicbrainz) {
    var sourceDir = Paths.get(env.getProperty("library.source.path"));
    var losslessDir = Paths.get(env.getProperty("library.lossless.path"));
    var lossyDir = Paths.get(env.getProperty("library.lossy.path"));
    return new MulimaService(library, metadata, musicbrainz, sourceDir, losslessDir, lossyDir);
  }
}
