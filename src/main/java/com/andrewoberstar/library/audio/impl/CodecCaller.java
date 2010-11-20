package com.andrewoberstar.library.audio.impl;

import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.andrewoberstar.library.audio.AudioFile;
import com.andrewoberstar.library.exception.CodecDestExistsException;
import com.andrewoberstar.library.exception.CodecFailureException;
import com.andrewoberstar.library.exception.CodecSourceMissingException;
import com.andrewoberstar.library.util.ProcessFuture;

public class CodecCaller implements Callable<AudioFile> {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final List<String> command;
	private final AudioFile source;
	private final AudioFile dest;
	
	public CodecCaller(List<String> command, AudioFile source, AudioFile dest) {
		this.command = command;
		this.source = source;
		this.dest = dest;
	}
	
	@Override
	public AudioFile call() throws Exception {
		if (!source.getFile().exists()) {
			throw new CodecSourceMissingException("Source file does not exist: " + source.getFile().getName());
		} else if (!dest.getFile().exists()) {
			throw new CodecDestExistsException("Must set overwrite to true: " + dest.getFile().getName());
		} else {
			logger.info("Executing command: " + command);
			ProcessFuture proc = new ProcessFuture(new ProcessBuilder(command).start());
			int exit = proc.get();
			if (exit > 0) {
				logger.error("Command failed.");
				logger.error("Stdout: " + proc.getOutput());
				logger.error("Stderr: " + proc.getError());
				throw new CodecFailureException("Coding failed for source (" + source.getFile().getName() + ") to dest (" + dest.getFile().getName() + ").");
			} else {
				return dest;
			}
		}
	}

}
