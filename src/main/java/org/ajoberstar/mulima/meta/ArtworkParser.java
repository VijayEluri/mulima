package org.ajoberstar.mulima.meta;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ArtworkParser implements MetadataParser {
    @Override
    public boolean accepts(Path file) {
        return file.endsWith("folder.jpg") || file.endsWith("folder.png");
    }

    @Override
    public CompletionStage<Metadata> parse(Path file) {
        var meta = Metadata.builder("generic")
                .setArtworkFile(file)
                .build();

        return CompletableFuture.completedStage(meta);
    }
}
