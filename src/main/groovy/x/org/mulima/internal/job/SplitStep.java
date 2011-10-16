package x.org.mulima.internal.job;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import x.org.mulima.api.MulimaService;
import x.org.mulima.api.audio.action.SplitterResult;
import x.org.mulima.api.audio.file.DiscFile;
import x.org.mulima.api.audio.file.TrackFile;

public class SplitStep implements Step<Set<TrackFile>> {
	private static final Logger logger = LoggerFactory.getLogger(SplitStep.class);
	private final MulimaService service;
	private final Set<DiscFile> inputs;
	private final File destDir;
	private Status status = Status.NOT_STARTED;
	private Set<TrackFile> outputs;
	
	public SplitStep(MulimaService service, Set<DiscFile> inputs) {
		this(service, inputs, service.getTempDir().newChild().getFile());
	}
	
	public SplitStep(MulimaService service, Set<DiscFile> inputs, File destDir) {
		this.service = service;
		this.inputs = inputs;
		this.destDir = destDir;
	}
	
	public boolean execute() {
		this.status = Status.IN_PROGRESS;
		outputs = new HashSet<TrackFile>();
		logger.debug("Splitting {} files", inputs.size());
		for (DiscFile input : inputs) {
			logger.debug("Splitting {}", input);
			SplitterResult result = service.getSplitter().split(input, destDir);
			if (result.isSuccess()) {
				outputs.addAll(result.getDest());
				logger.debug("SUCCESS: Split {}", input);
			} else {
				logger.error("FAILURE: [{}] Splitting {}", result.getExitVal(), input);
				logger.error("Output:\n{}", result.getOutput());
				logger.error("Error:\n{}", result.getError());
				this.status = Status.FAILURE;
				return false;
			}
		}
		this.status = Status.SUCCESS;
		return true;
	}
	
	public Set<TrackFile> call() {
		if (execute()) {
			return getOutputs();
		} else {
			return null;
		}
	}
	
	public Status getStatus() {
		return status;
	}
	
	public Set<TrackFile> getOutputs() {
		if (!Status.SUCCESS.equals(status)) {
			throw new IllegalStateException("Cannot get outputs in current state: " + status);
		}
		return outputs;
	}
}
