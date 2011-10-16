package org.mulima.api.library;

import java.util.Set;

public interface LibraryManager {
	void updateAll();
	void update(Library lib);
	void update(Set<Library> libs);
}
