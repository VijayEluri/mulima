package org.mulima.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mulima.api.file.FileHolder;
import org.mulima.exception.UncheckedMulimaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper methods for <code>File</code> operations.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public final class FileUtil {
  private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

  /**
   * This class should never be instantiated.
   *
   * @throws AssertionError always
   */
  private FileUtil() {
    throw new AssertionError("Cannot instantiate this class.");
  }

  /**
   * Returns a file in the same location, but with its extension changed to the one provided.
   *
   * @param original the original file
   * @param extension the new extension
   * @return a new file with specified extension
   */
  public static File changeExtension(FileHolder original, String extension) {
    return changeExtension(original.getFile(), extension);
  }

  /**
   * Returns a file in the same location, but with a different extension than the parameter.
   *
   * @param original the original file
   * @param extension the new extension
   * @return a new file with specified extension
   */
  public static File changeExtension(File original, String extension) {
    String ext = extension.charAt(0) == '.' ? extension : "." + extension;
    String path = original.getPath().replaceAll("\\.[^\\.]+$", ext);
    return new File(path);
  }

  /**
   * Gets the name of the file without the extension.
   *
   * @param file the file to get base name of
   * @return the name without the extension of file
   */
  public static String getBaseName(FileHolder file) {
    return getBaseName(file.getFile());
  }

  /**
   * Gets the name of the file without the extension.
   *
   * @param file the file to get base name of
   * @return the name without the extension of file
   */
  public static String getBaseName(File file) {
    String name = file.getName();
    int index = name.lastIndexOf('.');
    return name.substring(0, index);
  }

  /**
   * Gets the canonical path of a file. If an exception is thrown while getting the path, null is
   * returned.
   *
   * @param file the file to return the path of
   * @return the canonical path of the file, or null if there was an exception
   */
  public static String getSafeCanonicalPath(FileHolder file) {
    return getSafeCanonicalPath(file.getFile());
  }

  /**
   * Gets the canonical path of a file. If an exception is thrown while getting the path, null is
   * returned.
   *
   * @param file the file to return the path of
   * @return the canonical path of the file, or null if there was an exception
   */
  public static String getSafeCanonicalPath(File file) {
    try {
      return file.getCanonicalPath();
    } catch (IOException e) {
      LOGGER.warn("Problem getting canonical path: {}", file.getAbsolutePath());
      return null;
    }
  }

  /**
   * Returns a list of directories underneath this directory. This <strong>is</strong> recursive.
   *
   * @param dir the directory to search
   * @return a list of all child directories
   * @throws IllegalArgumentException if {@code dir} is not a directory
   */
  public static List<File> listDirsRecursive(FileHolder dir) {
    return listDirsRecursive(dir.getFile());
  }

  /**
   * Returns a list of directories underneath this directory. This <strong>is</strong> recursive.
   *
   * @param dir the directory to search
   * @return a list of all child directories
   * @throws IllegalArgumentException if {@code dir} is not a directory
   */
  public static List<File> listDirsRecursive(File dir) {
    // return listDirsRecursive(dir, new ArrayList<File>());
    LOGGER.trace("Beginning listDirsRecursive for {}", dir);
    return listDirsRecursive(dir, new ArrayList<>());
  }

  /**
   * Helper method for recursion of {@link #listDirsRecursive(File)}.
   *
   * @param dir directory to search for child directories
   * @param dirs list of directories to add children to
   * @return list of child directories (including any already included in {@code dirs})
   * @throws IllegalArgumentException if {@code dir} is not a directory
   */
  private static List<File> listDirsRecursive(File dir, List<File> dirs) {
    LOGGER.trace("Beginning listDirsRecursive for {}", dir);
    if (!dir.isDirectory()) {
      throw new IllegalArgumentException("Must pass a directory in as \"dir\".");
    }

    dirs.add(dir);
    for (File child : dir.listFiles()) {
      if (child.isDirectory()) {
        listDirsRecursive(child, dirs);
      }
    }
    LOGGER.trace("Ending listDirsRecursive for {}", dir);
    return dirs;
  }

  /**
   * Deletes a directory and all of its contents.
   *
   * @param dir the directory to delete
   */
  public static void deleteDir(FileHolder dir) {
    deleteDir(dir.getFile());
  }

  /**
   * Deletes a directory and all of its contents.
   *
   * @param dir the directory to delete
   */
  public static void deleteDir(File dir) {
    for (File file : dir.listFiles()) {
      if (file.isDirectory()) {
        deleteDir(file);
      }
      if (file.exists() && !file.delete()) {
        throw new UncheckedMulimaException("Could not delete: " + file);
      }
    }
    if (dir.exists() && !dir.delete()) {
      throw new UncheckedMulimaException("Could not delete: " + dir);
    }
  }

  /**
   * Copies a file to another directory.
   *
   * @param source the file to copy
   * @param dir the directory to copy to
   */
  public static File copy(FileHolder source, File dir) {
    return copy(source.getFile(), dir);
  }

  /**
   * Copies a file to another directory.
   *
   * @param source the file to copy
   * @param dir the directory to copy to
   */
  public static File copy(File source, File dir) {
    File dest = new File(dir, source.getName());
    try (FileChannel sourceChannel = new FileInputStream(source).getChannel();
        FileChannel destChannel = new FileOutputStream(dest).getChannel()) {
      sourceChannel.transferTo(0, sourceChannel.size(), destChannel);
      return dest;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * Copies all files in the collection to another directory. Any elements in the collection that are
   * not {@code File}'s or {@code FileHolder}'s will not be copied.
   *
   * @param files the files to copy
   * @param dir the directory to copy them to
   */
  public static Set<File> copyAll(Collection<?> files, File dir) {
    Set<File> dests = new HashSet<File>();
    for (Object object : files) {
      if (object instanceof File) {
        dests.add(copy((File) object, dir));
      } else if (object instanceof FileHolder) {
        dests.add(copy((FileHolder) object, dir));
      }
    }
    return dests;
  }
}
