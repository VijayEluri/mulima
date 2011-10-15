/*  
 *  Copyright (C) 2011  Andrew Oberstar.  All rights reserved.
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package z.org.mulima.api.audio;

import z.org.mulima.api.file.AudioFile;
import z.org.mulima.api.file.AudioFormat;

/**
 * The configuration of available codecs, taggers,
 * splitters, and joiners.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public interface CodecConfig {
	/**
	 * Gets the codec configured for a given file type.
	 * @param type the type of file
	 * @return the configured codec
	 */
	Codec getCodec(AudioFormat type);
	
	/**
	 * Gets the codec configured for files of the same
	 * type as the parameter.
	 * @param file a file of the type you would like to encode/decode
	 * @return the configured codec
	 */
	Codec getCodec(AudioFile file);
	
	/**
	 * Gets the tagger configured for a given file type.
	 * @param type the type of the file
	 * @return the configured tagger
	 */
	Tagger getTagger(AudioFormat type);
	
	/**
	 * Gets the tagger configured for file of the same
	 * type as the parameter.
	 * @param file a file of the type you would like to tag
	 * @return the configured tagger
	 */
	Tagger getTagger(AudioFile file);
	
	/**
	 * Gets the configured splitter.
	 * @return the configured splitter
	 */
	Splitter getSplitter();
	
	/**
	 * Gets the configured joiner.
	 * @return the configured joiner
	 */
	Joiner getJoiner();
}
