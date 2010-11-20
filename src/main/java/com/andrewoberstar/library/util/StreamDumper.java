package com.andrewoberstar.library.util;

import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dumps an <code>InputStream</code> to a <code>String</code>. The 
 * stream will be handled in a separate thread.  A common use for 
 * this would be to handle <code>Process</code> output.
 * 
 * @see Process, ProcessUtil, ProcessFuture
 *
 */
public class StreamDumper implements Callable<String> {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private InputStream in;
	private StringBuilder buffer;
	
	/**
	 * Constructs a <code>StreamDumper</code> without an associated
	 * <code>InputStream</code>.  Must use {@link #setInputStream(InputStream)}
	 * before running {@link #call()}.
	 */
	public StreamDumper() {
		this.buffer = new StringBuilder();
	}
	
	/**
	 * Constructs a <code>StreamDumper</code> with an associated
	 * <code>InputStream</code>.
	 * @param in the <code>InputStream</code> to dump
	 */
	public StreamDumper(InputStream in) {
		this.in = in;
		this.buffer = new StringBuilder();
	}
	
	/**
	 * Sets the <code>InputStream</code> to dump.
	 * @param is the <code>InputStream</code> to dump
	 */
	public void setInputStream(InputStream is) {
		this.in = is;
	}

	/**
	 * Dumps the contents of the <code>InputStream</code>
	 * and returns them as a <code>String</code>.
	 * @return a <code>String</code> of the contents of
	 * the <code>InputStream</code>
	 */
	@Override
	public String call() {
		logger.trace("Entering call method");
		Scanner input = new Scanner(in);
		
		while (input.hasNextLine() && !Thread.interrupted()) {
			buffer.append(input.nextLine());
			buffer.append("\n");
		}
		if (Thread.interrupted()) {
			logger.debug("Thread interrupted");
		}
		logger.trace("Exiting call method");
		
		return buffer.toString();
	}
}
