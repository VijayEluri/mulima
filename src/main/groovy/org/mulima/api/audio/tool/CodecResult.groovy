/*
 * Copyright 2010-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mulima.api.audio.tool

import org.mulima.api.file.audio.AudioFile
import org.mulima.api.proc.ProcessResult

/**
 * Represents the result of a codec operation.  Provides access to the
 * process's exit value, the source and destination files.
 * @author Andrew Oberstar
 * @since 0.1.0
 */
class CodecResult extends ProcessResult {
  /**
   * The source of the operation
   */
  final AudioFile source

  /**
   * The destination for the operation
   */
  final AudioFile dest

  /**
   * Constructs a codec result from a process result.
   * @param source the source of the codec operation
   * @param dest the destination of the codec operation
   * @param result the result of the codec process
   */
  CodecResult(AudioFile source, AudioFile dest, ProcessResult result) {
    this(source, dest, result.command, result.exitVal, result.output, result.error)
  }

  /**
   * Constructs a codec result from the parameters.
   * @param source the source of the codec operation
   * @param dest the destination of the codec operation
   * @param command the command executed
   * @param exitVal the exit value of the process
   * @param output the std out of the process
   * @param error the std err of the process
   */
  CodecResult(AudioFile source, AudioFile dest, String command, int exitVal, String output, String error) {
    super(command, exitVal, output, error)
    this.source = source
    this.dest = dest
  }
}
