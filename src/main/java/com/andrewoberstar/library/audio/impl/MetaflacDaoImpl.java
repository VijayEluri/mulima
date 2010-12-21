package com.andrewoberstar.library.audio.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.andrewoberstar.library.audio.AudioFile;
import com.andrewoberstar.library.audio.MetadataUtil;
import com.andrewoberstar.library.meta.GenericTag;
import com.andrewoberstar.library.meta.Metadata;
import com.andrewoberstar.library.meta.Track;
import com.andrewoberstar.library.meta.VorbisTag;
import com.andrewoberstar.library.util.ProcessCaller;

public class MetaflacDaoImpl implements MetadataUtil {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private String path = "metaflac";
	private String opts = "";
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public void setOpts(String opts) {
		this.opts = opts;
	}
	
	@Override
	public AudioFile write(AudioFile file, Metadata meta) throws Exception {
		return writeLater(file, meta).call();
	}

	@Override
	public Metadata read(AudioFile file) throws Exception {
		return readLater(file).call();
	}

	@Override
	public Callable<AudioFile> writeLater(AudioFile file, Metadata meta) {
		return new Writer(file, meta);
	}

	@Override
	public Callable<Metadata> readLater(AudioFile file) {
		return new Reader(file);
	}

	private class Writer implements Callable<AudioFile> {
		private final AudioFile file;
		private final Metadata meta;
		
		public Writer(AudioFile file, Metadata meta) {
			this.file = file;
			this.meta = meta;
		}
		
		@Override
		public AudioFile call() throws Exception {
			String filePath;
			try {
				filePath = file.getFile().getCanonicalPath();
			} catch (IOException e) {
				logger.error("Problem getting path.", e);
				return null;
			}
			
			List<String> command = new ArrayList<String>();
			command.add(path);
			if (!"".equals(opts))
				command.add(opts);
			command.add("--remove-all-tags");
			
			for (GenericTag generic : meta.getTags().getMap().keySet()) {
				VorbisTag tag = VorbisTag.valueOf(generic);
				if (tag != null) {
					for (String value : meta.getTags().getAll(tag)) {
						command.add("--set-tag=" + tag.toString() + "=" + value);
					}
				}
				
			}
			
			command.add("\"" + filePath + "\"");
			
			new ProcessCaller("setting tags on " + filePath, command).call();
			return file;
		}
	}
	
	private class Reader implements Callable<Metadata> {
		private final AudioFile file;
		
		public Reader(AudioFile file) {
			this.file = file;
		}
		
		@Override
		public Metadata call() throws Exception {
			String filePath;
			try {
				filePath = file.getFile().getCanonicalPath();
			} catch (IOException e) {
				logger.error("Problem getting path.", e);
				return null;
			}
			
			List<String> command = new ArrayList<String>();
			command.add(path);
			if (!"".equals(opts))
				command.add(opts);
			command.add("--list");
			command.add("--block-type=VORBIS_COMMENT");
			command.add("\"" + filePath + "\"");
			
			String output = new ProcessCaller("reading tags from " + filePath, command).call();
			
			Track track = new Track();
			Pattern regex = Pattern.compile("comment\\[[0-9]+\\]: ([A-Za-z]+)=(.+)");
			for (String line : output.split("\n")) {
				Matcher matcher = regex.matcher(line.trim());
				if (matcher.matches()) {
					String name = matcher.group(1).toUpperCase();
					String value = matcher.group(2);
					
					VorbisTag tag = VorbisTag.valueOf(name);
					if (tag != null) {
						track.getTags().add(tag, value);
					}
				}
			}
			
			return track;
		}
	}
}
