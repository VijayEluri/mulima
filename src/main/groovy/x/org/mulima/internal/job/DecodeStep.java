package x.org.mulima.internal.job;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import x.org.mulima.api.MulimaService;
import x.org.mulima.api.audio.AudioFormat;
import x.org.mulima.api.audio.action.Codec;
import x.org.mulima.api.audio.action.CodecResult;
import x.org.mulima.api.audio.file.AudioFile;

public class DecodeStep implements Step<Set<AudioFile>> {
	private static final Logger logger = LoggerFactory.getLogger(DecodeStep.class);
	private final MulimaService service;
	private final Set<AudioFile> inputs;
	private final File destDir;
	private Status status = Status.NOT_STARTED;
	private Set<AudioFile> outputs;
	
	public DecodeStep(MulimaService service, Set<AudioFile> inputs) {
		this(service, inputs, service.getTempDir().newChild().get());
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
			AudioFile output = service.getAudioFileFactory().createAudioFile(input, destDir, AudioFormat.WAVE);
			Codec codec = service.getCodec(input.getFormat());
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