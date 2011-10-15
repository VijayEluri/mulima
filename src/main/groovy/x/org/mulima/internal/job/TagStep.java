package x.org.mulima.internal.job;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import x.org.mulima.api.MulimaService;
import x.org.mulima.api.audio.action.TaggerResult;
import x.org.mulima.api.audio.file.AudioFile;

public class TagStep implements Step<Void> {
	private static final Logger logger = LoggerFactory.getLogger(TagStep.class);
	private final MulimaService service;
	private final Set<AudioFile> inputs;
	private Status status = Status.NOT_STARTED;
	
	public TagStep(MulimaService service, Set<AudioFile> inputs) {
		this.service = service;
		this.inputs = inputs;
	}
	
	public boolean execute() {
		this.status = Status.IN_PROGRESS;
		logger.debug("Tagging {} files", inputs.size());
		for (AudioFile input : inputs) {
			logger.debug("Tagging {}", input);
			TaggerResult result = service.getTagger(input.getFormat()).write(input);
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
	
	public Void call() {
		execute();
		return null;
	}
	
	public Status getStatus() {
		return status;
	}

	@Override
	public Void getOutputs() {
		return null;
	}
}
