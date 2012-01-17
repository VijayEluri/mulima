package org.mulima.api.audio.tool;

import org.mulima.api.file.audio.AudioFormat;

public interface ToolService {
	Codec getCodec(AudioFormat type);
	Tagger getTagger(AudioFormat type);
	Splitter getSplitter();
	Joiner getJoiner();
}
