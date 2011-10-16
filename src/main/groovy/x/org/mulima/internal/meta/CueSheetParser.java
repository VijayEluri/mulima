package x.org.mulima.internal.meta;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mulima.exception.UncheckedIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import x.org.mulima.api.file.FileParser;
import x.org.mulima.api.meta.CueSheet;

public class CueSheetParser implements FileParser<CueSheet> {
	private static final Logger logger = LoggerFactory.getLogger(CueSheetParser.class);
	private static final Pattern NUM_REGEX = Pattern.compile(".*\\(([0-9])\\)\\.cue");
	private static final Pattern LINE_REGEX = Pattern.compile("^((?:REM )?[A-Z0-9]+) [\"']?([^\"']*)[\"']?.*$");

	@Override
	public CueSheet parse(File file) {
		Matcher matcher = NUM_REGEX.matcher(file.getName());
		int num = matcher.find() ? Integer.valueOf(matcher.group(1)) : 1;
		CueSheet cue = new DefaultCueSheet(num);
		
		Scanner fin;
		try {
			fin = new Scanner(file);
		} catch (FileNotFoundException e) {
			throw new UncheckedIOException(e);
		}
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
				cue.getCuePoints().add(new DefaultCuePoint(currentTrack, index, time));
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

}
