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
import org.mulima.api.meta.Disc
import org.mulima.api.meta.Track
import org.mulima.api.meta.test.MetadataFactory
import org.mulima.internal.meta.DefaultAlbum
import org.mulima.internal.meta.DefaultDisc
import org.mulima.internal.meta.DefaultTrack

class AlbumXmlHelper {
	static MetadataFactory factory = new MetadataFactory()
	
	static {
		factory.with {
			registerImplementation Album, DefaultAlbum
			registerImplementation Disc, DefaultDisc
			registerImplementation Track, DefaultTrack
		}
	}
	
	static Album getExampleAlbum() {
		def album = factory.fromStringString([ARTIST:'Genesis', ALBUM:'Foxtrot', GENRE:'Progressive Rock', DATE:'1972', CDDB_ID:'520C0506'], Album)
		def cue = CueSheetHelper.exampleCue
		def disc = factory.fromStringString([DISC_NUMBER:'1'], Disc, album)
		
		disc.tracks.add(createTrack([TRACK_NUMBER:'1', TITLE:'Watcher of the Skies'], disc))
		disc.tracks.add(createTrack([TRACK_NUMBER:'2', TITLE:'Time Table'], disc))
		disc.tracks.add(createTrack([TRACK_NUMBER:'3', TITLE:"Get 'Em Out By Friday"], disc))
		disc.tracks.add(createTrack([TRACK_NUMBER:'4', TITLE:'Can-Utility and the Coastliners'], disc))
		disc.tracks.add(createTrack([TRACK_NUMBER:'5', TITLE:'Horizons'], disc))
		disc.tracks.add(createTrack([TRACK_NUMBER:'6', TITLE:"Supper's Ready"], disc))
		
		album.discs.add(disc)
		
		return album
	}
	
	static Track createTrack(Map tags, Disc disc) {
		return factory.fromStringString(tags, Track, disc)
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
}