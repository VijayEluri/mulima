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
 * Represents the result of a tagger operation.  Provides access to the
 * process's exit value, the file and metadata.
 * @author Andrew Oberstar
 * @since 0.1.0
 */
class TaggerResult extends ProcessResult {
  /**
   * The file to read or write to
   */
  final AudioFile file

  /**
   * Constructs a tagger result from a process result.
   * @param file the file read from or written to
   * @param result the result of the tagger process
   */
  TaggerResult(AudioFile file, ProcessResult result) {
    this(file, result.command, result.exitVal, result.output, result.error)
  }

  /**
   * Constructs a tagger result from the parameters.
   * @param file the file read from or written to
   * @param command the command executed
   * @param exitVal the exit value of the process
   * @param output the std out of the process
   * @param error the std err of the process
   */
  TaggerResult(AudioFile file, String command, int exitVal, String output, String error) {
    super(command, exitVal, output, error)
    this.file = file
  }
}
