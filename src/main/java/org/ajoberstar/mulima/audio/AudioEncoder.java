package org.ajoberstar.mulima.audio;

import java.nio.file.Path;
import java.util.concurrent.CompletionStage;

public interface AudioEncoder {
    boolean acceptsEncode(Path source, Path destination);
    CompletionStage<Void> encode(Path source, Path destination);
}
