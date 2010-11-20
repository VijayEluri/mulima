package com.andrewoberstar.library.meta.dao.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.andrewoberstar.library.meta.CueSheet
import com.andrewoberstar.library.meta.CueSheetTag
import com.andrewoberstar.library.meta.dao.FileMetadataDao

class CueSheetDaoImpl implements FileMetadataDao<CueSheet> {
	private final Logger logger = LoggerFactory.getLogger(CueSheetDaoImpl.class)
	
	def tagHelper(def writer, def item) {
		return { tag, indent ->
			def value = item.tags.getFlat(tag)			
			writer.println "${indent}${tag.toString(value)}"	
		}
	}
	
	void write(File file, CueSheet cue) {
		def writer = new PrintWriter(file)
		def cueTag = tagHelper(writer, cue)		
		cueTag(CueSheetTag.Cue.REM_GENRE, "")
		cueTag(CueSheetTag.Cue.REM_DATE, "")
		cueTag(CueSheetTag.Cue.REM_DISCID, "")
		cueTag(CueSheetTag.Cue.PERFORMER, "")
		cueTag(CueSheetTag.Cue.TITLE, "")
		cueTag(CueSheetTag.Cue.FILE, "")
		
		cue.tracks.each { track ->
			def trackTag = tagHelper(writer, track)
			writer.println String.format("  TRACK %1\$02d AUDIO", track.num)
			trackTag(CueSheetTag.Track.TITLE, "    ")
			trackTag(CueSheetTag.Track.PERFORMER, "    ")
			track.indices.each { index ->
				writer.println "    INDEX ${index.format()}"
			}
		}
		
		writer.close()
	}
	
	CueSheet read(File file) {
		def cue = new CueSheet()
		cue.file = file
		
		def track = null
		file.eachLine {
			def matcher = it.trim() =~ /^((?:REM )?[A-Z0-9]+) "?([^"]*)"?.*$/
			if (!matcher) {
				logger.debug("Invalid line: ${it}")
			}
			
			def name = matcher[0][1].trim().replaceAll(" ", "_")
			def value = matcher[0][2].trim()
			
			if ("TRACK" == name) {
				if (track != null) {
					cue.tracks.add(track)
				}
				
				track = new CueSheet.Track()
				track.num = Integer.valueOf(value.split(" ")[0])
			} else if (track == null) {
				def tag
				try {
					tag = CueSheetTag.Cue.valueOf(name)
				} catch (IllegalArgumentException e) {
					logger.debug(e.getMessage(), e)
					return
				}
				cue.tags.add(tag, value)
			} else if ("INDEX" == name) {
				def index = new CueSheet.Index()
				value = value.split(" ")
				index.num = Integer.valueOf(value[0])
				index.time = value[1]
				track.indices.add(index)
			} else {
				def tag
				try {
					tag = CueSheetTag.Track.valueOf(name)
				} catch (IllegalArgumentException e) {
					logger.debug(e.getMessage(), e)
					return
				}
				track.tags.add(tag, value)
			}
		}
		
		if (track != null) {
			cue.tracks.add(track)
		}
		
		def m = cue.tags.getFirst(CueSheetTag.Cue.FILE) =~ /.*\(([0-9])\)\.flac/
		cue.num = m ? Integer.valueOf(m[0][1]) : 1
		
		return cue
	}
}