package org.mulima.future.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mulima.future.meta.Metadata;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class LibraryService {
    private static final Logger logger = LogManager.getLogger(LibraryService.class);

    public List<Metadata> scan(Path dir) {
        // TODO implement
        return Collections.emptyList();
    }

    public boolean isUpToDate(Metadata album) {
        // TODO implement
        return false;
    }

}
