package org.mulima.meta.dao.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mulima.api.meta.CuePoint;
import org.mulima.api.meta.CueSheet;
import org.mulima.api.meta.impl.CueSheetTag;
import org.mulima.meta.dao.MetadataFileDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO that will read CueSheets.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class CueSheetDaoImpl implements MetadataFileDao<CueSheet> {
	private static final Pattern NUM_REGEX = Pattern.compile(".*\\(([0-9])\\)\\.cue");
	private static final Pattern LINE_REGEX = Pattern.compile("^((?:REM )?[A-Z0-9]+) [\"']?([^\"']*)[\"']?.*$");
	private final Logger logger = LoggerFactory.getLogger(getClass()); 
	
//	private String formatTag(CueSheetTag.Cue tag, String value, String indent) {
//		return indent + tag.toString(value);
//	}
	
	/**
	 * This is not implemented.
	 * @param file
	 * @param cue
	 * @throws FileNotFoundException
	 */
	@Override
	public void write(File file, CueSheet cue) throws FileNotFoundException {
		throw new UnsupportedOperationException("This method is not implemented at this time.");
//		PrintWriter writer = new PrintWriter(file);
//		
//		for (CueSheetTag.Cue tag : CueSheetTag.Cue.values()) {
//			writer.println(formatTag(tag, cue.getFlat(tag), ""));
//		}
//		
//		int currentTrack = -1;
//		for (CuePoint point : cue.getAllCuePoints()) {
//			if (point.getTrack() != currentTrack) {
//				writer.println(String.format("\tTRACK %1$02d AUDIO", point.getTrack()));
//				currentTrack = point.getTrack();
//			}
//			writer.println("\t\tINDEX " + point.getIndex() + " " + point.getTime());
//		}
//		
//		writer.close();
	}

	/**
	 * Parses a CueSheet from the specified file.
	 * @param file the file to parse
	 * @throws FileNotFoundException if the file does not exist
	 */
	@Override
	public CueSheet read(File file) throws FileNotFoundException {
		Matcher matcher = NUM_REGEX.matcher(file.getName());
		int num = matcher.find() ? Integer.valueOf(matcher.group(1)) : 1;
		CueSheet cue = new CueSheet(num, file);
		
		Scanner fin = new Scanner(file);
		int currentTrack = -1;
		while (fin.hasNext()) {
			String line = fin.nextLine().trim();
			matcher = LINE_REGEX.matcher(line);
			if (!matcher.find()) {
				logger.debug("Invalid line: " + line);
			}
			
			String name = matcher.group(1).trim().replaceAll(" ", "_");
			String value = matcher.group(2).trim();
			
			if ("TRACK".equals(name)) {
				currentTrack = Integer.valueOf(value.split(" ")[0]);
			} else if (currentTrack < 0) {
				try {
					CueSheetTag.Cue tag = CueSheetTag.Cue.valueOf(name);
					cue.add(tag, value);
				} catch (IllegalArgumentException e) {
					logger.debug(e.getMessage(), e);
				}
			} else if ("INDEX".equals(name)) {
				String[] values = value.split(" ");
				int index = Integer.valueOf(values[0]);
				String time = values[1];
				cue.getCuePoints().add(new CuePoint(currentTrack, index, time));
//			} else {
//				try {
//					CueSheetTag.Track tag = CueSheetTag.Track.valueOf(name);
//					track.add(tag, value);
//				} catch (IllegalArgumentException e) {
//					logger.debug(e.getMessage(), e);
//				}
			}
		}
		
		return cue;
	}

	/**
	 * This is not implemented.
	 * @param file
	 * @param cue
	 * @return 
	 */
	@Override
	public Callable<Void> writeLater(final File file, final CueSheet meta) {
		return new Callable<Void>() {
			public Void call() throws FileNotFoundException {
				write(file, meta);
				return null;
			}
		};
	}

	/**
	 * Creates a Callable that will parse a CueSheet from the specified file.
	 * @param file the file to parse
	 * @return a Callable to parse the file
	 */
	@Override
	public Callable<CueSheet> readLater(final File file) {
		return new Callable<CueSheet>() {
			public CueSheet call() throws FileNotFoundException {
				return read(file);
			}
		};
	}
}
