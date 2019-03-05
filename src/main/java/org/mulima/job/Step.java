package org.mulima.job;

import java.util.concurrent.Callable;

/**
 * An object that will execute an action to perform a task.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 * @param <T> the return type of the step
 */
public interface Step<T> extends Callable<T> {
  /**
   * Executes the step.
   *
   * @return {@code true} if the step succeeded, {@code false} otherwise
   */
  boolean execute();

  /**
   * Gets the current status of the step.
   *
   * @return the status
   */
  Status getStatus();

  /**
   * Gets the outputs of the step.
   *
   * @return the outputs
   */
  T getOutputs();
}
