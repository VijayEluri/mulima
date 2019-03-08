package org.ajoberstar.mulima.meta;

import java.nio.file.Path;
import java.util.concurrent.CompletionStage;

public interface MetadataWriter {
    boolean accepts(Path file);
    CompletionStage<Void> write(Metadata meta, Path file);
}
