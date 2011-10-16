package org.mulima.internal.job;

import java.util.concurrent.Callable;

public interface Step<T> extends Callable<T>{
	boolean execute();
	Status getStatus();
	T getOutputs();
}
