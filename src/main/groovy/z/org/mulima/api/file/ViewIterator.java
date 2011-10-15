package z.org.mulima.api.file;

import java.util.Iterator;

public class ViewIterator<E, V> implements Iterator<V> {
	private final Iterator<E> source;
	private final Viewer<E, V> viewer;
	
	public ViewIterator(Iterator<E> source, Viewer<E, V> viewer) {
		this.source = source;
		this.viewer = viewer;
	}
	
	@Override
	public boolean hasNext() {
		return source.hasNext();
	}

	@Override
	public V next() {
		return viewer.view(source.next());
	}

	@Override
	public void remove() {
		source.remove();
	}

}
