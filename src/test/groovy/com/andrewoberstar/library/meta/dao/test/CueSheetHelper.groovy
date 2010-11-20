/*
*  Copyright (C) 2010  Andrew Oberstar.  All rights reserved.
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

package com.andrewoberstar.library.meta.dao.test

import com.andrewoberstar.library.meta.CueSheet
import com.andrewoberstar.library.meta.TagSupportFactory;

class CueSheetHelper {
	static CueSheet getExampleCue() {
		def cue
		
		cue = new CueSheet()
		cue.num = 1
		cue.tags = TagSupportFactory.fromStringString([GENRE:'Progressive Rock', RELEASE_DATE:'1972', CDDB_ID:'520C0506', ARTIST:'Genesis', ALBUM:'Foxtrot', FILE:'Foxtrot.flac'])
		
		cue.tracks.add(
			createTrack(
				1, 
				[TITLE:'Watcher of the Skies', ARTIST:'Genesis'], 
				['00:00:00', '00:01:00']
			)
		)
		
		cue.tracks.add(
			createTrack(
				2,
				[TITLE:'Time Table', ARTIST:'Genesis'],
				['07:24:12', '07:24:16']
			)
		)
		
		cue.tracks.add(
			createTrack(
				3,
				[TITLE:"Get 'Em Out By Friday", ARTIST:'Genesis'],
				['12:10:40', '12:10:43']
			)
		)
				
		cue.tracks.add(
			createTrack(
				4,
				[TITLE:'Can-Utility and the Coastliners', ARTIST:'Genesis'],
				['20:46:12', '20:46:16']
			)
		)

		cue.tracks.add(
			createTrack(
				5,
				[TITLE:'Horizons', ARTIST:'Genesis'],
				['26:31:09', '26:31:13']
			)
		)
			
		cue.tracks.add(
			createTrack(
				6,
				[TITLE:"Supper's Ready", ARTIST:'Genesis'],
				['28:12:21', '28:12:25']
			)
		)
		
		return cue
	}
	
	private static def createTrack(def num, def tags, def indices) {
		def track = new CueSheet.Track()
		track.num = num
		track.tags = TagSupportFactory.fromStringString(tags)

		indices.eachWithIndex { time, i ->
			if (time == '') {
				return
			}
			
			def index = new CueSheet.Index()
			index.num = i
			index.time = time
			track.indices.add(index)
		}
		return track
	}
	
	static void writeExampleFile(File exampleFile) {
		def fout = new PrintWriter(exampleFile)
		
		fout.println("REM GENRE \"Progressive Rock\"")
		fout.println("REM DATE 1972")
		fout.println("REM DISCID 520C0506")
		fout.println("PERFORMER \"Genesis\"")
		fout.println("TITLE \"Foxtrot\"")
		fout.println("FILE \"Foxtrot.flac\" WAVE")
		fout.println("  TRACK 01 AUDIO")
		fout.println("    TITLE \"Watcher of the Skies\"")
		fout.println("    PERFORMER \"Genesis\"")
		fout.println("    INDEX 00 00:00:00")
		fout.println("    INDEX 01 00:01:00")
		fout.println("  TRACK 02 AUDIO")
		fout.println("    TITLE \"Time Table\"")
		fout.println("    PERFORMER \"Genesis\"")
		fout.println("    INDEX 00 07:24:12")
		fout.println("    INDEX 01 07:24:16")
		fout.println("  TRACK 03 AUDIO")
		fout.println("    TITLE \"Get 'Em Out By Friday\"")
		fout.println("    PERFORMER \"Genesis\"")
		fout.println("    INDEX 00 12:10:40")
		fout.println("    INDEX 01 12:10:43")
		fout.println("  TRACK 04 AUDIO")
		fout.println("    TITLE \"Can-Utility and the Coastliners\"")
		fout.println("    PERFORMER \"Genesis\"")
		fout.println("    INDEX 00 20:46:12")
		fout.println("    INDEX 01 20:46:16")
		fout.println("  TRACK 05 AUDIO")
		fout.println("    TITLE \"Horizons\"")
		fout.println("    PERFORMER \"Genesis\"")
		fout.println("    INDEX 00 26:31:09")
		fout.println("    INDEX 01 26:31:13")
		fout.println("  TRACK 06 AUDIO")
		fout.println("    TITLE \"Supper's Ready\"")
		fout.println("    PERFORMER \"Genesis\"")
		fout.println("    INDEX 00 28:12:21")
		fout.println("    INDEX 01 28:12:25")
		
		fout.close()
	}
}

/*
		//Track 1
		track = new CueSheet.Track()
		track.num = 1
		track.tags = TagSupportFactory.fromStringString([TITLE:'Watcher of the Skies', ARTIST:'Genesis'])

		index = new CueSheet.Index()
		index.num = 0
		index.time = '00:00:00'
		track.indices.add(index)
		
		index = new CueSheet.Index()
		index.num = 1
		index.time = '00:01:00'
		track.indices.add(index)
		
		cue.tracks.add(track)
		
		
		//Track 2
		track = new CueSheet.Track()
		track.num = 2
		track.tags = TagSupportFactory.fromStringString([TITLE:'Time Table', ARTIST:'Genesis'])

		index = new CueSheet.Index()
		index.num = 0
		index.time = '07:24:12'
		track.indices.add(index)
		
		index = new CueSheet.Index()
		index.num = 1
		index.time = '07:24:16'
		track.indices.add(index)
		
		cue.tracks.add(track)

		//Track 3
		track = new CueSheet.Track()
		track.num = 3
		track.tags = TagSupportFactory.fromStringString([TITLE:"Get 'Em Out By Friday", ARTIST:'Genesis'])

		index = new CueSheet.Index()
		index.num = 0
		index.time = '12:10:40'
		track.indices.add(index)
		
		index = new CueSheet.Index()
		index.num = 1
		index.time = '12:10:43'
		track.indices.add(index)
		
		cue.tracks.add(track)
		
		//Track 4
		track = new CueSheet.Track()
		track.num = 4
		track.tags = TagSupportFactory.fromStringString([TITLE:'Can-Utility and the Coastliners', ARTIST:'Genesis'])

		index = new CueSheet.Index()
		index.num = 0
		index.time = '20:46:12'
		track.indices.add(index)
		
		index = new CueSheet.Index()
		index.num = 1
		index.time = '20:46:16'
		track.indices.add(index)
		
		cue.tracks.add(track)

		//Track 5
		track = new CueSheet.Track()
		track.num = 5
		track.tags = TagSupportFactory.fromStringString([TITLE:'Horizons', ARTIST:'Genesis'])

		index = new CueSheet.Index()
		index.num = 0
		index.time = '26:31:09'
		track.indices.add(index)
		
		index = new CueSheet.Index()
		index.num = 1
		index.time = '26:31:13'
		track.indices.add(index)
		
		cue.tracks.add(track)

		//Track 6
		track = new CueSheet.Track()
		track.num = 6
		track.tags = TagSupportFactory.fromStringString([TITLE:"Supper's Ready", ARTIST:'Genesis'])

		index = new CueSheet.Index()
		index.num = 0
		index.time = '28:12:21'
		track.indices.add(index)
		
		index = new CueSheet.Index()
		index.num = 1
		index.time = '28:12:25'
		track.indices.add(index)
		
		cue.tracks.add(track)
		
*/