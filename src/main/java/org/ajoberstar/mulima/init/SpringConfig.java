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
import org.ajoberstar.mulima.audio.Flac;
import org.ajoberstar.mulima.audio.OpusEnc;
import org.ajoberstar.mulima.meta.CueSheet;
import org.ajoberstar.mulima.meta.MetadataParser;
import org.ajoberstar.mulima.meta.MetadataWriter;
import org.ajoberstar.mulima.meta.Metaflac;
import org.ajoberstar.mulima.meta.OpusInfo;
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
      @Override
      public String get(String key) {
        return env.getProperty("micrometer." + key);
      }
    };
    var registry = InfluxMeterRegistry.builder(config)
        .clock(Clock.SYSTEM)
        .build();

    Metrics.addRegistry(registry);
    Metrics.globalRegistry.config().commonTags(
        "application", "mulima",
        "execution", UUID.randomUUID().toString());
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
  public CueSheet cueSheet() {
    return new CueSheet();
  }

  @Bean
  public MetadataService metadata(CueSheet cueSheet, Metaflac metaflac, OpusInfo opusInfo) {
    return new MetadataService(cueSheet, metaflac, opusInfo);
  }

  @Bean
  public Metaflac metaflac(ProcessService process) {
    return new Metaflac(env.getProperty("metaflac.path", "metaflac"), process);
  }

  @Bean
  public Flac flac(ProcessService process, Metaflac metaflac) {
    return new Flac(env.getProperty("flac.path", "flac"), Integer.parseInt(env.getProperty("flac.compressionLevel", "5")), env.getProperty("shntool.path", "shntool"), process, metaflac);
  }

  @Bean
  public OpusInfo opusinfo(ProcessService process) {
    return new OpusInfo(env.getProperty("opusinfo.path", "opusinfo"), process);
  }

  @Bean
  public OpusEnc opusenc(ProcessService process) {
    return new OpusEnc(env.getProperty("opusenc.path", "opusenc"), Integer.parseInt(env.getProperty("opusenc.bitrate", "96")), process);
  }

  @Bean
  public MusicBrainzService musicbrainz(Metaflac metaflac) {
    var appdata = Paths.get(System.getenv("APPDATA"));
    if (appdata.isAbsolute()) {
      return new MusicBrainzService(HttpClient.newHttpClient(), metaflac, appdata.resolve("mulima").resolve("musicbrainz-cache"));
    } else {
      throw new IllegalArgumentException("APPDATA environment variable is not an absolute path.");
    }
  }

  @Bean
  public LibraryService library(MetadataService metadata, MusicBrainzService musicbrainz, Flac flac, OpusEnc opusenc) {
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
