package org.mulima.internal.job;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.mulima.api.audio.AudioFormat;
import org.mulima.api.audio.tool.Codec;
import org.mulima.api.audio.tool.CodecResult;
import org.mulima.api.file.audio.AudioFile;
import org.mulima.api.service.MulimaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DecodeStep implements Step<Set<AudioFile>> {
	private static final Logger logger = LoggerFactory.getLogger(DecodeStep.class);
	private final MulimaService service;
	private final Set<AudioFile> inputs;
	private final File destDir;
	private Status status = Status.NOT_STARTED;
	private Set<AudioFile> outputs;
	
	public DecodeStep(MulimaService service, Set<AudioFile> inputs) {
		this(service, inputs, service.getTempDir().newChild().getFile());
	}
	
	public DecodeStep(MulimaService service, Set<AudioFile> inputs, File destDir) {
		this.service = service;
		this.inputs = inputs;
		this.destDir = destDir;
	}
	
	public boolean execute() {
		this.status = Status.IN_PROGRESS;
		outputs = new HashSet<AudioFile>();
		logger.debug("Decoding {} files", inputs.size());
		for (AudioFile input : inputs) {
			logger.debug("Decoding {}", input);
			AudioFile output = service.getFileService().createAudioFile(input, destDir, AudioFormat.WAVE);
			Codec codec = service.getToolService().getCodec(input.getFormat());
			CodecResult result = codec.decode(input, output);
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
	
	public Set<AudioFile> call() {
		if (execute()) {
			return getOutputs();
		} else {
			return null;
		}
	}
	
	public Status getStatus() {
		return status;
	}
	
	public Set<AudioFile> getOutputs() {
		if (!Status.SUCCESS.equals(status)) {
			throw new IllegalStateException("Cannot get outputs in current state: " + status);
		}
		return outputs;
	}
}
