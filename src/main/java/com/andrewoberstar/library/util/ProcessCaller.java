package com.andrewoberstar.library.util;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.andrewoberstar.library.exception.ProcessFailureException;

public class ProcessCaller implements Callable<String> {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final String description;
	private final List<String> command;
	
	public ProcessCaller(String description, List<String> command) {
		this.description = description;
		this.command = command;
	}
	
	public ProcessCaller(String description, String... command) {
		this.description = description;
		this.command = Arrays.asList(command);
	}
	
	@Override
	public String call() throws Exception {
		logger.info("Starting: " + description);
		logger.debug("Executing command: " + command);
		ProcessFuture proc = new ProcessFuture(new ProcessBuilder(command).start());
		int exit = proc.get();
		if (exit > 0) {
			logger.error("Failed: " + description);
			logger.error("Stdout: " + proc.getOutput());
			logger.error("Stderr: " + proc.getError());
			throw new ProcessFailureException("Command failed: " + command);
		} else {
			logger.info("Success: " + description);
			return proc.getOutput();
		}
	}
}
