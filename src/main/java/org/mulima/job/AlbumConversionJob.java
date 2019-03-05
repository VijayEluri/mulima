package org.mulima.job;

import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mulima.exception.UncheckedMulimaException;
import org.mulima.file.DigestService;
import org.mulima.file.audio.AudioFile;
import org.mulima.file.audio.DiscFile;
import org.mulima.library.LibraryAlbum;
import org.mulima.service.MulimaService;
import org.mulima.util.FileUtil;

/**
 * A job to convert albums.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class AlbumConversionJob implements java.util.concurrent.Callable<Boolean> {
  private final Logger logger = LogManager.getLogger(AlbumConversionJob.class);
  private final MulimaService service;
  private final LibraryAlbum refAlbum;
  private final Set<LibraryAlbum> destAlbums;

  /**
   * Constructs a job from the parameters.
   *
   * @param service the service to use during job execution
   * @param refAlbum the reference album
   * @param destAlbums the destination albums
   */
  public AlbumConversionJob(
      MulimaService service, LibraryAlbum refAlbum, Set<LibraryAlbum> destAlbums) {
    this.service = service;
    this.refAlbum = refAlbum;
    this.destAlbums = destAlbums;
  }

  /** Executes the job. */
  @Override
  public Boolean call() throws Exception {
    return execute();
  }

  /** Executes the job. */
  public Boolean execute() {
    try {
      Set<LibraryAlbum> outdated = getOutdatedAlbums();
      if (outdated.size() == 0) {
        logger.debug(
            "Skipping conversion for " + refAlbum.getName() + ". No albums are out of date.");
        return true;
      }
      logger.info("Beginning conversion of: " + refAlbum.getName());
      var tempDir = Files.createTempDirectory("mulima");

      DecodeStep decode =
          new DecodeStep(service, refAlbum.getAudioFiles(), Files.createTempFile(tempDir, "audio", ".decoded").toFile());
      if (!decode.execute()) {
        logger.error("Failed to decode: " + refAlbum.getName());
        return false;
      }
      Set<AudioFile> tempFiles = new HashSet<AudioFile>();
      Set<DiscFile> discFiles = new HashSet<DiscFile>();
      for (AudioFile temp : decode.getOutputs()) {
        if (temp instanceof DiscFile) {
          discFiles.add((DiscFile) temp);
        } else {
          tempFiles.add(temp);
        }
      }
      SplitStep split = new SplitStep(service, discFiles, Files.createTempFile(tempDir, "audio", ".split").toFile());
      if (!split.execute()) {
        logger.error("Failed to split: " + refAlbum.getName());
        return false;
      }
      tempFiles.addAll(split.getOutputs());

      for (LibraryAlbum destAlbum : outdated) {
        destAlbum.setDir(destAlbum.getLib().determineDir(refAlbum.getAlbum()));
        destAlbum.cleanDir();
        EncodeStep encode =
            new EncodeStep(service, destAlbum.getLib().getFormat(), tempFiles, destAlbum.getDir());
        if (!encode.execute()) {
          logger.error("Failed to encode: " + refAlbum.getName());
          return false;
        }

        TagStep tag = new TagStep(service, encode.getOutputs());
        if (!tag.execute()) {
          logger.error("Failed to tag: " + refAlbum.getName());
          return false;
        }

        logger.debug("Starting to copy artwork to {}", destAlbum.getDir());
        FileUtil.copyAll(refAlbum.getArtwork(), destAlbum.getDir());
        logger.debug("Finished copying artwork to {}", destAlbum.getDir());
      }

      try {
        FileUtil.deleteDir(tempDir.toFile());
      } catch (UncheckedIOException e) {
        logger.warn("Failed to delete temp dir: {}", tempDir);
      }

      DigestService digestService = service.getDigestService();
      for (LibraryAlbum destAlbum : outdated) {
        digestService.write(destAlbum, refAlbum);
      }
      digestService.write(refAlbum, null);

      logger.info("Successfully converted: " + refAlbum.getName());
      return true;
    } catch (Exception e) {
      throw new UncheckedMulimaException("Failed to convert: " + refAlbum.getName(), e);
    }
  }

  /**
   * Gets a set of all outdated albums.
   *
   * @return the set of outdated albums
   */
  private Set<LibraryAlbum> getOutdatedAlbums() {
    Set<LibraryAlbum> tempAlbums = new HashSet<LibraryAlbum>();
    for (LibraryAlbum destAlbum : destAlbums) {
      if (!service.getLibraryService().isUpToDate(destAlbum, true)) {
        tempAlbums.add(destAlbum);
        logger.debug("Album is out of date: {}", destAlbum.getDir());
      } else {
        logger.debug("Album is up to date: {}", destAlbum.getDir());
      }
    }
    return tempAlbums;
  }
}
