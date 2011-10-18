package org.mulima.api.library;

import java.util.Set;

public interface LibraryManager {
	void processNew();
	void updateAll();
	void update(Library lib);
	void update(Set<Library> libs);
}
