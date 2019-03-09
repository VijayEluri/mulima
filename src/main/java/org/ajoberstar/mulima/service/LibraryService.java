package org.ajoberstar.mulima.service;

import org.ajoberstar.mulima.audio.FlacCodec;
import org.ajoberstar.mulima.audio.OpusEncoder;
import org.ajoberstar.mulima.meta.AlbumXmlParser;
import org.ajoberstar.mulima.meta.CueSheetParser;
import org.ajoberstar.mulima.meta.Metadata;
import org.ajoberstar.mulima.meta.MetaflacTagger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public final class LibraryService {
    private static final Logger logger = LogManager.getLogger(LibraryService.class);
//    private final AlbumXmlParser albumXmlParser;
//    private final CueSheetParser cueSheetParser;
//    private final FlacCodec flac;
//    private final OpusEncoder opusenc;
//    private final MetaflacTagger metaflac;


    public void convert(Path sourceDir, Path losslessDestDir, Path lossyDestDir) {

    }

    public void handleDirectory(Path albumDir) {
//        try {
//            Files.
//
//            Optional<CompletionStage<Metadata>> albumXml = Files.list(albumDir)
//                    .filter(albumXmlParser::accepts)
//                    .map(albumXmlParser::parse)
//                    .findAny();
//
//            Optional<CompletionStage<Metadata>> cueSheet = Files.list(albumDir)
//                    .filter(cueSheetParser::accepts)
//                    .map(cueSheetParser::parse)
//                    .findAny();
//
//            var artwork = Files.list(albumDir)
//                    .
//        } catch (IOException e) {
//            throw new UncheckedIOException(e);
//        }

    }


    public List<Metadata> scan(Path dir) {
        // TODO implement
        return Collections.emptyList();
    }

    public boolean isUpToDate(Metadata album) {
        // TODO implement
        return false;
    }
}
