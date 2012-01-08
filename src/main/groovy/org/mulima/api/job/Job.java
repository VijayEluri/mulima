package org.mulima.api.job;

import java.util.concurrent.Callable;

/**
 * An object that will execute a series of steps
 * to perform a task.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 * 
 * @param <T> the return type of the job
 */
public interface Job<T> extends Callable<T>{
	/**
	 * Executes the job.
	 * @return the result of the job
	 */
	T execute();
}
