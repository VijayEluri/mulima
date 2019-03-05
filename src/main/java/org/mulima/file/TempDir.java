package org.mulima.file;

import java.io.File;
import java.util.UUID;

import org.mulima.exception.UncheckedMulimaException;

/**
 * A temporary directory.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class TempDir implements FileHolder {
  private final File dir;

  /** Creates a temporary directory under {@code java.io.tmpdir}. */
  public TempDir() {
    this(new File(System.getProperty("java.io.tmpdir")));
  }

  /**
   * Creates a temporary directory at the specified path.
   *
   * @param path the path to create the dir at
   */
  public TempDir(String path) {
    this(new File(path));
  }

  /**
   * Creates a temporary directory at the specified file.
   *
   * @param tempDir the file to create the dir for
   */
  public TempDir(File tempDir) {
    this.dir = tempDir;
    if (!this.dir.exists() && !this.dir.mkdirs()) {
      throw new UncheckedMulimaException("Could not create temporary directory: " + tempDir);
    }
  }

  /**
   * Creates a temporary directory as a child of the specified temp dir. The created directory will
   * have a random name.
   *
   * @param parent the parent of the temp dir
   */
  public TempDir(TempDir parent) {
    this(parent, UUID.randomUUID().toString());
  }

  /**
   * Creates a temporary directory as a child of the specified dir. The created directory will have
   * the specified name.
   *
   * @param parent the parent of the temp dir
   * @param name the name of the directory
   */
  public TempDir(TempDir parent, String name) {
    this(new File(parent.getFile(), name));
  }

  /**
   * Gets the file this directory is created at.
   *
   * @return the file
   */
  public File getFile() {
    return dir;
  }

  /**
   * Creates a new temporary directory as a child of this one.
   *
   * @return the new temp dir
   */
  public TempDir newChild() {
    return new TempDir(this);
  }

  /**
   * Creates a new temporary directory as a child of this one, with the specified name.
   *
   * @param name the name of the child dir
   * @return the new temp dir
   */
  public TempDir newChild(String name) {
    return new TempDir(this, name);
  }
}
