package org.mulima.internal.job;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mulima.api.audio.tool.SplitterResult;
import org.mulima.api.file.audio.AudioFormat;
import org.mulima.api.file.audio.DiscFile;
import org.mulima.api.file.audio.TrackFile;
import org.mulima.api.job.Status;
import org.mulima.api.job.Step;
import org.mulima.api.meta.Track;
import org.mulima.api.service.MulimaService;
import org.mulima.exception.UncheckedMulimaException;

/**
 * A step to split a disc file into track files.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class SplitStep implements Step<Set<TrackFile>> {
  private static final Logger logger = LogManager.getLogger(SplitStep.class);
  private final MulimaService service;
  private final Set<DiscFile> inputs;
  private final File destDir;
  private Status status = Status.NOT_STARTED;
  private Set<TrackFile> outputs;

  /**
   * Constructs a step from the parameters.
   *
   * @param service the service to use during execution
   * @param inputs the files to split
   * @param destDir the directory to put the split files into
   */
  public SplitStep(MulimaService service, Set<DiscFile> inputs, File destDir) {
    this.service = service;
    this.inputs = inputs;
    this.destDir = destDir;
  }

  /** {@inheritDoc} */
  @Override
  public boolean execute() {
    this.status = Status.IN_PROGRESS;
    outputs = new HashSet<TrackFile>();
    logger.debug("Splitting {} files", inputs.size());
    for (DiscFile input : inputs) {
      File discDestDir = new File(destDir, Integer.toString(input.getDiscNum()));
      if (!discDestDir.exists() && !discDestDir.mkdirs()) {
        throw new UncheckedMulimaException("Could not create disc directory: " + discDestDir);
      }
      logger.debug("Splitting {}", input);
      if (input.getMeta().getTracks().size() == 1) {
        Track track = input.getMeta().getTracks().iterator().next();
        File destFile =
            new File(
                discDestDir,
                String.format(
                    "D%02dT%02d.%s",
                    input.getDiscNum(), track.getNum(), AudioFormat.WAVE.getExtension()));
        if (!input.getFile().renameTo(destFile)) {
          throw new UncheckedMulimaException("Could not move source file: " + input.getFile());
        }
        TrackFile trackFile = service.getFileService().createTrackFile(destFile);
        trackFile.setMeta(track);
        outputs.add(trackFile);
      } else {
        SplitterResult result = service.getToolService().getSplitter().split(input, discDestDir);
        if (result.isSuccess()) {
          outputs.addAll(result.getDest());

          for (TrackFile file : result.getDest()) {
            file.setMeta(input.getMeta().getTrack(file.getTrackNum()));
          }

          logger.debug("SUCCESS: Split {}", input);
        } else {
          logger.error("FAILURE: [{}] Splitting {}", result.getExitVal(), input);
          logger.error("Output:\n{}", result.getOutput());
          logger.error("Error:\n{}", result.getError());
          this.status = Status.FAILURE;
          return false;
        }
      }
    }
    this.status = Status.SUCCESS;
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public Set<TrackFile> call() {
    if (execute()) {
      return getOutputs();
    } else {
      return null;
    }
  }

  /** {@inheritDoc} */
  @Override
  public Status getStatus() {
    return status;
  }

  /**
   * Gets the split files.
   *
   * @return the split files
   * @throws IllegalStateException if the step is not in SUCCESS state
   */
  @Override
  public Set<TrackFile> getOutputs() {
    if (!Status.SUCCESS.equals(status)) {
      throw new IllegalStateException("Cannot get outputs in current state: " + status);
    }
    return outputs;
  }
}
