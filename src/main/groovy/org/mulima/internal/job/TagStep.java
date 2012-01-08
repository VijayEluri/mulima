package org.mulima.internal.job;

import java.util.Set;

import org.mulima.api.audio.tool.TaggerResult;
import org.mulima.api.file.audio.AudioFile;
import org.mulima.api.job.Status;
import org.mulima.api.job.Step;
import org.mulima.api.service.MulimaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A step to write metadata to audio files.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class TagStep implements Step<Void> {
	private static final Logger logger = LoggerFactory.getLogger(TagStep.class);
	private final MulimaService service;
	private final Set<AudioFile> inputs;
	private Status status = Status.NOT_STARTED;
	
	/**
	 * Constructs a step from the parameters.
	 * @param service the service to use during execution
	 * @param inputs the files to tag
	 */
	public TagStep(MulimaService service, Set<AudioFile> inputs) {
		this.service = service;
		this.inputs = inputs;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean execute() {
		this.status = Status.IN_PROGRESS;
		logger.debug("Tagging {} files", inputs.size());
		for (AudioFile input : inputs) {
			logger.debug("Tagging {}", input);
			TaggerResult result = service.getToolService().getTagger(input.getFormat()).write(input);
			if (result.isSuccess()) {
				logger.debug("SUCCESS: Tagged {}", input);
			} else {
				logger.error("FAILURE: [{}] Tagging {}", result.getExitVal(), input);
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
	 * {@inheritDoc}
	 */
	@Override
	public Void call() {
		execute();
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status getStatus() {
		return status;
	}

	/**
	 * Returns {@code null} always.  The files are
	 * tagged in place.
	 * @return {@code null}
	 */
	@Override
	public Void getOutputs() {
		return null;
	}
}
