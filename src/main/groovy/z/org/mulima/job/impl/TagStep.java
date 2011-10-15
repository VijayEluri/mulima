package z.org.mulima.job.impl;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import z.org.mulima.api.audio.Tagger;
import z.org.mulima.api.audio.TaggerResult;
import z.org.mulima.api.file.AudioFile;
import z.org.mulima.job.Context;
import z.org.mulima.job.Step;

public class TagStep implements Step {
	private static final Logger LOGGER = LoggerFactory.getLogger(TagStep.class);
	private final Context context;
	private final Tagger tagger;
	private final Set<AudioFile> sourceFiles;
	
	public TagStep(Context parentContext, Tagger tagger, Set<AudioFile> sourceFiles) {
		this.context = parentContext.newChild();
		this.tagger = tagger;
		this.sourceFiles = sourceFiles;
	}
	
	@Override
	public boolean execute() {
		LOGGER.trace("Entering execute.");
		LOGGER.debug("Tagging {} files", sourceFiles.size());
		for (AudioFile sourceFile : sourceFiles) {
			LOGGER.debug("Tagging {}", sourceFile);
			TaggerResult result = tagger.write(sourceFile);
			if (result.isSuccess()) {
				LOGGER.debug("SUCCESS: Tagged {}", sourceFile);
			} else {
				LOGGER.error("FAILURE: [{}] Tagging {}", result.getExitVal(), sourceFile);
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
	 * @return the tagger to use when decoding
	 */
	public Tagger getTagger() {
		return tagger;
	}

	/**
	 * @return the source files to decode
	 */
	public Set<AudioFile> getSourceFiles() {
		return sourceFiles;
	}
}
