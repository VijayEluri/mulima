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
package org.mulima.internal.meta.test

import org.mulima.api.meta.CueSheet
import org.mulima.api.meta.test.MetadataFactory
import org.mulima.internal.meta.DefaultCuePoint
import org.mulima.internal.meta.DefaultCueSheet

class CueSheetHelper {
	static MetadataFactory factory = new MetadataFactory()
	
	static {
		factory.registerImplementation CueSheet, DefaultCueSheet
	}
	
	static CueSheet getExampleCue() {
		CueSheet cue = factory.fromStringString([GENRE:'Progressive Rock', RELEASE_DATE:'1972', CDDB_ID:'520C0506', ARTIST:'Genesis', ALBUM:'Foxtrot', FILE:'Foxtrot.flac'], CueSheet)
		cue.num = 1
		
		cue.allCuePoints.with {
			//Watcher of the Skies
			add new DefaultCuePoint(1, 0, '00:00:00')
			add new DefaultCuePoint(1, 1, '00:01:00')
			
			//Time Table
			add new DefaultCuePoint(2, 0, '07:24:12')
			add new DefaultCuePoint(2, 1, '07:24:16')
			
			//Get 'Em Out By Friday
			add new DefaultCuePoint(3, 0, '12:10:40')
			add new DefaultCuePoint(3, 1, '12:10:43')
			
			//Can-Utility and the Coastliners
			add new DefaultCuePoint(4, 0, '20:46:12')
			add new DefaultCuePoint(4, 1, '20:46:16')
			
			//Horizons
			add new DefaultCuePoint(5, 0, '26:31:09')
			add new DefaultCuePoint(5, 1, '26:31:13')
			
			//Supper's Ready
			add new DefaultCuePoint(6, 0, '28:12:21')
			add new DefaultCuePoint(6, 1, '28:12:25')
		}
		
		return cue
	}
	
	static void writeExampleFile(File exampleFile) {
		exampleFile.withPrintWriter { writer ->
			writer.println '''\
REM GENRE \'Progressive Rock\'
REM DATE 1972
REM DISCID 520C0506
PERFORMER \'Genesis\'
TITLE \'Foxtrot\'
FILE \'Foxtrot.flac\' WAVE
  TRACK 01 AUDIO
    TITLE \'Watcher of the Skies\'
    PERFORMER \'Genesis\'
    INDEX 00 00:00:00
    INDEX 01 00:01:00
  TRACK 02 AUDIO
    TITLE \'Time Table\'
    PERFORMER \'Genesis\'
    INDEX 00 07:24:12
    INDEX 01 07:24:16
  TRACK 03 AUDIO
    TITLE \'Get \'Em Out By Friday\'
    PERFORMER \'Genesis\'
    INDEX 00 12:10:40
    INDEX 01 12:10:43
  TRACK 04 AUDIO
    TITLE \'Can-Utility and the Coastliners\'
    PERFORMER \'Genesis\'
    INDEX 00 20:46:12
    INDEX 01 20:46:16
  TRACK 05 AUDIO
    TITLE \'Horizons\'
    PERFORMER \'Genesis\'
    INDEX 00 26:31:09
    INDEX 01 26:31:13
  TRACK 06 AUDIO
    TITLE \'Supper\'s Ready\'
    PERFORMER \'Genesis\'
    INDEX 00 28:12:21
    INDEX 01 28:12:25
'''
			writer.close()
		}
	}
}
