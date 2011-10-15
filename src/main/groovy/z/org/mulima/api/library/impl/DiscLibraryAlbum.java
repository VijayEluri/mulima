package z.org.mulima.api.library.impl;

import java.io.File;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import z.org.mulima.api.file.AudioFile;
import z.org.mulima.api.file.DiscFile;
import z.org.mulima.api.library.Library;
import z.org.mulima.api.library.LibraryAlbum;
import z.org.mulima.api.meta.Album;
import z.org.mulima.api.meta.CueSheet;
import z.org.mulima.job.Context;

public class DiscLibraryAlbum extends AbstractLibraryAlbum implements LibraryAlbum {
	private final Library<DiscLibraryAlbum> lib; 
	private SortedSet<DiscFile> discFiles;
	private SortedSet<CueSheet> cues;
	private long lastModified = 0;
	
	public DiscLibraryAlbum(Context context, Library<DiscLibraryAlbum> lib, UUID id, File dir, Album album, LibraryAlbum source) {
		super(context, id, dir, album, source);
		this.lib = lib;
		this.discFiles = new TreeSet<DiscFile>(discFiles);
		this.cues = new TreeSet<CueSheet>(cues);
	}
	
	public Library<DiscLibraryAlbum> getLib() {
		return lib;
	}
	
	public SortedSet<AudioFile> getAudioFiles() {
		return new TreeSet<AudioFile>(discFiles);
	}
	
	public SortedSet<DiscFile> getDiscFiles() {
		if (getDir().lastModified() > lastModified) {
			discFiles = new TreeSet<DiscFile>();
			
		}
		return discFiles;
	}
	
	public DiscFile getAudioFile(int discNum) {
		for (DiscFile file : getDiscFiles()) {
			if (file.getDiscNum() == discNum) {
				return file;
			}
		}
		return null;
	}
	
	public SortedSet<CueSheet> getCues() {
		return cues;
	}
	
	public CueSheet getCue(int discNum) {
		for (CueSheet cue : getCues()) {
			if (cue.getNum() == discNum) {
				return cue;
			}
		}
		return null;
	}
}
