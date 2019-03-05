package org.mulima.job;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mulima.file.audio.AudioFile;
import org.mulima.file.audio.AudioFormat;
import org.mulima.service.MulimaService;

/**
 * A step to decode audio files to WAVE format in a directory.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class DecodeStep implements Step<Set<AudioFile>> {
  private static final Logger logger = LogManager.getLogger(DecodeStep.class);
  private final MulimaService service;
  private final Set<AudioFile> inputs;
  private final File destDir;
  private Status status = Status.NOT_STARTED;
  private Set<AudioFile> outputs;

  /**
   * Constructs a step from the parameters.
   *
   * @param service the service to use during execution
   * @param inputs the files to decode
   * @param destDir the directory to put the decoded files into
   */
  public DecodeStep(MulimaService service, Set<AudioFile> inputs, File destDir) {
    this.service = service;
    this.inputs = inputs;
    this.destDir = destDir;
  }

  /** Executes the step. */
  public boolean execute() {
    this.status = Status.IN_PROGRESS;
    outputs = new HashSet<>();
    logger.debug("Decoding {} files", inputs.size());
    for (var input : inputs) {
      logger.debug("Decoding {}", input);
      var output = service.getFileService().createAudioFile(input, destDir, AudioFormat.WAVE);
      var codec = service.getToolService().getCodec(input.getFormat());
      var result = codec.decode(input, output);
      if (result.isSuccess()) {
        outputs.add(result.getDest());
        logger.debug("SUCCESS: Decoded {}", input);
      } else {
        logger.error("FAILURE: [{}] Decoding {}", result.getExitVal(), input);
        logger.error("Output:\n{}", result.getOutput());
        logger.error("Error:\n{}", result.getError());
        this.status = Status.FAILURE;
        return false;
      }
    }
    this.status = Status.SUCCESS;
    return true;
  }

  /**
   * Executes the step.
   *
   * @return the decoded files
   */
  @Override
  public Set<AudioFile> call() {
    if (execute()) {
      return getOutputs();
    } else {
      return null;
    }
  }

  /** {@inheritDoc} */
  public Status getStatus() {
    return status;
  }

  /**
   * Gets the decoded files (now in WAVE format).
   *
   * @return the decoded files
   * @throws IllegalStateException if the step is not in SUCCESS state
   */
  public Set<AudioFile> getOutputs() {
    if (!Status.SUCCESS.equals(status)) {
      throw new IllegalStateException("Cannot get outputs in current state: " + status);
    }
    return outputs;
  }
}
