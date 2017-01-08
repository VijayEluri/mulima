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
package org.mulima.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logs the progress of an operation using SLF4J INFO level messages.
 *
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class SLF4JProgressBar implements ProgressBar {
  private final Logger logger = LoggerFactory.getLogger(ProgressBar.class);
  private final String name;
  private final int total;
  private final double ratio;
  private int count = 0;
  private int percent = 0;

  /**
   * Constructs a <code>ProgressBar</code> named <code>name</code> with <code>total</code>
   * operations to complete.
   *
   * @param name the name of the operation. This will be used to prefix the log messages.
   * @param total the total number of operations to complete for this <code>ProgressBar</code>
   */
  public SLF4JProgressBar(String name, int total) {
    this.name = name;
    this.total = total;
    ratio = (double) total / 100;
    logger.info(name + ": " + percent + "% complete (" + count + " of " + total + ")");
  }

  /**
   * Notify the <code>ProgressBar</code> that another operation has completed. This will print a log
   * message if a multiple of 10% of the operations are complete.
   *
   * @return the current number of complete operations
   */
  public int next() {
    synchronized (this) {
      count++;
      if (count > total) {
        return count;
      } else if (count % ratio < 1) {
        percent++;
        if (percent % 10 == 0) {
          logger.info(name + ": " + percent + "% complete (" + count + " of " + total + ")");
        }
      }
      return count;
    }
  }

  /**
   * Notify the <code>ProgressBar</code> that all operations have completed. This is handy to ensure
   * that the completion log message is printed.
   */
  public void done() {
    synchronized (this) {
      count = total;
      logger.info(name + ": " + percent + "% complete (" + count + " of " + total + ")");
    }
  }
}
