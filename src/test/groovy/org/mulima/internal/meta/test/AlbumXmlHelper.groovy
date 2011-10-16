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

import groovy.xml.MarkupBuilder

import org.mulima.api.meta.Album
import org.mulima.api.meta.Track
import org.mulima.api.meta.dao.impl.test.MetadataFactory
import org.mulima.internal.meta.DefaultAlbum
import org.mulima.internal.meta.DefaultDisc
import org.mulima.internal.meta.DefaultTrack

class AlbumXmlHelper {
	static Album getExampleAlbum() {
		def album
		def disc
		
		album = MetadataFactory.fromStringString([ARTIST:'Genesis', ALBUM:'Foxtrot', GENRE:'Progressive Rock', DATE:'1972', CDDB_ID:'520C0506'], DefaultAlbum.class)
		
		def cue = CueSheetHelper.exampleCue
		
		disc = MetadataFactory.fromStringString([DISC_NUMBER:'1'], DefaultDisc.class)
		
		disc.tracks.add(createTrack([TRACK_NUMBER:'1', TITLE:'Watcher of the Skies']))
		disc.tracks.add(createTrack([TRACK_NUMBER:'2', TITLE:'Time Table']))
		disc.tracks.add(createTrack([TRACK_NUMBER:'3', TITLE:"Get 'Em Out By Friday"]))
		disc.tracks.add(createTrack([TRACK_NUMBER:'4', TITLE:'Can-Utility and the Coastliners']))
		disc.tracks.add(createTrack([TRACK_NUMBER:'5', TITLE:'Horizons']))
		disc.tracks.add(createTrack([TRACK_NUMBER:'6', TITLE:"Supper's Ready"]))
		
		album.discs.add(disc)
		
		return album
	}
	
	static Track createTrack(Map tags) {
		Track track = MetadataFactory.fromStringString(tags, DefaultTrack.class)
		return track
	}
	
	static void writeExampleFile(File exampleFile) {
		def writer = new PrintWriter(exampleFile)
		def indenter = new IndentPrinter(writer)
		def xml = new MarkupBuilder(indenter)
		
		xml.album {			
			tag(name:'album', value:'Foxtrot')
			tag(name:'artist', value:'Genesis')
			tag(name:'genre', value:'Progressive Rock')
			tag(name:'date', value:'1972')
			tag(name:'cddbId', value:'520C0506')
			
			disc {
				tag(name:'discNumber', value:'1')
				track {
					tag(name:'trackNumber', value:'1')
					tag(name:'title', value:'Watcher of the Skies')
				}
				track {
					tag(name:'trackNumber', value:'2')
					tag(name:'title', value:'Time Table')
				}
				track {
					tag(name:'trackNumber', value:'3')
					tag(name:'title', value:"Get 'Em Out By Friday")
				}
				track {
					tag(name:'trackNumber', value:'4')
					tag(name:'title', value:'Can-Utility and the Coastliners')
				}
				track {
					tag(name:'trackNumber', value:'5')
					tag(name:'title', value:'Horizons')
				}
				track {
					tag(name:'trackNumber', value:'6')
					tag(name:'title', value:"Supper's Ready")
				}
			}
		}
		writer.close()
	}
	
//	cue(num:'1') {
//		track(num:'1') {
//			
//		}
//		track(num:'2') {
//			index(num:'0', time:'07:24:12')
//			index(num:'1', time:'07:24:16')
//		}
//		track(num:'3') {
//			index(num:'0', time:'12:10:40')
//			index(num:'1', time:'12:10:43')
//		}
//		track(num:'4') {
//			index(num:'0', time:'20:46:12')
//			index(num:'1', time:'20:46:16')
//		}
//		track(num:'5') {
//			index(num:'0', time:'26:31:09')
//			index(num:'1', time:'26:31:13')
//		}
//		track(num:'6') {
//			index(num:'0', time:'28:12:21')
//			index(num:'1', time:'28:12:25')
//		}
//	}
}