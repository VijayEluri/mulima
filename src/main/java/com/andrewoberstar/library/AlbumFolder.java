package com.andrewoberstar.library;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.andrewoberstar.library.audio.AudioFile;
import com.andrewoberstar.library.meta.Album;
import com.andrewoberstar.library.meta.CueSheet;
import com.andrewoberstar.library.meta.GenericTag;

public class AlbumFolder {
	private Album album = null;
	private File folder = null;
	private List<AudioFile> audioFiles = new ArrayList<AudioFile>();
	private List<CueSheet> cues = new ArrayList<CueSheet>();
	
	public AlbumFolder() {
		this(null);
	}
	
	public AlbumFolder(File folder) {
		setFolder(folder);
	}
	
	public Album getAlbum() {
		return album;
	}
	
	public void setAlbum(Album album) {
		this.album = album;
	}
	
	public File getFolder() {
		return folder;
	}
	
	public void setFolder(File folder) {
		this.folder = folder;
	}
	
	public List<AudioFile> getAudioFiles() {
		return audioFiles;
	}
	
	public void setAudioFiles(List<AudioFile> audioFiles) {
		this.audioFiles = audioFiles;
	}
	
	public AudioFile getAudioFile(CueSheet cue) {
		for (AudioFile file : getAudioFiles()) {
			int num = parseNum(file.getFile());
			if (num == cue.getNum())
				return file;
		}
		return null;
	}
	
	public List<CueSheet> getCues() {
		return cues;
	}
	
	public void setCues(List<CueSheet> cues) {
		this.cues = cues;
	}
	
	public CueSheet getCue(AudioFile file) {
		int num = parseNum(file.getFile());
		for (CueSheet cue : getCues()) {
			if (num == cue.getNum())
				return cue;
		}
		return null;
	}
	
	public static AlbumFolder createAlbumFolder(File rootDir, Album album) throws IOException {
		String artist = album.getTags().getFlat(GenericTag.ARTIST);
		String title = album.getTags().getFlat(GenericTag.ALBUM);
		File dir = new File(rootDir, artist + File.separator + title);
		if (!dir.mkdirs())
			throw new IOException("Could not make directory: " + dir.getCanonicalPath());
		
		AlbumFolder temp = new AlbumFolder(dir);
		temp.setAlbum(album);
		return temp;
	}
	
	private int parseNum(File file) {
		Matcher m = Pattern.compile(".*\\(([0-9]+)\\)\\.(flac|cue)").matcher(file.getName());
		return m.matches() ? Integer.valueOf(m.group(1)) : 1;
	}
}
