package org.mulima.internal.job;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.mulima.api.audio.tool.Codec;
import org.mulima.api.audio.tool.CodecResult;
import org.mulima.api.file.audio.AudioFile;
import org.mulima.api.file.audio.AudioFormat;
import org.mulima.api.job.Status;
import org.mulima.api.job.Step;
import org.mulima.api.service.MulimaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A step to encode WAVE files to another format.
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class EncodeStep implements Step<Set<AudioFile>> {
	private static final Logger logger = LoggerFactory.getLogger(EncodeStep.class);
	private final MulimaService service;
	private final AudioFormat format;
	private final Set<AudioFile> inputs;
	private final File destDir;
	private Status status = Status.NOT_STARTED;
	private Set<AudioFile> outputs;
	
	/**
	 * Constructs a step from the parameters.
	 * @param service the service to use during execution
	 * @param format the format to encode the files to
	 * @param inputs the files to encode
	 * @param destDir the directory to put the encoded files into
	 */
	public EncodeStep(MulimaService service, AudioFormat format, Set<AudioFile> inputs, File destDir) {
		this.service = service;
		this.format = format;
		this.inputs = inputs;
		this.destDir = destDir;
	}
	
	/**
	 * Executes this step.
	 */
	@Override
	public boolean execute() {
		this.status = Status.IN_PROGRESS;
		outputs = new HashSet<AudioFile>();
		logger.debug("Encoding {} files", inputs.size());
		for (AudioFile input : inputs) {
			logger.debug("Encoding {}", input);
			AudioFile output = service.getFileService().createAudioFile(input, destDir, format);
			Codec codec = service.getToolService().getCodec(format);
			CodecResult result = codec.encode(input, output);
			if (result.isSuccess()) {
				outputs.add(result.getDest());
				logger.debug("SUCCESS: Encoded {}", input);
			} else {
				logger.error("FAILURE: [{}] Encoding {}", result.getExitVal(), input);
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
	 * Executes this step.
	 * @return the encoded files
	 */
	@Override
	public Set<AudioFile> call() {
		if (execute()) {
			return getOutputs();
		} else {
			return null;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status getStatus() {
		return status;
	}
	
	/**
	 * Gets the encoded files.
	 * @return the encoded files
	 * @throws IllegalStateException if the step is not in SUCCESS state
	 */
	@Override
	public Set<AudioFile> getOutputs() {
		if (!Status.SUCCESS.equals(status)) {
			throw new IllegalStateException("Cannot get outputs in current state: " + status);
		}
		return outputs;
	}
}
