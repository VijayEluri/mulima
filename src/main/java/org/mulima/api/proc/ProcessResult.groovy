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
package org.mulima.api.proc

import org.mulima.util.StringUtil

/**
 * Represents the result of a Process execution.  Provides
 * access to the exit value as well as the standard out
 * and standard error output.
 * @author Andrew Oberstar
 * @since 0.1.0
 */
class ProcessResult {
  /**
   * The command that was executed
   */
  final String command

  /**
   * The exit value of the process
   */
  final int exitVal

  /**
   * The system output of the process
   */
  final String output

  /**
   * The system error output of the process
   */
  final String error

  ProcessResult(List<String> command, int exitVal, String output, String error) {
    this(StringUtil.join(command, ' '), exitVal, output, error)
  }

  ProcessResult(String command, int exitVal, String output, String error) {
    this.command = command
    this.exitVal = exitVal
    this.output = output
    this.error = error
  }

  /**
   * @return true if the process was successful (exit value
   * of 0), false otherwise
   */
  boolean isSuccess() {
    return exitVal == 0
  }
}
