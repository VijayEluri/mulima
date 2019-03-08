package org.ajoberstar.mulima.init;

import org.ajoberstar.mulima.meta.AlbumXmlParser;
import org.ajoberstar.mulima.meta.CueSheetParser;
import org.ajoberstar.mulima.meta.MetaflacTagger;
import org.ajoberstar.mulima.meta.NeroAacTagger;
import org.ajoberstar.mulima.service.MusicBrainzService;
import org.ajoberstar.mulima.util.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

@Configuration
public class SpringConfig {
    @Bean
    public ExecutorService commandsExecutor() {
        var procs = Runtime.getRuntime().availableProcessors();
        var threads = Math.max(procs - 1, 1);
        return Executors.newFixedThreadPool(threads);
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
    public MetaflacTagger metaflac(ExecutorService commandsExecutor) {
        return new MetaflacTagger("C:\\Users\\andre\\bin\\metaflac.exe", commandsExecutor);
    }

    @Bean
    public NeroAacTagger neroaactag(ExecutorService commandsExecutor) {
        return new NeroAacTagger("C:\\Users\\andre\\bin\neroaactag.exe", commandsExecutor);
    }

    @Bean
    public MusicBrainzService musicbrainz() {
        return new MusicBrainzService(HttpClients.rateLimited(1_000));
    }
}
