package z.org.mulima.job.impl;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import z.org.mulima.api.audio.Codec;
import z.org.mulima.api.audio.CodecResult;
import z.org.mulima.api.file.AudioFile;
import z.org.mulima.api.file.AudioFiles;
import z.org.mulima.job.Context;
import z.org.mulima.job.Step;

public class EncodeStep implements Step {
	private static final Logger LOGGER = LoggerFactory.getLogger(EncodeStep.class);
	private final Context context;
	private final Codec codec;
	private final Set<AudioFile> sourceFiles;
	private final File destinationDir;
	private Set<AudioFile> destinationFiles;
	
	public EncodeStep(Context parentContext, Codec codec, Set<AudioFile> sourceFiles) {
		this(parentContext, codec, sourceFiles, null);
	}
	
	public EncodeStep(Context parentContext, Codec codec, Set<AudioFile> sourceFiles, File destinationDir) {
		this.context = parentContext.newChild();
		this.codec = codec;
		this.sourceFiles = sourceFiles;
		this.destinationDir = destinationDir == null ? context.getTempDir().get() : destinationDir;
	}

	@Override
	public boolean execute() {
		LOGGER.trace("Entering execute.");
		destinationFiles = new HashSet<AudioFile>();
		LOGGER.debug("Encoding {} files", sourceFiles.size());
		for (AudioFile sourceFile : sourceFiles) {
			LOGGER.debug("Encoding {}", sourceFile);
			AudioFile destFile = AudioFiles.createAudioFile(sourceFile, getDestinationDir(), codec.getFormat());
			CodecResult result = codec.encode(sourceFile, destFile);
			if (result.isSuccess()) {
				destinationFiles.add(result.getDest());
				LOGGER.debug("SUCCESS: Encoded {}", sourceFile);
			} else {
				LOGGER.error("FAILURE: [{}] Encoding {}", result.getExitVal(), sourceFile);
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
	 * @return the codec to use when decoding
	 */
	public Codec getCodec() {
		return codec;
	}

	/**
	 * @return the source files to decode
	 */
	public Set<AudioFile> getSourceFiles() {
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
	public Set<AudioFile> getDestinationFiles() {
		return destinationFiles;
	}
}
