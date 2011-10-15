package x.org.mulima.internal.job;

import java.util.concurrent.Callable;

public interface Job<T> extends Callable<T>{
	T execute();
}
