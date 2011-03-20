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
package org.mulima.audio.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.mulima.audio.AudioFile;
import org.mulima.audio.Codec;
import org.mulima.audio.CodecConfig;
import org.mulima.audio.CodecResult;
import org.mulima.audio.Joiner;
import org.mulima.audio.JoinerResult;
import org.mulima.audio.Operation;
import org.mulima.audio.Splitter;
import org.mulima.audio.SplitterResult;
import org.mulima.audio.Tagger;
import org.mulima.audio.TaggerResult;
import org.mulima.meta.CueSheet;
import org.mulima.meta.Track;
import org.mulima.proc.TaskListener;

/**
 * Service that allows the execution of codec operations.
 */
public class CodecService {
	private CodecConfig config;
	private ExecutorService executor;
	private Map<Operation, List<TaskListener>> listeners = new HashMap<Operation, List<TaskListener>>();
	
	/**
	 * Constructs a codec service without a codec config.
	 */
	public CodecService() {
		this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	}
	
	/**
	 * Constructs a codec service with the specified codec config.
	 * @param config codec config
	 */
	public CodecService(CodecConfig config) {
		this();
		this.config = config;
	}
	
	/**
	 * Submits an encode operation. Uses the codec specified in the
	 * config for the type of the destination file. 
	 * @param source the source file
	 * @param dest the destination file
	 * @return a future codec result
	 */
	public Future<CodecResult> submitEncode(AudioFile source, AudioFile dest) {
		Codec codec = config.getCodec(dest);
		Future<CodecResult> task = executor.submit(codec.encodeLater(source, dest));
		fireTaskSubmitted(Operation.ENCODE, task);
		return task;
	}
	
	/**
	 * Submits a decode operation.  Uses the codec specified in the
	 * config for the type of the source file.
	 * @param source the source file
	 * @param dest the destination file
	 * @return a future codec result
	 */
	public Future<CodecResult> submitDecode(AudioFile source, AudioFile dest) {
		Codec codec = config.getCodec(source);
		Future<CodecResult> task = executor.submit(codec.decodeLater(source, dest));
		fireTaskSubmitted(Operation.DECODE, task);
		return task;
	}
	
	/**
	 * Submits a split operation.  Uses the splitter specified in the config.
	 * @param source the source file
	 * @param cue the cue sheet to use to split it
	 * @param destDir the destination directory of the split files
	 * @return a future splitter result
	 */
	public Future<SplitterResult> submitSplit(AudioFile source, CueSheet cue, File destDir) {
		Splitter util = config.getSplitter();
		Future<SplitterResult> task = executor.submit(util.splitLater(source, cue, destDir));
		fireTaskSubmitted(Operation.SPLIT, task);
		return task;
	}
	
	/**
	 * Submits a join operation. Uses the joiner specified in the config.
	 * @param sources a list of the source files
	 * @param dest the destination file
	 * @return a future joiner result
	 */
	public Future<JoinerResult> submitJoin(List<AudioFile> sources, AudioFile dest) {
		Joiner util = config.getJoiner();
		Future<JoinerResult> task = executor.submit(util.joinLater(sources, dest));
		fireTaskSubmitted(Operation.JOIN, task);
		return task;
	}
	
	/**
	 * Submits an operation to read metadata from a file.  Uses the tagger
	 * specified in the config for the type of the file.
	 * @param file the file to read from
	 * @return a future tagger result
	 */
	public Future<TaggerResult> submitReadMeta(AudioFile file) {
		Tagger util = config.getTagger(file);
		Future<TaggerResult> task = executor.submit(util.readLater(file));
		fireTaskSubmitted(Operation.TAG, task);
		return task;
	}
	
	/**
	 * Submits an operation to write metadata to a file.  Uses the tagger
	 * specified in the config for the type of the file.
	 * @param file the file to write to
	 * @param meta the metadata to write
	 * @return a future tagger result
	 */
	public Future<TaggerResult> submitWriteMeta(AudioFile file, Track meta) {
		Tagger util = config.getTagger(file);
		Future<TaggerResult> task = executor.submit(util.writeLater(file, meta));
		fireTaskSubmitted(Operation.TAG, task);
		return task;
	}
	
	/**
	 * Initiates an orderly shutdown in which previously submitted tasks
	 * are executed, but no new tasks will be accepted. Invocation has 
	 * no additional effect if already shut down.
	 */
	public void shutdown() {
		executor.shutdown();
	}
	
	/**
	 * Attempts to stop all actively executing tasks, halts the processing of 
	 * waiting tasks, and returns a list of the tasks that were awaiting execution. 
	 * There are no guarantees beyond best-effort attempts to stop processing 
	 * actively executing tasks. For example, typical implementations will 
	 * cancel via Thread.interrupt, so any task that fails to respond to interrupts 
	 * may never terminate.
	 * @return a list of all tasks that weren't executed
	 */
	public List<Runnable> shutdownNow() {
		return executor.shutdownNow();
	}
	
	/**
	 * Registers a listener with this codec service.  It will be
	 * notified of all task submissions for the specified operation.
	 * @param op the operation to be listen for
	 * @param listener the listener to register 
	 */
	public void registerListener(Operation op, TaskListener listener) {
		if (!listeners.containsKey(op)) {
			listeners.put(op, new ArrayList<TaskListener>());
		}
		listeners.get(op).add(listener);
	}
	
	/**
	 * Notifies all listeners for the specified operation
	 * that a task was submitted.
	 * @param op the operation to notify for
	 * @param task the task that was submitted
	 */
	private void fireTaskSubmitted(Operation op, Future<?> task) {
		if (listeners.containsKey(op)) {
			for (TaskListener listener : listeners.get(op)) {
				listener.taskSubmitted(task);
			}
		}
	}
}
