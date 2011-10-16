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
package org.mulima.job;

import java.io.File;
import java.util.Deque;
import java.util.LinkedList;

import org.mulima.api.audio.CodecConfig;
import org.mulima.cache.DigestService;

/**
 * Holds information describing the context of the app.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class Context {
	private static Context rootContext = new Context();
	private static ThreadLocal<Deque<Context>> current = new ThreadLocal<Deque<Context>>();
	
	private CodecConfig codecConfig;
	private DigestService digestService;
	private File tempDir;
	
	/**
	 * Constructs a blank context.
	 */
	public Context() {
		setCodecConfig(null);
		setDigestService(null);
		setTempDir(null);
	}
	
	/**
	 * Constructs a context with values copied from the
	 * passed context.
	 * @param context the context to copy
	 */
	public Context(Context context) {
		setCodecConfig(context.getCodecConfig());
		setDigestService(context.getDigestService());
		setTempDir(context.getTempDir());
	}
	
	/**
	 * Gets the codec config for this context.
	 * @return the codec config
	 */
	public final CodecConfig getCodecConfig() {
		return codecConfig;
	}

	/**
	 * Sets the codec config for this context
	 * @param codecConfig the codec config
	 */
	public final void setCodecConfig(CodecConfig codecConfig) {
		this.codecConfig = codecConfig;
	}
	
	/**
	 * Gets the digest service for this context.
	 * @return the digest service
	 */
	public final DigestService getDigestService() {
		return digestService;
	}
	
	/**
	 * Sets the digest service for this context.
	 * @param digestService the digest service
	 */
	public final void setDigestService(DigestService digestService) {
		this.digestService = digestService;
	}
	
	/**
	 * Gets the temporary directory for this context.
	 * @return the temp dir
	 */
	public final File getTempDir() {
		return tempDir;
	}
	
	/**
	 * Sets the temporary directory for this context.
	 * @param tempDir the temp dir
	 */
	public final void setTempDir(File tempDir) {
		this.tempDir = tempDir;
	}

	/**
	 * Gets the root context.
	 * @return the root context
	 */
	public static Context getRoot() {
		return rootContext;
	}
	
	/**
	 * Gets the thread's current context.
	 * @return the current context
	 */
	public static Context getCurrent() {
		return getCurrentStack().peek();
	}
	
	/**
	 * Pops the current context of the stack.
	 * @return the popped context
	 */
	public static Context popCurrent() {
		return getCurrentStack().pop();
	}
	
	/**
	 * Pushes a new context that copies the current one onto the stack.
	 */
	public static Context pushContext() {
		return pushContext(new Context(getCurrent()));
	}
	
	/**
	 * Pushes a new context onto the stack.
	 * @param context the context to push
	 */
	public static Context pushContext(Context context) {
		getCurrentStack().push(context);
		return getCurrent();
	}
	
	/**
	 * Gets the current thread's stack.
	 * @return the current stack
	 */
	private static Deque<Context> getCurrentStack() {
		Deque<Context> stack = current.get();
		if (stack == null) {
			stack = new LinkedList<Context>();
			stack.push(new Context(getRoot()));
		}
		return stack;
	}
}