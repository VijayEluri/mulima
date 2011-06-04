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
package org.mulima.meta.dao.impl.test

import org.mulima.api.meta.CuePoint
import org.mulima.api.meta.CueSheet
import org.mulima.api.meta.dao.impl.test.MetadataFactory

class CueSheetHelper {
	static CueSheet getExampleCue() {
		def cue = MetadataFactory.fromStringString([GENRE:'Progressive Rock', RELEASE_DATE:'1972', CDDB_ID:'520C0506', ARTIST:'Genesis', ALBUM:'Foxtrot', FILE:'Foxtrot.flac'], CueSheet.class)
		cue.num = 1
		
		cue.cuePoints.with {
			//Watcher of the Skies
			add new CuePoint(1, 0, '00:00:00')
			add new CuePoint(1, 1, '00:01:00')
			
			//Time Table
			add new CuePoint(1, 0, '07:24:12')
			add new CuePoint(1, 1, '07:24:16')
			
			//Get 'Em Out By Friday
			add new CuePoint(1, 0, '12:10:40')
			add new CuePoint(1, 1, '12:10:43')
			
			//Can-Utility and the Coastliners
			add new CuePoint(1, 0, '20:46:12')
			add new CuePoint(1, 1, '20:46:16')
			
			//Horizons
			add new CuePoint(1, 0, '26:31:09')
			add new CuePoint(1, 1, '26:31:13')
			
			//Supper's Ready
			add new CuePoint(1, 0, '28:12:21')
			add new CuePoint(1, 1, '28:12:25')
		}
		
		return cue
	}
	
	static void writeExampleFile(File exampleFile) {
		exampleFile.withPrintWriter {
			it.with {
				println('REM GENRE \'Progressive Rock\'')
				println('REM DATE 1972')
				println('REM DISCID 520C0506')
				println('PERFORMER \'Genesis\'')
				println('TITLE \'Foxtrot\'')
				println('FILE \'Foxtrot.flac\' WAVE')
				println('  TRACK 01 AUDIO')
				println('    TITLE \'Watcher of the Skies\'')
				println('    PERFORMER \'Genesis\'')
				println('    INDEX 00 00:00:00')
				println('    INDEX 01 00:01:00')
				println('  TRACK 02 AUDIO')
				println('    TITLE \'Time Table\'')
				println('    PERFORMER \'Genesis\'')
				println('    INDEX 00 07:24:12')
				println('    INDEX 01 07:24:16')
				println('  TRACK 03 AUDIO')
				println('    TITLE \'Get \'Em Out By Friday\'')
				println('    PERFORMER \'Genesis\'')
				println('    INDEX 00 12:10:40')
				println('    INDEX 01 12:10:43')
				println('  TRACK 04 AUDIO')
				println('    TITLE \'Can-Utility and the Coastliners\'')
				println('    PERFORMER \'Genesis\'')
				println('    INDEX 00 20:46:12')
				println('    INDEX 01 20:46:16')
				println('  TRACK 05 AUDIO')
				println('    TITLE \'Horizons\'')
				println('    PERFORMER \'Genesis\'')
				println('    INDEX 00 26:31:09')
				println('    INDEX 01 26:31:13')
				println('  TRACK 06 AUDIO')
				println('    TITLE \'Supper\'s Ready\'')
				println('    PERFORMER \'Genesis\'')
				println('    INDEX 00 28:12:21')
				println('    INDEX 01 28:12:25')
				close()
			}
		}
	}
}
