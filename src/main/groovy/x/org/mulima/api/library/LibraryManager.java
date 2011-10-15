package x.org.mulima.api.library;

import java.util.Set;

public interface LibraryManager {
	Set<ReferenceLibrary> getRefLibs();
	Set<Library> getDestLibs();
	void updateAll();
	void update(Library lib);
	void update(Set<Library> libs);
}
