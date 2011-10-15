package z.org.mulima.job.impl;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import z.org.mulima.api.audio.Splitter;
import z.org.mulima.api.audio.SplitterResult;
import z.org.mulima.api.file.DiscFile;
import z.org.mulima.api.file.TrackFile;
import z.org.mulima.job.Context;
import z.org.mulima.job.Step;

public class SplitStep implements Step {
	private static final Logger LOGGER = LoggerFactory.getLogger(SplitStep.class);
	private final Context context;
	private final Splitter splitter;
	private final Set<DiscFile> sourceFiles;
	private final File destinationDir;
	private Set<TrackFile> destinationFiles;
	
	public SplitStep(Context parentContext, Splitter splitter, Set<DiscFile> sourceFiles) {
		this(parentContext, splitter, sourceFiles, null);
	}
	
	public SplitStep(Context parentContext, Splitter splitter, Set<DiscFile> sourceFiles, File destinationDir) {
		this.context = parentContext.newChild();
		this.splitter = splitter;
		this.sourceFiles = sourceFiles;
		this.destinationDir = destinationDir == null ? context.getTempDir().get() : destinationDir;
	}

	@Override
	public boolean execute() {
		LOGGER.trace("Entering execute.");
		destinationFiles = new HashSet<TrackFile>();
		LOGGER.debug("Splitting {} files", sourceFiles.size());
		for (DiscFile sourceFile : sourceFiles) {
			LOGGER.debug("Splitting {}", sourceFile);
			SplitterResult result = splitter.split(sourceFile, destinationDir);
			if (result.isSuccess()) {
				destinationFiles.addAll(result.getDest());
				LOGGER.debug("SUCCESS: Split {}", sourceFile);
			} else {
				LOGGER.error("FAILURE: [{}] Splitting {}", result.getExitVal(), sourceFile);
				LOGGER.error("Output:\n{}", result.getOutput());
				LOGGER.error("Error:\n{}", result.getError());
				LOGGER.trace("Exiting execute.");
				return false;
			}
		}
		LOGGER.trace("Exiting execute.");
		return true;
	}

	/**
	 * @return the splitter to use when decoding
	 */
	public Splitter getSplitter() {
		return splitter;
	}

	/**
	 * @return the source files to decode
	 */
	public Set<DiscFile> getSourceFiles() {
		return sourceFiles;
	}

	/**
	 * @return the destination directory to put the
	 * decoded files in
	 */
	public File getDestinationDir() {
		return destinationDir;
	}
	
	/**
	 * @return the destionation files
	 */
	public Set<TrackFile> getDestinationFiles() {
		return destinationFiles;
	}
}
