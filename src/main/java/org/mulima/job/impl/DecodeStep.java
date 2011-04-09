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
package org.mulima.job.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.mulima.audio.AudioFile;
import org.mulima.audio.CodecResult;
import org.mulima.audio.util.CodecService;
import org.mulima.job.Context;
import org.mulima.job.Step;
import org.mulima.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andy
 *
 */
public class DecodeStep implements Step {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public Context call() throws Exception {
		Context.pushContext();
		
		//logger.info("Decoding " + refAlbum.getAlbum().getFlat(GenericTag.ALBUM));
		List<Future<CodecResult>> futures = new ArrayList<Future<CodecResult>>();
		for (AudioFile file : Context.getCurrent().getInputFiles()) {
			try {
				futures.add(CodecService.getInstance().submitDecode(file, AudioFile.createTempFile(file)));
			} catch (IOException e) {
				logger.error("Failed to create temp file for: " + file);
				return Context.popCurrent();
			}
		}
		
		Set<AudioFile> tempFiles = new HashSet<AudioFile>();
		for (Future<CodecResult> future : futures) {
			try {
				CodecResult result = future.get();
				if (result.getExitVal() == 0) {
					tempFiles.add(result.getDest());
				} else {
					logger.error("Failed decoding "
						+ FileUtil.getSafeCanonicalPath(result.getSource()));
					logger.error("Command: " + result.getCommand());
					logger.error("Stdout: " + result.getOutput());
					logger.error("StdErr: " + result.getError());
					return Context.popCurrent();
				}
			} catch (InterruptedException e) {
				logger.error("Failed decoding.", e);
				return Context.popCurrent();
			} catch (ExecutionException e) {
				logger.error("Failed decoding.", e);
				return Context.popCurrent();
			}
		}
		//logger.debug("Decoded " + tempFiles.size() + " file(s) for " + refAlbum.getDir().getName());
		Context.getCurrent().setOutputFiles(tempFiles);
		return Context.popCurrent();
	}

}
