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
package org.mulima.internal.proc

import java.util.concurrent.Callable

import org.mulima.api.proc.ProcessResult
import org.mulima.exception.UncheckedMulimaException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Executes a <code>Process</code>.  This is an alternative to {@link ProcessBuilder#start()}
 * and {@link Runtime#exec(String)} that will give you a {@link ProcessResult} object.
 * @author Andrew Oberstar
 * @since 0.1.0
 */
class ProcessCaller implements Callable {
  private static final Logger logger = LoggerFactory.getLogger(ProcessCaller)
  private final String description
  private final List command
  private final String input;

  /**
   * Constructs a process caller with the specified operating system program and arguments.
   * @param command the list containing the program and its arguments.
   * @param input the input to this process (optional)
   */
  ProcessCaller(List command, String input = null) {
    this(null, command, input)
  }

  /**
  * Constructs a process caller with the specified operating system program and arguments.
  * @param description a description of the process to be executed
  * @param command the list containing the program and its arguments.
  * @param input the input to this process (optional)
  */
   ProcessCaller(String description, List command, String input = null) {
     this.description = description
     this.command = command
     this.input = input
   }

  /**
   * Starts a process using the command specified in the constructor.
   * @return a process result holding the output of the process.
   * @throws FatalMulimaException if there is a problem with the process
   */
  @Override
  ProcessResult call() {
    logger.debug('Starting: {}', description)
    logger.debug('Executing command: {}', command)
    Process proc = new ProcessBuilder(command).start()

    if (input) {
      proc.outputStream.withWriter { writer ->
        writer.write(input)
      }
    }

    StringBuilder output = new StringBuilder()
    StringBuilder error = new StringBuilder()
    proc.waitForProcessOutput(output, error)
    int exit = proc.exitValue()
    logger.debug('Finished: {}', description)
    return new ProcessResult(command, exit, output.toString(), error.toString())
  }
}
