package z.org.mulima.api.file;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class ViewSet<E, V> implements Set<V> {
	private final Set<E> source;
	private final Viewer<E, V> viewer;
	
	public ViewSet(Set<E> source, Viewer<E, V> viewer) {
		this.source = source;
		this.viewer = viewer;
	}
	
	@Override
	public int size() {
		return source.size();
	}

	@Override
	public boolean isEmpty() {
		return source.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		if (o == null) {
			return false;
		}
		for (V elem : this) {
			if (o.equals(elem)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterator<V> iterator() {
		return new ViewIterator<E, V>(source.iterator(), viewer);
	}

	@Override
	public Object[] toArray() {
		Object[] array = new Object[size()];
		int i = 0;
		for (V elem : this) {
			array[i++] = elem;
		}
		return array;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		T[] array;
		if (a.length >= size()) {
			array = a;
		} else {
			array = (T[]) new Object[size()];
		}
		int i = 0;
		for (V elem : this) {
			array[i++] = (T) elem;
		}
		return array;
	}

	@Override
	public boolean add(V e) {
		throw new UnsupportedOperationException("Not supported for this class.");
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("Not supported for this class.");
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends V> c) {
		throw new UnsupportedOperationException("Not supported for this class.");
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("Not supported for this class.");
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("Not supported for this class.");
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException("Not supported for this class.");
	}
}
