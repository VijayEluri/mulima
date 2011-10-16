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

public class EncodeStep implements Step<Set<AudioFile>> {
	private static final Logger logger = LoggerFactory.getLogger(EncodeStep.class);
	private final MulimaService service;
	private final AudioFormat format;
	private final Set<AudioFile> inputs;
	private final File destDir;
	private Status status = Status.NOT_STARTED;
	private Set<AudioFile> outputs;
	
	public EncodeStep(MulimaService service, AudioFormat format, Set<AudioFile> inputs) {
		this(service, format, inputs, service.getTempDir().newChild().get());
	}
	
	public EncodeStep(MulimaService service, AudioFormat format, Set<AudioFile> inputs, File destDir) {
		this.service = service;
		this.format = format;
		this.inputs = inputs;
		this.destDir = destDir;
	}
	
	public boolean execute() {
		this.status = Status.IN_PROGRESS;
		outputs = new HashSet<AudioFile>();
		logger.debug("Encoding {} files", inputs.size());
		for (AudioFile input : inputs) {
			logger.debug("Encoding {}", input);
			AudioFile output = service.getAudioFileFactory().createAudioFile(input, destDir, format);
			Codec codec = service.getCodec(format);
			CodecResult result = codec.decode(input, output);
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