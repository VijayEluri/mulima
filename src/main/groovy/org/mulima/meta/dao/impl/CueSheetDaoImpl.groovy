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

import java.io.File;
import java.io.Writer;
import java.util.concurrent.Callable;

import org.mulima.api.meta.CueSheet;
import org.mulima.api.meta.Metadata;
import org.mulima.meta.dao.MetadataFileDao;
import org.slf4j.Logger;

class CueSheetDaoImpl implements MetadataFileDao<CueSheet> {
	private final Logger logger = LoggerFactory.getLogger(CueSheetDaoImpl.class)
	
	def tagHelper(Writer writer, Metadata item) {
		return { tag, indent ->
			def value = item.getFlat(tag)			
			writer.println "${indent}${tag.toString(value)}"	
		}
	}
	
	void write(File file, CueSheet cue) {
		def writer = new PrintWriter(file)
		def cueTag = tagHelper(writer, cue)		
		cueTag(CueSheetTag.Cue.REM_GENRE, '')
		cueTag(CueSheetTag.Cue.REM_DATE, '')
		cueTag(CueSheetTag.Cue.REM_DISCID, '')
		cueTag(CueSheetTag.Cue.PERFORMER, '')
		cueTag(CueSheetTag.Cue.TITLE, '')
		cueTag(CueSheetTag.Cue.FILE, '')
		
		cue.tracks.each { track ->
			def trackTag = tagHelper(writer, track)
			writer.println String.format('\tTRACK %1\$02d AUDIO', track.num)
			trackTag(CueSheetTag.Track.TITLE, '\t\t')
			trackTag(CueSheetTag.Track.PERFORMER, '\t\t')
			track.indices.each { index ->
				writer.println "\t\tINDEX ${index.format()}"
			}
		}
		
		writer.close()
	}
	
	Callable<Void> writeLater(File file, CueSheet cue) {
		return new Callable<Void>() {
			Void call() {
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
			
			def name = matcher[0][1].trim().replaceAll(' ', '_')
			def value = matcher[0][2].trim()
			
			if ('TRACK' == name) {
				if (track != null) {
					cue.tracks.add(track)
				}
				
				track = new CueSheet.Track()
				track.num = Integer.valueOf(value.split(' ')[0])
			} else if (track == null) {
				try {
					def tag = CueSheetTag.Cue.valueOf(name)
					cue.add(tag, value)
				} catch (IllegalArgumentException e) {
					logger.debug(e.message, e)
				}
			} else if ('INDEX' == name) {
				def index = new CueSheet.Index()
				value = value.split(' ')
				index.num = Integer.valueOf(value[0])
				index.time = value[1]
				track.indices.add(index)
			} else {
				try {
					def tag = CueSheetTag.Track.valueOf(name)
					track.add(tag, value)
				} catch (IllegalArgumentException e) {
					logger.debug(e.message, e)
				}
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
			CueSheet call() {
				return read(file)
			}
		}
	}
}