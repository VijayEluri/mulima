/*
*  Copyright (C) 2011  Andrew Oberstar.  All rights reserved.
*  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
*
*  This program is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*
*  This program is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  You should have received a copy of the GNU General Public License
*  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.mulima.meta.dao.impl

import java.util.concurrent.Callable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.mulima.meta.CueSheet
import org.mulima.meta.impl.CueSheetTag
import org.mulima.meta.dao.MetadataFileDao

class CueSheetDaoImpl implements MetadataFileDao<CueSheet> {
	private final Logger logger = LoggerFactory.getLogger(CueSheetDaoImpl.class)
	
	def tagHelper(def writer, def item) {
		return { tag, indent ->
			def value = item.getFlat(tag)			
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
	
	Callable<Void> writeLater(File file, CueSheet cue) {
		return new Callable<Void>() {
			public Void call() {
				write(file, cue)
			}
		}
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
				cue.add(tag, value)
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
				track.add(tag, value)
			}
		}
		
		if (track != null) {
			cue.tracks.add(track)
		}
		
		def m = cue.getFirst(CueSheetTag.Cue.FILE) =~ /.*\(([0-9])\)\.flac/
		cue.num = m ? Integer.valueOf(m[0][1]) : 1
		
		return cue
	}
	
	Callable<CueSheet> readLater(File file) {
		return new Callable<CueSheet>() {
			public CueSheet call() {
				return read(file)
			}
		}
	}
}