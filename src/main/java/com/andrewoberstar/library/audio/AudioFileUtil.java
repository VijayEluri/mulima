/*  
 *  Copyright (C) 2010  Andrew Oberstar.  All rights reserved.
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

package com.andrewoberstar.library.audio;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

import com.andrewoberstar.library.meta.CueSheet;

public interface AudioFileUtil {
	List<AudioFile> split(AudioFile image, CueSheet cue, File destDir) throws Exception;
	AudioFile join(List<AudioFile> files, AudioFile dest) throws Exception;
	Callable<List<AudioFile>> splitLater(AudioFile image, CueSheet cue, File destDir);
	Callable<AudioFile> joinLater(List<AudioFile> files, AudioFile dest);
}