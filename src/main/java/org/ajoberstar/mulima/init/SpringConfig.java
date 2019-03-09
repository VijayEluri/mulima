package org.ajoberstar.mulima.init;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import org.ajoberstar.mulima.audio.FlacCodec;
import org.ajoberstar.mulima.audio.OpusEncoder;
import org.ajoberstar.mulima.meta.AlbumXmlParser;
import org.ajoberstar.mulima.meta.ArtworkParser;
import org.ajoberstar.mulima.meta.CueSheetParser;
import org.ajoberstar.mulima.meta.MetadataParser;
import org.ajoberstar.mulima.meta.MetadataWriter;
import org.ajoberstar.mulima.meta.MetaflacTagger;
import org.ajoberstar.mulima.meta.OpusInfoParser;
import org.ajoberstar.mulima.service.LibraryService;
import org.ajoberstar.mulima.service.MetadataService;
import org.ajoberstar.mulima.service.MusicBrainzService;
import org.ajoberstar.mulima.service.ProcessService;
import org.ajoberstar.mulima.util.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
  @Bean
  public ProcessService process() {
    var procs = Runtime.getRuntime().availableProcessors();
    var threads = Math.max(procs - 1, 1);
    var executor = Executors.newFixedThreadPool(threads);
    return new ProcessService(executor);
  }

  @Bean
  public AlbumXmlParser albumXml() {
    return new AlbumXmlParser(ForkJoinPool.commonPool());
  }

  @Bean
  public CueSheetParser cueSheet() {
    return new CueSheetParser(ForkJoinPool.commonPool());
  }

  @Bean
  public ArtworkParser artwork() {
    return new ArtworkParser();
  }

  @Bean
  public MetadataService metadata(List<MetadataParser> parsers, List<MetadataWriter> writers) {
    return new MetadataService(parsers, writers);
  }

  @Bean
  public MetaflacTagger metaflac(ProcessService process) {
    return new MetaflacTagger("C:\\Users\\andre\\bin\\metaflac.exe", process);
  }

  @Bean
  public FlacCodec flac(ProcessService process) {
    return new FlacCodec("C:\\Users\\andre\\bin\\flac.exe", 8, "C:\\Users\\andre\\bin\\shntool.exe", process);
  }

  @Bean
  public OpusInfoParser opusinfo(ProcessService process) {
    return new OpusInfoParser("C:\\Users\\andre\\bin\\opusinfo.exe", process);
  }

  @Bean
  public OpusEncoder opusenc(ProcessService process) {
    return new OpusEncoder("C:\\Users\\andre\\bin\\opusenc.exe", 128, process);
  }

  @Bean
  public MusicBrainzService musicbrainz(ProcessService process) {
    return new MusicBrainzService(HttpClients.rateLimited(1_000), process);
  }

  @Bean LibraryService library(MetadataService metadata, MusicBrainzService musicbrainz, FlacCodec flac, OpusEncoder opusenc) {
    return new LibraryService(metadata, musicbrainz, flac, opusenc);
  }
}
