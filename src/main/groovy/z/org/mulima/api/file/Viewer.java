package z.org.mulima.api.file;

public interface Viewer<T, E> {
	E view(T object);
}
