package org.mulima.internal.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.mulima.api.file.Digest;
import org.mulima.api.file.DigestEntry;
import org.mulima.api.file.FileComposer;
import org.mulima.api.file.FileParser;

/**
 * A DAO that parses and composes digest objects
 * to files.
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class DigestDao implements FileParser<Digest>, FileComposer<Digest> {
  private static final String ID_KEY = "id";

  /**
   * Parses the specified digest file.
   * @param file the file to parse
   * @return a digest representing the
   * file contents
   */
  public Digest parse(File file) {
    Properties props = new Properties();
    try (InputStream stream = Files.newInputStream(file.toPath())) {
      props.load(stream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    UUID id = UUID.fromString(props.get(ID_KEY).toString());
    Set<DigestEntry> entries = new HashSet<>();
    props.forEach((key, value) -> {
      if (ID_KEY == key) {
        // do nothing
      } else {
        entries.add(new StoredDigestEntry(key.toString(), value.toString()));
      }
    });
    return new LazyDigest(id, entries);
  }

  /**
   * Composes a digest object to a file.
   * @param file the file to compose to
   * @param digest the digest to compose
   */
  public void compose(File file, Digest digest) {
    if (digest == null) {
      throw new IllegalArgumentException("Digest cannot be null.");
    } else if (digest.getId() == null) {
      throw new IllegalArgumentException("Digest ID cannot be null.");
    }

    Properties props = new Properties();
    props.put(ID_KEY, digest.getId().toString());
    digest.getEntries().forEach(entry -> {
      props.put(entry.getFileName(), entry.toString());
    });
    try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
      props.store(writer, null);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
